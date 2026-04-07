package springboot_25_26_ING_3_ISI_FR_groupe_5.Notification.Services;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Notification.Services.EmailInterface;

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
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            </head>
            <body style="margin:0;padding:0;background:#0c0c0e;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0"
                     style="background:#0c0c0e;padding:40px 20px;">
                <tr><td align="center">
                  <table width="560" cellpadding="0" cellspacing="0"
                         style="background:#131316;border-radius:16px;
                                border:1px solid rgba(255,255,255,0.07);
                                overflow:hidden;max-width:100%%;">

                    <!-- En-tête rouge -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#dc2626,#b91c1c);
                                 padding:32px 40px;text-align:center;">
                        <div style="font-size:2rem;margin-bottom:8px;">📖</div>
                        <h1 style="margin:0;color:#fff;font-size:1.6rem;
                                   font-weight:900;letter-spacing:-0.02em;">
                          Carnet<span style="opacity:0.8;">Rouge</span>
                        </h1>
                        <p style="margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:0.85rem;">
                          Plateforme de gestion académique
                        </p>
                      </td>
                    </tr>

                    <!-- Corps -->
                    <tr>
                      <td style="padding:36px 40px;">
                        <h2 style="margin:0 0 6px;color:#f0f0f2;font-size:1.1rem;font-weight:700;">
                          Bonjour %s %s 👋
                        </h2>
                        <p style="margin:0 0 24px;color:rgba(240,240,242,0.6);font-size:0.88rem;line-height:1.6;">
                          Votre compte a été créé sur la plateforme <strong style="color:#f0f0f2;">CarnetRouge</strong>.
                          Vous trouverez ci-dessous vos identifiants de connexion.
                        </p>

                        <!-- Carte identifiants -->
                        <div style="background:#1a1a1f;border:1px solid rgba(220,38,38,0.2);
                                    border-radius:12px;padding:24px;margin-bottom:24px;">
                          <p style="margin:0 0 4px;font-size:0.67rem;font-weight:700;
                                    text-transform:uppercase;letter-spacing:0.1em;
                                    color:rgba(240,240,242,0.35);">Vos identifiants</p>

                          <!-- Email -->
                          <div style="margin-top:14px;">
                            <p style="margin:0 0 4px;font-size:0.72rem;color:rgba(240,240,242,0.45);
                                      text-transform:uppercase;letter-spacing:0.08em;">Email</p>
                            <div style="background:#0c0c0e;border:1px solid rgba(255,255,255,0.08);
                                        border-radius:8px;padding:10px 14px;">
                              <code style="color:#38bdf8;font-size:0.88rem;">%s</code>
                            </div>
                          </div>

                          <!-- Mot de passe -->
                          <div style="margin-top:12px;">
                            <p style="margin:0 0 4px;font-size:0.72rem;color:rgba(240,240,242,0.45);
                                      text-transform:uppercase;letter-spacing:0.08em;">Mot de passe temporaire</p>
                            <div style="background:#0c0c0e;border:1px solid rgba(220,38,38,0.25);
                                        border-radius:8px;padding:10px 14px;
                                        display:flex;align-items:center;justify-content:space-between;">
                              <code style="color:#ef4444;font-size:1rem;font-weight:700;
                                           letter-spacing:0.05em;">%s</code>
                              <span style="font-size:0.7rem;color:rgba(240,240,242,0.35);">
                                À changer à la 1ère connexion
                              </span>
                            </div>
                          </div>

                          <!-- Rôle -->
                          <div style="margin-top:12px;">
                            <p style="margin:0 0 4px;font-size:0.72rem;color:rgba(240,240,242,0.45);
                                      text-transform:uppercase;letter-spacing:0.08em;">Rôle</p>
                            <span style="display:inline-block;background:rgba(220,38,38,0.1);
                                         color:#ef4444;border:1px solid rgba(220,38,38,0.2);
                                         border-radius:6px;padding:4px 12px;
                                         font-size:0.75rem;font-weight:700;text-transform:uppercase;">
                              %s
                            </span>
                          </div>
                        </div>

                        <!-- Bouton connexion -->
                        <div style="text-align:center;margin-bottom:24px;">
                          <a href="http://localhost:8080/login"
                             style="display:inline-block;background:#dc2626;color:#fff;
                                    text-decoration:none;padding:12px 32px;
                                    border-radius:8px;font-weight:700;font-size:0.88rem;">
                            Se connecter →
                          </a>
                        </div>

                        <!-- Avertissement sécurité -->
                        <div style="background:rgba(245,158,11,0.06);
                                    border:1px solid rgba(245,158,11,0.18);
                                    border-radius:9px;padding:14px 16px;">
                          <p style="margin:0;font-size:0.78rem;color:rgba(245,158,11,0.85);line-height:1.6;">
                            ⚠️ <strong>Sécurité :</strong> Ce mot de passe est temporaire.
                            Changez-le dès votre première connexion.
                            Ne partagez jamais vos identifiants.
                          </p>
                        </div>
                      </td>
                    </tr>

                    <!-- Pied -->
                    <tr>
                      <td style="padding:20px 40px;border-top:1px solid rgba(255,255,255,0.06);
                                 text-align:center;">
                        <p style="margin:0;font-size:0.72rem;color:rgba(240,240,242,0.25);">
                          © 2026 CarnetRouge — Plateforme académique · Email généré automatiquement
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
