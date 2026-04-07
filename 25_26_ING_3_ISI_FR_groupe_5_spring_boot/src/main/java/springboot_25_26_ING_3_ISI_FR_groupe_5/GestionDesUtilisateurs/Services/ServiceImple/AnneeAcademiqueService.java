package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ANNEACADEMIQUEACTIVER;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ANNEACADEMIQUENOTFOUND;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ANNEEACDEMIQUEEXISTEXCEPTION;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.IMPOSSIBLLEDESUPRIMERANNEEACADEMIQU;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.AnneeAcademiqueRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.SemestreRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeRepo;
    private final SemestreRepository semestreRepo;

    @Transactional
    public Annee_academique creer(String nom, LocalDate dateDebut, LocalDate dateFin, boolean active) {

        if (anneeRepo.existsByNom(nom)) {
            throw new ANNEEACDEMIQUEEXISTEXCEPTION("L'année académique " + nom + " existe déjà");
        }

        // ✅ Vérifier que dateDebut < dateFin
        if (dateDebut.isAfter(dateFin)) {
            throw new ANNEEACDEMIQUEEXISTEXCEPTION("La date de début doit être antérieure à la date de fin");
        }

        // Vérifier chevauchement
        boolean overlap = anneeRepo.findAll().stream().anyMatch(annee ->
                (dateDebut.isBefore(annee.getDateFin()) && dateFin.isAfter(annee.getDateDebut()))
        );

        if (overlap) {
            throw new ANNEEACDEMIQUEEXISTEXCEPTION("Les dates chevauchent une année académique existante");
        }

        Annee_academique annee = new Annee_academique();
        annee.setNom(nom);
        annee.setDateDebut(dateDebut);
        annee.setDateFin(dateFin);
        annee.setActive(active);

        return anneeRepo.save(annee);
    }

    @Transactional
    public Annee_academique modifier(Long id, String nom, LocalDate dateDebut, LocalDate dateFin, boolean active) {
        Annee_academique annee = findById(id);

        // ✅ Vérifier que dateDebut < dateFin
        if (dateDebut.isAfter(dateFin)) {
            throw new ANNEEACDEMIQUEEXISTEXCEPTION("La date de début doit être antérieure à la date de fin");
        }

        // Vérifier chevauchement (sauf elle-même)
        boolean overlap = anneeRepo.findAll().stream()
                .filter(a -> !a.getId().equals(id))
                .anyMatch(a -> (dateDebut.isBefore(a.getDateFin()) && dateFin.isAfter(a.getDateDebut())));

        if (overlap) {
            throw new ANNEEACDEMIQUEEXISTEXCEPTION("Les dates chevauchent une année académique existante");
        }

        annee.setNom(nom);
        annee.setDateDebut(dateDebut);
        annee.setDateFin(dateFin);
        annee.setActive(active);

        return anneeRepo.save(annee);
    }

    @Transactional
    public Annee_academique activer(Long anneeId) {
        Annee_academique nouvelleAnnee = findById(anneeId);

        anneeRepo.findByActiveTrue().ifPresent(ancienne -> {
            // Désactiver le semestre actif de l'ancienne année
            semestreRepo.findByAnneeAcademiqueIdAndActifTrue(ancienne.getId())
                    .ifPresent(semestre -> {
                        semestre.setActif(false);
                        semestreRepo.save(semestre);
                    });
            // Désactiver l'ancienne année
            ancienne.setActive(false);
            anneeRepo.save(ancienne);
        });

        nouvelleAnnee.setActive(true);
        return anneeRepo.save(nouvelleAnnee);
    }

    public Annee_academique getAnneeActive() {
        return anneeRepo.findByActiveTrue()
                .orElseThrow(() -> new ANNEACADEMIQUEACTIVER("Aucune année académique active"));
    }

    public List<Annee_academique> getAll() {
        return anneeRepo.findAllByOrderByNomDesc();
    }

    public Annee_academique findById(Long id) {
        return anneeRepo.findById(id)
                .orElseThrow(() -> new ANNEACADEMIQUENOTFOUND("Année académique introuvable"));
    }

    @Transactional
    public void supprimer(Long anneeId) {
        Annee_academique annee = findById(anneeId);
        if (annee.isActive()) {
            throw new IMPOSSIBLLEDESUPRIMERANNEEACADEMIQU("Impossible de supprimer l'année en cours");
        }
        anneeRepo.delete(annee);
    }
}