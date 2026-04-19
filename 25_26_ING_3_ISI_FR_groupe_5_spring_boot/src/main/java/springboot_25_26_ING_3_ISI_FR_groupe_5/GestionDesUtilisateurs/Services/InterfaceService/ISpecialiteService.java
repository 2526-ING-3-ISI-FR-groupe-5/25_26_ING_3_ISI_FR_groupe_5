package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface ISpecialiteService {

    Specialite creer(Specialite specialite, Utilisateur acteur);

    Specialite modifier(Long id, Specialite data, Utilisateur acteur);

    Specialite findById(Long id);

    List<Specialite> getAll();

    List<Specialite> getByFiliere(Long filiereId);

    void supprimer(Long id, Utilisateur acteur);
}