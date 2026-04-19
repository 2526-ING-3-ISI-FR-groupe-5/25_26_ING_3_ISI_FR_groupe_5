package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.ProgrammationUERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.InterfaceProgrammeUE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgrammationUEService implements InterfaceProgrammeUE {

    private final ProgrammationUERepository programmationRepo;
    private final UEService ueService;
    private final SemestreService semestreService;
    private final ClassesService classesService;
    private final EnseignantRepository enseignantRepo;
    private final AnneeAcademiqueService anneeService;

    @Transactional
    @Override
    public ProgrammationUE programmer(
            Long ueId,
            Long semestreId,
            Long classeId,
            Long dheure,
            Long nbrCredit,
            Set<Long> enseignantIds
    ) {
        UE ue = ueService.findById(ueId);
        Semestre semestre = semestreService.findById(semestreId);
        Classe classe = classesService.findById(classeId);

        if (programmationRepo.existsByUeIdAndClasseIdAndSemestreId(ueId, classeId, semestreId)) {
            throw new RuntimeException("Cette UE est déjà programmée pour cette classe et ce semestre");
        }

        Set<Enseignant> enseignants = enseignantIds.stream()
                .map(ensId -> enseignantRepo.findById(ensId)
                        .orElseThrow(() -> new RuntimeException("Enseignant introuvable : " + ensId)))
                .collect(Collectors.toSet());

        ProgrammationUE programmation = new ProgrammationUE();
        programmation.setUe(ue);
        programmation.setSemestre(semestre);
        programmation.setClasse(classe);
        programmation.setDheure(dheure);
        programmation.setNbrCredit(nbrCredit);
        programmation.setEnseignants(enseignants);
        programmation.setLibelle(ue.getLibelle());

        return programmationRepo.save(programmation);
    }

    public List<ProgrammationUE> getByAnnee(Long anneeId) {
        return programmationRepo.findBySemestre_AnneeAcademique_Id(anneeId);
    }

    @Transactional
    @Override
    public ProgrammationUE modifier(
            Long id,
            Long dheure,
            Long nbrCredit,
            Set<Long> enseignantIds,
            String libelle,
            String libelleAnglais
    ) {
        ProgrammationUE programmation = findById(id);

        Annee_academique anneeActive = anneeService.getAnneeActive();
        Annee_academique anneeProgram = programmation.getSemestre().getAnneeAcademique();

        if (!anneeProgram.getId().equals(anneeActive.getId())) {
            throw new RuntimeException("Impossible de modifier une programmation d'une année passée");
        }

        programmation.setDheure(dheure);
        programmation.setNbrCredit(nbrCredit);

        if (libelle != null && !libelle.trim().isEmpty()) {
            programmation.setLibelle(libelle);
        }
        if (libelleAnglais != null && !libelleAnglais.trim().isEmpty()) {
            programmation.setLibelleAnglais(libelleAnglais);
        }

        Set<Enseignant> enseignants = enseignantIds.stream()
                .map(ensId -> enseignantRepo.findById(ensId)
                        .orElseThrow(() -> new RuntimeException("Enseignant introuvable : " + ensId)))
                .collect(Collectors.toSet());

        programmation.setEnseignants(enseignants);

        return programmationRepo.save(programmation);
    }

    @Transactional
    @Override
    public List<ProgrammationUE> getByClasseAndAnnee(Long classeId, Long anneeId) {
        return programmationRepo.findByClasseAndAnnee(classeId, anneeId);
    }

    @Transactional
    @Override
    public List<ProgrammationUE> getByEnseignantAndAnnee(Long enseignantId, Long anneeId) {
        return programmationRepo.findByEnseignantAndAnnee(enseignantId, anneeId);
    }

    public ProgrammationUE findById(Long id) {
        return programmationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Programmation introuvable"));
    }

    @Transactional
    @Override
    public void supprimer(Long id) {
        ProgrammationUE programmation = findById(id);

        Annee_academique anneeActive = anneeService.getAnneeActive();
        Annee_academique anneeProgram = programmation.getSemestre().getAnneeAcademique();

        if (!anneeProgram.getId().equals(anneeActive.getId())) {
            throw new RuntimeException("Impossible de supprimer une programmation d'une année passée");
        }

        programmationRepo.delete(programmation);
    }

    @Transactional
    @Override
    public void dupliquerVersNouvelleAnnee(Long ancienneAnneeId, Long nouvelleAnneeId) {

        List<Semestre> nouveauxSemestres = semestreService.getByAnnee(nouvelleAnneeId);

        if (nouveauxSemestres.isEmpty()) {
            throw new RuntimeException("Veuillez d'abord créer les semestres de la nouvelle année");
        }

        List<Semestre> anciensSemestres = semestreService.getByAnnee(ancienneAnneeId);

        for (Semestre ancienSemestre : anciensSemestres) {

            Semestre nouveauSemestre = nouveauxSemestres.stream()
                    .filter(s -> s.getTypeSemestre().equals(ancienSemestre.getTypeSemestre()))
                    .findFirst()
                    .orElse(null);

            if (nouveauSemestre == null) continue;

            List<ProgrammationUE> anciennes = programmationRepo.findBySemestreId(ancienSemestre.getId());

            for (ProgrammationUE ancienne : anciennes) {
                if (programmationRepo.existsByUeIdAndClasseIdAndSemestreId(
                        ancienne.getUe().getId(),
                        ancienne.getClasse().getId(),
                        nouveauSemestre.getId())) {
                    continue;
                }

                ProgrammationUE nouvelle = new ProgrammationUE();
                nouvelle.setUe(ancienne.getUe());
                nouvelle.setSemestre(nouveauSemestre);
                nouvelle.setClasse(ancienne.getClasse());
                nouvelle.setDheure(ancienne.getDheure());
                nouvelle.setNbrCredit(ancienne.getNbrCredit());
                nouvelle.setEnseignants(ancienne.getEnseignants());
                nouvelle.setLibelle(ancienne.getLibelle());
                nouvelle.setLibelleAnglais(ancienne.getLibelleAnglais());

                programmationRepo.save(nouvelle);
            }
        }
    }
@Override
public List<ProgrammationUE> getProgrammationsByEnseignant(Long enseignantId) {
        return programmationRepo.findByEnseignantId(enseignantId);
    }
@Transactional
@Override
public List<Classe> getClassesByEnseignant(Long enseignantId) {
        return programmationRepo.findClassesByEnseignantId(enseignantId);
    }
}