package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Fichier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String nomFichier;
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Justificatif> justificatifs;
}
