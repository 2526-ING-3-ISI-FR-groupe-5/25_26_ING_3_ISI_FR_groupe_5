package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.web.multipart.MultipartFile;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.imports.ImportResultDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.EmailAlreadyUsedException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ImportException;

import java.io.IOException;

public interface IImportExcelService {

    springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.imports.ImportResultDTO importerUtilisateurs(MultipartFile file,
                                                                                                                   Annee_academique annee, Semestre semestre)
            throws IOException, ImportException, EmailAlreadyUsedException;

    ImportResultDTO importerEtudiants(MultipartFile file,
                                      Annee_academique annee, Semestre semestre)
            throws IOException, ImportException, EmailAlreadyUsedException;
}