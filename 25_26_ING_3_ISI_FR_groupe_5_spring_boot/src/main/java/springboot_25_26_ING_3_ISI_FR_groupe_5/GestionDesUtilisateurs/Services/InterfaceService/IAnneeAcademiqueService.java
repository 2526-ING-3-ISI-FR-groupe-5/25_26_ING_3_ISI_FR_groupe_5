package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDate;
import java.util.List;

public interface IAnneeAcademiqueService {

    Annee_academique creer(String nom, LocalDate dateDebut, LocalDate dateFin, boolean active, Utilisateur acteur);

    Annee_academique modifier(Long id, String nom, LocalDate dateDebut, LocalDate dateFin, boolean active, Utilisateur acteur);

    Annee_academique activer(Long anneeId, Utilisateur acteur);

    Annee_academique getAnneeActive();

    List<Annee_academique> getAll();

    Annee_academique findById(Long id);

    void supprimer(Long anneeId, Utilisateur acteur);
}