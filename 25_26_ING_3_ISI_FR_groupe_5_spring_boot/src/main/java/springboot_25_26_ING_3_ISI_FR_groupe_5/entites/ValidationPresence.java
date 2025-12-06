package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import javax.xml.crypto.Data;
import java.util.Date;
@Entity
public class ValidationPresence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String qrCode;
    private  String codePin;
    private Date dateDeCreation;
}
