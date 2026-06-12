package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.EtudiantExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.UtilisateurExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.imports.ImportResultDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.ExportMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IExportExcelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IExportPdfService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IImportExcelService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/export-import")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
public class ExportImportController {

    private final IExportExcelService exportExcelService;
    private final IExportPdfService exportPdfService;
    private final IImportExcelService importExcelService;
    private final AnneeAcademiqueService anneeService;
    private final InstitutSecurityService securityService;
    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final ExportMapper exportMapper;

    // ══════════════════════════════════════════
    // PAGE PRINCIPALE
    // ══════════════════════════════════════════

    @GetMapping
    public String pageExportImport(Model model) {
        Long institutId = securityService.getInstitutIdCourant();
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutId);
        List<Annee_academique> annees = anneeService.getByInstitut(institutId);
        Semestre semestreActif = anneeService.getSemestreActif(institutId);

        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("semestreActif", semestreActif);
        model.addAttribute("annees", annees);
        return "export-import/index";
    }

    // ══════════════════════════════════════════
    // TÉLÉCHARGER MODÈLE
    // ══════════════════════════════════════════

    @GetMapping("/modele/utilisateur")
    public ResponseEntity<byte[]> telechargerModeleUtilisateur(
            @RequestParam(required = false) Long anneeId) throws IOException {
        Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
        Semestre semestre = securityService.getInstitutIdCourant() != null
                ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

        byte[] data = exportExcelService.genererModeleUtilisateur(annee, semestre);
        return buildExcelResponse(data, "modele_import_utilisateurs.xlsx");
    }

    @GetMapping("/modele/etudiant")
    public ResponseEntity<byte[]> telechargerModeleEtudiant(
            @RequestParam(required = false) Long anneeId) throws IOException {
        Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
        Semestre semestre = securityService.getInstitutIdCourant() != null
                ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

        byte[] data = exportExcelService.genererModeleEtudiant(annee, semestre);
        return buildExcelResponse(data, "modele_import_etudiants.xlsx");
    }

    // ══════════════════════════════════════════
    // EXPORT EXCEL
    // ══════════════════════════════════════════

    @GetMapping("/excel/utilisateurs")
    public ResponseEntity<byte[]> exportExcelUtilisateurs(
            @RequestParam(required = false) Long anneeId) throws IOException {
        Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
        Semestre semestre = securityService.getInstitutIdCourant() != null
                ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

        Long institutId = securityService.getInstitutIdCourant();
        List<Utilisateur> utilisateurs = (institutId != null)
                ? utilisateurRepository.findByInstitutId(institutId)
                : utilisateurRepository.findAll();

        List<UtilisateurExportDTO> data = exportMapper.toExportDTOList(utilisateurs);
        byte[] excel = exportExcelService.exportUtilisateurs(data, annee, semestre);
        return buildExcelResponse(excel, "utilisateurs.xlsx");
    }

    @GetMapping("/excel/etudiants")
    public ResponseEntity<byte[]> exportExcelEtudiants(
            @RequestParam(required = false) Long anneeId) throws IOException {
        Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
        Semestre semestre = securityService.getInstitutIdCourant() != null
                ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

        Long institutId = securityService.getInstitutIdCourant();
        List<Etudiant> etudiants = (institutId != null)
                ? etudiantRepository.findByInstitutId(institutId)
                : etudiantRepository.findAll();

        List<EtudiantExportDTO> data = exportMapper.toEtudiantExportDTOList(etudiants);
        byte[] excel = exportExcelService.exportEtudiants(data, annee, semestre);
        return buildExcelResponse(excel, "etudiants.xlsx");
    }

    // ══════════════════════════════════════════
    // EXPORT PDF
    // ══════════════════════════════════════════

    @GetMapping("/pdf/utilisateurs")
    public ResponseEntity<byte[]> exportPdfUtilisateurs(
            @RequestParam(required = false) Long anneeId) throws IOException {
        Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
        Semestre semestre = securityService.getInstitutIdCourant() != null
                ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

        Long institutId = securityService.getInstitutIdCourant();
        List<Utilisateur> utilisateurs = (institutId != null)
                ? utilisateurRepository.findByInstitutId(institutId)
                : utilisateurRepository.findAll();

        List<UtilisateurExportDTO> data = exportMapper.toExportDTOList(utilisateurs);
        byte[] pdf = exportPdfService.exportUtilisateurs(data, annee, semestre);
        return buildPdfResponse(pdf, "utilisateurs.pdf");
    }

    @GetMapping("/pdf/etudiants")
    public ResponseEntity<byte[]> exportPdfEtudiants(
            @RequestParam(required = false) Long anneeId) throws IOException {
        Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
        Semestre semestre = securityService.getInstitutIdCourant() != null
                ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

        Long institutId = securityService.getInstitutIdCourant();
        List<Etudiant> etudiants = (institutId != null)
                ? etudiantRepository.findByInstitutId(institutId)
                : etudiantRepository.findAll();

        List<EtudiantExportDTO> data = exportMapper.toEtudiantExportDTOList(etudiants);
        byte[] pdf = exportPdfService.exportEtudiants(data, annee, semestre);
        return buildPdfResponse(pdf, "etudiants.pdf");
    }

    // ══════════════════════════════════════════
    // IMPORT EXCEL
    // ══════════════════════════════════════════

    @PostMapping("/import/utilisateurs")
    public String importerUtilisateurs(
            @RequestParam MultipartFile fichier,
            @RequestParam(required = false) Long anneeId,
            RedirectAttributes ra) {
        try {
            Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
            Semestre semestre = securityService.getInstitutIdCourant() != null
                    ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

            ImportResultDTO resultat = importExcelService.importerUtilisateurs(fichier, annee, semestre);
            ra.addFlashAttribute("success",
                    String.format("%d importés, %d ignorés, %d erreurs",
                            resultat.getLignesImportees(), resultat.getLignesIgnorees(), resultat.getLignesErreurs()));
            if (!resultat.getErreurs().isEmpty()) {
                ra.addFlashAttribute("importErreurs", resultat.getErreurs());
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur import : " + e.getMessage());
        }
        return "redirect:/admin/export-import";
    }

    @PostMapping("/import/etudiants")
    public String importerEtudiants(
            @RequestParam MultipartFile fichier,
            @RequestParam(required = false) Long anneeId,
            RedirectAttributes ra) {
        try {
            Annee_academique annee = anneeId != null ? anneeService.findById(anneeId) : null;
            Semestre semestre = securityService.getInstitutIdCourant() != null
                    ? anneeService.getSemestreActif(securityService.getInstitutIdCourant()) : null;

            ImportResultDTO resultat = importExcelService.importerEtudiants(fichier, annee, semestre);
            ra.addFlashAttribute("success",
                    String.format("%d importés, %d ignorés, %d erreurs",
                            resultat.getLignesImportees(), resultat.getLignesIgnorees(), resultat.getLignesErreurs()));
            if (!resultat.getErreurs().isEmpty()) {
                ra.addFlashAttribute("importErreurs", resultat.getErreurs());
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur import : " + e.getMessage());
        }
        return "redirect:/admin/export-import";
    }

    // ══════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════

    private ResponseEntity<byte[]> buildExcelResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}