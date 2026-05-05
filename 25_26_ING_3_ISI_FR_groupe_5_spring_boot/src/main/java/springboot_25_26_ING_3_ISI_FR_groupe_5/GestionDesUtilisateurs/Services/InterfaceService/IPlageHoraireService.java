package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDate;
import java.util.List;

public interface IPlageHoraireService {

    // Consultation
    PlageHoraireResponse findById(Long id);
    List<PlageHoraireResponse> getByClasse(Long classeId);
    List<PlageHoraireResponse> getByClasseAndSemestre(Long classeId, Long semestreId);
    List<PlageHoraireResponse> getByClasseAndSemaine(Long classeId, LocalDate debut, LocalDate fin);
    List<PlageHoraireResponse> getByEnseignant(Long enseignantId);
    List<PlageHoraireResponse> getByEnseignantAndSemestre(Long enseignantId, Long semestreId);
    List<PlageHoraireResponse> getCoursAujourdhui(Long classeId);
    List<PlageHoraireResponse> getCoursEnseignantAujourdhui(Long enseignantId);

    // Création
    PlageHoraireResponse creer(PlageHoraireRequest request, Utilisateur auteur);
    List<PlageHoraireResponse> creerRecurrence(PlageHoraireRecurrenceRequest request, Utilisateur auteur);
    PlageHoraireResponse creerParDragDrop(PlageHoraireDragDropRequest request, Utilisateur auteur);

    // Modification
    PlageHoraireResponse modifier(Long id, PlageHoraireRequest request, Utilisateur auteur);
    PlageHoraireResponse affecterEnseignants(Long id, List<Long> enseignantIds, Utilisateur auteur);
    PlageHoraireResponse deplacer(Long id, PlageHoraireDragDropRequest request, Utilisateur auteur);

    // Suppression
    void supprimer(Long id, Utilisateur auteur);
    void supprimerParProgrammationUE(Long programmationUEId, Utilisateur auteur);

    // Statistiques
    long getTotalDureeMinutes(Long classeId, Long semestreId);
    long getTotalCours(Long classeId, Long semestreId);

    // Méthode interne
    PlageHoraire findEntityById(Long id);

    @Transactional(readOnly = true)
    List<PlageHoraireResponse> getByEnseignantAndSemaine(
            Long enseignantId, LocalDate debut, LocalDate fin);
}