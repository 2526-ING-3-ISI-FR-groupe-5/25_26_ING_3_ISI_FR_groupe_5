package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeRole;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nom;
    @Column(nullable = false, unique = true)
    private  String description;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateCreation;
    private  Boolean active;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> permission= new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private  Collection<Utilisateur> utilisateur=new ArrayList<>();


}