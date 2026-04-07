package springboot_25_26_ING_3_ISI_FR_groupe_5.Notification;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RestController;

@RestController // RestController est plus simple pour un test direct
public class EmailController {

    private final JavaMailSender mailSender;

    // Le constructeur doit avoir le même nom que la classe
    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //@GetMapping("/ad/test-email")
    public String envoyerBonjour() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            // REMPLACE PAR TON ADRESSE POUR TESTER
            message.setTo("junior.noumedem@saintjeaningenieur.org");
            message.setSubject("Test Automatique");
            message.setText("Bonjour ! Ceci est un message envoyé automatiquement au chargement de l'URL.");
            message.setFrom("votre-email-gmail@gmail.com");

            mailSender.send(message);

            return "Succès : Le message 'Bonjour' a été envoyé ! Vérifie ta boîte mail.";
        } catch (Exception e) {
            return "Erreur lors de l'envoi : " + e.getMessage();
        }
    }
}