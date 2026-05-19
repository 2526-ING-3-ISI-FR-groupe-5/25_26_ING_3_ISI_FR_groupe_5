package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum;

public enum TypeMigration {
    COMPLETE,    // Migration complète de l'institut
    INSTITUT,    // Un ou plusieurs instituts
    ECOLE,       // Une ou plusieurs écoles
    FILIERE,     // Une ou plusieurs filières
    SPECIALITE,  // Une ou plusieurs spécialités
    NIVEAU,      // Un ou plusieurs niveaux
    CLASSE,      // Une ou plusieurs classes
    ETUDIANT,    // Un ou plusieurs étudiants
    ENSEIGNANT,  // Un ou plusieurs enseignants
    ASSISTANT,   // Un ou plusieurs assistants pédagogiques
    UE,          // Une ou plusieurs UE
    SIMULATION,  // Simulation sans exécution réelle
    SELECTIVE    // Migration sélective multi-entités
}