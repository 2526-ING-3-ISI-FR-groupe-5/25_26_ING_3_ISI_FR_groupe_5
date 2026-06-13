package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.EtudiantExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.UtilisateurExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IExportPdfService;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportPdfService implements IExportPdfService {

    private static final String APP_NAME    = "CarnetRouge";
    private static final float  MARGIN      = 40f;
    private static final float  PAGE_WIDTH  = 842f;  // A4 paysage
    private static final float  PAGE_HEIGHT = 595f;
    private static final float  ROW_HEIGHT  = 16f;

    // Couleur primaire du projet : bleu #4f7ef8
    private static final Color COLOR_PRIMARY     = new Color(79, 126, 248);
    private static final Color COLOR_HEADER_TEXT = Color.WHITE;
    private static final Color COLOR_ROW_EVEN    = new Color(245, 247, 255);
    private static final Color COLOR_ROW_ODD     = Color.WHITE;
    private static final Color COLOR_BORDER      = new Color(220, 224, 240);
    private static final Color COLOR_TEXT        = new Color(28, 34, 48);
    private static final Color COLOR_MUTED       = new Color(107, 114, 128);

    // ══════════════════════════════════════════════
    // EXPORT PDF UTILISATEURS
    // ══════════════════════════════════════════════

    @Override
    public byte[] exportUtilisateurs(List<UtilisateurExportDTO> utilisateurs,
                                     Annee_academique annee, Semestre semestre) throws IOException {
        try (PDDocument document = new PDDocument()) {
            String[] headers   = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Type", "Grade/Fonc.", "Date naiss.", "Actif"};
            float[]  colWidths = {35,   95,    95,       155,    85,           110,   90,             80,            45};

            List<String[]> rows = new ArrayList<>();
            for (UtilisateurExportDTO u : utilisateurs) {
                rows.add(new String[]{
                        u.getId() != null ? u.getId().toString() : "",
                        safe(u.getNom()),
                        safe(u.getPrenom()),
                        safe(u.getEmail()),
                        safe(u.getTelephone()),
                        typeLabel(u.getType()),
                        getSpecificField(u),
                        u.getDateNaissance() != null ? u.getDateNaissance().toString() : "",
                        u.isActive() ? "Oui" : "Non"
                });
            }

            renderDocument(document, annee, semestre, "Liste du Personnel", utilisateurs.size(), headers, colWidths, rows);
            return toByteArray(document);
        }
    }

    // ══════════════════════════════════════════════
    // EXPORT PDF ÉTUDIANTS
    // ══════════════════════════════════════════════

    @Override
    public byte[] exportEtudiants(List<EtudiantExportDTO> etudiants,
                                  Annee_academique annee, Semestre semestre) throws IOException {
        try (PDDocument document = new PDDocument()) {
            String[] headers   = {"Matricule", "Nom", "Prénom", "Email", "Téléphone", "Classe", "Niveau", "Filière", "Année", "Semestre", "Actif"};
            float[]  colWidths = {75,          75,    75,       140,    75,           65,       60,       75,        60,      55,         35};

            List<String[]> rows = new ArrayList<>();
            for (EtudiantExportDTO e : etudiants) {
                rows.add(new String[]{
                        safe(e.getMatricule()),
                        safe(e.getNom()),
                        safe(e.getPrenom()),
                        safe(e.getEmail()),
                        safe(e.getTelephone()),
                        safe(e.getClasseNom()),
                        safe(e.getNiveauNom()),
                        safe(e.getFiliereNom()),
                        safe(e.getAnneeAcademique()),
                        safe(e.getSemestre()),
                        e.isActive() ? "Oui" : "Non"
                });
            }

            renderDocument(document, annee, semestre, "Liste des Étudiants", etudiants.size(), headers, colWidths, rows);
            return toByteArray(document);
        }
    }

    // ══════════════════════════════════════════════
    // MOTEUR DE RENDU — gère la pagination proprement
    // ══════════════════════════════════════════════

    private void renderDocument(PDDocument document, Annee_academique annee, Semestre semestre,
                                String titre, int total, String[] headers, float[] colWidths,
                                List<String[]> rows) throws IOException {

        PDPage firstPage = newPage(document);
        PDPageContentStream cs = new PDPageContentStream(document, firstPage);
        float y = PAGE_HEIGHT - MARGIN;

        // En-tête première page
        y = drawPageHeader(cs, document, y, annee, semestre, titre, total);

        // En-tête tableau
        y = drawTableHeader(cs, y, headers, colWidths);

        // Lignes de données
        for (int r = 0; r < rows.size(); r++) {
            if (y - ROW_HEIGHT < MARGIN + 25) {
                drawPageFooter(cs, document.getNumberOfPages());
                cs.close();

                PDPage newPage = newPage(document);
                cs = new PDPageContentStream(document, newPage);
                y = PAGE_HEIGHT - MARGIN;
                y = drawContinuationHeader(cs, titre);
                y = drawTableHeader(cs, y, headers, colWidths);
            }

            boolean even = (r % 2 == 0);
            y = drawDataRow(cs, y, rows.get(r), colWidths, even);
        }

        drawPageFooter(cs, document.getNumberOfPages());
        cs.close();
    }

    // ══════════════════════════════════════════════
    // DESSIN : EN-TÊTE PRINCIPALE
    // ══════════════════════════════════════════════

    private float drawPageHeader(PDPageContentStream cs, PDDocument document, float y,
                                 Annee_academique annee, Semestre semestre,
                                 String titre, int total) throws IOException {
        // Logo
        try (InputStream logoStream = getClass().getResourceAsStream("/static/favicon/icon-192x192.png")) {
            if (logoStream != null) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");
                cs.drawImage(logo, MARGIN, y - 26, 26, 26);
            }
        } catch (Exception ignored) {}

        // "CarnetRouge"
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 15);
        cs.setNonStrokingColor(COLOR_PRIMARY);
        cs.newLineAtOffset(MARGIN + 32, y - 8);
        cs.showText(APP_NAME);
        cs.endText();

        // Ligne séparatrice
        y -= 32;
        cs.setStrokingColor(COLOR_PRIMARY);
        cs.setLineWidth(0.8f);
        cs.moveTo(MARGIN, y);
        cs.lineTo(PAGE_WIDTH - MARGIN, y);
        cs.stroke();
        y -= 10;

        // Institut + Titre sur la même ligne
        String institutNom = (annee != null && annee.getInstitut() != null)
                ? annee.getInstitut().getNom() : "—";
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        cs.setNonStrokingColor(COLOR_MUTED);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Institut : " + institutNom);
        cs.endText();
        y -= 13;

        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        cs.setNonStrokingColor(COLOR_TEXT);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(titre);
        cs.endText();
        y -= 14;

        // Méta
        String anneeStr    = annee    != null ? annee.getNom()                               : "Toutes";
        String semestreStr = semestre != null ? semestre.getTypeSemestre().getLibelle()      : "Tous";
        String meta = "Année : " + anneeStr + "  ·  Semestre : " + semestreStr +
                "  ·  " + total + " élément(s)  ·  Exporté le : " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
        cs.setNonStrokingColor(COLOR_MUTED);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(meta);
        cs.endText();
        y -= 16;

        return y;
    }

    private float drawContinuationHeader(PDPageContentStream cs, String titre) throws IOException {
        float y = PAGE_HEIGHT - MARGIN;
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        cs.setNonStrokingColor(COLOR_PRIMARY);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(APP_NAME + " — " + titre + " (suite)");
        cs.endText();
        y -= 18;
        return y;
    }

    // ══════════════════════════════════════════════
    // DESSIN : TABLEAU
    // ══════════════════════════════════════════════

    private float drawTableHeader(PDPageContentStream cs, float y, String[] headers, float[] colWidths) throws IOException {
        float x = MARGIN;
        for (int i = 0; i < headers.length; i++) {
            drawCell(cs, x, y - ROW_HEIGHT, colWidths[i], ROW_HEIGHT, headers[i], true, false);
            x += colWidths[i];
        }
        return y - ROW_HEIGHT;
    }

    private float drawDataRow(PDPageContentStream cs, float y, String[] data, float[] colWidths, boolean even) throws IOException {
        float x = MARGIN;
        for (int i = 0; i < data.length && i < colWidths.length; i++) {
            drawCell(cs, x, y - ROW_HEIGHT, colWidths[i], ROW_HEIGHT, data[i], false, even);
            x += colWidths[i];
        }
        return y - ROW_HEIGHT;
    }

    private void drawCell(PDPageContentStream cs, float x, float y, float w, float h,
                          String text, boolean isHeader, boolean evenRow) throws IOException {
        // Fond
        if (isHeader) {
            cs.setNonStrokingColor(COLOR_PRIMARY);
        } else {
            cs.setNonStrokingColor(evenRow ? COLOR_ROW_EVEN : COLOR_ROW_ODD);
        }
        cs.addRect(x, y, w, h);
        cs.fill();

        // Bordure
        cs.setStrokingColor(COLOR_BORDER);
        cs.setLineWidth(0.3f);
        cs.addRect(x, y, w, h);
        cs.stroke();

        // Texte tronqué
        String display = truncate(text, w, 7);
        cs.beginText();
        cs.setFont(
            new PDType1Font(isHeader ? Standard14Fonts.FontName.HELVETICA_BOLD : Standard14Fonts.FontName.HELVETICA),
            7
        );
        cs.setNonStrokingColor(isHeader ? COLOR_HEADER_TEXT : COLOR_TEXT);
        cs.newLineAtOffset(x + 3, y + 4);
        cs.showText(display);
        cs.endText();
    }

    // ══════════════════════════════════════════════
    // DESSIN : PIED DE PAGE
    // ══════════════════════════════════════════════

    private void drawPageFooter(PDPageContentStream cs, int pageNum) throws IOException {
        cs.setStrokingColor(COLOR_BORDER);
        cs.setLineWidth(0.5f);
        cs.moveTo(MARGIN, 28);
        cs.lineTo(PAGE_WIDTH - MARGIN, 28);
        cs.stroke();

        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 7);
        cs.setNonStrokingColor(COLOR_MUTED);
        cs.newLineAtOffset(MARGIN, 18);
        cs.showText("Généré par " + APP_NAME + " — Document confidentiel — Page " + pageNum);
        cs.endText();
    }

    // ══════════════════════════════════════════════
    // UTILITAIRES
    // ══════════════════════════════════════════════

    private PDPage newPage(PDDocument document) {
        PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
        document.addPage(page);
        return page;
    }

    private byte[] toByteArray(PDDocument document) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        return baos.toByteArray();
    }

    private String typeLabel(String type) {
        if (type == null) return "";
        return switch (type.toUpperCase()) {
            case "ENS" -> "Enseignant";
            case "AST" -> "Assistant pédagogique";
            case "SUR" -> "Surveillant";
            default    -> type;
        };
    }

    private String safe(String s) {
        return s != null ? s : "";
    }

    private String truncate(String text, float cellWidth, float fontSize) {
        if (text == null || text.isEmpty()) return "";
        // Estimation approximative : 0.6 * fontSize = largeur moyenne d'un caractère
        int maxChars = (int) ((cellWidth - 6) / (fontSize * 0.55));
        if (maxChars <= 0) return "";
        return text.length() <= maxChars ? text : text.substring(0, maxChars - 1) + "…";
    }

    private String getSpecificField(UtilisateurExportDTO u) {
        if ("ENS".equals(u.getType()) && u.getGrade()    != null) return u.getGrade();
        if ("AST".equals(u.getType()) && u.getFonction() != null) return u.getFonction();
        if ("SUR".equals(u.getType()) && u.getSecteur()  != null) return u.getSecteur();
        return "";
    }
}
