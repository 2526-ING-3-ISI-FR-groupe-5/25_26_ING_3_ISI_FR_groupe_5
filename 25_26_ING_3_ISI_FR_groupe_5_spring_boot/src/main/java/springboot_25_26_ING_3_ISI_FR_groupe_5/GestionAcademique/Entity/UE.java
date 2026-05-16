package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_ue_code_specialite", columnNames = {"code", "specialite_id"})
})
public class UE extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(length = 500)
    private String libelle;

    @Column(length = 500)
    private String libelleAnglais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @OneToMany(mappedBy = "ue")
    @Builder.Default
    private Set<ProgrammationUE> programmations = new HashSet<>();

    // ✅ Helper pour filtrage multi-institut
    public Long getInstitutId() {
        return specialite != null ? specialite.getInstitutId() : null;
    }
}