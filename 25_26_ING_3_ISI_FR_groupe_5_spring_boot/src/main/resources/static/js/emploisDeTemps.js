document.addEventListener('DOMContentLoaded', () => {

    // ══════════════════════════════════════════════
    // STATE & UTILS
    // ══════════════════════════════════════════════
    let TEACHERS = [];
    let cards =[], pendingDel = null;
    let selTeachers = new Set(), selSubject = null, colorCours = '#dc2626', colorEv = '#dc2626';
    let mobSelTeachers = new Set(), mobSelSubject = null, mobColorCours = '#dc2626', mobColorEv = '#dc2626';
    let calendar, fcDrag;
    let resizeInProgress = false;
    let currentClasseId = null;

    const $ = id => document.getElementById(id);
    const uid = () => Math.random().toString(36).slice(2, 9);

    // ✅ Fonction globale pour éviter les bugs de fuseau horaire UTC
    function getLocalISODate(dateObj) {
        const y = dateObj.getFullYear();
        const m = String(dateObj.getMonth() + 1).padStart(2, '0');
        const d = String(dateObj.getDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
    }

    let tt;
    function toast(msg) {
        clearTimeout(tt);
        $('toast').textContent = msg;
        $('toast').classList.add('on');
        tt = setTimeout(() => $('toast').classList.remove('on'), 2800);
    }
    function showLoading() { $('loading-bar').classList.add('on'); }
    function hideLoading() { $('loading-bar').classList.remove('on'); }

    // ══════════════════════════════════════════════
    // CHARGEMENT DONNÉES CLASSE (Enseignants + UEs)
    // ══════════════════════════════════════════════
    async function loadDonneesClasse(classeId) {
        if (!classeId) { TEACHERS = []; return; }
        try {
            showLoading();
            const res = await fetch(`/admin/classes/${classeId}/ues-enseignants`);
            if (!res.ok) throw new Error('Erreur chargement données classe');
            const ues = await res.json();
            const teacherMap = new Map();
            ues.forEach(ue => {
                ue.enseignants.forEach(e => {
                    if (!teacherMap.has(e.id)) {
                        teacherMap.set(e.id, { id: e.id, name: e.nom + ' ' + e.prenom, color: e.couleur, subjects: [] });
                    }
                    teacherMap.get(e.id).subjects.push({ id: ue.id, nom: ue.nom, code: ue.code });
                });
            });
            TEACHERS = Array.from(teacherMap.values());
        } catch (err) {
            toast('Erreur chargement données de la classe');
        } finally {
            hideLoading();
        }
    }

    // ══════════════════════════════════════════════
    // SAUVEGARDE, SUPPRESSION ET MISE À JOUR (CRUD)
    // ══════════════════════════════════════════════
    async function sauvegarderSeance(fcEvent) {
        if (!currentClasseId) { toast('⚠ Sélectionnez une classe'); fcEvent.remove(); return; }
        if (fcEvent.extendedProps?.isPause || fcEvent.extendedProps?.fromBackend) return;
        const props = fcEvent.extendedProps;
        if (!props.ueId || !props.enseignantId) { toast('⚠ Cours incomplet'); fcEvent.remove(); return; }
        const start = fcEvent.start;
        const end   = fcEvent.end || new Date(start.getTime() + 3600000);
        showLoading();
        try {
            const body = {
                jour: getLocalISODate(start),
                heureDebut: start.toTimeString().slice(0, 8),
                heureFin: end.toTimeString().slice(0, 8),
                salle: props.salle || '',
                couleur: fcEvent.backgroundColor || '#dc2626',
                classeId: currentClasseId,
                ueId: props.ueId,
                enseignantId: props.enseignantId
            };
            const res = await fetch('/admin/emplois-du-temps/creer', {
                method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
            });
            if (!res.ok) { const msg = await res.text(); toast('⚠ ' + msg); fcEvent.remove(); return; }
            const saved = await res.json();
            fcEvent.setProp('id', String(saved.id));
            fcEvent.setExtendedProp('dbId', saved.id);
            fcEvent.setExtendedProp('fromBackend', true);
            toast('✅ Cours enregistré');
        } catch { toast('Erreur sauvegarde'); fcEvent.remove(); }
        finally { hideLoading(); }
    }

    async function supprimerSeance(fcEvent) {
        const dbId = fcEvent.extendedProps?.dbId;
        if (!dbId) { fcEvent.remove(); return; }
        showLoading();
        try {
            const res = await fetch(`/admin/emplois-du-temps/supprimer/${dbId}`, { method: 'DELETE' });
            if (!res.ok) { toast('Erreur suppression'); return; }
            fcEvent.remove();
            toast('Séance supprimée');
        } catch { toast('Erreur suppression'); }
        finally { hideLoading(); }
    }

    async function mettreAJourSeance(fcEvent, revertFn) {
        const dbId = fcEvent.extendedProps?.dbId;
        if (!dbId || !currentClasseId) { if (typeof revertFn === 'function') revertFn(); return; }
        const props = fcEvent.extendedProps;
        const start = fcEvent.start;
        const end   = fcEvent.end || new Date(start.getTime() + 3600000);
        showLoading();
        try {
            const body = {
                jour: getLocalISODate(start),
                heureDebut: start.toTimeString().slice(0, 8),
                heureFin: end.toTimeString().slice(0, 8),
                salle: props.salle || '',
                couleur: fcEvent.backgroundColor || '#dc2626',
                classeId: currentClasseId,
                ueId: props.ueId,
                enseignantId: props.enseignantId
            };
            const res = await fetch(`/admin/emplois-du-temps/modifier/${dbId}`, {
                method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
            });
            if (!res.ok) {
                const msg = await res.text();
                toast('⚠ ' + msg);
                if (typeof revertFn === 'function') revertFn();
                return;
            }
            toast('✅ Séance mise à jour');
        } catch {
            toast('Erreur de mise à jour');
            if (typeof revertFn === 'function') revertFn();
        } finally { hideLoading(); }
    }

    // ══════════════════════════════════════════════
    // TABS SIDEBAR & SWATCHES
    // ══════════════════════════════════════════════
    document.querySelectorAll('.sbtab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.sbtab').forEach(t => t.classList.remove('on'));
            document.querySelectorAll('.panel').forEach(p => p.classList.remove('on'));
            tab.classList.add('on');
            $('panel-' + tab.dataset.tab).classList.add('on');
        });
    });

    function switchTab(tab) {
        document.querySelectorAll('.sbtab').forEach(t => t.classList.remove('on'));
        document.querySelectorAll('.panel').forEach(p => p.classList.remove('on'));
        document.querySelector(`[data-tab="${tab}"]`).classList.add('on');
        $('panel-' + tab).classList.add('on');
    }

    function initSwatches(containerId, pickId, onChange) {
        const cont = $(containerId), pick = $(pickId);
        cont.querySelectorAll('.sw[data-c]').forEach(sw => {
            sw.addEventListener('click', () => {
                cont.querySelectorAll('.sw,.sw-pick').forEach(s => s.classList.remove('on'));
                sw.classList.add('on'); onChange(sw.dataset.c);
            });
        });
        cont.querySelector('.sw-pick').addEventListener('click', () => pick.click());
        pick.addEventListener('input', e => {
            cont.querySelectorAll('.sw,.sw-pick').forEach(s => s.classList.remove('on'));
            const sp = cont.querySelector('.sw-pick');
            sp.classList.add('on'); sp.style.background = e.target.value;
            onChange(e.target.value);
        });
    }
    initSwatches('sw-cours',     'cpick-cours',     c => { colorCours = c; updateCoursPreview(); });
    initSwatches('sw-ev',        'cpick-ev',        c => { colorEv = c; updateEvPreview(); });
    initSwatches('mob-sw-cours', 'mob-cpick-cours', c => { mobColorCours = c; updateMobCoursPreview(); });
    initSwatches('mob-sw-ev',    'mob-cpick-ev',    c => { mobColorEv = c; updateMobEvPreview(); });

    // ══════════════════════════════════════════════
    // CHIPS ENSEIGNANTS / MATIÈRES DESKTOP
    // ══════════════════════════════════════════════
    function buildTeacherChips() {
        const grid = $('teacher-chips');
        if (!TEACHERS.length) {
            grid.innerHTML = `<span style="font-size:11px;color:var(--muted);font-style:italic">${currentClasseId ? 'Aucun enseignant.' : 'Sélectionnez une classe...'}</span>`;
            return;
        }
        const visible = selSubject ? TEACHERS.filter(t => t.subjects.some(s => s.id === selSubject.id)) : TEACHERS;
        grid.innerHTML = visible.map(t => `<div class="chip${selTeachers.has(t.name) ? ' on' : ''}" data-teacher="${t.name}" data-id="${t.id}">${t.name}</div>`).join('');
        grid.querySelectorAll('.chip').forEach(chip => {
            chip.addEventListener('click', () => {
                const name = chip.dataset.teacher;
                if (selTeachers.has(name)) { selTeachers.delete(name); if (!selTeachers.size) selSubject = null; }
                else selTeachers.add(name);
                updateCours();
            });
        });
    }

    function buildSubjectChips() {
        const grid = $('subject-chips');
        const selectedObjs = TEACHERS.filter(t => selTeachers.has(t.name));
        const available = selectedObjs.length > 0
            ? [...new Map(selectedObjs.flatMap(t => t.subjects).map(s => [s.id, s])).values()]
            : [...new Map(TEACHERS.flatMap(t => t.subjects).map(s => [s.id, s])).values()];
        if (!available.length) { grid.innerHTML = '<span style="font-size:12px;color:var(--muted);font-style:italic">Aucune matière.</span>'; return; }
        grid.innerHTML = available.map(s => `<div class="chip chip-subj${selSubject?.id === s.id ? ' on' : ''}" data-subj="${s.nom}" data-id="${s.id}">${s.nom}</div>`).join('');
        grid.querySelectorAll('.chip-subj').forEach(chip => {
            chip.addEventListener('click', () => {
                const id = parseInt(chip.dataset.id);
                if (selSubject?.id === id) { selSubject = null; selTeachers = new Set(); }
                else { selSubject = available.find(s => s.id === id); selTeachers = new Set(TEACHERS.filter(t => t.subjects.some(s => s.id === id)).map(t => t.name)); }
                updateCours();
            });
        });
    }

    function updateCours() {
        buildTeacherChips(); buildSubjectChips(); updateCoursPreview();
        const info = $('sel-info');
        if (selTeachers.size && selSubject) info.textContent = [...selTeachers].join(' + ') + ' — ' + selSubject.nom;
        else if (selSubject) info.textContent = 'Matière : ' + selSubject.nom;
        else if (selTeachers.size) info.textContent = selTeachers.size + ' enseignant(s) — choisissez une matière';
        else info.textContent = '';
        const valid = selSubject && selTeachers.size && TEACHERS.some(t => selTeachers.has(t.name) && t.subjects.some(s => s.id === selSubject.id));
        $('btn-create-cours').disabled = !valid;
    }

    function updateCoursPreview() {
        const p = $('prev-cours');
        if (selTeachers.size && selSubject) { $('pv-t').textContent = selSubject.nom; $('pv-s').textContent = [...selTeachers].join(' / '); p.style.background = colorCours; p.classList.add('show'); }
        else p.classList.remove('show');
    }

    $('btn-create-cours').addEventListener('click', () => {
        if (!selSubject || !selTeachers.size) return;
        const enseignantObj = TEACHERS.find(t => selTeachers.has(t.name));
        const salle = $('cours-salle').value.trim();
        cards.push({ id: uid(), title: selSubject.nom, sub: [...selTeachers].join(' / ') + (salle ? ' · ' + salle : ''), color: colorCours, isPause: false, ueId: selSubject.id, enseignantId: enseignantObj?.id, salle });
        renderCards(); switchTab('liste');
        toast('Carte créée → onglet "À placer"');
    });

    function updateEvPreview() {
        const titre = $('ev-titre').value.trim();
        $('btn-create-ev').disabled = !titre;
        const p = $('prev-ev');
        if (titre) { $('ev-pv-t').textContent = titre; $('ev-pv-s').textContent = $('ev-detail').value.trim(); p.style.background = colorEv; p.classList.add('show'); }
        else p.classList.remove('show');
    }
    $('ev-titre').addEventListener('input', updateEvPreview);
    $('ev-detail').addEventListener('input', updateEvPreview);
    $('btn-create-ev').addEventListener('click', () => {
        const titre = $('ev-titre').value.trim();
        if (!titre) return;
        cards.push({ id: uid(), title: titre, sub: $('ev-detail').value.trim(), color: colorEv, isPause: false, isEvent: true });
        $('ev-titre').value = ''; $('ev-detail').value = ''; $('ev-desc').value = '';
        updateEvPreview(); renderCards(); switchTab('liste');
        toast('Événement créé → onglet "À placer"');
    });

    // ══════════════════════════════════════════════
    // CHIPS MOBILE
    // ══════════════════════════════════════════════
    function buildMobTeacherChips() {
        const grid = $('mob-teacher-chips');
        if (!TEACHERS.length) { grid.innerHTML = '<span style="font-size:11px;color:var(--muted);font-style:italic">Aucun enseignant.</span>'; return; }
        const visible = mobSelSubject ? TEACHERS.filter(t => t.subjects.some(s => s.id === mobSelSubject.id)) : TEACHERS;
        grid.innerHTML = visible.map(t => `<div class="chip${mobSelTeachers.has(t.name) ? ' on' : ''}" data-teacher="${t.name}" data-id="${t.id}">${t.name}</div>`).join('');
        grid.querySelectorAll('.chip').forEach(chip => {
            chip.addEventListener('click', () => {
                const name = chip.dataset.teacher;
                if (mobSelTeachers.has(name)) { mobSelTeachers.delete(name); if (!mobSelTeachers.size) mobSelSubject = null; }
                else mobSelTeachers.add(name);
                updateMobCours();
            });
        });
    }

    function buildMobSubjectChips() {
        const grid = $('mob-subject-chips');
        const selectedObjs = TEACHERS.filter(t => mobSelTeachers.has(t.name));
        const available = selectedObjs.length > 0
            ? [...new Map(selectedObjs.flatMap(t => t.subjects).map(s => [s.id, s])).values()]
            : [...new Map(TEACHERS.flatMap(t => t.subjects).map(s => [s.id, s])).values()];
        if (!available.length) { grid.innerHTML = '<span style="font-size:12px;color:var(--muted);font-style:italic">Sélectionnez un enseignant…</span>'; return; }
        grid.innerHTML = available.map(s => `<div class="chip chip-subj${mobSelSubject?.id === s.id ? ' on' : ''}" data-subj="${s.nom}" data-id="${s.id}">${s.nom}</div>`).join('');
        grid.querySelectorAll('.chip-subj').forEach(chip => {
            chip.addEventListener('click', () => {
                const id = parseInt(chip.dataset.id);
                if (mobSelSubject?.id === id) { mobSelSubject = null; mobSelTeachers = new Set(); }
                else { mobSelSubject = available.find(s => s.id === id); mobSelTeachers = new Set(TEACHERS.filter(t => t.subjects.some(s => s.id === id)).map(t => t.name)); }
                updateMobCours();
            });
        });
    }

    function updateMobCours() {
        buildMobTeacherChips(); buildMobSubjectChips(); updateMobCoursPreview();
        $('mob-btn-create-cours').disabled = !(mobSelSubject && mobSelTeachers.size);
        const info = $('mob-sel-info');
        if (mobSelTeachers.size && mobSelSubject) info.textContent = [...mobSelTeachers].join(' + ') + ' — ' + mobSelSubject.nom;
        else if (mobSelTeachers.size) info.textContent = mobSelTeachers.size + ' enseignant(s)';
        else info.textContent = '';
    }

    function updateMobCoursPreview() {
        const p = $('mob-prev-cours');
        if (mobSelTeachers.size && mobSelSubject) { $('mob-pv-t').textContent = mobSelSubject.nom; $('mob-pv-s').textContent = [...mobSelTeachers].join(' / '); p.style.background = mobColorCours; p.classList.add('show'); }
        else p.classList.remove('show');
    }

    $('mob-btn-create-cours').addEventListener('click', () => {
        if (!mobSelSubject || !mobSelTeachers.size) return;
        const enseignantObj = TEACHERS.find(t => mobSelTeachers.has(t.name));
        cards.push({ id: uid(), title: mobSelSubject.nom, sub: [...mobSelTeachers].join(' / '), color: mobColorCours, isPause: false, ueId: mobSelSubject.id, enseignantId: enseignantObj?.id });
        renderCards(); closeMobModal();
        toast('Carte créée → onglet "À placer"');
    });

    function updateMobEvPreview() {
        const titre = $('mob-ev-titre').value.trim();
        $('mob-btn-create-ev').disabled = !titre;
        const p = $('mob-prev-ev');
        if (titre) { $('mob-ev-pv-t').textContent = titre; $('mob-ev-pv-s').textContent = $('mob-ev-detail').value.trim(); p.style.background = mobColorEv; p.classList.add('show'); }
        else p.classList.remove('show');
    }
    $('mob-ev-titre').addEventListener('input', updateMobEvPreview);
    $('mob-ev-detail').addEventListener('input', updateMobEvPreview);
    $('mob-btn-create-ev').addEventListener('click', () => {
        const titre = $('mob-ev-titre').value.trim();
        if (!titre) return;
        cards.push({ id: uid(), title: titre, sub: $('mob-ev-detail').value.trim(), color: mobColorEv, isPause: false, isEvent: true });
        $('mob-ev-titre').value = ''; $('mob-ev-detail').value = '';
        updateMobEvPreview(); renderCards(); closeMobModal();
        toast('Événement créé → onglet "À placer"');
    });

    document.querySelectorAll('.mob-tab').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.mob-tab').forEach(b => { b.style.background = 'transparent'; b.style.color = 'var(--muted)'; });
            btn.style.background = 'var(--red)'; btn.style.color = '#fff';
            const tab = btn.dataset.mtab;
            $('mob-panel-cours').style.display     = tab === 'cours'     ? 'block' : 'none';
            $('mob-panel-evenement').style.display = tab === 'evenement' ? 'block' : 'none';
        });
    });

    function openMobModal()  { $('mob-modal').classList.add('on'); document.body.style.overflow = 'hidden'; }
    function closeMobModal() { $('mob-modal').classList.remove('on'); document.body.style.overflow = ''; }
    $('fab').addEventListener('click', openMobModal);
    $('mob-modal-close').addEventListener('click', closeMobModal);
    $('mob-modal').addEventListener('click', e => { if (e.target === $('mob-modal')) closeMobModal(); });

    // ══════════════════════════════════════════════
    // DRAG CARDS
    // ══════════════════════════════════════════════
    function renderCards() {
        const list = $('cards');
        list.innerHTML = cards.map(c => `
        <div class="dc fc-event" id="dc-${c.id}"
             data-title="${c.title}${c.sub ? '\n' + c.sub : ''}"
             data-color="${c.color}" data-cid="${c.id}" data-pause="0"
             data-ue-id="${c.ueId || ''}" data-ens-id="${c.enseignantId || ''}" data-salle="${c.salle || ''}"
             style="background:${c.color}">
            <div class="dc-t">${c.title}</div>
            ${c.sub ? `<div class="dc-s">${c.sub}</div>` : ''}
            ${c.isEvent ? `<div class="dc-badge">📌 Événement</div>` : ''}
            <button class="dc-x" data-del="${c.id}">✕</button>
        </div>`).join('');
        list.querySelectorAll('.dc-x').forEach(btn => {
            btn.addEventListener('click', e => { e.stopPropagation(); cards = cards.filter(c => c.id !== btn.dataset.del); renderCards(); });
        });
        $('empty-cards').style.display = cards.length === 0 ? 'flex' : 'none';
        initFCDrag();
    }

    function initFCDrag() {
        if (fcDrag) fcDrag.destroy();
        fcDrag = new FullCalendar.Draggable($('sb-body'), {
            itemSelector: '.fc-event',
            eventData: el => ({
                id: uid(),
                title: el.getAttribute('data-title'),
                backgroundColor: el.getAttribute('data-color'),
                borderColor:     el.getAttribute('data-color'),
                extendedProps: {
                    cid:          el.getAttribute('data-cid'),
                    isPause:      el.getAttribute('data-pause') === '1',
                    ueId:         el.getAttribute('data-ue-id') ? parseInt(el.getAttribute('data-ue-id')) : null,
                    enseignantId: el.getAttribute('data-ens-id') ? parseInt(el.getAttribute('data-ens-id')) : null,
                    salle:        el.getAttribute('data-salle') || ''
                }
            })
        });
    }

    // ══════════════════════════════════════════════
    // FULLCALENDAR — cache natif via events: function()
    // ══════════════════════════════════════════════
    calendar = new FullCalendar.Calendar($('calendar'), {
        locale: 'fr', initialView: 'timeGridWeek',
        firstDay: 1, hiddenDays: [0], headerToolbar: false,
        dayHeaderFormat: { weekday: 'long', day: 'numeric' },
        slotMinTime: '07:00:00', slotMaxTime: '19:00:00',
        slotDuration: '00:30:00', slotLabelInterval: '01:00:00',
        slotLabelFormat: { hour: '2-digit', minute: '2-digit', hour12: false },
        allDaySlot: false, editable: true, droppable: true,
        eventDurationEditable: true, eventResizableFromStart: true,
        defaultTimedEventDuration: '01:00:00', snapDuration: '00:30:00',

        eventOverlap: (still, moving) => moving.extendedProps?.isPause || still.extendedProps?.isPause,

        // ✅ FullCalendar gère le cache et les requêtes automatiquement
        events: async function(fetchInfo, successCallback, failureCallback) {
            if (!currentClasseId) { successCallback([]); return; }
            showLoading();
            try {
                const startStr = getLocalISODate(fetchInfo.start);
                const endStr   = getLocalISODate(fetchInfo.end);
                const res = await fetch(`/admin/emplois-du-temps/api/classe/${currentClasseId}?debut=${startStr}&fin=${endStr}`);
                if (!res.ok) throw new Error('Erreur API');
                const seances = await res.json();
                successCallback(seances.map(s => ({
                    id:              String(s.id),
                    title:           s.ueNom + '\n' + s.enseignantNom + ' ' + s.enseignantPrenom,
                    start:           s.jour + 'T' + s.heureDebut,
                    end:             s.jour + 'T' + s.heureFin,
                    backgroundColor: s.couleur || '#dc2626',
                    borderColor:     s.couleur || '#dc2626',
                    extendedProps: {
                        dbId:         s.id,
                        ueId:         s.ueId,
                        enseignantId: s.enseignantId,
                        ueNom:        s.ueNom,
                        ueCode:       s.ueCode,
                        salle:        s.salle,
                        classeNom:    s.classeNom,
                        fromBackend:  true
                    }
                })));
            } catch (err) {
                toast('Erreur de chargement EDT');
                failureCallback(err);
            } finally {
                hideLoading();
            }
        },

        datesSet(info) {
            const s = info.start;
            const e = new Date(info.end); e.setDate(e.getDate() - 1);
            const f = d => d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' });
            $('wlabel').textContent = `${f(s)} — ${f(e)}`;
        },

        drop(info) {
            if (info.draggedEl.getAttribute('data-pause') !== '1' && $('chk-rm').checked) {
                cards = cards.filter(c => c.id !== info.draggedEl.getAttribute('data-cid'));
                renderCards();
            }
            if (window.innerWidth <= 640) closeSidebar();
        },

        eventReceive(info) {
            if (!info.event.extendedProps?.isClone) {
                info.event.setEnd(new Date(info.event.start.getTime() + 3600000));
            }
            sauvegarderSeance(info.event);
        },

        eventDrop(info)   { mettreAJourSeance(info.event, info.revert); },
        eventResize(info) { mettreAJourSeance(info.event, info.revert); },

        eventClick(info) {
            pendingDel = info.event;
            const parts = info.event.title.split('\n');
            $('m-t').textContent = parts[0];
            $('m-d').textContent = parts[1] || (info.event.extendedProps?.isPause ? 'Supprimer cette pause ?' : 'Supprimer cet événement ?');
            $('mbg').classList.add('on');
        },

        eventContent(arg) {
            const parts = arg.event.title.split('\n');
            if (arg.event.extendedProps?.isPause) {
                return { html: `<div class="ev-pause" style="position:relative;height:100%"><span>⏸ PAUSE</span><div class="ev-lhandle"></div><div class="ev-hhandle"></div></div>` };
            }
            const salle = arg.event.extendedProps?.salle ? ` · ${arg.event.extendedProps.salle}` : '';
            return { html: `<div class="ev"><div class="ev-lhandle"></div><div class="ev-t">${parts[0] || ''}</div>${parts[1] ? `<div class="ev-s">${parts[1]}${salle}</div>` : ''}<div class="ev-hhandle"></div></div>` };
        },

        eventDidMount: info => setupHResize(info.event, info.el),
    });

    calendar.render();

    // ══════════════════════════════════════════════
    // SÉLECTEUR DE CLASSE
    // ══════════════════════════════════════════════
    $('classe-select').addEventListener('change', async (e) => {
        currentClasseId = e.target.value ? parseInt(e.target.value) : null;
        selTeachers = new Set(); selSubject = null;
        mobSelTeachers = new Set(); mobSelSubject = null;
        if (!currentClasseId) {
            TEACHERS = [];
            buildTeacherChips(); buildSubjectChips();
            buildMobTeacherChips(); buildMobSubjectChips();
            calendar.removeAllEvents();
            return;
        }
        toast('Chargement…');
        await loadDonneesClasse(currentClasseId);
        buildTeacherChips(); buildSubjectChips();
        buildMobTeacherChips(); buildMobSubjectChips();
        // ✅ FullCalendar recharge automatiquement via events: function()
        calendar.refetchEvents();
    });

    // ══════════════════════════════════════════════
    // RESIZE HORIZONTAL
    // ══════════════════════════════════════════════
    function getColumnRects() {
        return Array.from(document.querySelectorAll('#calendar .fc-timegrid-col[data-date]')).map(col => {
            const r = col.getBoundingClientRect();
            return { left: r.left, right: r.right, width: r.width, date: col.getAttribute('data-date') };
        });
    }

    function setupHResize(fcEvent, el) {
        const tryAttach = () => {
            const rightHandle = el.querySelector('.ev-hhandle');
            const leftHandle  = el.querySelector('.ev-lhandle');
            if (!rightHandle && !leftHandle) { requestAnimationFrame(tryAttach); return; }

            function startDrag(e, isLeft) {
                if (resizeInProgress) return;
                const isTouch = e.type === 'touchstart';
                if (!isTouch) e.preventDefault();
                e.stopPropagation();
                const currentEvent = calendar.getEventById(fcEvent.id) || fcEvent;
                const elRect    = el.getBoundingClientRect();
                const origStart = new Date(currentEvent.start);
                const origEnd   = currentEvent.end ? new Date(currentEvent.end) : new Date(origStart.getTime() + 3600000);
                const color = currentEvent.backgroundColor || '#dc2626';
                const ghost = $('h-ghost');
                ghost.style.cssText = `display:block;background:${color};opacity:.3;border-radius:6px;top:${elRect.top}px;height:${elRect.height}px;position:fixed;z-index:999;pointer-events:none;`;
                ghost.style.left = elRect.left + 'px'; ghost.style.width = elRect.width + 'px';
                let finalStart = origStart, finalEnd = origStart;

                function onMove(ev) {
                    if (isTouch && ev.cancelable) ev.preventDefault();
                    const mx = isTouch ? ev.touches[0].clientX : ev.clientX;
                    const colRects = getColumnRects();
                    if (!colRects.length) return;
                    let tCol = null;
                    for (const c of colRects) { if (mx >= c.left && mx <= c.right) { tCol = c; break; } }
                    if (!tCol) { if (mx < colRects[0].left) tCol = colRects[0]; else if (mx > colRects[colRects.length-1].right) tCol = colRects[colRects.length-1]; }
                    if (!tCol) return;
                    const startIdx = colRects.findIndex(c => c.date === getLocalISODate(origStart));
                    if (startIdx === -1) return;
                    const tIdx = colRects.indexOf(tCol);
                    if (isLeft) {
                        let validIdx = Math.min(tIdx, startIdx), validCol = colRects[validIdx];
                        finalStart = new Date(validCol.date + 'T' + origStart.toTimeString().slice(0,8));
                        finalEnd = origStart;
                        ghost.style.left = validCol.left + 'px'; ghost.style.width = (elRect.right - validCol.left) + 'px';
                    } else {
                        let validIdx = Math.max(tIdx, startIdx), validCol = colRects[validIdx];
                        finalStart = origStart;
                        finalEnd = new Date(validCol.date + 'T' + origStart.toTimeString().slice(0,8));
                        ghost.style.left = elRect.left + 'px'; ghost.style.width = (validCol.right - elRect.left) + 'px';
                    }
                }

                async function onUp() {
                    if (isTouch) { document.removeEventListener('touchmove', onMove); document.removeEventListener('touchend', onUp); }
                    else { document.removeEventListener('mousemove', onMove); document.removeEventListener('mouseup', onUp); }
                    ghost.style.display = 'none';

                    if (finalStart.getTime() === origStart.getTime() && finalEnd.getTime() === origEnd.getTime()) return;

                    const title     = currentEvent.title;
                    const props     = { ...currentEvent.extendedProps };
                    const startTime = origStart.toTimeString().slice(0, 8);
                    const endTime   = origEnd.toTimeString().slice(0, 8);
                    const origDateStr  = getLocalISODate(origStart);
                    const finalDateStr = getLocalISODate(isLeft ? finalStart : finalEnd);
                    const startDateStr = isLeft ? finalDateStr : origDateStr;
                    const endDateStr   = isLeft ? origDateStr  : finalDateStr;
                    const cols = getColumnRects().filter(c => c.date >= startDateStr && c.date <= endDateStr);
                    if (!cols.length) return;

                    const originalEventId = currentEvent.id;
                    currentEvent.remove();
                    resizeInProgress = true;

                    try {
                        const promises = cols.map(async (col) => {
                            const ds = new Date(col.date + 'T' + startTime);
                            const de = new Date(col.date + 'T' + endTime);

                            // ✅ Jour d'origine — on réaffiche sans recréer en base
                            const isOriginalDay = (col.date === origDateStr);
                            const alreadyInDb   = isOriginalDay && props.fromBackend && props.dbId;

                            if (alreadyInDb) {
                                calendar.addEvent({
                                    id: originalEventId, title, start: ds, end: de,
                                    backgroundColor: color, borderColor: color,
                                    extendedProps: { ...props }
                                });
                                return;
                            }

                            // ✅ Nouveau jour — créer visuellement + sauvegarder en base
                            const tempId = uid();
                            calendar.addEvent({
                                id: tempId, title, start: ds, end: de,
                                backgroundColor: color, borderColor: color,
                                extendedProps: { ...props, isClone: false, fromBackend: false, dbId: null }
                            });

                            if (!currentClasseId || !props.ueId || !props.enseignantId) return;

                            try {
                                const postRes = await fetch('/admin/emplois-du-temps/creer', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify({
                                        jour: col.date, heureDebut: startTime, heureFin: endTime,
                                        salle: props.salle || '', couleur: color,
                                        classeId: currentClasseId, ueId: props.ueId, enseignantId: props.enseignantId
                                    })
                                });
                                if (!postRes.ok) {
                                    const msg = await postRes.text();
                                    throw new Error(msg || 'Erreur création');
                                }
                                const saved = await postRes.json();
                                const ev = calendar.getEventById(tempId);
                                if (ev) {
                                    ev.setProp('id', String(saved.id));
                                    ev.setExtendedProp('dbId', saved.id);
                                    ev.setExtendedProp('fromBackend', true);
                                }
                            } catch (colError) {
                                toast(`⚠ ${col.date} : ${colError.message}`);
                                const ev = calendar.getEventById(tempId);
                                if (ev) ev.remove();
                            }
                        });

                        await Promise.all(promises);

                    } finally {
                        resizeInProgress = false;
                    }
                }

                if (isTouch) { document.addEventListener('touchmove', onMove, {passive: false}); document.addEventListener('touchend', onUp); }
                else { document.addEventListener('mousemove', onMove); document.addEventListener('mouseup', onUp); }
            }

            if (rightHandle) { rightHandle.addEventListener('mousedown', e => startDrag(e, false)); rightHandle.addEventListener('touchstart', e => startDrag(e, false), {passive: false}); }
            if (leftHandle)  { leftHandle.addEventListener('mousedown',  e => startDrag(e, true));  leftHandle.addEventListener('touchstart',  e => startDrag(e, true),  {passive: false}); }
        };
        requestAnimationFrame(tryAttach);
    }

    // ══════════════════════════════════════════════
    // NAVIGATION & VUES
    // ══════════════════════════════════════════════
    $('btn-prev').addEventListener('click', () => calendar.prev());
    $('btn-next').addEventListener('click', () => calendar.next());
    $('winput').addEventListener('change', e => { if (e.target.value) calendar.gotoDate(e.target.value); });
    $('winput').value = getLocalISODate(new Date());

    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.view-btn').forEach(b => b.classList.remove('on'));
            btn.classList.add('on');
            calendar.changeView(btn.dataset.view);
        });
    });

    function openSidebar()  { $('sb').classList.add('open'); $('sb-overlay').classList.add('on'); document.body.style.overflow = 'hidden'; }
    function closeSidebar() { $('sb').classList.remove('open'); $('sb-overlay').classList.remove('on'); document.body.style.overflow = ''; }
    $('btn-menu').addEventListener('click', openSidebar);
    $('btn-sb-close').addEventListener('click', closeSidebar);
    $('sb-overlay').addEventListener('click', closeSidebar);

    function adaptView() {
        const w = window.innerWidth, cv = calendar.view.type;
        if (w <= 400 && cv !== 'timeGridDay') calendar.changeView('timeGridDay');
        else if (w > 400 && w <= 640 && cv !== 'timeGridWeek') calendar.changeView('timeGridWeek');
        else if (w > 640 && cv !== 'timeGridWeek') calendar.changeView('timeGridWeek');
        calendar.updateSize();
    }
    window.addEventListener('resize', () => { clearTimeout(window._rt); window._rt = setTimeout(adaptView, 180); });
    setTimeout(adaptView, 100);

    // MODALES
    $('btn-clear').addEventListener('click', () => $('clear-bg').classList.add('on'));
    $('clear-cancel').addEventListener('click', () => $('clear-bg').classList.remove('on'));
    $('clear-confirm').addEventListener('click', () => { calendar.getEvents().forEach(e => e.remove()); $('clear-bg').classList.remove('on'); toast('Emploi du temps vidé'); });
    $('clear-bg').addEventListener('click', e => { if (e.target === $('clear-bg')) $('clear-bg').classList.remove('on'); });

    $('m-cancel').addEventListener('click', () => { $('mbg').classList.remove('on'); pendingDel = null; });
    $('m-confirm').addEventListener('click', () => { if (pendingDel) supprimerSeance(pendingDel); $('mbg').classList.remove('on'); pendingDel = null; });
    $('mbg').addEventListener('click', e => { if (e.target === $('mbg')) { $('mbg').classList.remove('on'); pendingDel = null; } });

    // ══════════════════════════════════════════════
    // INIT
    // ══════════════════════════════════════════════
    function init() {
        buildTeacherChips();
        buildSubjectChips();
        buildMobTeacherChips();
        buildMobSubjectChips();
        renderCards();
        updateEvPreview();
        updateMobEvPreview();
    }

    init();

});