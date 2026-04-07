package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import io.jsonwebtoken.lang.Classes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
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
    public MigrationResultat migrer(Long nouvelleAnneeId) {

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findById(nouvelleAnneeId);

        // Vérifier que la nouvelle année n'est pas déjà active
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

        // ── ÉTAPE 3 : Activer la nouvelle année ──
        anneeService.activer(nouvelleAnneeId);

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
                // Décision non encore prise → on ignore
                resultat.ajouterIgnore(ancienne.getEtudiant().getMatricule());
                continue;
            }

            switch (decision) {

                case "ADMIS" -> {
                    // Trouver le niveau supérieur
                    Niveau niveauActuel = ancienne.getClasse().getNiveau();
                    Optional<Niveau> niveauSup = niveauService.getNiveauSuperieur(niveauActuel);

                    if (niveauSup.isEmpty()) {
                        // Étudiant diplômé → pas de nouvelle inscription
                        resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
                        break;
                    }

                    // ✅ CORRECTION : Trouver la classe correspondante au niveau supérieur
                    Classe classeSuperieure = trouverClasseCorrespondante(
                            ancienne.getClasse(),
                            niveauSup.get()
                    );

                    // Créer nouvelle inscription
                    creerNouvelleInscription(
                            ancienne.getEtudiant(),
                            classeSuperieure,
                            nouvelleAnnee
                    );
                    resultat.ajouterAdmis(ancienne.getEtudiant().getMatricule());
                }

                case "REDOUBLANT" -> {
                    // Reste dans la même classe
                    creerNouvelleInscription(
                            ancienne.getEtudiant(),
                            ancienne.getClasse(),
                            nouvelleAnnee
                    );
                    resultat.ajouterRedoublant(ancienne.getEtudiant().getMatricule());
                }

                case "EXCLU" -> {
                    // Désactiver l'étudiant
                    ancienne.getEtudiant().setActive(false);
                    resultat.ajouterExclu(ancienne.getEtudiant().getMatricule());
                }

                case "DIPLOME" -> {
                    // Pas de nouvelle inscription
                    resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
                }
            }
        }
    }

    // ══════════════════════════════════════════
    // TROUVER la classe du niveau supérieur
    // dans la même spécialité (via le niveau)
    // ══════════════════════════════════════════
    private Classe trouverClasseCorrespondante(Classe actuelle, Niveau niveauSup) {

        // ✅ CORRECTION : Récupérer la spécialité depuis le niveau actuel
        Long specialiteId = actuelle.getNiveau().getSpecialite() != null
                ? actuelle.getNiveau().getSpecialite().getId()
                : null;

        // Récupérer toutes les classes du niveau supérieur
        List<Classe> classesNiveauSup = classesRepo.findByNiveauId(niveauSup.getId());

        // Filtrer par spécialité si nécessaire
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

        // Retourner la première classe (ou implémenter une logique de répartition)
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
        // Vérifier qu'elle n'existe pas déjà
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