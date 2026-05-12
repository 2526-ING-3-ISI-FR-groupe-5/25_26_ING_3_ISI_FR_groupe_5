package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.InterfaceService;

import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.ArrayList;
import java.util.List;

public interface IMigrationService {

    MigrationResultat migrer(Long nouvelleAnneeId, Utilisateur acteur);
    MigrationResultat migrerPourInstitut(Long institutId, Long nouvelleAnneeId, Utilisateur acteur);
    MigrationResultat simuler(Long nouvelleAnneeId);
    @Transactional(readOnly = true)
    MigrationResultat simuler(Long institutId, Long nouvelleAnneeId);
    MigrationResultat simulerPourInstitut(Long institutId, Long nouvelleAnneeId);
    List<String> getEtudiantsSansDecision();
    MigrationResultat migrerEtudiant(Long etudiantId, Long nouvelleAnneeId, Utilisateur acteur);
    MigrationResultat migrerEnseignant(Long enseignantId, Long nouvelleAnneeId, Utilisateur acteur);
    MigrationResultat migrerUE(Long ueId, Long nouvelleAnneeId, Utilisateur acteur);
    MigrationResultat migrerClasse(Long classeId, Long nouvelleAnneeId, Utilisateur acteur);
    MigrationResultat migrerFiliere(Long filiereId, Long nouvelleAnneeId, Utilisateur acteur);
    MigrationResultat migrerNiveau(Long niveauId, Long nouvelleAnneeId, Utilisateur acteur);

    // ═══════════════════════════════════════════════════════════
    // CLASSE INTERNE CORRIGÉE
    // ═══════════════════════════════════════════════════════════
    class MigrationResultat {
        private int admis;
        private int redoublants;
        private int exclus;
        private int diplomes;
        private int ignores;

        private final List<String> admisList = new ArrayList<>();
        private final List<String> redoublantsList = new ArrayList<>();
        private final List<String> exclusList = new ArrayList<>();
        private final List<String> diplomesList = new ArrayList<>();
        private final List<String> ignoresList = new ArrayList<>();

        public void ajouterAdmis(String matricule) {
            admis++; if (matricule != null) admisList.add(matricule);
        }
        public void ajouterRedoublant(String matricule) {
            redoublants++; if (matricule != null) redoublantsList.add(matricule);
        }
        public void ajouterExclu(String matricule) {
            exclus++; if (matricule != null) exclusList.add(matricule);
        }
        public void ajouterDiplome(String matricule) {
            diplomes++; if (matricule != null) diplomesList.add(matricule);
        }
        public void ajouterIgnore(String matricule) {
            ignores++; if (matricule != null) ignoresList.add(matricule);
        }

        // Getters compteurs
        public int getAdmis() { return admis; }
        public int getRedoublants() { return redoublants; }
        public int getExclus() { return exclus; }
        public int getDiplomes() { return diplomes; }
        public int getIgnores() { return ignores; }
        public int getTotalTraite() { return admis + redoublants + exclus + diplomes + ignores; }

        // ✅ Getters listes (noms clairs pour le mapping manuel)
        public List<String> getAdmisList() { return List.copyOf(admisList); }
        public List<String> getRedoublantsList() { return List.copyOf(redoublantsList); }
        public List<String> getExclusList() { return List.copyOf(exclusList); }
        public List<String> getDiplomesList() { return List.copyOf(diplomesList); }
        public List<String> getIgnoresList() { return List.copyOf(ignoresList); }

        @Override
        public String toString() {
            return String.format("Admis: %d, Redoublants: %d, Exclus: %d, Diplômés: %d, Ignorés: %d",
                    admis, redoublants, exclus, diplomes, ignores);
        }
    }
}