package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSeance;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.PlageHoraireMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IPlageHoraireService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.SemestreRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireDragDropRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireRecurrenceRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.PlageHoraireRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.ProgrammationUERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.JournalActionService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlageHoraireService implements IPlageHoraireService {

    private final PlageHoraireRepository plageHoraireRepository;
    private final ClassesRepository classesRepository;
    private final SemestreRepository semestreRepository;
    private final ProgrammationUERepository programmationUERepository;
    private final EnseignantRepository enseignantRepository;
    private final PlageHoraireMapper plageHoraireMapper;
    private final IJournalActionService journalActionService;
    private final AnneeAcademiqueService anneeAcademiqueService;

    // ============================================
    // Consultation
    // ============================================

    @Override
    @Transactional(readOnly = true)
    public PlageHoraireResponse findById(Long id) {
        return plageHoraireMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> getByClasse(Long classeId) {
        return plageHoraireRepository.findByClasseId(classeId)
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> getByClasseAndSemestre(
            Long classeId, Long semestreId) {
        return plageHoraireRepository
                .findByClasseIdAndSemestreId(classeId, semestreId)
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> getByClasseAndSemaine(
            Long classeId, LocalDate debut, LocalDate fin) {
        return plageHoraireRepository
                .findByClasseAndSemaine(classeId, debut, fin)
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> getByEnseignant(Long enseignantId) {
        return plageHoraireRepository.findByEnseignantId(enseignantId)
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> getByEnseignantAndSemestre(
            Long enseignantId, Long semestreId) {
        return plageHoraireRepository
                .findByEnseignantIdAndSemestreId(enseignantId, semestreId)
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> getCoursAujourdhui(Long classeId) {
        return plageHoraireRepository
                .findCoursAujourdhui(classeId, LocalDate.now())
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> getCoursEnseignantAujourdhui(
            Long enseignantId) {
        return plageHoraireRepository
                .findCoursEnseignantAujourdhui(enseignantId, LocalDate.now())
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    // ============================================
    // Création simple
    // ============================================

    @Override
    @Transactional
    public PlageHoraireResponse creer(
            PlageHoraireRequest request,
            Utilisateur auteur) {

        // 1. Valider les données
        validerRequest(request.getClasseId(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), null);

        // 2. Récupérer les entités liées
        Classe classe = findClasse(request.getClasseId());
        Semestre semestre = findSemestreActif(classe);
        ProgrammationUE programmationUE = request.getProgrammationUEId() != null
                ? findProgrammationUE(request.getProgrammationUEId())
                : null;

        // 3. Récupérer les enseignants
        Set<Enseignant> enseignants = findEnseignants(
                request.getEnseignantIds(), programmationUE
        );

        // 4. Construire la plage
        PlageHoraire plage = PlageHoraire.builder()
                .jour(request.getJour())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .salle(request.getSalle())
                .couleur(request.getCouleur())
                .titre(request.getTitre())
                .typeSeance(request.getTypeSeance() != null
                        ? request.getTypeSeance() : TypeSeance.CM)
                .classe(classe)
                .semestre(semestre)
                .programmationUE(programmationUE)
                .enseignants(enseignants)
                .build();

        plageHoraireRepository.save(plage);

        // ✅ Journaliser
        journalActionService.journaliserCreationSeance(
                auteur,
                plage.getId(),
                plage.getTitreAffiche() + " — " + plage.getJour()
        );

        log.info("✅ Plage créée : {} le {}",
                plage.getTitreAffiche(), plage.getJour());

        return plageHoraireMapper.toResponse(plage);
    }

    // ============================================
    // Création par récurrence
    // ============================================

    @Override
    @Transactional
    public List<PlageHoraireResponse> creerRecurrence(
            PlageHoraireRecurrenceRequest request,
            Utilisateur auteur) {

        // 1. Récupérer les entités liées
        Classe classe = findClasse(request.getClasseId());
        Semestre semestre = findSemestreActif(classe);
        ProgrammationUE programmationUE = findProgrammationUE(
                request.getProgrammationUEId()
        );
        Set<Enseignant> enseignants = findEnseignants(
                request.getEnseignantId() != null
                        ? List.of(request.getEnseignantId())
                        : null,
                programmationUE
        );

        List<PlageHoraire> plagesCrees = new ArrayList<>();
        LocalDate dateActuelle = request.getDateDebut();

        // 2. Parcourir chaque jour entre dateDebut et dateFin
        while (!dateActuelle.isAfter(request.getDateFin())) {

            final LocalDate jourActuel = dateActuelle;

            // ✅ Vérifier si ce jour est dans les jours sélectionnés
            if (request.getJours().contains(jourActuel.getDayOfWeek())) {

                // ✅ Vérifier les conflits avant de créer
                boolean conflit = verifierConflits(
                        classe.getId(),
                        null,
                        jourActuel,
                        request.getHeureDebut(),
                        request.getHeureFin(),
                        enseignants,
                        null
                );

                if (!conflit) {
                    PlageHoraire plage = PlageHoraire.builder()
                            .jour(jourActuel)
                            .heureDebut(request.getHeureDebut())
                            .heureFin(request.getHeureFin())
                            .salle(request.getSalle())
                            .couleur(request.getCouleur())
                            .typeSeance(TypeSeance.CM)
                            .classe(classe)
                            .semestre(semestre)
                            .programmationUE(programmationUE)
                            .enseignants(enseignants)
                            .build();

                    plagesCrees.add(plage);
                } else {
                    log.warn("⚠️ Conflit détecté le {} — séance ignorée",
                            jourActuel);
                }
            }

            dateActuelle = dateActuelle.plusDays(1);
        }

        // 3. Sauvegarder toutes les plages en une fois
        plageHoraireRepository.saveAll(plagesCrees);

        // ✅ Journaliser
        journalActionService.journaliserCreationSeance(
                auteur,
                null,
                plagesCrees.size() + " séances récurrentes créées"
        );

        log.info("✅ {} séances récurrentes créées", plagesCrees.size());

        return plagesCrees.stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    // ============================================
    // Création par Drag & Drop
    // ============================================

    @Override
    @Transactional
    public PlageHoraireResponse creerParDragDrop(
            PlageHoraireDragDropRequest request,
            Utilisateur auteur) {

        // 1. Valider
        validerRequest(request.getClasseId(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), null);

        // 2. Récupérer les entités
        Classe classe = findClasse(request.getClasseId());
        Semestre semestre = findSemestreActif(classe);
        ProgrammationUE programmationUE = request.getProgrammationUEId() != null
                ? findProgrammationUE(request.getProgrammationUEId())
                : null;
        Set<Enseignant> enseignants = findEnseignants(
                request.getEnseignantId() != null
                        ? List.of(request.getEnseignantId())
                        : null,
                programmationUE
        );

        // 3. Construire
        TypeSeance typeSeance = request.getTypeSeance() != null
                ? TypeSeance.valueOf(request.getTypeSeance()) : TypeSeance.CM;

        PlageHoraire plage = PlageHoraire.builder()
                .jour(request.getJour())
                .jourFin(request.getJourFin())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .salle(request.getSalle())
                .couleur(request.getCouleur())
                .titre(request.getTitre())
                .typeSeance(typeSeance)
                .classe(classe)
                .semestre(semestre)
                .programmationUE(programmationUE)
                .enseignants(enseignants)
                .build();

        plageHoraireRepository.save(plage);

        // ✅ Journaliser
        journalActionService.journaliserCreationSeance(
                auteur,
                plage.getId(),
                "Drag & Drop — " + plage.getTitreAffiche()
                        + " le " + plage.getJour()
        );

        log.info("✅ Plage créée par drag & drop : {}",
                plage.getTitreAffiche());

        return plageHoraireMapper.toResponse(plage);
    }

    // ============================================
    // Modifier
    // ============================================

    @Override
    @Transactional
    public PlageHoraireResponse modifier(
            Long id,
            PlageHoraireRequest request,
            Utilisateur auteur) {

        PlageHoraire plage = findEntityById(id);

        // 1. Valider — exclure la séance en cours
        validerRequest(request.getClasseId(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), id);

        // 2. Mettre à jour
        plage.setJour(request.getJour());
        plage.setHeureDebut(request.getHeureDebut());
        plage.setHeureFin(request.getHeureFin());
        plage.setSalle(request.getSalle());
        plage.setCouleur(request.getCouleur());
        plage.setTitre(request.getTitre());

        if (request.getTypeSeance() != null) {
            plage.setTypeSeance(request.getTypeSeance());
        }

        // 3. Mettre à jour les enseignants si fournis
        if (request.getEnseignantIds() != null
                && !request.getEnseignantIds().isEmpty()) {
            Set<Enseignant> enseignants = findEnseignants(
                    request.getEnseignantIds(), plage.getProgrammationUE()
            );
            plage.setEnseignants(enseignants);
        }

        plageHoraireRepository.save(plage);

        // ✅ Journaliser
        journalActionService.journaliserModificationSeance(
                auteur,
                id,
                plage.getTitreAffiche() + " — " + plage.getJour()
        );

        return plageHoraireMapper.toResponse(plage);
    }

    // ============================================
    // Affecter des enseignants
    // ============================================

    @Override
    @Transactional
    public PlageHoraireResponse affecterEnseignants(
            Long id,
            List<Long> enseignantIds,
            Utilisateur auteur) {

        PlageHoraire plage = findEntityById(id);

        // ✅ Vérifier conflits pour chaque enseignant
        enseignantIds.forEach(enseignantId -> {
            boolean conflit = plageHoraireRepository
                    .existsConflitEnseignantSaufId(
                            enseignantId,
                            plage.getJour(),
                            plage.getHeureDebut(),
                            plage.getHeureFin(),
                            id
                    );
            if (conflit) {
                Enseignant e = enseignantRepository.findById(enseignantId)
                        .orElseThrow();
                throw new IllegalStateException(
                        "Conflit horaire pour l'enseignant : "
                                + e.getNom() + " " + e.getPrenom()
                );
            }
        });

        // Récupérer et affecter
        Set<Enseignant> enseignants = new HashSet<>(
                enseignantRepository.findAllById(enseignantIds)
        );
        plage.setEnseignants(enseignants);
        plageHoraireRepository.save(plage);

        // ✅ Journaliser
        journalActionService.journaliserModificationSeance(
                auteur,
                id,
                "Affectation enseignants — " + plage.getTitreAffiche()
        );

        log.info("✅ Enseignants affectés à la plage {}",
                plage.getTitreAffiche());

        return plageHoraireMapper.toResponse(plage);
    }

    // ============================================
    // Déplacer (Drag & Drop existant)
    // ============================================

    @Override
    @Transactional
    public PlageHoraireResponse deplacer(
            Long id,
            PlageHoraireDragDropRequest request,
            Utilisateur auteur) {

        PlageHoraire plage = findEntityById(id);

        // 1. Valider — exclure la séance en cours
        validerRequest(request.getClasseId(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), id);

        // 2. Mettre à jour uniquement le créneau
        plage.setJour(request.getJour());
        plage.setJourFin(request.getJourFin());
        plage.setHeureDebut(request.getHeureDebut());
        plage.setHeureFin(request.getHeureFin());

        if (request.getSalle() != null) plage.setSalle(request.getSalle());

        plageHoraireRepository.save(plage);

        // ✅ Journaliser
        journalActionService.journaliserModificationSeance(
                auteur,
                id,
                "Déplacement — " + plage.getTitreAffiche()
                        + " vers " + plage.getJour()
        );

        log.info("✅ Plage déplacée : {} → {}",
                plage.getTitreAffiche(), plage.getJour());

        return plageHoraireMapper.toResponse(plage);
    }

    // ============================================
    // Suppression
    // ============================================

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur auteur) {
        PlageHoraire plage = findEntityById(id);
        String titre = plage.getTitreAffiche();

        plageHoraireRepository.delete(plage);

        // ✅ Journaliser
        journalActionService.journaliserSuppressionSeance(
                auteur, id, titre
        );

        log.info("🗑️ Plage supprimée : {}", titre);
    }

    @Override
    @Transactional
    public void supprimerParProgrammationUE(
            Long programmationUEId,
            Utilisateur auteur) {

        plageHoraireRepository.deleteByProgrammationUEId(programmationUEId);

        journalActionService.journaliserSuppressionSeance(
                auteur,
                programmationUEId,
                "Suppression par ProgrammationUE : " + programmationUEId
        );

        log.info("🗑️ Plages supprimées pour ProgrammationUE {}",
                programmationUEId);
    }

    // ============================================
    // Statistiques
    // ============================================

    @Override
    @Transactional(readOnly = true)
    public long getTotalDureeMinutes(Long classeId, Long semestreId) {
        return plageHoraireRepository
                .findCoursByClasseAndSemestre(classeId, semestreId)
                .stream()
                .mapToLong(PlageHoraire::getDureeMinutes)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCours(Long classeId, Long semestreId) {
        return plageHoraireRepository
                .countCoursByClasseAndSemestre(classeId, semestreId);
    }

    // ============================================
    // Méthode interne
    // ============================================

    @Override
    public PlageHoraire findEntityById(Long id) {
        return plageHoraireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plage horaire introuvable : " + id
                ));
    }

    // ============================================
    // Méthodes utilitaires privées
    // ============================================

    private void validerRequest(
            Long classeId,
            LocalDate jour,
            LocalTime heureDebut,
            LocalTime heureFin,
            Long idExclu) {

        // 1. Cohérence des heures
        if (!heureDebut.isBefore(heureFin)) {
            throw new IllegalArgumentException(
                    "L'heure de début doit être avant l'heure de fin"
            );
        }

        // 2. Conflit classe
        boolean conflitClasse = idExclu == null
                ? plageHoraireRepository.existsConflitClasse(
                classeId, jour, heureDebut, heureFin)
                : plageHoraireRepository.existsConflitClasseSaufId(
                classeId, jour, heureDebut, heureFin, idExclu);

        if (conflitClasse) {
            throw new IllegalStateException(
                    "Conflit horaire : la classe a déjà un cours à ce créneau"
            );
        }
    }

    private boolean verifierConflits(
            Long classeId,
            String salle,
            LocalDate jour,
            LocalTime heureDebut,
            LocalTime heureFin,
            Set<Enseignant> enseignants,
            Long idExclu) {

        // Conflit classe
        boolean conflitClasse = idExclu == null
                ? plageHoraireRepository.existsConflitClasse(
                classeId, jour, heureDebut, heureFin)
                : plageHoraireRepository.existsConflitClasseSaufId(
                classeId, jour, heureDebut, heureFin, idExclu);

        if (conflitClasse) return true;

        // Conflit salle
        if (salle != null && !salle.isBlank()) {
            boolean conflitSalle = idExclu == null
                    ? plageHoraireRepository.existsConflitSalle(
                    salle, jour, heureDebut, heureFin)
                    : plageHoraireRepository.existsConflitSalleSaufId(
                    salle, jour, heureDebut, heureFin, idExclu);
            if (conflitSalle) return true;
        }

        // Conflit enseignants
        for (Enseignant enseignant : enseignants) {
            boolean conflitEns = idExclu == null
                    ? plageHoraireRepository.existsConflitEnseignant(
                    enseignant.getId(), jour, heureDebut, heureFin)
                    : plageHoraireRepository.existsConflitEnseignantSaufId(
                    enseignant.getId(), jour, heureDebut, heureFin, idExclu);
            if (conflitEns) return true;
        }

        return false;
    }

    private Classe findClasse(Long classeId) {
        return classesRepository.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classe introuvable : " + classeId
                ));
    }

    private Semestre findSemestreActif(Classe classe) {
        return semestreRepository
                .findByAnneeAcademiqueIdAndActiveTrue(anneeAcademiqueService.getAnneeActive().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun semestre actif trouvé"
                ));
    }

    private ProgrammationUE findProgrammationUE(Long id) {
        return programmationUERepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProgrammationUE introuvable : " + id
                ));
    }

    private Set<Enseignant> findEnseignants(
            List<Long> enseignantIds,
            ProgrammationUE programmationUE) {

        // ✅ Si des enseignants sont fournis → les utiliser
        if (enseignantIds != null && !enseignantIds.isEmpty()) {
            return new HashSet<>(
                    enseignantRepository.findAllById(enseignantIds)
            );
        }

        // ✅ Sinon → utiliser les enseignants de la ProgrammationUE
        if (programmationUE != null
                && !programmationUE.getEnseignants().isEmpty()) {
            return new HashSet<>(programmationUE.getEnseignants());
        }

        return new HashSet<>();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PlageHoraireResponse> getByEnseignantAndSemaine(
            Long enseignantId, LocalDate debut, LocalDate fin) {
        return plageHoraireRepository
                .findByEnseignantAndJourBetween(enseignantId, debut, fin)
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }


    /**
     * Récupère la liste des cours (hors pauses/évènements)
     * d'un enseignant pour une date donnée.
     */
    public List<PlageHoraire> findCoursEnseignantAujourdhui(Long ensId, LocalDate date) {
        return plageHoraireRepository.findCoursEnseignantAujourdhui(ensId, date);
    }
}