package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response;

// Liste des IDs des utilisateurs

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RoleResponseDTO {
     private Long id;
    @NotBlank(message = "Le nom du rôle est requis")
    private String nom;
    private String description;
    private Boolean active;
    Set<PermissionResponseDTO> permissions;


}