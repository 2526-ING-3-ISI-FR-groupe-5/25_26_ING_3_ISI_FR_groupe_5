package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Export;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurExportDTO {

    @CsvBindByName(column = "ID")
    private Long id;

    @CsvBindByName(column = "Nom")
    private String nom;

    @CsvBindByName(column = "Prénom")
    private String prenom;

    @CsvBindByName(column = "Email")
    private String email;

    @CsvBindByName(column = "Téléphone")
    private String telephone;

    @CsvBindByName(column = "Sexe")
    private String sexe;

    @CsvBindByName(column = "Type")
    private String type;

    @CsvBindByName(column = "Statut")
    private String status;

    @CsvBindByName(column = "Date création")
    @CsvDate("yyyy-MM-dd")
    private Date dateCreation;

    @CsvBindByName(column = "Dernière connexion")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    private Date lastLogin;
}
