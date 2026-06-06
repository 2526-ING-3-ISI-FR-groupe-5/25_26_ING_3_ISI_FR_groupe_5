package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.InstitutContexteActif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutContexteActifRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.ProgrammationUERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IProgrammationUEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.SemestreService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.UEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgrammationUEService implements IProgrammationUEService {

    private final ProgrammationUERepository programmationRepo;
    private final UEService ueService;
    private final SemestreService semestreService;
    private final ClassesService classesService;
    private final EnseignantRepository enseignantRepo;
    private final AnneeAcademiqueService anneeService;
    private final InstitutContexteActifRepository contexteRepo;
    private final InstitutSecurityService securityService;

    // ═══════════════════════════════════════════════════════════
    // CRÉATION
    // ═══════════════════════════════════════════════════════════

    @Transactional
    @Override
    public ProgrammationUE programmer(
            Long ueId, Long semestreId, Long classeId,
            Long dheure, Long nbrCredit, Set<Long> enseignantIds) {

        UE ue = ueService.findById(ueId);
        Semestre semestre = semestreService.findById(semestreId);
        Classe classe = classesService.findById(classeId);

        Long institutId = getInstitutIdFromClasse(classe);
        if (!securityService.canManageInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut");
        }

        if (programmationRepo.existsByUeIdAndClasseIdAndSemestreId(ueId, classeId, semestreId)) {
            throw new DuplicateResourceException("ProgrammationUE", "UE " + ueId + " Classe " + classeId + " Semestre " + semestreId);
        }

        Set<Enseignant> enseignants = resolveEnseignants(enseignantIds, institutId);

        ProgrammationUE programmation = ProgrammationUE.builder()
                .ue(ue).semestre(semestre).classe(classe)
                .dheure(dheure).nbrCredit(nbrCredit).enseignants(enseignants)
                .libelle(ue.getLibelle()).libelleAnglais(ue.getLibelleAnglais())
                .build();

        log.info("Programmation créée : UE={} Classe={} Semestre={}",
                ue.getNom(), classe.getNom(), semestre.getTypeSemestre());
        return programmationRepo.save(programmation);
    }

    // ═══════════════════════════════════════════════════════════
    // MODIFICATION
    // ═══════════════════════════════════════════════════════════

    @Transactional
    @Override
    public ProgrammationUE modifier(
            Long id, Long dheure, Long nbrCredit,
            Set<Long> enseignantIds, String libelle, String libelleAnglais) {

        ProgrammationUE programmation = findById(id);

        Long institutId = getInstitutIdFromProgrammation(programmation);
        if (!securityService.canManageInstitut(institutId)) {
            throw new AccessDeniedException("Accès refusé à cet institut");
        }

        InstitutContexteActif contexteActif = contexteRepo.findByInstitutId(institutId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun contexte actif pour cet institut"));

        if (!programmation.getSemestre().getAnneeAcademique().getId()
                .equals(contexteActif.getAnneeAcademique().getId())) {
            throw new IllegalStateException("Impossible de modifier une programmation d'une année fermée");
        }

        programmation.setDheure(dheure);
        programmation.setNbrCredit(nbrCredit);
        if (libelle != null && !libelle.trim().isEmpty()) programmation.setLibelle(libelle);
        if (libelleAnglais != null && !libelleAnglais.trim().isEmpty()) programmation.setLibelleAnglais(libelleAnglais);

        programmation.setEnseignants(resolveEnseignants(enseignantIds, institutId));

        log.info("Programmation modifiée : id={}", id);
        return programmationRepo.save(programmation);
    }

    // ═══════════════════════════════════════════════════════════
    // SUPPRESSION
    // ═══════════════════════════════════════════════════════════

    @Transactional
    @Override
    public void supprimer(Long id) {
        ProgrammationUE programmation = findById(id);

        Long institutId = getInstitutIdFromProgrammation(programmation);
        if (!securityService.canManageInstitut(institutId)) {
            throw new AccessDeniedException("Accès refusé à cet institut");
        }

        InstitutContexteActif contexteActif = contexteRepo.findByInstitutId(institutId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun contexte actif pour cet institut"));

        if (!programmation.getSemestre().getAnneeAcademique().getId()
                .equals(contexteActif.getAnneeAcademique().getId())) {
            throw new IllegalStateException("Impossible de supprimer une programmation d'une année fermée");
        }

        log.info("Programmation supprimée : id={}", id);
        programmationRepo.delete(programmation);
    }

    /**
     * ✅ IMPLÉMENTÉ — Supprime toutes les programmations UE d'une année académique.
     *
     * Utilisé exclusivement par le rollback de migration pour nettoyer
     * les programmations dupliquées vers N+1.
     *
     * Bypass volontaire des vérifications de contexte actif : cette méthode
     * est appelée par MigrationService qui a déjà validé les droits en amont.
     * On ne vérifie pas le contexte car N+1 n'est justement plus le contexte
     * actif au moment du rollback.
     */
    @Transactional
    public void supprimerProgrammationsAnnee(Long anneeId) {
        List<ProgrammationUE> programmations = programmationRepo.findByAnneeAcademiqueId(anneeId);

        if (programmations.isEmpty()) {
            log.warn("⚠️ Aucune programmation trouvée pour l'année {} — rien à supprimer", anneeId);
            return;
        }

        programmationRepo.deleteAll(programmations);
        log.info("🗑️ {} programmation(s) supprimée(s) pour l'année id={}", programmations.size(), anneeId);
    }

    // ═══════════════════════════════════════════════════════════
    // RECHERCHE & LISTES
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammationUE> getByClasseAndAnnee(Long classeId, Long anneeId) {
        if (classeId == null) {
            Long institutCible = securityService.getInstitutIdCourant();
            return anneeId != null && institutCible != null
                    ? programmationRepo.findByAnneeAcademiqueIdAndInstitutId(anneeId, institutCible)
                    : programmationRepo.findByAnneeAcademiqueId(anneeId);
        }
        Classe classe = classesService.findById(classeId);
        if (!securityService.canAccessInstitut(getInstitutIdFromClasse(classe))) {
            throw new AccessDeniedException("Accès refusé à cette classe");
        }
        return programmationRepo.findByClasseAndAnnee(classeId, anneeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammationUE> getByEnseignantAndAnnee(Long enseignantId, Long anneeId) {
        Long institutCible = securityService.getInstitutIdCourant();
        return institutCible != null
                ? programmationRepo.findByEnseignantIdAndInstitutId(enseignantId, institutCible)
                : programmationRepo.findByEnseignantAndAnnee(enseignantId, anneeId);
    }

    @Transactional(readOnly = true)
    @Override
    public ProgrammationUE findById(Long id) {
        ProgrammationUE prog = programmationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programmation introuvable : " + id));
        if (!securityService.canAccessInstitut(getInstitutIdFromProgrammation(prog))) {
            throw new AccessDeniedException("Accès refusé");
        }
        return prog;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammationUE> getProgrammationsByEnseignant(Long enseignantId) {
        Long institutCible = securityService.getInstitutIdCourant();
        if (institutCible != null) {
            return programmationRepo.findByEnseignantIdAndInstitutId(enseignantId, institutCible);
        }
        Long anneeActiveId = anneeService.getAnneeActive().getId();
        return programmationRepo.findByEnseignantIdAndAnneeId(enseignantId, anneeActiveId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Classe> getClassesByEnseignant(Long enseignantId) {
        Long anneeActiveId = anneeService.getAnneeActive().getId();
        return programmationRepo.findByEnseignantIdAndAnneeId(enseignantId, anneeActiveId)
                .stream()
                .map(ProgrammationUE::getClasse)
                .distinct()
                .toList();
    }

    public List<ProgrammationUE> getByClasse(Long classeId) {
        return programmationRepo.findByClasseId(classeId);
    }

    public List<ProgrammationUE> getByAnnee(Long anneeId) {
        Long institutCible = securityService.getInstitutIdCourant();
        return institutCible != null
                ? programmationRepo.findByAnneeAcademiqueIdAndInstitutId(anneeId, institutCible)
                : programmationRepo.findByAnneeAcademiqueId(anneeId);
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION — CLONAGE N → N+1
    // ═══════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<ProgrammationUE> getProgrammationsMigrables(Long sourceAnneeId, Long institutId) {
        if (!securityService.canManageInstitut(institutId)) {
            throw new AccessDeniedException("Accès refusé");
        }
        return programmationRepo.findMigrablesBySourceAnneeAndInstitut(sourceAnneeId, institutId);
    }

    @Transactional
    public ProgrammationUE clonerVersNouvelleAnnee(
            Long sourceId, Long cibleSemestreId, Set<Long> nouveauxEnseignantIds) {

        ProgrammationUE source = findById(sourceId);
        Semestre cibleSemestre = semestreService.findById(cibleSemestreId);

        if (programmationRepo.existsByUeIdAndClasseIdAndSemestreId(
                source.getUe().getId(), source.getClasse().getId(), cibleSemestreId)) {
            log.warn("Programmation déjà existante : UE={} Classe={} Semestre={}",
                    source.getUe().getNom(), source.getClasse().getNom(), cibleSemestre.getTypeSemestre());
            return null;
        }

        Set<Enseignant> enseignants = (nouveauxEnseignantIds != null && !nouveauxEnseignantIds.isEmpty())
                ? resolveEnseignants(nouveauxEnseignantIds, getInstitutIdFromProgrammation(source))
                : source.getEnseignants();

        ProgrammationUE clone = ProgrammationUE.builder()
                .ue(source.getUe()).semestre(cibleSemestre).classe(source.getClasse())
                .dheure(source.getDheure()).nbrCredit(source.getNbrCredit())
                .enseignants(enseignants)
                .libelle(source.getLibelle()).libelleAnglais(source.getLibelleAnglais())
                .build();

        log.info("Programmation clonée : source={} → Semestre={}", sourceId, cibleSemestreId);
        return programmationRepo.save(clone);
    }

    @Transactional
    @Override
    @Deprecated
    public void dupliquerVersNouvelleAnnee(Long ancienneAnneeId, Long nouvelleAnneeId) {
        log.warn("dupliquerVersNouvelleAnnee() est déprécié — utiliser le workflow sélectif");

        List<Semestre> nouveauxSemestres = semestreService.getByAnnee(nouvelleAnneeId);
        if (nouveauxSemestres.isEmpty()) {
            throw new IllegalStateException("Veuillez d'abord créer les semestres de la nouvelle année");
        }

        int clones = 0;
        for (Semestre ancienSemestre : semestreService.getByAnnee(ancienneAnneeId)) {
            Semestre nouveauSemestre = nouveauxSemestres.stream()
                    .filter(s -> s.getTypeSemestre().equals(ancienSemestre.getTypeSemestre()))
                    .findFirst().orElse(null);
            if (nouveauSemestre == null) continue;

            for (ProgrammationUE ancienne : programmationRepo.findBySemestreId(ancienSemestre.getId())) {
                if (programmationRepo.existsByUeIdAndClasseIdAndSemestreId(
                        ancienne.getUe().getId(), ancienne.getClasse().getId(), nouveauSemestre.getId())) continue;
                clonerVersNouvelleAnnee(ancienne.getId(), nouveauSemestre.getId(), null);
                clones++;
            }
        }
        log.info("Migration en masse terminée : {} programmations clonées", clones);
    }

    @Override @Transactional @Deprecated
    public void dupliquerUEVersNouvelleAnnee(Long ueId, Long ancienneAnneeId, Long nouvelleAnneeId) {
        log.warn("dupliquerUEVersNouvelleAnnee() est déprécié");
    }

    @Override @Transactional @Deprecated
    public void dupliquerEnseignantVersNouvelleAnnee(Long enseignantId, Long ancienneAnneeId, Long nouvelleAnneeId) {
        log.warn("dupliquerEnseignantVersNouvelleAnnee() est déprécié");
    }

    // ═══════════════════════════════════════════════════════════
    // UTILITAIRES INTERNES
    // ═══════════════════════════════════════════════════════════

    private Set<Enseignant> resolveEnseignants(Set<Long> enseignantIds, Long institutId) {
        Set<Enseignant> enseignants = new HashSet<>();
        for (Long ensId : enseignantIds) {
            Enseignant ens = enseignantRepo.findById(ensId)
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable : " + ensId));
            if (institutId != null && ens.getInstitut() != null
                    && !ens.getInstitut().getId().equals(institutId)) {
                throw new AccessDeniedException(
                        "L'enseignant " + ens.getNom() + " n'appartient pas à cet institut");
            }
            enseignants.add(ens);
        }
        return enseignants;
    }

    private Long getInstitutIdFromProgrammation(ProgrammationUE prog) {
        return getInstitutIdFromClasse(prog.getClasse());
    }

    private Long getInstitutIdFromClasse(Classe classe) {
        if (classe == null || classe.getNiveau() == null
                || classe.getNiveau().getFiliere() == null
                || classe.getNiveau().getFiliere().getEcole() == null
                || classe.getNiveau().getFiliere().getEcole().getInstitut() == null) {
            throw new IllegalStateException("Chemin académique incomplet pour déterminer l'institut");
        }
        return classe.getNiveau().getFiliere().getEcole().getInstitut().getId();
    }

}