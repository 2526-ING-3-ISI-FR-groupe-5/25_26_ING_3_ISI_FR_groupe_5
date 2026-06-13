package springboot_25_26_ING_3_ISI_FR_groupe_5.Notification.Services;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service("NotificationEmailService")
@RequiredArgsConstructor
public class EmailService implements EmailInterface {

    private final JavaMailSender mailSender;

    // ✅ Email de bienvenue envoyé à la création d'un utilisateur
    @Override
    public void envoyerEmailBienvenue(String destinataire, String prenom, String nom,
                                      String motDePasse, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("juniornoumedem02@gmail.com");
            helper.setTo(destinataire);
            helper.setSubject("🎓 Bienvenue sur CarnetRouge — Vos identifiants de connexion");
            helper.setText(construireCorpsEmail(prenom, nom, destinataire, motDePasse, role), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email à : " + destinataire, e);
        }
    }

    // ✅ Corps HTML de l'email
    @Override
    public String construireCorpsEmail(String prenom, String nom, String email,
                                       String motDePasse, String role) {
        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head><meta charset="UTF-8"/></head>
                <body style="margin:0;padding:0;background:#f5f5f5;font-family:Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:30px 15px;">
                    <tr><td align="center">
                      <table width="500" cellpadding="0" cellspacing="0"
                             style="background:#fff;border-radius:8px;overflow:hidden;max-width:100%%;">
                
                        <!-- Header -->
                        <tr>
                          <td style="background:#1a1a2e;padding:24px 30px;text-align:center;">
                            <h1 style="margin:0;color:#fff;font-size:1.3rem;">📘 CarnetRouge</h1>
                            <p style="margin:4px 0 0;color:#aab;font-size:0.8rem;">Plateforme de gestion academique</p>
                          </td>
                        </tr>
                
                        <!-- Corps -->
                        <tr>
                          <td style="padding:30px;">
                            <p style="margin:0 0 16px;color:#333;font-size:0.9rem;">
                              Bonjour <strong>%s %s</strong>,
                            </p>
                            <p style="margin:0 0 20px;color:#555;font-size:0.85rem;line-height:1.5;">
                              Votre compte a ete cree. Voici vos identifiants de connexion :
                            </p>
                
                            <!-- Bloc identifiants -->
                            <div style="background:#f8f9fa;border:1px solid #e0e0e0;border-radius:6px;padding:16px 20px;margin-bottom:20px;">
                              <p style="margin:0 0 8px;font-size:0.8rem;color:#333;">
                                <strong>Email :</strong> <span style="color:#2563eb;">%s</span>
                              </p>
                              <p style="margin:0 0 8px;font-size:0.8rem;color:#333;">
                                <strong>Mot de passe :</strong> <code style="background:#fee;color:#c00;padding:2px 6px;border-radius:3px;font-size:0.85rem;">%s</code>
                              </p>
                              <p style="margin:0;font-size:0.8rem;color:#333;">
                                <strong>Role :</strong> <span style="background:#e8e8ff;color:#333;padding:2px 8px;border-radius:3px;font-size:0.75rem;font-weight:600;">%s</span>
                              </p>
                            </div>
                
                            <div style="text-align:center;margin-bottom:16px;">
                              <a href="http://localhost:8080/login"
                                 style="display:inline-block;background:#2563eb;color:#fff;text-decoration:none;
                                        padding:10px 24px;border-radius:5px;font-size:0.85rem;font-weight:600;">
                                Se connecter
                              </a>
                            </div>
                
                            <p style="margin:0;font-size:0.75rem;color:#999;text-align:center;">
                              Changez votre mot de passe des la premiere connexion.
                            </p>
                          </td>
                        </tr>
                
                        <!-- Footer -->
                        <tr>
                          <td style="padding:16px 30px;border-top:1px solid #eee;text-align:center;">
                            <p style="margin:0;font-size:0.7rem;color:#aaa;">
                              Cet email a ete genere automatiquement — Merci de ne pas y repondre.
                            </p>
                          </td>
                        </tr>
                
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(prenom, nom, email, motDePasse, role);
    }
}