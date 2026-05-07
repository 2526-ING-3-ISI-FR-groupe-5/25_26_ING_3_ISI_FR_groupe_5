package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireResponse;

import java.time.LocalDate;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

public interface PlageHoraireService {

    // CRUD
    PlageHoraireResponse creer(PlageHoraireRequest request);
    PlageHoraireResponse modifier(Long id, PlageHoraireRequest request);
    void supprimer(Long id);
    PlageHoraireResponse findById(Long id);

    // EDT par classe
    List<PlageHoraireResponse> getEdtClasse(Long classeId);
    List<PlageHoraireResponse> getEdtClasseSemaine(Long classeId, LocalDate debut, LocalDate fin);

    // EDT par enseignant
    List<PlageHoraireResponse> getEdtEnseignant(Long enseignantId);
    List<PlageHoraireResponse> getEdtEnseignantSemaine(Long enseignantId, LocalDate debut, LocalDate fin);

    // Pour FullCalendar
    List<PlageHoraireResponse> getEdtClassePourCalendrier(Long classeId, LocalDate debut, LocalDate fin);
}