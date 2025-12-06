package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.UniqueConstraint;
import jdk.jfr.DataAmount;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeSexe;

import java.util.Date;

public class Utilisateur {
    protected Long id;
    protected String nom;
    @Column(unique = true)
    protected String email;
    protected String telephone;
    protected   String motDePasse;
    @Enumerated(EnumType.STRING)
    protected TypeSexe sexe;
    protected Boolean active;
    protected Date dateCreation;

}
