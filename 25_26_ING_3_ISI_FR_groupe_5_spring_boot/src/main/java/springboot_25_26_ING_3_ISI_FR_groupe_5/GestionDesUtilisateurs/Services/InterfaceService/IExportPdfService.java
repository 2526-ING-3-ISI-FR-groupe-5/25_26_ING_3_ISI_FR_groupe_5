package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.EtudiantExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.UtilisateurExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;

import java.io.IOException;
import java.util.List;

public interface IExportPdfService {

    byte[] exportUtilisateurs(List<UtilisateurExportDTO> utilisateurs,
                              Annee_academique annee, Semestre semestre)
            throws IOException;

    byte[] exportEtudiants(List<EtudiantExportDTO> etudiants,
                           Annee_academique annee, Semestre semestre)
            throws IOException;
}