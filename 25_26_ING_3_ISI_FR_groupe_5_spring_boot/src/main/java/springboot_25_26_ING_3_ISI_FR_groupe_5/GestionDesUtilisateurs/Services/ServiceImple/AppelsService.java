package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.appel.AppelRetardRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.appel.AppelsCheckManuelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.appel.AppelsRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.AppelsRepository;

import java.util.ArrayList;
import java.util.List;

import static springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutPresence.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AppelsService {

    private final AppelsRepository appelsRepository;
    private final EtudiantService etudiantService;
    private final EnseignantService enseignantService;
    private final PlageHoraireService plageHoraireService;
    private final SessionAppelService sessionAppelService;

    // ══════════════════════════════════════════
    // GET
    // ══════════════════════════════════════════

    @Transactional(readOnly = true)
    public Appels findById(Long id) {
        return appelsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appel introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Appels> getByPlageHoraire(Long plageHoraireId) {
        return appelsRepository.findByPlageHoraireId(plageHoraireId);
    }

    @Transactional(readOnly = true)
    public List<Appels> getByEtudiant(Long etudiantId) {
        return appelsRepository.findByEtudiantId(etudiantId);
    }

    @Transactional(readOnly = true)
    public List<Appels> getBySession(Long sessionId) {
        return appelsRepository.findBySessionAppelId(sessionId);
    }

    @Transactional(readOnly = true)
    public List<Appels> getRetardsByPlage(Long plageHoraireId) {
        return appelsRepository.findByPlageHoraireIdAndStatut(plageHoraireId, RETARD);
    }

    // ══════════════════════════════════════════
    // POST — Appel unitaire
    // ══════════════════════════════════════════

    public Appels creer(AppelsRequest req) {
        Etudiant etudiant     = etudiantService.findById(req.getEtudiantId());
        PlageHoraire plage = plageHoraireService.findEntityById(req.getPlageHoraireId());

        Enseignant enseignant = req.getEnseignantId() != null
                ? enseignantService.findById(req.getEnseignantId()) : null;
        SessionAppel session  = req.getSessionAppelId() != null
                ? sessionAppelService.findById(req.getSessionAppelId()) : null;

        Appels appel = Appels.builder()
                .etudiant(etudiant)
                .plageHoraire(plage)
                .enseignant(enseignant)
                .sessionAppel(session)
                .statut(StatutPresence.EN_ATTENTE)
                .synchronise(true)
                .build();

        // Appliquer le statut fourni
        if (req.getStatut() != null) {
            appliquerStatut(appel, req, plage, enseignant);
        }

        return appelsRepository.save(appel);
    }

    // ══════════════════════════════════════════
    // POST — Appel manuel en lot (check list)
    // ══════════════════════════════════════════

    public List<Appels> enregistrerAppelManuel(AppelsCheckManuelRequest req) {
        PlageHoraire plage = plageHoraireService.findEntityById(req.getPlageHoraireId());

        Enseignant enseignant = enseignantService.findById(req.getEnseignantId());

        // Récupérer tous les appels existants pour cette plage
        List<Appels> appelsExistants = appelsRepository.findByPlageHoraireId(req.getPlageHoraireId());
        List<Appels> resultats = new ArrayList<>();

        // ── Présents ──
        if (req.getEtudiantIdsPresents() != null) {
            for (Long etudiantId : req.getEtudiantIdsPresents()) {
                Appels appel = trouverOuCreer(appelsExistants, etudiantId, plage, enseignant);
                appel.marquerPresent(enseignant, MethodeValidation.MANUELLE);
                resultats.add(appelsRepository.save(appel));
            }
        }

        // ── Partiels ──
        if (req.getPresencesPartielles() != null) {
            for (var pp : req.getPresencesPartielles()) {
                Appels appel = trouverOuCreer(appelsExistants, pp.getEtudiantId(), plage, enseignant);
                appel.marquerPartiel(pp.getNbHeuresPresent(), enseignant);
                resultats.add(appelsRepository.save(appel));
            }
        }

        // ── Retards — uniquement premier cours du matin ──
        if (req.getRetards() != null) {
            for (var r : req.getRetards()) {
                Appels appel = trouverOuCreer(appelsExistants, r.getEtudiantId(), plage, enseignant);
                // marquerRetard() lève IllegalStateException si non autorisé
                appel.marquerRetard(r.getHeureArrivee(), enseignant);
                if (r.getCommentaire() != null) appel.setCommentaire(r.getCommentaire());
                resultats.add(appelsRepository.save(appel));
            }
        }

        // ── Absents — tous ceux encore EN_ATTENTE après traitement ──
        appelsExistants.stream()
                .filter(Appels::isEnAttente)
                .filter(a -> resultats.stream().noneMatch(r -> r.getId().equals(a.getId())))
                .forEach(a -> {
                    a.marquerAbsent(enseignant);
                    resultats.add(appelsRepository.save(a));
                });

        return resultats;
    }

    // ══════════════════════════════════════════
    // POST — Marquer retard seul (depuis UI enseignant)
    // ══════════════════════════════════════════

    public Appels marquerRetard(AppelRetardRequest req) {
        PlageHoraire plage = plageHoraireService.findEntityById(req.getPlageHoraireId());

        Enseignant enseignant = enseignantService.findById(req.getEnseignantId());

        List<Appels> existants = appelsRepository.findByPlageHoraireId(req.getPlageHoraireId());
        Appels appel = trouverOuCreer(existants, req.getEtudiantId(), plage, enseignant);

        // La règle métier est encapsulée dans l'entité
        appel.marquerRetard(req.getHeureArrivee(), enseignant);
        if (req.getCommentaire() != null) appel.setCommentaire(req.getCommentaire());

        return appelsRepository.save(appel);
    }

    // ══════════════════════════════════════════
    // PUT — Modifier un appel existant
    // ══════════════════════════════════════════

    public Appels modifier(Long id, AppelsRequest req) {
        Appels appel = findById(id);
        PlageHoraire plage = appel.getPlageHoraire();
        Enseignant enseignant = req.getEnseignantId() != null
                ? enseignantService.findById(req.getEnseignantId())
                : appel.getEnseignant();

        if (req.getStatut() != null) {
            appliquerStatut(appel, req, plage, enseignant);
        }
        if (req.getCommentaire() != null) appel.setCommentaire(req.getCommentaire());

        return appelsRepository.save(appel);
    }

    // ══════════════════════════════════════════
    // DELETE
    // ══════════════════════════════════════════

    public void supprimer(Long id) {
        appelsRepository.delete(findById(id));
    }

    // ══════════════════════════════════════════
    // PRIVÉ — Helpers
    // ══════════════════════════════════════════

    /**
     * Trouve l'appel existant pour un étudiant sur une plage,
     * ou en crée un nouveau s'il n'existe pas encore.
     */
    private Appels trouverOuCreer(List<Appels> existants, Long etudiantId,
                                  PlageHoraire plage, Enseignant enseignant) {
        return existants.stream()
                .filter(a -> a.getEtudiant().getId().equals(etudiantId))
                .findFirst()
                .orElseGet(() -> {
                    Etudiant etudiant = etudiantService.findById(etudiantId);
                    return Appels.builder()
                            .etudiant(etudiant)
                            .plageHoraire(plage)
                            .enseignant(enseignant)
                            .statut(StatutPresence.EN_ATTENTE)
                            .synchronise(true)
                            .build();
                });
    }

    /**
     * Applique le statut demandé en déléguant aux méthodes métier de l'entité.
     */
    private void appliquerStatut(Appels appel, AppelsRequest req,
                                 PlageHoraire plage, Enseignant enseignant) {
        switch (req.getStatut()) {
            case PRESENT -> appel.marquerPresent(enseignant,
                    req.getMethode() != null ? req.getMethode() : MethodeValidation.MANUELLE);
            case ABSENT  -> appel.marquerAbsent(enseignant);
            case PARTIEL -> appel.marquerPartiel(req.getNbHeuresPresent(), enseignant);
            case RETARD  -> appel.marquerRetard(req.getHeureArrivee(), enseignant);
            default      -> { /* EN_ATTENTE / JUSTIFIE → géré ailleurs */ }
        }
    }

    // ══════════════════════════════════════════
    // VALIDATION QR / PIN (inchangée)
    // ══════════════════════════════════════════

    public Appels validerParCode(AppelsRequest req) {
        SessionAppel session = sessionAppelService.findById(req.getSessionAppelId());

        if (!session.isValide()) throw new RuntimeException("Session expirée ou invalide.");
        if (!session.getCode().equals(req.getCodeSaisi()))
            throw new RuntimeException("Code invalide.");

        // Vérification géolocalisation si périmètre défini
        if (req.getLatitudeEtudiant() != null && req.getLongitudeEtudiant() != null) {
            boolean dansPerimetre = session.estDansLePerimetre(
                    req.getLatitudeEtudiant(), req.getLongitudeEtudiant());
            if (!dansPerimetre) throw new RuntimeException("Étudiant hors périmètre autorisé.");
        }

        PlageHoraire plage    = session.getPlageHoraire();
        Enseignant enseignant = session.getEnseignant();
        List<Appels> existants = appelsRepository.findByPlageHoraireId(plage.getId());

        Appels appel = trouverOuCreer(existants, req.getEtudiantId(), plage, enseignant);
        appel.marquerPresent(enseignant, req.getMethode() != null ? req.getMethode() : session.getMethode());
        appel.setCodeUtilise(req.getCodeSaisi());
        appel.setLatitudeEtudiant(req.getLatitudeEtudiant());
        appel.setLongitudeEtudiant(req.getLongitudeEtudiant());
        appel.setDansLePerimetre(true);
        appel.setSessionAppel(session);

        return appelsRepository.save(appel);
    }
}