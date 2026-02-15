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
public class AuthResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String token;
    private String refreshToken;
    private String type;
    private List<String> roles;
    private List<String> permissions;
    private boolean firstLogin;
    private boolean enabled;
    private boolean accountLocked;
}
