package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;


import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cycle extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeCycle typeCycle;
    @OneToMany(mappedBy = "cycle", fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Filiere> filieres = new ArrayList<>();


}