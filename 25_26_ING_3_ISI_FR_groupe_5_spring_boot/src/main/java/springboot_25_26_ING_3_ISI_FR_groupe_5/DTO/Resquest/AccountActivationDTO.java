package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountActivationDTO {

    @NotBlank(message = "L'email est requis")
    private String email;

    @NotBlank(message = "Le code OTP est requis")
    @Size(min = 6, max = 6, message = "Le code OTP doit contenir 6 caractères")
    private String otpCode;
}
