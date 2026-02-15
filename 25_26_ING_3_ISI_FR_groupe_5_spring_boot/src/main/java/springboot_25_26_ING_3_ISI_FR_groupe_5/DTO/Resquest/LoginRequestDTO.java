package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
}