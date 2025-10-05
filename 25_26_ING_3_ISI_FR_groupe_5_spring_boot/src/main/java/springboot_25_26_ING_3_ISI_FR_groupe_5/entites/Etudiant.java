package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.NiveauEtudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeSexe;

import java.util.Date;

public class Etudiant {
    private  int id;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private String  matricule;
    private  NiveauEtudiant type;
    private  TypeSexe sexe;

    private Date date_naissance;

}

