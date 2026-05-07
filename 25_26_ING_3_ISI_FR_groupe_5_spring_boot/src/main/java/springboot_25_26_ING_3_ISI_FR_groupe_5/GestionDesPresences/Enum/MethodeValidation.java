package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

public enum MethodeValidation {
    MANUELLE,   // Enseignant coche manuellement
    QR_CODE,    // Étudiant scanne un QR code
    CODE_PIN    // Étudiant saisit un code PIN
}