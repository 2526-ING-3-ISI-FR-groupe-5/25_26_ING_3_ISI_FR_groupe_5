package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSession;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.SessionAppelRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EnseignantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionAppelService {

    private final SessionAppelRepository sessionAppelRepository;
    private final AppelsRepository appelsRepository;
    private final EtudiantRepository etudiantRepository;
    private final PlageHoraireService plageHoraireService;
    private final EnseignantService enseignantService;
    private final QRCodeService qrCodeService;

    private static final String CHARSET_OFFLINE = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    // ══════════════════════════════════════════
    // GET
    // ══════════════════════════════════════════

    @Transactional(readOnly = true)
    public SessionAppel findById(Long id) {
        return sessionAppelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session introuvable : " + id));
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

    @Transactional(readOnly = true)
    public SessionAppel getSessionActivePourClasse(Long classeId) {
        return sessionAppelRepository.findActiveByClasseId(classeId).orElse(null);
    }

    @Transactional(readOnly = true)
    public SessionAppel getSessionOfflineActive(Long classeId) {
        return sessionAppelRepository.findOfflineActiveByClasseId(classeId).orElse(null);
    }

    // ══════════════════════════════════════════
    // CRÉER SESSION NORMALE
    // ══════════════════════════════════════════

    /**
     * Crée une session normale.
     *
     * ✅ MODIFIÉ — QR_CODE et CODE_PIN utilisent maintenant le MÊME PIN 6 chiffres.
     *
     * AVANT : QR_CODE → UUID aléatoire (ex: "a3f8-...")
     *         CODE_PIN → 6 chiffres (ex: "847291")
     *         → deux codes différents, deux méthodes incompatibles
     *
     * APRÈS : les deux → PIN 6 chiffres (ex: "847291")
     *         Le QR Code encode l'URL complète contenant ce PIN.
     *         L'étudiant peut scanner OU taper le même code.
     *
     * Le QR Code base64 est retourné via session.getQrCodeBase64()
     * pour affichage dans le template enseignant.
     */
    public SessionAppel creer(SessionAppelRequest req, Long enseignantId) {
        PlageHoraire plage = plageHoraireService.findEntityById(req.getPlageHoraireId());
        Enseignant enseignant = enseignantService.findById(enseignantId);

        // Désactiver toute session normale active existante
        sessionAppelRepository.findByPlageHoraireIdAndActifTrue(req.getPlageHoraireId())
                .ifPresent(s -> {
                    s.setActif(false);
                    sessionAppelRepository.save(s);
                    log.info("Session {} desactivee avant creation nouvelle", s.getId());
                });

        int duree = req.getDureeMinutes() != null ? req.getDureeMinutes() : 3;

        // ✅ PIN 6 chiffres — même code pour QR et PIN
        String pin = genererPin();

        SessionAppel session = SessionAppel.builder()
                .plageHoraire(plage)
                .enseignant(enseignant)
                .methode(req.getMethode())
                .typeSession(TypeSession.NORMALE)
                .code(pin)                          // ✅ toujours le PIN
                .dateGeneration(LocalDateTime.now())
                .dateExpiration(LocalDateTime.now().plusMinutes(duree))
                .actif(true)
                .coursTermine(false)
                .latitudeEnseignant(req.getLatitudeEnseignant())
                .longitudeEnseignant(req.getLongitudeEnseignant())
                .perimetreMetres(req.getPerimetreMetres())
                .build();

        SessionAppel saved = sessionAppelRepository.save(session);

        // ✅ Générer le QR Code contenant l'URL avec le PIN
        // Le QR encode : https://monapp.com/etudiant/valider-presence?session=123&pin=847291
        if (req.getMethode() == MethodeValidation.QR_CODE) {
            String qrBase64 = qrCodeService.genererQRCodeSession(saved.getId(), pin);
            saved.setQrCodeBase64(qrBase64);
            saved = sessionAppelRepository.save(saved);
        }

        initialiserAppelsEtudiants(saved);
        log.info("Session {} creee : plage={} methode={} pin={}",
                saved.getId(), plage.getId(), req.getMethode(), pin);
        return saved;
    }

    // ══════════════════════════════════════════
    // CRÉER SESSION OFFLINE
    // ══════════════════════════════════════════

    /**
     * Crée une session offline avec code 8 caractères alphanumériques.
     * Pas d'expiration par temps — expire à la fin du cours.
     * Pas de QR Code — code dicté ou affiché au tableau.
     */
    public SessionAppel creerSessionOffline(Long plageHoraireId, Long enseignantId) {
        PlageHoraire plage = plageHoraireService.findEntityById(plageHoraireId);
        Enseignant enseignant = enseignantService.findById(enseignantId);

        sessionAppelRepository.findOfflineActiveByClasseId(plage.getClasse().getId())
                .ifPresent(s -> {
                    s.setActif(false);
                    sessionAppelRepository.save(s);
                });

        SessionAppel session = SessionAppel.builder()
                .plageHoraire(plage)
                .enseignant(enseignant)
                .methode(MethodeValidation.CODE_PIN)
                .typeSession(TypeSession.OFFLINE)
                .code(genererCodeOffline())
                .dateGeneration(LocalDateTime.now())
                .dateExpiration(null)               // pas d'expiration par temps
                .actif(true)
                .coursTermine(false)
                .latitudeEnseignant(null)
                .longitudeEnseignant(null)
                .perimetreMetres(null)
                .build();

        SessionAppel saved = sessionAppelRepository.save(session);
        initialiserAppelsEtudiants(saved);
        log.info("Session OFFLINE creee : plage={} code={}", plageHoraireId, saved.getCode());
        return saved;
    }

    // ══════════════════════════════════════════
    // ACTIONS
    // ══════════════════════════════════════════

    public SessionAppel terminerCours(Long sessionId) {
        SessionAppel session = findById(sessionId);
        cloturerAppelsEnAttente(session);
        session.setCoursTermine(true);
        session.setActif(false);
        session.setHeureFinReelle(LocalDateTime.now());

        // Terminer aussi la session offline associée
        sessionAppelRepository.findOfflineActiveByClasseId(
                        session.getPlageHoraire().getClasse().getId())
                .ifPresent(offline -> {
                    offline.setCoursTermine(true);
                    offline.setActif(false);
                    offline.setHeureFinReelle(LocalDateTime.now());
                    sessionAppelRepository.save(offline);
                });

        return sessionAppelRepository.save(session);
    }

    public SessionAppel arreterSession(Long sessionId) {
        SessionAppel session = findById(sessionId);
        session.setActif(false);
        return sessionAppelRepository.save(session);
    }

    /**
     * Renouvelle le code d'une session.
     * ✅ MODIFIÉ — génère un nouveau PIN 6 chiffres et un nouveau QR Code si nécessaire.
     */
    public SessionAppel renouvelerCode(Long sessionId, int dureeMinutes, Long enseignantId) {
        SessionAppel session = findById(sessionId);

        if (session.isCoursTermine()) {
            throw new RuntimeException("Le cours est deja termine.");
        }
        if (!session.getEnseignant().getId().equals(enseignantId)) {
            throw new RuntimeException(
                    "Vous ne pouvez renouveler que le code d'une session que vous avez creee.");
        }

        // Nouveau PIN
        String nouveauPin = session.getTypeSession() == TypeSession.OFFLINE
                ? genererCodeOffline()
                : genererPin();

        session.setCode(nouveauPin);
        session.setDateGeneration(LocalDateTime.now());

        if (session.getTypeSession() != TypeSession.OFFLINE) {
            session.setDateExpiration(LocalDateTime.now().plusMinutes(dureeMinutes));
        }

        // Régénérer le QR Code si la méthode est QR_CODE
        if (session.getMethode() == MethodeValidation.QR_CODE) {
            String qrBase64 = qrCodeService.genererQRCodeSession(session.getId(), nouveauPin);
            session.setQrCodeBase64(qrBase64);
        }

        session.setActif(true);
        log.info("Code renouvele session {} : nouveau pin={}", sessionId, nouveauPin);
        return sessionAppelRepository.save(session);
    }

    @Transactional
    public void supprimer(Long id) {
        SessionAppel session = sessionAppelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session introuvable : " + id));

        if (session.isCoursTermine()) {
            throw new RuntimeException(
                    "Impossible de supprimer une session dont le cours est termine.");
        }

        List<Appels> appels = appelsRepository.findBySessionAppelId(id);
        if (!appels.isEmpty()) appelsRepository.deleteAll(appels);

        sessionAppelRepository.delete(session);
        log.warn("Session {} supprimee avec {} appels", id, appels.size());
    }

    // ══════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════

    /**
     * ✅ MODIFIÉ — Un seul générateur de PIN 6 chiffres pour QR et CODE_PIN.
     * QR_CODE n'utilise plus UUID — il encode ce PIN dans l'URL du QR.
     */
    private String genererPin() {
        return String.format("%06d", new SecureRandom().nextInt(999999));
    }

    /**
     * Code offline 8 caractères alphanumériques sans ambiguïté visuelle.
     * (pas de 0/O, 1/I/L)
     */
    private String genererCodeOffline() {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARSET_OFFLINE.charAt(rnd.nextInt(CHARSET_OFFLINE.length())));
        }
        return sb.toString();
    }

    private void initialiserAppelsEtudiants(SessionAppel session) {
        if (session.getPlageHoraire() == null
                || session.getPlageHoraire().getClasse() == null) return;

        Long classeId = session.getPlageHoraire().getClasse().getId();
        etudiantRepository.findByClasseIdAndActiveTrue(classeId).forEach(etudiant ->
                appelsRepository.findByEtudiantIdAndPlageHoraireId(
                        etudiant.getId(), session.getPlageHoraire().getId()
                ).ifPresentOrElse(appel -> {
                    appel.setSessionAppel(session);
                    if (appel.getEnseignant() == null)
                        appel.setEnseignant(session.getEnseignant());
                    appelsRepository.save(appel);
                }, () -> appelsRepository.save(Appels.builder()
                        .etudiant(etudiant)
                        .plageHoraire(session.getPlageHoraire())
                        .enseignant(session.getEnseignant())
                        .sessionAppel(session)
                        .statut(StatutPresence.EN_ATTENTE)
                        .synchronise(true)
                        .build()))
        );
    }

    private void cloturerAppelsEnAttente(SessionAppel session) {
        appelsRepository.findBySessionAppelIdAndStatut(
                        session.getId(), StatutPresence.EN_ATTENTE)
                .forEach(appel -> {
                    appel.marquerAbsent(session.getEnseignant());
                    appelsRepository.save(appel);
                });
    }
}