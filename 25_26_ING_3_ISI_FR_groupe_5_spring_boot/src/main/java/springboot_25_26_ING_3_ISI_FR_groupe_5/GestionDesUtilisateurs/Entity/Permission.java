package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener.class)   // Pour @CreatedDate / @LastModifiedDate
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Veillez renseigner la permission")
    private String nom;

    @NotBlank(message = "Veillez renseigner la description")
    @Column(unique = true, nullable = false)
    private String description;

    private Boolean active;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    protected LocalDateTime creatAt;

    @LastModifiedDate
    @Column(insertable = false)
    protected LocalDateTime updateAt;

    // Côté inverse de la relation
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
}