package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.imports.ImportResultDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeContrat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.EmailAlreadyUsedException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ImportException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IImportExcelService;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExcelService implements IImportExcelService {

    private final UtilisateurRepository utilisateurRepository;
    private final EnseignantRepository enseignantRepository;
    private final AssistantRepository assistantRepository;
    private final SurveillantRepository surveillantRepository;
    private final EtudiantRepository etudiantRepository;
    private final RoleRepository roleRepository;
    private final ClassesRepository classesRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstitutSecurityService securityService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ══════════════════════════════════════════════
    // IMPORT UTILISATEURS
    // ══════════════════════════════════════════════

    @Override
    @Transactional
    public ImportResultDTO importerUtilisateurs(MultipartFile file, Annee_academique annee, Semestre semestre)
            throws IOException, ImportException, EmailAlreadyUsedException {

        ImportResultDTO resultat = new ImportResultDTO();
        Institut institut = securityService.getInstitutCourant();
        Role roleEnseignant = roleRepository.findByNom("ENSEIGNANT")
                .orElseThrow(() -> new ImportException("Rôle ENSEIGNANT introuvable"));
        Role roleAssistant = roleRepository.findByNom("ASSISTANT")
                .orElseThrow(() -> new ImportException("Rôle ASSISTANT introuvable"));
        Role roleSurveillant = roleRepository.findByNom("SURVEILLANT")
                .orElseThrow(() -> new ImportException("Rôle SURVEILLANT introuvable"));

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            resultat.setTotalLignes(sheet.getLastRowNum()); // -1 pour l'en-tête

            for (int i = 6; i <= sheet.getLastRowNum(); i++) { // Ligne 0-3 = en-tête app, 4 = colonnes, 5 = exemple, 6+ = données
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    resultat.setLignesIgnorees(resultat.getLignesIgnorees() + 1);
                    continue;
                }

                try {
                    String nom = getCellValue(row, 0);
                    String prenom = getCellValue(row, 1);
                    String email = getCellValue(row, 2);
                    String telephone = getCellValue(row, 3);
                    String dateNaissanceStr = getCellValue(row, 4);
                    String type = getCellValue(row, 5);

                    if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || type.isEmpty()) {
                        throw new ImportException("Champs obligatoires manquants (ligne " + (i + 1) + ")");
                    }

                    if (utilisateurRepository.existsByEmail(email)) {
                        resultat.setLignesIgnorees(resultat.getLignesIgnorees() + 1);
                        resultat.getErreurs().add("Email déjà utilisé : " + email + " (ligne " + (i + 1) + ")");
                        continue;
                    }

                    LocalDate dateNaissance = parseDate(dateNaissanceStr);
                    String motDePasseBrut = genererMotDePasse();
                    String motDePasseEncode = passwordEncoder.encode(motDePasseBrut);

                    Utilisateur utilisateur = switch (type.toUpperCase()) {
                        case "ENS" -> {
                            Enseignant ens = new Enseignant();
                            ens.setGrade(getCellValue(row, 6));
                            ens.setTypeEnseignant(getCellValue(row, 7));
                            ens.setRoles(new HashSet<>(Set.of(roleEnseignant)));
                            yield ens;
                        }
                        case "AST" -> {
                            AssistantPedagogique ast = new AssistantPedagogique();
                            ast.setFonction(getCellValue(row, 7));
                            ast.setRoles(new HashSet<>(Set.of(roleAssistant)));
                            yield ast;
                        }
                        case "SUR" -> {
                            Surveillant sur = new Surveillant();
                            sur.setSecteur(getCellValue(row, 7));
                            sur.setTypeContrat(parseTypeContrat(getCellValue(row, 8)));
                            sur.setRoles(new HashSet<>(Set.of(roleSurveillant)));
                            yield sur;
                        }
                        default -> throw new ImportException("Type invalide : " + type + " (ligne " + (i + 1) + ")");
                    };

                    utilisateur.setNom(nom);
                    utilisateur.setPrenom(prenom);
                    utilisateur.setEmail(email);
                    utilisateur.setTelephone(telephone);
                    utilisateur.setDateNaissance(dateNaissance);
                    utilisateur.setPassword(motDePasseEncode);
                    utilisateur.setActive(true);
                    utilisateur.setFirstLogin(true);
                    utilisateur.setInstitut(institut);

                    utilisateurRepository.save(utilisateur);
                    resultat.setLignesImportees(resultat.getLignesImportees() + 1);

                } catch (ImportException e) {
                    resultat.setLignesErreurs(resultat.getLignesErreurs() + 1);
                    resultat.getErreurs().add(e.getMessage());
                } catch (Exception e) {
                    resultat.setLignesErreurs(resultat.getLignesErreurs() + 1);
                    resultat.getErreurs().add("Erreur ligne " + (i + 1) + " : " + e.getMessage());
                }
            }
        }

        return resultat;
    }

    // ══════════════════════════════════════════════
    // IMPORT ÉTUDIANTS
    // ══════════════════════════════════════════════

    @Override
    @Transactional
    public ImportResultDTO importerEtudiants(MultipartFile file, Annee_academique annee, Semestre semestre)
            throws IOException, ImportException, EmailAlreadyUsedException {

        ImportResultDTO resultat = new ImportResultDTO();
        Institut institut = securityService.getInstitutCourant();
        Role roleEtudiant = roleRepository.findByNom("ETUDIANT")
                .orElseThrow(() -> new ImportException("Rôle ETUDIANT introuvable"));

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            resultat.setTotalLignes(sheet.getLastRowNum());

            for (int i = 6; i <= sheet.getLastRowNum(); i++) { // Ligne 4 = colonnes, 5 = exemple, 6+ = données
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    resultat.setLignesIgnorees(resultat.getLignesIgnorees() + 1);
                    continue;
                }

                try {
                    String matricule = getCellValue(row, 0);
                    String nom = getCellValue(row, 1);
                    String prenom = getCellValue(row, 2);
                    String email = getCellValue(row, 3);
                    String telephone = getCellValue(row, 4);
                    String dateNaissanceStr = getCellValue(row, 5);
                    String classeIdStr = getCellValue(row, 6);

                    if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || classeIdStr.isEmpty()) {
                        throw new ImportException("Champs obligatoires manquants (ligne " + (i + 1) + ")");
                    }

                    if (utilisateurRepository.existsByEmail(email)) {
                        resultat.setLignesIgnorees(resultat.getLignesIgnorees() + 1);
                        resultat.getErreurs().add("Email déjà utilisé : " + email + " (ligne " + (i + 1) + ")");
                        continue;
                    }

                    Long classeId = Long.parseLong(classeIdStr);
                    int finalI = i;
                    Classe classe = classesRepository.findById(classeId)
                            .orElseThrow(() -> new ImportException("Classe introuvable : " + classeId + " (ligne " + (finalI + 1) + ")"));

                    LocalDate dateNaissance = parseDate(dateNaissanceStr);
                    String motDePasseBrut = genererMotDePasse();
                    String motDePasseEncode = passwordEncoder.encode(motDePasseBrut);

                    Etudiant etudiant = new Etudiant();
                    etudiant.setMatricule(matricule.isEmpty() ? genererMatricule() : matricule);
                    etudiant.setNom(nom);
                    etudiant.setPrenom(prenom);
                    etudiant.setEmail(email);
                    etudiant.setTelephone(telephone);
                    etudiant.setDateNaissance(dateNaissance);
                    etudiant.setPassword(motDePasseEncode);
                    etudiant.setActive(true);
                    etudiant.setFirstLogin(true);
                    etudiant.setInstitut(institut);
                    etudiant.setClasse(classe);
                    etudiant.setRoles(new HashSet<>(Set.of(roleEtudiant)));

                    etudiantRepository.save(etudiant);
                    resultat.setLignesImportees(resultat.getLignesImportees() + 1);

                } catch (ImportException e) {
                    resultat.setLignesErreurs(resultat.getLignesErreurs() + 1);
                    resultat.getErreurs().add(e.getMessage());
                } catch (Exception e) {
                    resultat.setLignesErreurs(resultat.getLignesErreurs() + 1);
                    resultat.getErreurs().add("Erreur ligne " + (i + 1) + " : " + e.getMessage());
                }
            }
        }

        return resultat;
    }

    // ══════════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════════

    private String getCellValue(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMAT);
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (!getCellValue(row, i).isEmpty()) return false;
        }
        return true;
    }

    private LocalDate parseDate(String dateStr) throws ImportException {
        if (dateStr == null || dateStr.isEmpty()) {
            return LocalDate.of(2000, 1, 1); // date par défaut
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ImportException("Format de date invalide : " + dateStr + " (attendu : AAAA-MM-JJ)");
        }
    }

    private TypeContrat parseTypeContrat(String value) {
        if (value == null || value.isEmpty()) return TypeContrat.CDD;
        try {
            return TypeContrat.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TypeContrat.CDD;
        }
    }

    private String genererMotDePasse() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String genererMatricule() {
        String year = String.valueOf(java.time.Year.now().getValue());
        long sequence = System.nanoTime() % 100000;
        return String.format("ETU-%s-%05d", year, Math.abs(sequence));
    }


}