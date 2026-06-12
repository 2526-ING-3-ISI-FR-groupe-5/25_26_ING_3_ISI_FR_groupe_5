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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportPdfService implements IExportPdfService {

    private static final String APP_NAME = "CarnetRouge";
    private static final float MARGIN = 40f;
    private static final float PAGE_WIDTH = 842f;
    private static final float PAGE_HEIGHT = 595f;

    // ══════════════════════════════════════════════
    // EXPORT PDF UTILISATEURS
    // ══════════════════════════════════════════════

    public byte[] exportUtilisateurs(List<UtilisateurExportDTO> utilisateurs,
                                     Annee_academique annee, Semestre semestre) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
        document.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(document, page);
        float y = PAGE_HEIGHT - MARGIN;

        y = addHeader(cs, y, annee, semestre, "Liste des Utilisateurs", utilisateurs.size(), document);

        String[] headers = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Type", "Grade/Fonc.", "Date naiss.", "Actif"};
        float[] colWidths = {40, 80, 80, 160, 80, 40, 80, 70, 40};
        y = addTable(cs, y, headers, colWidths, utilisateurs.size(), document, (rowNum, rowY) -> {
            UtilisateurExportDTO u = utilisateurs.get(rowNum);
            return new String[]{
                    u.getId() != null ? u.getId().toString() : "",
                    u.getNom() != null ? u.getNom() : "",
                    u.getPrenom() != null ? u.getPrenom() : "",
                    u.getEmail() != null ? u.getEmail() : "",
                    u.getTelephone() != null ? u.getTelephone() : "",
                    u.getType() != null ? u.getType() : "",
                    getSpecificField(u),
                    u.getDateNaissance() != null ? u.getDateNaissance().toString() : "",
                    u.isActive() ? "Oui" : "Non"
            };
        });

        addFooter(cs);
        cs.close();
        return toByteArray(document);
    }

    // ══════════════════════════════════════════════
    // EXPORT PDF ÉTUDIANTS
    // ══════════════════════════════════════════════

    public byte[] exportEtudiants(List<EtudiantExportDTO> etudiants,
                                  Annee_academique annee, Semestre semestre) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
        document.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(document, page);
        float y = PAGE_HEIGHT - MARGIN;

        y = addHeader(cs, y, annee, semestre, "Liste des Etudiants", etudiants.size(), document);

        String[] headers = {"Matricule", "Nom", "Prénom", "Email", "Téléphone",
                "Classe", "Niveau", "Filière", "Année", "Semestre", "Actif"};
        float[] colWidths = {70, 70, 70, 140, 70, 60, 60, 70, 60, 50, 35};
        y = addTable(cs, y, headers, colWidths, etudiants.size(), document, (rowNum, rowY) -> {
            EtudiantExportDTO e = etudiants.get(rowNum);
            return new String[]{
                    e.getMatricule() != null ? e.getMatricule() : "",
                    e.getNom() != null ? e.getNom() : "",
                    e.getPrenom() != null ? e.getPrenom() : "",
                    e.getEmail() != null ? e.getEmail() : "",
                    e.getTelephone() != null ? e.getTelephone() : "",
                    e.getClasseNom() != null ? e.getClasseNom() : "",
                    e.getNiveauNom() != null ? e.getNiveauNom() : "",
                    e.getFiliereNom() != null ? e.getFiliereNom() : "",
                    e.getAnneeAcademique() != null ? e.getAnneeAcademique() : "",
                    e.getSemestre() != null ? e.getSemestre() : "",
                    e.isActive() ? "Oui" : "Non"
            };
        });

        addFooter(cs);
        cs.close();
        return toByteArray(document);
    }

    // ══════════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════════

    @FunctionalInterface
    private interface RowDataProvider {
        String[] getData(int rowNum, float y);
    }

    private byte[] toByteArray(PDDocument document) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        return baos.toByteArray();
    }

    private void addLogo(PDPageContentStream cs, PDDocument document, float x, float y) throws IOException {
        try (InputStream logoStream = getClass().getResourceAsStream("/favicon/icon-192x192.png")) {
            if (logoStream != null) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(document,
                        logoStream.readAllBytes(), "logo");
                cs.drawImage(logo, x, y - 22, 22, 22);
            }
        } catch (Exception ignored) {}
    }

    private float addHeader(PDPageContentStream cs, float y, Annee_academique annee,
                            Semestre semestre, String titre, int nbElements, PDDocument document) throws IOException {
        addLogo(cs, document, MARGIN, y);

        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        cs.setNonStrokingColor(new Color(63, 63, 170));
        cs.newLineAtOffset(MARGIN + 28, y);
        cs.showText(APP_NAME);
        cs.endText();
        y -= 24;

        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        cs.setNonStrokingColor(Color.BLACK);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(titre);
        cs.endText();
        y -= 20;

        String anneeStr = annee != null ? annee.getNom() : "N/A";
        String semestreStr = semestre != null ? semestre.getTypeSemestre().getLibelle() : "N/A";
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        cs.setNonStrokingColor(Color.DARK_GRAY);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Année : " + anneeStr + "  |  Semestre : " + semestreStr +
                "  |  Éléments : " + nbElements +
                "  |  Exporté le : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        cs.endText();
        y -= 25;

        return y;
    }

    private float addTable(PDPageContentStream cs, float y, String[] headers, float[] colWidths,
                           int nbRows, PDDocument document, RowDataProvider dataProvider) throws IOException {
        float x = MARGIN;
        float rowHeight = 16f;

        for (int i = 0; i < headers.length; i++) {
            drawCell(cs, x, y - rowHeight, colWidths[i], rowHeight, headers[i], true);
            x += colWidths[i];
        }
        y -= rowHeight;

        for (int r = 0; r < nbRows; r++) {
            x = MARGIN;
            String[] rowData = dataProvider.getData(r, y);
            for (int i = 0; i < rowData.length && i < headers.length; i++) {
                drawCell(cs, x, y - rowHeight, colWidths[i], rowHeight, rowData[i], false);
                x += colWidths[i];
            }
            y -= rowHeight;

            if (y < MARGIN + 40) {
                cs.close();
                PDPage newPage = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
                document.addPage(newPage);
                cs = new PDPageContentStream(document, newPage);
                y = PAGE_HEIGHT - MARGIN;
            }
        }

        return y - 10;
    }

    private void drawCell(PDPageContentStream cs, float x, float y, float w, float h,
                          String text, boolean isHeader) throws IOException {
        cs.setNonStrokingColor(isHeader ? new Color(99, 102, 241) : Color.WHITE);
        cs.addRect(x, y, w, h);
        cs.fill();
        cs.setNonStrokingColor(Color.BLACK);
        cs.setStrokingColor(Color.LIGHT_GRAY);
        cs.addRect(x, y, w, h);
        cs.stroke();

        cs.beginText();
        if (isHeader) {
            cs.setNonStrokingColor(Color.WHITE);
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 7);
        } else {
            cs.setNonStrokingColor(Color.BLACK);
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 7);
        }
        cs.newLineAtOffset(x + 2, y + 4);
        cs.showText(text != null ? text : "");
        cs.endText();
        cs.setNonStrokingColor(Color.BLACK);
    }

    private void addFooter(PDPageContentStream cs) throws IOException {
        cs.beginText();
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 7);
        cs.setNonStrokingColor(Color.LIGHT_GRAY);
        cs.newLineAtOffset(MARGIN, 20);
        cs.showText("Généré par " + APP_NAME + " — Document confidentiel");
        cs.endText();
    }

    private String getSpecificField(UtilisateurExportDTO u) {
        if ("ENS".equals(u.getType()) && u.getGrade() != null) return u.getGrade();
        if ("AST".equals(u.getType()) && u.getFonction() != null) return u.getFonction();
        if ("SUR".equals(u.getType()) && u.getSecteur() != null) return u.getSecteur();
        return "";
    }
}