package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Justificatif;

import java.util.ArrayList;
import java.util.Collection;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ASP")
public class AssistantPedagogique extends Utilisateur {

  private String fonction;

  @OneToMany(mappedBy = "assistantPedagogique")
  @Builder.Default
  private Collection<Justificatif> justificatif = new ArrayList<>();

  @ManyToMany
  @JoinTable(
          name = "assistant_classes",
          joinColumns = @JoinColumn(name = "assistant_id"),
          inverseJoinColumns = @JoinColumn(name = "classe_id")
  )
  @Builder.Default
  private Collection<Classe> classes = new ArrayList<>();
}