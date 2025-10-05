package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import javax.annotation.processing.Generated;

@Entity
public class Filiere {
    @Id
    @GeneratedValue(stategy=GenerationType.IDENTITY)
    private int id;
    private  String nom;
    private String code;
    private String niveau;
    private  String description;


}
