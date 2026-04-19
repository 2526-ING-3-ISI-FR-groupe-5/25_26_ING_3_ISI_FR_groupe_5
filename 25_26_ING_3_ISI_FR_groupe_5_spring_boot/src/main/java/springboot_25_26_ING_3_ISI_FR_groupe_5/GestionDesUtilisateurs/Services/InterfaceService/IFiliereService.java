package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.filiere.FiliereRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface IFiliereService {

    Filiere creer(FiliereRequest request, Utilisateur acteur);

    Filiere modifier(Long id, FiliereRequest request, Utilisateur acteur);

    Filiere findById(Long id);

    List<Filiere> getAll();

    List<Filiere> getByEcole(Long ecoleId);

    List<Filiere> getByCycle(Long cycleId);

    List<Filiere> getByEcoleAndCycle(Long ecoleId, Long cycleId);

    List<Filiere> searchByNom(String nom);

    boolean existsByCodeAndEcole(String code, Long ecoleId);

    void supprimer(Long id, Utilisateur acteur);

    long count();
}