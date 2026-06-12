package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.EtudiantExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.UtilisateurExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IExportExcelService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportExcelService implements IExportExcelService {

    private static final String APP_NAME = "CarnetRouge";

    // ══════════════════════════════════════════════
    // EXPORT UTILISATEURS
    // ══════════════════════════════════════════════

    @Override
    public byte[] exportUtilisateurs(List<UtilisateurExportDTO> utilisateurs,
                                     Annee_academique annee, Semestre semestre) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Utilisateurs");

        addAppHeader(sheet, annee, semestre, "Liste des Utilisateurs", utilisateurs.size());

        String[] headers = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Type",
                "Grade/Fonction", "Date naissance", "Actif", "Institut"};
        int headerRowNum = 4;
        Row headerRow = sheet.createRow(headerRowNum);
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = headerRowNum + 1;
        for (UtilisateurExportDTO u : utilisateurs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
            row.createCell(1).setCellValue(u.getNom() != null ? u.getNom() : "");
            row.createCell(2).setCellValue(u.getPrenom() != null ? u.getPrenom() : "");
            row.createCell(3).setCellValue(u.getEmail() != null ? u.getEmail() : "");
            row.createCell(4).setCellValue(u.getTelephone() != null ? u.getTelephone() : "");
            row.createCell(5).setCellValue(u.getType() != null ? u.getType() : "");
            row.createCell(6).setCellValue(getSpecificField(u));
            row.createCell(7).setCellValue(u.getDateNaissance() != null ? u.getDateNaissance().toString() : "");
            row.createCell(8).setCellValue(u.isActive() ? "Oui" : "Non");
            row.createCell(9).setCellValue(u.getInstitutNom() != null ? u.getInstitutNom() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    @Override
    public byte[] exportUtilisateurs(List<UtilisateurExportDTO> utilisateurs) throws IOException {
        return exportUtilisateurs(utilisateurs, null, null);
    }

    // ══════════════════════════════════════════════
    // EXPORT ÉTUDIANTS
    // ══════════════════════════════════════════════

    @Override
    public byte[] exportEtudiants(List<EtudiantExportDTO> etudiants,
                                  Annee_academique annee, Semestre semestre) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Etudiants");

        addAppHeader(sheet, annee, semestre, "Liste des Etudiants", etudiants.size());

        String[] headers = {"Matricule", "Nom", "Prénom", "Email", "Téléphone",
                "Classe", "Niveau", "Filière", "Année", "Semestre",
                "Statut", "Décision", "Actif"};
        int headerRowNum = 4;
        Row headerRow = sheet.createRow(headerRowNum);
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = headerRowNum + 1;
        for (EtudiantExportDTO e : etudiants) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getMatricule() != null ? e.getMatricule() : "");
            row.createCell(1).setCellValue(e.getNom() != null ? e.getNom() : "");
            row.createCell(2).setCellValue(e.getPrenom() != null ? e.getPrenom() : "");
            row.createCell(3).setCellValue(e.getEmail() != null ? e.getEmail() : "");
            row.createCell(4).setCellValue(e.getTelephone() != null ? e.getTelephone() : "");
            row.createCell(5).setCellValue(e.getClasseNom() != null ? e.getClasseNom() : "");
            row.createCell(6).setCellValue(e.getNiveauNom() != null ? e.getNiveauNom() : "");
            row.createCell(7).setCellValue(e.getFiliereNom() != null ? e.getFiliereNom() : "");
            row.createCell(8).setCellValue(e.getAnneeAcademique() != null ? e.getAnneeAcademique() : "");
            row.createCell(9).setCellValue(e.getSemestre() != null ? e.getSemestre() : "");
            row.createCell(10).setCellValue(e.getStatutInscription() != null ? e.getStatutInscription() : "");
            row.createCell(11).setCellValue(e.getDecisionFinAnnee() != null ? e.getDecisionFinAnnee() : "");
            row.createCell(12).setCellValue(e.isActive() ? "Oui" : "Non");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    @Override
    public byte[] exportEtudiants(List<EtudiantExportDTO> etudiants) throws IOException {
        return exportEtudiants(etudiants, null, null);
    }

    // ══════════════════════════════════════════════
    // MODÈLES
    // ══════════════════════════════════════════════

    @Override
    public byte[] genererModeleUtilisateur(Annee_academique annee, Semestre semestre) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Modele");
        addAppHeader(sheet, annee, semestre, "Modele d'import Utilisateurs", 0);

        String[] headers = {"Nom*", "Prénom*", "Email*", "Téléphone*", "Date naissance* (AAAA-MM-JJ)",
                "Type* (ENS/AST/SUR)", "Grade", "Fonction", "Secteur"};
        Row headerRow = sheet.createRow(4);
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Row exemple = sheet.createRow(5);
        exemple.createCell(0).setCellValue("Dupont");
        exemple.createCell(1).setCellValue("Jean");
        exemple.createCell(2).setCellValue("jean.dupont@ecole.com");
        exemple.createCell(3).setCellValue("0601020304");
        exemple.createCell(4).setCellValue("1985-06-15");
        exemple.createCell(5).setCellValue("ENS");
        exemple.createCell(6).setCellValue("Professeur");

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    @Override
    public byte[] genererModeleUtilisateur() throws IOException {
        return genererModeleUtilisateur(null, null);
    }

    @Override
    public byte[] genererModeleEtudiant(Annee_academique annee, Semestre semestre) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Modele");
        addAppHeader(sheet, annee, semestre, "Modele d'import Etudiants", 0);

        String[] headers = {"Matricule", "Nom*", "Prénom*", "Email*", "Téléphone*",
                "Date naissance* (AAAA-MM-JJ)", "Classe ID*"};
        Row headerRow = sheet.createRow(4);
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Row exemple = sheet.createRow(5);
        exemple.createCell(0).setCellValue("ETU-2025-001");
        exemple.createCell(1).setCellValue("Diallo");
        exemple.createCell(2).setCellValue("Fatou");
        exemple.createCell(3).setCellValue("fatou.diallo@ecole.com");
        exemple.createCell(4).setCellValue("0701020304");
        exemple.createCell(5).setCellValue("2002-08-12");
        exemple.createCell(6).setCellValue("5");

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    @Override
    public byte[] genererModeleEtudiant() throws IOException {
        return genererModeleEtudiant(null, null);
    }

    // ══════════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════════

    private void addAppHeader(Sheet sheet, Annee_academique annee, Semestre semestre, String titre, int nbElements) {
        Row row0 = sheet.createRow(0);
        Cell cell0 = row0.createCell(0);
        cell0.setCellValue(APP_NAME);
        cell0.setCellStyle(createTitleStyle(sheet.getWorkbook()));

        Row row1 = sheet.createRow(1);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue(titre);
        cell1.setCellStyle(createSubtitleStyle(sheet.getWorkbook()));

        Row row2 = sheet.createRow(2);
        String anneeStr = annee != null ? annee.getNom() : "N/A";
        String semestreStr = semestre != null ? semestre.getTypeSemestre().getLibelle() : "N/A";
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue("Annee : " + anneeStr + "  |  Semestre : " + semestreStr +
                (nbElements > 0 ? "  |  Elements : " + nbElements : "") +
                "  |  Exporte le : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    private CellStyle createTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.INDIGO.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createSubtitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        return style;
    }

    @Override
    public CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    @Override
    public String getSpecificField(UtilisateurExportDTO u) {
        if ("ENS".equals(u.getType()) && u.getGrade() != null) return u.getGrade();
        if ("AST".equals(u.getType()) && u.getFonction() != null) return u.getFonction();
        if ("SUR".equals(u.getType()) && u.getSecteur() != null) return u.getSecteur();
        return "";
    }
}