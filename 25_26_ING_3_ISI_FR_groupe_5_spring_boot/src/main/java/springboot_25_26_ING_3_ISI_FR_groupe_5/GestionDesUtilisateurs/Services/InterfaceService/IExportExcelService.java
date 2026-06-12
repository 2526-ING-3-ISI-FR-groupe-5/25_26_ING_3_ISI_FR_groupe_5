package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.EtudiantExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.UtilisateurExportDTO;

import java.io.IOException;
import java.util.List;

public interface IExportExcelService {
    byte[] exportUtilisateurs(List<UtilisateurExportDTO> utilisateurs,
                              Annee_academique annee, Semestre semestre) throws IOException;

    // ══════════════════════════════════════════════
    // EXPORT UTILISATEURS (ENS, AST, SUR)
    // ══════════════════════════════════════════════
    byte[] exportUtilisateurs(List<UtilisateurExportDTO> utilisateurs) throws IOException;

    byte[] exportEtudiants(List<EtudiantExportDTO> etudiants,
                           Annee_academique annee, Semestre semestre) throws IOException;

    // ══════════════════════════════════════════════
    // EXPORT ÉTUDIANTS
    // ══════════════════════════════════════════════
    byte[] exportEtudiants(List<EtudiantExportDTO> etudiants) throws IOException;

    byte[] genererModeleUtilisateur(Annee_academique annee, Semestre semestre) throws IOException;

    // ══════════════════════════════════════════════
    // MODÈLE EXCEL (TEMPLATE)
    // ══════════════════════════════════════════════
    byte[] genererModeleUtilisateur() throws IOException;

    byte[] genererModeleEtudiant(Annee_academique annee, Semestre semestre) throws IOException;

    byte[] genererModeleEtudiant() throws IOException;

    // ══════════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════════
    CellStyle createHeaderStyle(Workbook workbook);

    String getSpecificField(UtilisateurExportDTO u);
}
