package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Evenement;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EvenementRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.PlageHoraireRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlageHoraireService {

    private final PlageHoraireRepository plageRepo;
    private final ClassesService classesService;
    private final ProgrammationUEService programmationService;
    private final EvenementRepository evenementRepo;
    private final AnneeAcademiqueService anneeService;

    // ══════════════════════════════════════════
    // CRÉER une séance
    // ══════════════════════════════════════════
    @Transactional
    public PlageHoraire creer(
            Long classeId,
            Long programmationId,
            LocalDate jour,
            LocalTime heureDebut,
            LocalTime heureFin,
            String salle,
            String couleur
    ) {
        Classe classe = classesService.findById(classeId);
        ProgrammationUE programmation = programmationService.findById(programmationId);

        // Vérifier que le jour n'est pas un événement férié
        verifierConflitEvenement(programmation.getSemestre().getAnneeAcademique().getId(), jour);

        // Vérifier que l'heure de début est avant l'heure de fin
        if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
            throw new RuntimeException("L'heure de début doit être antérieure à l'heure de fin");
        }

        // Vérifier conflit enseignant
        if (programmation.getEnseignants() != null) {
            programmation.getEnseignants().forEach(enseignant -> {
                if (plageRepo.existsConflitEnseignant(
                        enseignant.getId(), jour, heureDebut, heureFin)) {
                    throw new RuntimeException(
                            "Conflit d'horaire pour l'enseignant : "
                                    + enseignant.getNom() + " " + enseignant.getPrenom()
                    );
                }
            });
        }

        // Vérifier conflit classe
        if (plageRepo.existsConflitClasse(classeId, jour, heureDebut, heureFin)) {
            throw new RuntimeException(
                    "Conflit d'horaire pour la classe : " + classe.getNom()
            );
        }

        // Vérifier conflit salle
        if (salle != null && !salle.isEmpty()) {
            if (plageRepo.existsConflitSalle(salle, jour, heureDebut, heureFin)) {
                throw new RuntimeException(
                        "Conflit d'horaire pour la salle : " + salle
                );
            }
        }

        PlageHoraire plage = PlageHoraire.builder()
                .classe(classe)
                .programmationUE(programmation)
                .semestre(programmation.getSemestre())  // ✅ Ajout du semestre
                .jour(jour)
                .heureDebut(heureDebut)
                .heureFin(heureFin)
                .salle(salle)
                .couleur(couleur != null ? couleur : "#3b82f6")
                .build();

        return plageRepo.save(plage);
    }

    // ══════════════════════════════════════════
    // MODIFIER une séance
    // ══════════════════════════════════════════
    @Transactional
    public PlageHoraire modifier(
            Long id,
            LocalDate jour,
            LocalTime heureDebut,
            LocalTime heureFin,
            String salle,
            String couleur
    ) {
        PlageHoraire plage = findById(id);

        // Vérifier conflit événement
        verifierConflitEvenement(
                plage.getProgrammationUE().getSemestre().getAnneeAcademique().getId(),
                jour
        );

        // Vérifier que l'heure de début est avant l'heure de fin
        if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
            throw new RuntimeException("L'heure de début doit être antérieure à l'heure de fin");
        }

        // Vérifier conflit enseignant (sauf cette séance)
        if (plage.getProgrammationUE().getEnseignants() != null) {
            plage.getProgrammationUE().getEnseignants().forEach(enseignant -> {
                if (plageRepo.existsConflitEnseignantSaufId(
                        enseignant.getId(), jour, heureDebut, heureFin, id)) {
                    throw new RuntimeException(
                            "Conflit d'horaire pour l'enseignant : "
                                    + enseignant.getNom() + " " + enseignant.getPrenom()
                    );
                }
            });
        }

        // Vérifier conflit classe (sauf cette séance)
        if (plageRepo.existsConflitClasseSaufId(
                plage.getClasse().getId(), jour, heureDebut, heureFin, id)) {
            throw new RuntimeException(
                    "Conflit d'horaire pour la classe : " + plage.getClasse().getNom()
            );
        }

        // Vérifier conflit salle (sauf cette séance)
        if (salle != null && !salle.isEmpty()) {
            if (plageRepo.existsConflitSalleSaufId(salle, jour, heureDebut, heureFin, id)) {
                throw new RuntimeException(
                        "Conflit d'horaire pour la salle : " + salle
                );
            }
        }

        plage.setJour(jour);
        plage.setHeureDebut(heureDebut);
        plage.setHeureFin(heureFin);
        plage.setSalle(salle);
        if (couleur != null) {
            plage.setCouleur(couleur);
        }

        return plageRepo.save(plage);
    }

    // ══════════════════════════════════════════
    // RÉCUPÉRER le planning d'une classe
    // ══════════════════════════════════════════
    public List<PlageHoraire> getPlanningClasse(Long classeId) {
        return plageRepo.findByClasseId(classeId);
    }

    public List<PlageHoraire> getPlanningClasseSemaine(
            Long classeId, LocalDate debut, LocalDate fin) {
        return plageRepo.findByClasseIdAndJourBetween(classeId, debut, fin);
    }

    // ══════════════════════════════════════════
    // RÉCUPÉRER le planning d'un enseignant
    // ══════════════════════════════════════════
    public List<PlageHoraire> getPlanningEnseignant(Long enseignantId) {
        return plageRepo.findByProgrammationUEEnseignantsId(enseignantId);
    }

    public List<PlageHoraire> getPlanningEnseignantSemaine(
            Long enseignantId, LocalDate debut, LocalDate fin) {
        return plageRepo.findByEnseignantAndJourBetween(enseignantId, debut, fin);
    }

    // ══════════════════════════════════════════
    // RÉCUPÉRER le planning d'une UE
    // ══════════════════════════════════════════
    public List<PlageHoraire> getPlanningByUe(Long ueId) {
        return plageRepo.findByProgrammationUEUeId(ueId);
    }

    // ══════════════════════════════════════════
    // RÉCUPÉRER le planning d'un semestre
    // ══════════════════════════════════════════
    public List<PlageHoraire> getPlanningBySemestre(Long semestreId) {
        return plageRepo.findByProgrammationUESemestreId(semestreId);
    }

    // ══════════════════════════════════════════
    // RÉCUPÉRER le planning d'une année
    // ══════════════════════════════════════════
    public List<PlageHoraire> getPlanningByAnnee(Long anneeId) {
        return plageRepo.findByAnnee(anneeId);
    }

    // ══════════════════════════════════════════
    // RÉCUPÉRER par ID
    // ══════════════════════════════════════════
    public PlageHoraire findById(Long id) {
        return plageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plage horaire introuvable"));
    }

    // ══════════════════════════════════════════
    // SUPPRIMER une séance
    // ══════════════════════════════════════════
    @Transactional
    public void supprimer(Long id) {
        PlageHoraire plage = findById(id);
        plageRepo.delete(plage);
    }

    // ══════════════════════════════════════════
    // SUPPRIMER toutes les séances d'une programmation
    // ══════════════════════════════════════════
    @Transactional
    public void supprimerParProgrammation(Long programmationUEId) {
        plageRepo.deleteByProgrammationUEId(programmationUEId);
    }

    // ══════════════════════════════════════════
    // VÉRIFIER qu'il n'y a pas d'événement ce jour
    // ══════════════════════════════════════════
    private void verifierConflitEvenement(Long anneeId, LocalDate jour) {
        List<Evenement> evenements = evenementRepo
                .findEvenementsActifsALaDate(anneeId, jour);
        if (!evenements.isEmpty()) {
            throw new RuntimeException(
                    "Impossible de planifier une séance le " + jour
                            + " : " + evenements.get(0).getNom()
            );
        }
    }
}