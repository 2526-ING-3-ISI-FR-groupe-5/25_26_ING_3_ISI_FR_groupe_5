package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class QRCodeService {

    // URL de base de l'application — configurable dans application.properties
    // Exemple : app.base-url=https://abc123.ngrok.io
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final int QR_WIDTH  = 300;
    private static final int QR_HEIGHT = 300;

    // ══════════════════════════════════════════
    // GÉNÉRATION QR CODE POUR SESSION D'APPEL
    // ══════════════════════════════════════════

    /**
     * Génère un QR Code contenant l'URL de validation de présence.
     *
     * L'URL encodée dans le QR ressemble à :
     * https://monapp.ngrok.io/etudiant/valider-presence?session=123&pin=847291
     *
     * Quand l'étudiant scanne :
     * → le navigateur ouvre l'URL
     * → le formulaire est pré-rempli automatiquement
     * → la validation se déclenche sans saisie manuelle
     *
     * @param sessionId  identifiant de la session d'appel
     * @param pin        code PIN 6 chiffres
     * @return image QR Code encodée en base64 (utilisable directement dans src="data:image/png;base64,...")
     */
    public String genererQRCodeSession(Long sessionId, String pin) {
        String url = baseUrl + "/etudiant/valider-presence?session=" + sessionId + "&pin=" + pin;
        return genererQRCode(url);
    }

    /**
     * Génère un QR Code à partir d'un contenu quelconque.
     *
     * @param contenu texte ou URL à encoder
     * @return image PNG encodée en base64
     */
    public String genererQRCode(String contenu) {
        try {
            QRCodeWriter writer = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = writer.encode(contenu, BarcodeFormat.QR_CODE,
                    QR_WIDTH, QR_HEIGHT, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            byte[] pngBytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(pngBytes);

            log.debug("QR Code genere pour : {}", contenu);
            return base64;

        } catch (WriterException | IOException e) {
            log.error("Erreur generation QR Code : {}", e.getMessage());
            throw new RuntimeException("Impossible de generer le QR Code", e);
        }
    }
}