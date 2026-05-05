package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.PlageHoraireRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.SessionAppelRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionAppelService {
    private final PlageHoraireRepository plageHoraireRepository;
    private final SessionAppelRepository sessionAppelRepository;
    private final PlageHoraireService plageHoraireService;
    private final EnseignantService enseignantService;

    // ══════════════════════════════════════════
    // GET
    // ══════════════════════════════════════════

    public SessionAppel getSessionActivePourClasse(Long classeId) {
        List<PlageHoraire> plages = plageHoraireRepository.findByClasseId(classeId);
        if (plages == null || plages.isEmpty()) return null;

        for (PlageHoraire plage : plages) {
            var sessionOpt = sessionAppelRepository.findByPlageHoraireIdAndActifTrue(plage.getId());
            if (sessionOpt.isPresent()) {
                return sessionOpt.get();
            }
        }
        return null;
    }


    @Transactional(readOnly = true)
    public SessionAppel findById(Long id) {
        return sessionAppelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session d'appel introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<SessionAppel> getByPlage(Long plageHoraireId) {
        return sessionAppelRepository.findByPlageHoraireId(plageHoraireId);
    }

    @Transactional(readOnly = true)
    public SessionAppel getSessionActive(Long plageHoraireId) {
        return sessionAppelRepository.findByPlageHoraireIdAndActifTrue(plageHoraireId)
                .orElseThrow(() -> new RuntimeException("Aucune session active pour cette plage."));
    }

    // ══════════════════════════════════════════
    // POST — Créer une session
    // ══════════════════════════════════════════

    public SessionAppel creer(SessionAppelRequest req, Long enseignantId) {
        PlageHoraire plage = plageHoraireService.findEntityById(req.getPlageHoraireId());

        Enseignant enseignant = enseignantService.findById(enseignantId);

        // Désactiver toute session active existante sur la même plage
        sessionAppelRepository.findByPlageHoraireIdAndActifTrue(req.getPlageHoraireId())
                .ifPresent(s -> { s.setActif(false); sessionAppelRepository.save(s); });

        int duree = req.getDureeMinutes() != null ? req.getDureeMinutes() : 3;

        SessionAppel session = SessionAppel.builder()
                .plageHoraire(plage)
                .enseignant(enseignant)
                .methode(req.getMethode())
                .code(genererCode(req.getMethode()))
                .dateGeneration(LocalDateTime.now())
                .dateExpiration(LocalDateTime.now().plusMinutes(duree))
                .actif(true)
                .coursTermine(false)
                .latitudeEnseignant(req.getLatitudeEnseignant())
                .longitudeEnseignant(req.getLongitudeEnseignant())
                .perimetreMetres(req.getPerimetreMetres())
                .build();

        return sessionAppelRepository.save(session);
    }

    // ══════════════════════════════════════════
    // PUT — Terminer le cours
    // ══════════════════════════════════════════

    public SessionAppel terminerCours(Long sessionId) {
        SessionAppel session = findById(sessionId);
        session.setCoursTermine(true);
        session.setActif(false);
        session.setHeureFinReelle(LocalDateTime.now());
        return sessionAppelRepository.save(session);
    }

    // ══════════════════════════════════════════
    // PUT — Renouveler le code (QR/PIN expiré)
    // ══════════════════════════════════════════

    public SessionAppel renouvelerCode(Long sessionId, int dureeMinutes) {
        SessionAppel session = findById(sessionId);
        if (session.isCoursTermine()) throw new RuntimeException("Le cours est déjà terminé.");
        session.setCode(genererCode(session.getMethode()));
        session.setDateGeneration(LocalDateTime.now());
        session.setDateExpiration(LocalDateTime.now().plusMinutes(dureeMinutes));
        session.setActif(true);
        return sessionAppelRepository.save(session);
    }

    // ══════════════════════════════════════════
    // PRIVÉ — Générateur de code
    // ══════════════════════════════════════════

    private String genererCode(MethodeValidation methode) {
        return switch (methode) {
            case QR_CODE  -> UUID.randomUUID().toString();
            case CODE_PIN -> String.format("%06d", new SecureRandom().nextInt(999999));
            default       -> null;
        };
    }

}