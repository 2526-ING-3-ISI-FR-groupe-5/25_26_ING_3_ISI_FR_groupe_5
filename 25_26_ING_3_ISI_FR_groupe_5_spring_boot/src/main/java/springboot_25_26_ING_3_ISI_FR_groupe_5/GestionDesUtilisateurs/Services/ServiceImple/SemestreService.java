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

    private static final int MAX_SEMESTRES_PAR_ANNEE = 2;

    @Transactional
    public Semestre creer(TypeSemestre typeSemestre, Long anneeAcademiqueId,
                          LocalDate dateDebut, LocalDate dateFin, boolean actif) {

        Annee_academique annee = anneeService.findById(anneeAcademiqueId);

        // 1. Vérifier le nombre maximum de semestres (2 max par année)
        long nombreSemestresActuels = semestreRepository.countByAnneeAcademiqueId(anneeAcademiqueId);
        if (nombreSemestresActuels >= MAX_SEMESTRES_PAR_ANNEE) {
            throw new RuntimeException("Une année académique ne peut avoir que " + MAX_SEMESTRES_PAR_ANNEE + " semestres maximum. " +
                    "L'année " + annee.getNom() + " a déjà " + nombreSemestresActuels + " semestre(s).");
        }

        // 2. Vérifier si le semestre existe déjà
        if (semestreRepository.existsByAnneeAcademiqueIdAndTypeSemestre(anneeAcademiqueId, typeSemestre)) {
            throw new DuplicateResourceException("Semestre", typeSemestre.getLibelle() + " pour l'année " + annee.getNom());
        }

        // 3. Contrainte : Les dates du semestre doivent être dans l'année académique
        if (dateDebut.isBefore(annee.getDateDebut()) || dateFin.isAfter(annee.getDateFin())) {
            throw new RuntimeException("Les dates du semestre doivent être comprises dans l'année académique. " +
                    "L'année va du " + annee.getDateDebut() + " au " + annee.getDateFin());
        }

        // 4. Contrainte : dateDebut < dateFin
        if (!dateDebut.isBefore(dateFin)) {
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        // 5. Vérifier que les dates ne chevauchent pas un autre semestre
        List<Semestre> semestresExistants = semestreRepository.findByAnneeAcademiqueId(anneeAcademiqueId);
        for (Semestre existing : semestresExistants) {
            if (dateDebut.isBefore(existing.getDateFin()) && dateFin.isAfter(existing.getDateDebut())) {
                throw new RuntimeException("Les dates du semestre chevauchent celles du " + existing.getTypeSemestre().getLibelle() +
                        " (" + existing.getDateDebut() + " au " + existing.getDateFin() + ")");
            }
        }

        Semestre semestre = new Semestre();
        semestre.setTypeSemestre(typeSemestre);
        semestre.setAnneeAcademique(annee);
        semestre.setDateDebut(dateDebut);
        semestre.setDateFin(dateFin);
        semestre.setActif(actif);

        // Si c'est le premier semestre et qu'on veut l'activer, on l'active
        if (actif && nombreSemestresActuels == 0) {
            semestre.setActif(true);
        } else if (actif && nombreSemestresActuels > 0) {
            // Désactiver les autres si on active celui-ci
            List<Semestre> autresSemestres = semestreRepository.findByAnneeAcademiqueId(anneeAcademiqueId);
            autresSemestres.forEach(s -> s.setActif(false));
        }

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

    public long countByAnnee(Long anneeId) {
        return semestreRepository.countByAnneeAcademiqueId(anneeId);
    }

    public boolean isMaxSemestresReached(Long anneeId) {
        return countByAnnee(anneeId) >= MAX_SEMESTRES_PAR_ANNEE;
    }

    @Transactional
    public void supprimer(Long id) {
        Semestre semestre = findById(id);
        semestreRepository.delete(semestre);
    }
}