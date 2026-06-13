document.addEventListener('DOMContentLoaded', () => {

    // ══════════════════════════════════════════════
    // STATE
    // ══════════════════════════════════════════════
    let wizStep        = 1;
    const wizTotal     = 4;
    let selectedType     = null;

    const STEP_TITLES = [
        'Informations personnelles',
        'Rôle & Permissions',
        'Assignations (UE / Classes)',
        'Confirmation & Envoi'
    ];
    const STEP_SUBS = [
        'Renseignez les informations de base de l\'utilisateur',
        'Attribuez un rôle et configurez les permissions',
        'Sélectionnez les UE ou classes associées',
        'Vérifiez les informations avant de créer le compte'
    ];

    // ══════════════════════════════════════════════
    // NAVIGATION
    // ══════════════════════════════════════════════
    function wizNext() {
        if (!wizValidateStep(wizStep)) return;
        if (wizStep < wizTotal) {
            wizStep++;
            wizRender();
        }
    }

    function wizPrev() {
        if (wizStep > 1) {
            wizStep--;
            wizRender();
        }
    }

    // ══════════════════════════════════════════════
    // RENDU STEPPER
    // ══════════════════════════════════════════════
    function wizRender() {
        // Panels
        document.querySelectorAll('.step-panel').forEach((p, i) => {
            p.classList.toggle('hidden', i + 1 !== wizStep);
        });

        // Indicateurs stepper (Gestion sémantique par classes)
        document.querySelectorAll('.step-item').forEach((item, i) => {
            const step = i + 1;
            item.classList.remove('active', 'done');
            if (step === wizStep) {
                item.classList.add('active');
            } else if (step < wizStep) {
                item.classList.add('done');
            }
        });

        // Lignes de progression (Utilisation de Tailwind)
        for (let i = 1; i <= wizTotal - 1; i++) {
            const line = document.getElementById(`line-${i}-${i + 1}`);
            if (line) {
                const isCompleted = wizStep > i;
                line.classList.toggle('bg-emerald-500', isCompleted);
                line.style.background = isCompleted ? '' : 'var(--color-bgsubtle)';
            }
        }

        // Titres
        document.getElementById('step-title').textContent    = STEP_TITLES[wizStep - 1];
        document.getElementById('step-subtitle').textContent = STEP_SUBS[wizStep - 1];
        document.getElementById('step-counter').textContent  = `Étape ${wizStep} sur ${wizTotal}`;

        // Boutons
        const btnBack   = document.getElementById('btn-back');
        const btnNext   = document.getElementById('btn-next');
        const btnSubmit = document.getElementById('btn-submit');

        btnBack.classList.toggle('hidden',   wizStep === 1);
        btnBack.classList.toggle('flex',     wizStep > 1);
        btnBack.classList.toggle('items-center', wizStep > 1);

        btnNext.classList.toggle('hidden',   wizStep === wizTotal);
        btnNext.classList.toggle('flex',     wizStep !== wizTotal);
        btnNext.classList.toggle('items-center', wizStep !== wizTotal);

        btnSubmit.classList.toggle('hidden', wizStep !== wizTotal);
        btnSubmit.classList.toggle('flex',   wizStep === wizTotal);
        btnSubmit.classList.toggle('items-center', wizStep === wizTotal);

        // Étape 3 — afficher/masquer selon le type
        if (wizStep === 3) {
            const surMsg   = document.getElementById('step3-sur-msg');
            const ensArea  = document.getElementById('step3-ens-area');
            const astArea  = document.getElementById('step3-ast-area');
            const isSur = selectedType === 'SUR';
            const isEns = selectedType === 'ENS';
            const isAst = selectedType === 'AST';

            surMsg?.classList.toggle('hidden', !isSur);
            surMsg?.classList.toggle('flex',   isSur);
            ensArea?.classList.toggle('hidden', !isEns);
            astArea?.classList.toggle('hidden', !isAst);
        }

        // Étape 4 — construire le résumé
        if (wizStep === wizTotal) {
            buildSummary();
        }
    }

    // ══════════════════════════════════════════════
    // VALIDATION PAR ÉTAPE
    // ══════════════════════════════════════════════
    function wizValidateStep(step) {
        let ok = true;

        if (step === 1) {
            ok = validate('f-nom',       'err-nom',       v => v.trim().length >= 2) && ok;
            ok = validate('f-prenom',    'err-prenom',    v => v.trim().length >= 2) && ok;
            ok = validate('f-email',     'err-email',     v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim())) && ok;
            ok = validate('f-telephone', 'err-telephone', v => v.trim().length >= 8) && ok;
            ok = validate('f-dob',       'err-dob',       v => v && new Date(v) < new Date()) && ok;

            if (!selectedType) {
                document.getElementById('err-type').classList.remove('hidden');
                ok = false;
            } else {
                document.getElementById('err-type').classList.add('hidden');
            }
        }

        if (step === 2) {
            const anyRoleChecked = document.querySelector('.role-checkbox:checked');
            if (!anyRoleChecked) {
                document.getElementById('err-role').classList.remove('hidden');
                ok = false;
            } else {
                document.getElementById('err-role').classList.add('hidden');
            }
        }

        return ok;
    }

    function validate(fieldId, errId, rule) {
        const field = document.getElementById(fieldId);
        const err   = document.getElementById(errId);
        if (!field) return true;
        const valid = rule(field.value);
        field.classList.toggle('error', !valid);
        if (err) err.classList.toggle('hidden', valid);
        return valid;
    }

    // ══════════════════════════════════════════════
    // SÉLECTION TYPE UTILISATEUR
    // ══════════════════════════════════════════════
    document.querySelectorAll('input[name="typeUtilisateur"]').forEach(radio => {
        radio.addEventListener('change', () => {
            selectedType = radio.value;

            ['fields-ENS', 'fields-AST', 'fields-SUR'].forEach(id => {
                document.getElementById(id)?.classList.add('hidden');
            });
            document.getElementById(`fields-${selectedType}`)?.classList.remove('hidden');

            document.getElementById('err-type').classList.add('hidden');
        });
    });

    // ══════════════════════════════════════════════
    // SÉLECTION RÔLE
    // ══════════════════════════════════════════════
    window.toggleRole = function(card, event) {
        const cb = card.querySelector('.role-checkbox');
        if (!cb) return;

        if (event && event.target === cb) {
            card.classList.toggle('selected', cb.checked);
            updatePermsVisibility();
            document.getElementById('err-role').classList.add('hidden');
            return;
        }

        cb.checked = !cb.checked;
        card.classList.toggle('selected', cb.checked);
        updatePermsVisibility();
        document.getElementById('err-role').classList.add('hidden');
    };

    function updatePermsVisibility() {
        const container = document.getElementById('perms-container');
        const checkedRoles = document.querySelectorAll('.role-checkbox:checked');

        if (checkedRoles.length === 0) {
            container.classList.add('hidden');
            document.querySelectorAll('[id^="perms-group-"]').forEach(g => {
                g.classList.add('hidden');
                g.classList.remove('grid');
            });
            return;
        }

        container.classList.remove('hidden');
        document.querySelectorAll('[id^="perms-group-"]').forEach(g => {
            g.classList.add('hidden');
            g.classList.remove('grid');
        });

        checkedRoles.forEach(cb => {
            const group = document.getElementById(`perms-group-${cb.value}`);
            if (group) {
                group.classList.remove('hidden');
                group.classList.add('grid');
            }
        });
    }

    // ══════════════════════════════════════════════
    // TOGGLE PERMISSION
    // ══════════════════════════════════════════════
    window.togglePermCard = function(label, event) {
        const cb = label.querySelector('input[type="checkbox"]');
        if (!cb) return;

        if (event && event.target === cb) {
            label.classList.toggle('checked', cb.checked);
            return;
        }

        setTimeout(() => {
            label.classList.toggle('checked', cb.checked);
        }, 0);
    };

    // ══════════════════════════════════════════════
    // FILTRE CLASSES
    // ══════════════════════════════════════════════
    window.filterClasses = function(query) {
        const q = query.toLowerCase();
        document.querySelectorAll('.classe-card').forEach(card => {
            const nom = (card.getAttribute('data-nom') || '').toLowerCase();
            card.classList.toggle('hidden', q && !nom.includes(q));
        });
    };

    // SÉLECTION RAPIDE CLASSES
    document.getElementById('btn-select-all-classes')?.addEventListener('click', () => {
        document.querySelectorAll('.classe-card:not(.hidden) input[type="checkbox"]').forEach(cb => cb.checked = true);
    });
    document.getElementById('btn-deselect-all-classes')?.addEventListener('click', () => {
        document.querySelectorAll('.classe-card input[type="checkbox"]').forEach(cb => cb.checked = false);
    });

    // ══════════════════════════════════════════════
    // FILTRE UE
    // ══════════════════════════════════════════════
    window.filterUE = function(query) {
        filterUEGrid(query);
    };

    function filterUEGrid(query = '') {
        const q = (query || '').toLowerCase();
        const emptyMsg = document.getElementById('ue-empty-msg');
        let visibleCount = 0;

        document.querySelectorAll('.ue-card').forEach(card => {
            const nom = (card.getAttribute('data-nom') || '').toLowerCase();
            const code = (card.getAttribute('data-code') || '').toLowerCase();

            const visible = !q || nom.includes(q) || code.includes(q);

            card.classList.toggle('hidden', !visible);
            if (visible) visibleCount++;
        });

        if (emptyMsg) {
            emptyMsg.classList.toggle('hidden', visibleCount > 0);
        }
        updateUECounter();
    }

    function updateUECounter() {
        const count = document.querySelectorAll('.ue-checkbox:checked').length;
        const el = document.getElementById('ue-count');
        if (el) el.textContent = count;
    }

    document.addEventListener('change', function(e) {
        if (e.target.classList.contains('ue-checkbox')) {
            updateUECounter();
        }
    });

    // Sélection rapide UE
    document.getElementById('btn-select-all-ue')?.addEventListener('click', () => {
        document.querySelectorAll('.ue-card:not(.hidden) .ue-checkbox').forEach(cb => cb.checked = true);
        updateUECounter();
    });
    document.getElementById('btn-deselect-all-ue')?.addEventListener('click', () => {
        document.querySelectorAll('.ue-checkbox').forEach(cb => cb.checked = false);
        updateUECounter();
    });

    // ══════════════════════════════════════════════
    // CONSTRUIRE LE RÉSUMÉ (Étape 4)
    // ══════════════════════════════════════════════
    function buildSummary() {
        const nom       = document.getElementById('f-nom')?.value       || '—';
        const prenom    = document.getElementById('f-prenom')?.value    || '—';
        const email     = document.getElementById('f-email')?.value     || '—';
        const telephone = document.getElementById('f-telephone')?.value || '—';
        const dob       = document.getElementById('f-dob')?.value       || '—';
        const typeLabel = { ENS: 'Enseignant', AST: 'Assistant pédagogique', SUR: 'Surveillant' };

        // Rôles sélectionnés
        const rolesChoisis = [];
        document.querySelectorAll('.role-checkbox:checked').forEach(cb => {
            const card = cb.closest('.role-card');
            if (card) {
                rolesChoisis.push(card.getAttribute('data-role-name') || '');
            }
        });

        // Champs spécifiques selon le type
        let specifique = '';
        if (selectedType === 'ENS') {
            const grade = document.getElementById('f-grade')?.value || '—';
            const categ = document.getElementById('f-typeEnseignant')?.value || '—';
            specifique = `
                <div class="confirm-row"><span class="confirm-key">Grade</span><span class="confirm-val">${grade}</span></div>
                <div class="confirm-row"><span class="confirm-key">Catégorie</span><span class="confirm-val">${categ}</span></div>`;
        } else if (selectedType === 'AST') {
            const fonc = document.getElementById('f-fonction')?.value || '—';
            specifique = `
                <div class="confirm-row"><span class="confirm-key">Fonction</span><span class="confirm-val">${fonc}</span></div>`;
        } else if (selectedType === 'SUR') {
            const sect = document.getElementById('f-secteur')?.value     || '—';
            const cont = document.getElementById('f-typeContrat')?.value || '—';
            specifique = `
                <div class="confirm-row"><span class="confirm-key">Secteur</span><span class="confirm-val">${sect}</span></div>
                <div class="confirm-row"><span class="confirm-key">Contrat</span><span class="confirm-val">${cont}</span></div>`;
        }

        // Permissions désactivées
        const permsDesactivees = [];
        document.querySelectorAll('.wiz-perm-check:not(:checked)').forEach(cb => {
            permsDesactivees.push(cb.getAttribute('data-perm-desc') || cb.value);
        });
        const permsHtml = permsDesactivees.length > 0
            ? permsDesactivees.map(p => `<span class="confirm-badge-perm"><i class="bi bi-lock-fill"></i>${p}</span>`).join(' ')
            : '<span style="color:rgba(240,240,242,0.35);font-style:italic;">Toutes les permissions actives</span>';

        // UE sélectionnées (ENS)
        let ueHtml = '';
        if (selectedType === 'ENS') {
            const ues = [];
            document.querySelectorAll('.ue-checkbox:checked').forEach(cb => {
                const card = cb.closest('.ue-card');
                if (card) ues.push(card.querySelector('p.text-xs')?.textContent || '');
            });
            ueHtml = ues.length > 0
                ? ues.map(u => `<span class="text-xs px-2 py-0.5 rounded bg-white/5 border border-white/8">${u}</span>`).join(' ')
                : '<span style="color:rgba(240,240,242,0.35);font-style:italic;">Aucune UE sélectionnée</span>';
        }

        // Classes sélectionnées (AST)
        let classesHtml = '';
        if (selectedType === 'AST') {
            const classesChoisies = [];
            document.querySelectorAll('.classe-card input:checked').forEach(cb => {
                const card = cb.closest('.classe-card');
                if (card) classesChoisies.push(card.querySelector('p')?.textContent || '');
            });
            classesHtml = classesChoisies.length > 0
                ? classesChoisies.map(c => `<span class="text-xs px-2 py-0.5 rounded bg-white/5 border border-white/8">${c}</span>`).join(' ')
                : '<span style="color:rgba(240,240,242,0.35);font-style:italic;">Aucune classe sélectionnée</span>';
        }

        document.getElementById('confirm-summary').innerHTML = `
            <div class="confirm-row"><span class="confirm-key">Nom complet</span><span class="confirm-val">${nom} ${prenom}</span></div>
            <div class="confirm-row"><span class="confirm-key">Email</span><span class="confirm-val" style="color:#38bdf8;">${email}</span></div>
            <div class="confirm-row"><span class="confirm-key">Téléphone</span><span class="confirm-val">${telephone}</span></div>
            <div class="confirm-row"><span class="confirm-key">Naissance</span><span class="confirm-val">${dob}</span></div>
            <div class="confirm-row"><span class="confirm-key">Type</span><span class="confirm-val">${typeLabel[selectedType] || '—'}</span></div>
            ${specifique}
            <div class="confirm-row"><span class="confirm-key">Rôle(s)</span><span class="confirm-val">${rolesChoisis.join(', ') || '—'}</span></div>
            <div class="confirm-row"><span class="confirm-key">Perm. désactivées</span><span class="confirm-val" style="display:flex;flex-wrap:wrap;gap:4px;">${permsHtml}</span></div>
            ${selectedType === 'ENS' ? `<div class="confirm-row"><span class="confirm-key">UE</span><span class="confirm-val" style="display:flex;flex-wrap:wrap;gap:4px;">${ueHtml}</span></div>` : ''}
            ${selectedType === 'AST' ? `<div class="confirm-row"><span class="confirm-key">Classes</span><span class="confirm-val" style="display:flex;flex-wrap:wrap;gap:4px;">${classesHtml}</span></div>` : ''}
            ${selectedType === 'SUR' ? `<div class="confirm-row"><span class="confirm-key">Assignation</span><span class="confirm-val" style="color:rgba(240,240,242,0.35);font-style:italic;">Aucune requise</span></div>` : ''}
        `;

        // Aperçu email
        document.getElementById('prev-prenom').textContent = prenom;
        document.getElementById('prev-email').textContent  = email;

        // Remplir les champs hidden
        setHidden('h-nom',         nom);
        setHidden('h-prenom',      prenom);
        setHidden('h-email',       email);
        setHidden('h-telephone',   telephone);
        setHidden('h-dob',         dob);
        setHidden('h-type',        selectedType || '');
        setHidden('h-grade',       document.getElementById('f-grade')?.value        || '');
        setHidden('h-typeEns',     document.getElementById('f-typeEnseignant')?.value || '');
        setHidden('h-fonction',    document.getElementById('f-fonction')?.value      || '');
        setHidden('h-secteur',     document.getElementById('f-secteur')?.value       || '');
        setHidden('h-typeContrat', document.getElementById('f-typeContrat')?.value   || '');

        // Rôles → champs hidden multiples
        const rolesContainer = document.getElementById('h-roles-container');
        rolesContainer.innerHTML = '';
        document.querySelectorAll('.role-checkbox:checked').forEach(cb => {
            const inp = document.createElement('input');
            inp.type  = 'hidden';
            inp.name  = 'roleIds';
            inp.value = cb.value;
            rolesContainer.appendChild(inp);
        });

        // Permissions désactivées
        const permsContainer = document.getElementById('h-perms-container');
        permsContainer.innerHTML = '';
        document.querySelectorAll('.wiz-perm-check:not(:checked)').forEach(cb => {
            const inp = document.createElement('input');
            inp.type  = 'hidden';
            inp.name  = 'permissionsDesactivees';
            inp.value = cb.value;
            permsContainer.appendChild(inp);
        });

        // UE (ENS)
        const ueContainer = document.getElementById('h-ue-container');
        ueContainer.innerHTML = '';
        document.querySelectorAll('.ue-checkbox:checked').forEach(cb => {
            const inp = document.createElement('input');
            inp.type  = 'hidden';
            inp.name  = 'ueIds';
            inp.value = cb.value;
            ueContainer.appendChild(inp);
        });

        // Classes (AST)
        const classesContainer = document.getElementById('h-classes-container');
        classesContainer.innerHTML = '';
        document.querySelectorAll('.classe-card input:checked').forEach(cb => {
            const inp = document.createElement('input');
            inp.type  = 'hidden';
            inp.name  = 'classesIds';
            inp.value = cb.value;
            classesContainer.appendChild(inp);
        });
    }

    function setHidden(id, val) {
        const el = document.getElementById(id);
        if (el) el.value = val;
    }

    // ══════════════════════════════════════════════
    // SOUMETTRE
    // ══════════════════════════════════════════════
    function wizSubmit() {
        const btn = document.getElementById('btn-submit');
        btn.disabled = true;
        btn.innerHTML = '<i class="bi bi-arrow-repeat spin"></i> Création en cours...';
        document.getElementById('wiz-form').submit();
    }

    // ══════════════════════════════════════════════
    // TOAST
    // ══════════════════════════════════════════════
    function showToast(type, icon, msg) {
        const toast = document.getElementById('toast-el');
        if (!toast) return;
        const toastI   = document.getElementById('toast-i');
        const toastMsg = document.getElementById('toast-msg');
        const colors = { ok: '#4ade80', err: '#f87171', info: '#38bdf8' };

        toastI.className = `bi ${icon} text-base`;
        toastI.style.color = colors[type] || '#f0f0f2';
        toastMsg.textContent = msg;
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3500);
    }

    const params = new URLSearchParams(window.location.search);
    if (params.get('success')) showToast('ok',  'bi-check-circle-fill', decodeURIComponent(params.get('success')));
    if (params.get('error'))   showToast('err', 'bi-x-circle-fill',     decodeURIComponent(params.get('error')));

    // ══════════════════════════════════════════════
    // INIT
    // ══════════════════════════════════════════════
    document.getElementById('btn-next')?.addEventListener('click', wizNext);
    document.getElementById('btn-back')?.addEventListener('click', wizPrev);
    document.getElementById('btn-submit')?.addEventListener('click', wizSubmit);

    wizRender();
});