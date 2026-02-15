package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

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
    @Enumerated(EnumType.STRING)
    private TypeRole nom;
    @Column(nullable = false, unique = true)
    private  String description;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateCreation;
    private  Boolean active;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<Permission> permissions;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Permission> permission= new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    private  Collection<Utilisateur> utilisateur=new ArrayList<>();
}