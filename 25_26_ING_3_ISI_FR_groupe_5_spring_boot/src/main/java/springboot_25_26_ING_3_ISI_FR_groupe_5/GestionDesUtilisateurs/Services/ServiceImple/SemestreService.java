package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.SemestreRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SemestreService {

    private final SemestreRepository semestreRepository;
    private final AnneeAcademiqueService anneeService;

    @Transactional
    public Semestre creer(TypeSemestre typeSemestre, Long anneeAcademiqueId,
                          LocalDate dateDebut, LocalDate dateFin, boolean actif) {

        Annee_academique annee = anneeService.findById(anneeAcademiqueId);

        // Vérifier si le semestre existe déjà
        if (semestreRepository.existsByAnneeAcademiqueIdAndTypeSemestre(anneeAcademiqueId, typeSemestre)) {
            throw new DuplicateResourceException("Semestre", typeSemestre.getLibelle() + " pour l'année " + annee.getNom());
        }

        // Contrainte : Les dates du semestre doivent être dans l'année académique
        if (dateDebut.isBefore(annee.getDateDebut()) || dateFin.isAfter(annee.getDateFin())) {
            throw new RuntimeException("Les dates du semestre doivent être comprises dans l'année académique");
        }

        // Contrainte : dateDebut < dateFin
        if (!dateDebut.isBefore(dateFin)) {
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        Semestre semestre = new Semestre();
        semestre.setTypeSemestre(typeSemestre);
        semestre.setAnneeAcademique(annee);
        semestre.setDateDebut(dateDebut);
        semestre.setDateFin(dateFin);
        semestre.setActif(actif);

        return semestreRepository.save(semestre);
    }

    @Transactional
    public Semestre activerSemestre(Long id) {
        Semestre semestre = findById(id);

        // Désactiver tous les semestres de la même année
        List<Semestre> semestres = semestreRepository.findByAnneeAcademiqueId(semestre.getAnneeAcademique().getId());
        semestres.forEach(s -> s.setActif(false));

        // Activer le semestre choisi
        semestre.setActif(true);

        return semestreRepository.save(semestre);
    }

    @Transactional
    public Semestre desactiverSemestre(Long id) {
        Semestre semestre = findById(id);
        semestre.setActif(false);
        return semestreRepository.save(semestre);
    }

    public Semestre findById(Long id) {
        return semestreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semestre"));
    }

    public List<Semestre> getByAnnee(Long anneeId) {
        return semestreRepository.findByAnneeAcademiqueId(anneeId);
    }

    public Semestre getSemestreActif(Long anneeId) {
        return semestreRepository.findByAnneeAcademiqueIdAndActifTrue(anneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Semestre actif pour l'année " + anneeId));
    }

    public Semestre getByAnneeAndType(Long anneeId, TypeSemestre type) {
        return semestreRepository.findByAnneeAcademiqueIdAndTypeSemestre(anneeId, type)
                .orElseThrow(() -> new ResourceNotFoundException("Semestre " + type.getLibelle() + " pour l'année"));
    }

    @Transactional
    public void supprimer(Long id) {
        Semestre semestre = findById(id);
        semestreRepository.delete(semestre);
    }
}