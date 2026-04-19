package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MigrationService {

    private final InscriptionRepository inscriptionRepo;
    private final AnneeAcademiqueService anneeService;
    private final SemestreService semestreService;
    private final NiveauService niveauService;
    private final ClassesService classesService;
    private final ClassesRepository classesRepo;
    private final ProgrammationUEService programmationService;

    // ══════════════════════════════════════════
    // MIGRER vers une nouvelle année académique
    // ══════════════════════════════════════════
    @Transactional
    public MigrationResultat migrer(Long nouvelleAnneeId, Utilisateur acteur) {

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findById(nouvelleAnneeId);

        if (nouvelleAnnee.isActive()) {
            throw new RuntimeException("Cette année est déjà active");
        }

        MigrationResultat resultat = new MigrationResultat();

        // ── ÉTAPE 1 : Migrer les étudiants ──
        migrerEtudiants(ancienneAnnee, nouvelleAnnee, resultat);

        // ── ÉTAPE 2 : Dupliquer les programmations UE ──
        programmationService.dupliquerVersNouvelleAnnee(
                ancienneAnnee.getId(),
                nouvelleAnnee.getId()
        );

        // ── ÉTAPE 3 : Activer la nouvelle année (avec l'acteur)
        anneeService.activer(nouvelleAnneeId, acteur);  // ✅ Correction

        return resultat;
    }

    // ══════════════════════════════════════════
    // MIGRER les étudiants
    // ══════════════════════════════════════════
    private void migrerEtudiants(
            Annee_academique ancienneAnnee,
            Annee_academique nouvelleAnnee,
            MigrationResultat resultat
    ) {
        List<Inscription> anciennes = inscriptionRepo
                .findByAnneeAcademiqueId(ancienneAnnee.getId());

        for (Inscription ancienne : anciennes) {

            String decision = ancienne.getDecisionFinAnnee();

            if (decision == null) {
                resultat.ajouterIgnore(ancienne.getEtudiant().getMatricule());
                continue;
            }

            switch (decision) {

                case "ADMIS" -> {
                    Niveau niveauActuel = ancienne.getClasse().getNiveau();
                    Optional<Niveau> niveauSup = niveauService.getNiveauSuperieur(niveauActuel);

                    if (niveauSup.isEmpty()) {
                        resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
                        break;
                    }

                    Classe classeSuperieure = trouverClasseCorrespondante(
                            ancienne.getClasse(),
                            niveauSup.get()
                    );

                    creerNouvelleInscription(
                            ancienne.getEtudiant(),
                            classeSuperieure,
                            nouvelleAnnee
                    );
                    resultat.ajouterAdmis(ancienne.getEtudiant().getMatricule());
                }

                case "REDOUBLANT" -> {
                    creerNouvelleInscription(
                            ancienne.getEtudiant(),
                            ancienne.getClasse(),
                            nouvelleAnnee
                    );
                    resultat.ajouterRedoublant(ancienne.getEtudiant().getMatricule());
                }

                case "EXCLU" -> {
                    ancienne.getEtudiant().setActive(false);
                    resultat.ajouterExclu(ancienne.getEtudiant().getMatricule());
                }

                case "DIPLOME" -> {
                    resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
                }
            }
        }
    }

    // ══════════════════════════════════════════
    // TROUVER la classe du niveau supérieur
    // ══════════════════════════════════════════
    private Classe trouverClasseCorrespondante(Classe actuelle, Niveau niveauSup) {

        Long specialiteId = actuelle.getNiveau().getSpecialite() != null
                ? actuelle.getNiveau().getSpecialite().getId()
                : null;

        List<Classe> classesNiveauSup = classesRepo.findByNiveauId(niveauSup.getId());

        if (specialiteId != null) {
            classesNiveauSup = classesNiveauSup.stream()
                    .filter(c -> c.getNiveau() != null &&
                            c.getNiveau().getSpecialite() != null &&
                            c.getNiveau().getSpecialite().getId().equals(specialiteId))
                    .toList();
        }

        if (classesNiveauSup.isEmpty()) {
            throw new RuntimeException(
                    "Aucune classe trouvée pour le niveau supérieur : " + niveauSup.getNom()
            );
        }

        return classesNiveauSup.get(0);
    }

    // ══════════════════════════════════════════
    // CRÉER une nouvelle inscription
    // ══════════════════════════════════════════
    private void creerNouvelleInscription(
            Etudiant etudiant,
            Classe classe,
            Annee_academique annee
    ) {
        if (inscriptionRepo.existsByEtudiantIdAndAnneeAcademiqueId(
                etudiant.getId(), annee.getId())) {
            return;
        }

        Inscription nouvelle = new Inscription();
        nouvelle.setEtudiant(etudiant);
        nouvelle.setClasse(classe);
        nouvelle.setAnneeAcademique(annee);
        inscriptionRepo.save(nouvelle);
    }
}