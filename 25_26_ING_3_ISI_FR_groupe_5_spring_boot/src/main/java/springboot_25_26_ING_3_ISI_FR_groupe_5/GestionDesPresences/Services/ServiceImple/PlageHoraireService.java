package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSeance;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.PlageHoraireMapper;
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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IPlageHoraireService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> findByEnseignantId(Long enseignantId) {
        return plageHoraireRepository.findByEnseignantId(enseignantId)
                .stream()
                .map(plageHoraireMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlageHoraireResponse> findByEnseignantAndJour(Long enseignantId, LocalDate jour) {
        return findCoursEnseignantAujourdhui(enseignantId, jour)
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

        Classe classe = findClasse(request.getClasseId());
        Semestre semestre = findSemestreActif();
        ProgrammationUE programmationUE = request.getProgrammationUEId() != null
                ? findProgrammationUE(request.getProgrammationUEId())
                : null;

        // Récupérer et valider les enseignants
        Set<Enseignant> enseignants = findEnseignants(
                request.getEnseignantIds(), programmationUE
        );

        // 1. Valider de façon exhaustive l'absence de conflits (Classe, Enseignants, Salles) [3]
        validerCreationConflits(classe.getId(), request.getSalle(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), enseignants, null);

        // 2. Construire la plage
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

        Classe classe = findClasse(request.getClasseId());
        Semestre semestre = findSemestreActif();
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

        // 1. Parcourir chaque jour entre dateDebut et dateFin
        while (!dateActuelle.isAfter(request.getDateFin())) {

            final LocalDate jourActuel = dateActuelle;

            // ✅ Vérifier si ce jour est dans les jours de la semaine sélectionnés
            if (request.getJours().contains(jourActuel.getDayOfWeek())) {

                // ✅ Vérifier les conflits de classe, d'enseignants ET de salle (Corrigé) [2]
                boolean conflit = verifierConflits(
                        classe.getId(),
                        request.getSalle(), // <--- CORRIGÉ (Saisie de la salle prise en compte)
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
                    log.warn("⚠️ Conflit détecté le {} — séance récurrente ignorée",
                            jourActuel);
                }
            }

            dateActuelle = dateActuelle.plusDays(1);
        }

        // 2. Sauvegarder toutes les plages en une fois
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

        Classe classe = findClasse(request.getClasseId());
        Semestre semestre = findSemestreActif();
        ProgrammationUE programmationUE = request.getProgrammationUEId() != null
                ? findProgrammationUE(request.getProgrammationUEId())
                : null;
        Set<Enseignant> enseignants = findEnseignants(
                request.getEnseignantId() != null
                        ? List.of(request.getEnseignantId())
                        : null,
                programmationUE
        );

        // 1. Valider de façon exhaustive (Classe, Enseignants, Salles) [3]
        validerCreationConflits(classe.getId(), request.getSalle(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), enseignants, null);

        // 2. Construire la plage
        TypeSeance typeSeance = TypeSeance.CM;
        if (request.getTypeSeance() != null) {
            try {
                typeSeance = TypeSeance.valueOf(request.getTypeSeance());
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Type de séance invalide : {} — Utilisation du type CM par défaut", request.getTypeSeance());
            }
        }

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
        Classe classe = findClasse(request.getClasseId());

        // 1. Récupérer et évaluer les enseignants à mettre à jour
        Set<Enseignant> enseignants = plage.getEnseignants();
        if (request.getEnseignantIds() != null && !request.getEnseignantIds().isEmpty()) {
            enseignants = findEnseignants(request.getEnseignantIds(), plage.getProgrammationUE());
        }

        // 2. Valider de façon exhaustive (Classe, Enseignants, Salles) en excluant le créneau en cours [3]
        validerCreationConflits(classe.getId(), request.getSalle(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), enseignants, id);

        // 3. Mettre à jour l'entité
        plage.setClasse(classe);
        plage.setJour(request.getJour());
        plage.setHeureDebut(request.getHeureDebut());
        plage.setHeureFin(request.getHeureFin());
        plage.setSalle(request.getSalle());
        plage.setCouleur(request.getCouleur());
        plage.setTitre(request.getTitre());
        plage.setEnseignants(enseignants);

        if (request.getTypeSeance() != null) {
            plage.setTypeSeance(request.getTypeSeance());
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
        Classe classe = findClasse(request.getClasseId());

        // 1. Valider de façon exhaustive en excluant l'ID actuel pour le Drag & Drop [3]
        validerCreationConflits(classe.getId(), request.getSalle(), request.getJour(),
                request.getHeureDebut(), request.getHeureFin(), plage.getEnseignants(), id);

        // 2. Mettre à jour uniquement le créneau déplacé
        plage.setClasse(classe);
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
    // Méthodes utilitaires privées de validation [3]
    // ============================================

    /**
     * Valide de façon stricte la cohérence horaire et l'absence de chevauchements
     * de créneaux (pour la classe, l'enseignant ou la salle).
     */
    private void validerCreationConflits(
            Long classeId,
            String salle,
            LocalDate jour,
            LocalTime heureDebut,
            LocalTime heureFin,
            Set<Enseignant> enseignants,
            Long idExclu) {

        // 1. Cohérence temporelle
        if (!heureDebut.isBefore(heureFin)) {
            throw new IllegalArgumentException(
                    "L'heure de début doit être antérieure à l'heure de fin."
            );
        }

        // 2. Vérification de l'ensemble des conflits potentiels
        boolean conflitDetecte = verifierConflits(classeId, salle, jour, heureDebut, heureFin, enseignants, idExclu);

        if (conflitDetecte) {
            throw new IllegalStateException(
                    "Conflit de planification détecté : ce créneau chevauche un cours existant pour cette classe, cet enseignant ou cette salle."
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
        if (enseignants != null) {
            for (Enseignant enseignant : enseignants) {
                boolean conflitEns = idExclu == null
                        ? plageHoraireRepository.existsConflitEnseignant(
                        enseignant.getId(), jour, heureDebut, heureFin)
                        : plageHoraireRepository.existsConflitEnseignantSaufId(
                        enseignant.getId(), jour, heureDebut, heureFin, idExclu);
                if (conflitEns) return true;
            }
        }

        return false;
    }

    private Classe findClasse(Long classeId) {
        return classesRepository.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classe introuvable : " + classeId
                ));
    }

    private Semestre findSemestreActif() { // <--- Nettoyé (Paramètre inutilisé Classe retiré) [4]
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

        // Si des enseignants spécifiques sont fournis -> les utiliser
        if (enseignantIds != null && !enseignantIds.isEmpty()) {
            return new HashSet<>(
                    enseignantRepository.findAllById(enseignantIds)
            );
        }

        // Sinon -> utiliser les enseignants de la ProgrammationUE
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