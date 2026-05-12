package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.ISessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.PlageHoraireRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.SessionAppelRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.PlageHoraireService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EnseignantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionAppelService implements ISessionAppelService {
    private final PlageHoraireRepository plageHoraireRepository;
    private final SessionAppelRepository sessionAppelRepository;
    private final AppelsRepository appelsRepository;
    private final EtudiantRepository etudiantRepository;
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

        SessionAppel savedSession = sessionAppelRepository.save(session);
        initialiserAppelsEtudiants(savedSession);
        return savedSession;
    }

    // ══════════════════════════════════════════
    // PUT — Terminer le cours
    // ══════════════════════════════════════════

    public SessionAppel terminerCours(Long sessionId) {
        SessionAppel session = findById(sessionId);
        cloturerAppelsEnAttente(session);
        session.setCoursTermine(true);
        session.setActif(false);
        session.setHeureFinReelle(LocalDateTime.now());
        return sessionAppelRepository.save(session);
    }

    public SessionAppel arreterSession(Long sessionId) {
        SessionAppel session = findById(sessionId);
        session.setActif(false);
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

    private void initialiserAppelsEtudiants(SessionAppel session) {
        if (session.getPlageHoraire() == null || session.getPlageHoraire().getClasse() == null) {
            return;
        }

        Long classeId = session.getPlageHoraire().getClasse().getId();
        etudiantRepository.findByClasseIdAndActiveTrue(classeId).forEach(etudiant -> {
            appelsRepository.findByEtudiantIdAndPlageHoraireId(
                    etudiant.getId(), session.getPlageHoraire().getId()
            ).ifPresentOrElse(appel -> {
                appel.setSessionAppel(session);
                if (appel.getEnseignant() == null) appel.setEnseignant(session.getEnseignant());
                appelsRepository.save(appel);
            }, () -> appelsRepository.save(Appels.builder()
                            .etudiant(etudiant)
                            .plageHoraire(session.getPlageHoraire())
                            .enseignant(session.getEnseignant())
                            .sessionAppel(session)
                            .statut(StatutPresence.EN_ATTENTE)
                            .synchronise(true)
                            .build()));
        });
    }

    private void cloturerAppelsEnAttente(SessionAppel session) {
        appelsRepository.findBySessionAppelIdAndStatut(session.getId(), StatutPresence.EN_ATTENTE)
                .forEach(appel -> {
                    appel.marquerAbsent(session.getEnseignant());
                    appelsRepository.save(appel);
                });
    }

    @Transactional
    public void supprimer(Long id) {
        SessionAppel session = sessionAppelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session d'appel non trouvée avec l'ID : " + id));

        // Optionnel : Supprimer les appels associés
        List<Appels> appels = appelsRepository.findBySessionAppelId(id);
        if (!appels.isEmpty()) {
            appelsRepository.deleteAll(appels);
            log.info("{} appels supprimés pour la session {}", appels.size(), id);
        }

        // Supprimer la session
        sessionAppelRepository.delete(session);
        log.warn("Session d'appel {} supprimée définitivement avec tous ses appels", id);
    }
}
