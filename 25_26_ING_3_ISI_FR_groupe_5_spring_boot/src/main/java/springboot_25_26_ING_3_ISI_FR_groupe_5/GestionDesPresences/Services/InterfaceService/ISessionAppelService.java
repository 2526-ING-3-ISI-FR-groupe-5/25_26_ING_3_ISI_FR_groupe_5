package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;

import java.util.List;

public interface ISessionAppelService {

    // Recherche
    SessionAppel findById(Long id);
    List<SessionAppel> getByPlage(Long plageHoraireId);
    SessionAppel getSessionActive(Long plageHoraireId);
    SessionAppel getSessionActivePourClasse(Long classeId);

    // Création et gestion
    SessionAppel creer(SessionAppelRequest req, Long enseignantId);
    SessionAppel terminerCours(Long sessionId);
    SessionAppel arreterSession(Long sessionId);
    SessionAppel renouvelerCode(Long sessionId, int dureeMinutes);
}
