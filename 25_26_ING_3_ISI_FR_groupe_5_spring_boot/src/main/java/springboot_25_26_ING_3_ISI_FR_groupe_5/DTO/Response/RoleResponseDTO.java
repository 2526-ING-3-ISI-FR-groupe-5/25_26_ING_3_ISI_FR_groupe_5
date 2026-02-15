package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Response;

// Liste des IDs des utilisateurs

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RoleResponseDTO {

    @NotBlank(message = "Le nom du rôle est requis")
    private String nom;

    private String description;

    private Boolean active = true; // Par défaut actif

    // IDs des permissions à assigner (si vous gérez les permissions)
    private List<Long> permissionIds;
}