package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;

import java.util.List;

public interface ISessionAppelService {

    // ══════════════════════════════════════════
    // RECHERCHE
    // ══════════════════════════════════════════

    SessionAppel findById(Long id);
    List<SessionAppel> getByPlage(Long plageHoraireId);
    SessionAppel getSessionActive(Long plageHoraireId);
    SessionAppel getSessionActivePourClasse(Long classeId);
    SessionAppel getSessionOfflineActive(Long classeId);

    // ══════════════════════════════════════════
    // CRÉATION ET GESTION
    // ══════════════════════════════════════════

    SessionAppel creer(SessionAppelRequest req, Long enseignantId);
    SessionAppel creerSessionOffline(Long plageHoraireId, Long enseignantId);

    // ✅ Signatures mises à jour avec enseignantId pour les contrôles de sécurité
    SessionAppel terminerCours(Long sessionId, Long enseignantId);
    SessionAppel arreterSession(Long sessionId, Long enseignantId);

    SessionAppel renouvelerCode(Long sessionId, int dureeMinutes, Long enseignantId);
    void supprimer(Long id);
}