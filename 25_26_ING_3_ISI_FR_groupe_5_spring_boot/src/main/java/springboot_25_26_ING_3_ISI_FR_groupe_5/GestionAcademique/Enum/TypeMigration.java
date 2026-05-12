package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

public enum TypeMigration {
    COMPLETE,    // Tous les étudiants
    ETUDIANT,    // Un étudiant spécifique
    ENSEIGNANT,  // Un enseignant spécifique
    UE,          // Une UE spécifique
    CLASSE,      // Une classe spécifique
    FILIERE,     // Une filière spécifique
    SIMULATION, NIVEAU       // Un niveau spécifique
}