package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private  String description;
    private Date dateCreation;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<Permission> permissions;
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Role> roles;

}