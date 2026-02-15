package springboot_25_26_ING_3_ISI_FR_groupe_5.Repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Utilisateur;

public interface UtilisateurRepository extends CrudRepository<Utilisateur,Integer>{

    Utilisateur findByUsername(@NotBlank(message = "L'email ne peut pas être vide.") @Email(message = "L'email doit être un format valide.") @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères.") String email);
}
