package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponseDTO {
    private Long id;
    private String nom;
    private Boolean active;
    private List<String> roles; // Noms des rôles qui ont cette permission
}
