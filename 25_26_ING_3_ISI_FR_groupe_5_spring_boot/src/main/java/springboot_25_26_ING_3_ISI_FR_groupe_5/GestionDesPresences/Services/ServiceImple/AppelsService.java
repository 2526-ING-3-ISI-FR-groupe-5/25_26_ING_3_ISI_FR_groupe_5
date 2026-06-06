package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IAppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IPlageHoraireService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.ISessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelRetardRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsCheckManuelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EnseignantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EtudiantService;

import java.time.LocalTime;
import java.util.*;

import static springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppelsService implements IAppelsService {

    private final AppelsRepository appelsRepository;
    private final EtudiantService etudiantService;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantService enseignantService;
    private final IPlageHoraireService plageHoraireService;
    private final ISessionAppelService sessionAppelService;

    // ── RECHERCHE ──

    @Transactional(readOnly = true)
    public Appels findById(Long id) {
        return appelsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appel introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Appels> getByPlageHoraire(Long id) {
        return appelsRepository.findByPlageHoraireId(id);
    }

    @Transactional(readOnly = true)
    public List<Appels> getByEtudiant(Long id) {
        return appelsRepository.findByEtudiantId(id);
    }

    @Transactional(readOnly = true)
    public List<Appels> getBySession(Long id) {
        return appelsRepository.findBySessionAppelId(id);
    }

    @Transactional(readOnly = true)
    public List<Appels> getRetardsByPlage(Long id) {
        return appelsRepository.findByPlageHoraireIdAndStatut(id, RETARD);
    }

    @Transactional(readOnly = true)
    public List<Appels> getRetardsByEtudiant(Long etudiantId) {
        return appelsRepository.findByEtudiantIdAndStatut(etudiantId, StatutPresence.RETARD);
    }

    // ── ACTIONS ──

    public Appels creer(AppelsRequest req) {
        Etudiant etu = etudiantService.findById(req.getEtudiantId());
        PlageHoraire ph = plageHoraireService.findEntityById(req.getPlageHoraireId());
        Enseignant ens = req.getEnseignantId() != null
                ? enseignantService.findById(req.getEnseignantId()) : null;

        Appels appel = appelsRepository
                .findByEtudiantIdAndPlageHoraireId(etu.getId(), ph.getId())
                .orElse(Appels.builder().etudiant(etu).plageHoraire(ph).build());

        appliquerStatutDepuisRequest(appel, req, ens);
        return appelsRepository.save(appel);
    }

    public List<Appels> enregistrerAppelManuel(AppelsCheckManuelRequest req) {
        PlageHoraire ph = plageHoraireService.findEntityById(req.getPlageHoraireId());
        Enseignant ens = enseignantService.findById(req.getEnseignantId());

        SessionAppel session = getSessionActiveOrNull(ph.getId());

        initialiserAppelsPourPlage(ph, ens, session);

        // ✅ CORRIGÉ — Utilisation de la méthode optimisée pour éviter le problème N+1
        List<Appels> tousLesAppels = appelsRepository.findByPlageHoraireIdWithDetails(ph.getId());
        List<Appels> resultats = new ArrayList<>();
        Set<Long> traites = new HashSet<>();

        // 1. Présents
        if (req.getEtudiantIdsPresents() != null) {
            req.getEtudiantIdsPresents().forEach(id -> {
                Appels a = trouverDansListe(tousLesAppels, id);
                a.marquerPresent(ens, MethodeValidation.MANUELLE);
                resultats.add(appelsRepository.save(a));
                traites.add(id);
            });
        }

        // 2. Partiels
        if (req.getPresencesPartielles() != null) {
            req.getPresencesPartielles().forEach(pp -> {
                Appels a = trouverDansListe(tousLesAppels, pp.getEtudiantId());
                a.marquerPartiel(pp.getNbHeuresPresent(), ens);
                resultats.add(appelsRepository.save(a));
                traites.add(pp.getEtudiantId());
            });
        }

        // 3. Retards
        if (req.getRetards() != null) {
            req.getRetards().forEach(r -> {
                validerHeureArrivee(r.getHeureArrivee(), ph);
                Appels a = trouverDansListe(tousLesAppels, r.getEtudiantId());
                a.marquerRetard(r.getHeureArrivee(), ens);
                a.setCommentaire(r.getCommentaire());
                resultats.add(appelsRepository.save(a));
                traites.add(r.getEtudiantId());
            });
        }

        // 4. Absents — tous ceux non mentionnés dans la requête
        tousLesAppels.stream()
                .filter(a -> !traites.contains(a.getEtudiant().getId()))
                .forEach(a -> {
                    a.marquerAbsent(ens);
                    resultats.add(appelsRepository.save(a));
                });

        return resultats;
    }

    public Appels marquerRetard(AppelRetardRequest req) {
        PlageHoraire ph = plageHoraireService.findEntityById(req.getPlageHoraireId());
        validerHeureArrivee(req.getHeureArrivee(), ph);

        Appels a = appelsRepository
                .findByEtudiantIdAndPlageHoraireId(req.getEtudiantId(), ph.getId())
                .orElseThrow(() -> new IllegalStateException("Appel non initialisé"));

        a.marquerRetard(req.getHeureArrivee(), enseignantService.findById(req.getEnseignantId()));
        a.setCommentaire(req.getCommentaire());
        return appelsRepository.save(a);
    }

    public Appels modifier(Long id, AppelsRequest req) {
        Appels a = findById(id);
        Enseignant ens = req.getEnseignantId() != null
                ? enseignantService.findById(req.getEnseignantId())
                : a.getEnseignant();
        appliquerStatutDepuisRequest(a, req, ens);
        if (req.getCommentaire() != null) a.setCommentaire(req.getCommentaire());
        return appelsRepository.save(a);
    }

    public void supprimer(Long id) {
        appelsRepository.delete(findById(id));
    }

    @Transactional
    @Override
    public void ajusterHeures(Long appelId, int nbHeuresPresent) {
        Appels appel = appelsRepository.findById(appelId)
                .orElseThrow(() -> new EntityNotFoundException("Appel non trouvé : " + appelId));

        if (nbHeuresPresent < 0) {
            throw new IllegalArgumentException("Le nombre d'heures ne peut pas être négatif");
        }
        int dureeHeures = (int) appel.getPlageHoraire().getDureeHeures();
        if (nbHeuresPresent > dureeHeures) {
            throw new IllegalArgumentException(
                    "Le nombre d'heures (" + nbHeuresPresent +
                            ") ne peut pas dépasser la durée du cours (" + dureeHeures + "h)");
        }

        appel.setNbHeuresPresent(nbHeuresPresent);
        appel.setStatut(nbHeuresPresent > 0 ? StatutPresence.PARTIEL : StatutPresence.ABSENT);
        appelsRepository.save(appel);
    }

    public Appels validerParCode(AppelsRequest req, Long etudiantId) {
        SessionAppel session = sessionAppelService.findById(req.getSessionAppelId());

        if (!session.isValide()) {
            throw new IllegalStateException("Session expirée ou fermée.");
        }
        if (!session.getCode().equals(req.getCodeSaisi())) {
            throw new IllegalArgumentException("Code invalide.");
        }

        // ✅ CORRIGÉ — Validation explicite de la présence de coordonnées GPS si périmètre activé
        if (session.getPerimetreMetres() != null) {
            if (req.getLatitudeEtudiant() == null || req.getLongitudeEtudiant() == null) {
                throw new IllegalStateException(
                        "Validation impossible : vous devez activer et autoriser la géolocalisation pour valider votre présence.");
            }

            boolean estPresentPhysiquement = session.estDansLePerimetre(
                    req.getLatitudeEtudiant(), req.getLongitudeEtudiant());
            if (!estPresentPhysiquement) {
                throw new IllegalStateException(
                        "Validation impossible : vous n'êtes pas localisé dans le périmètre de l'établissement.");
            }
        }

        Appels appel = appelsRepository
                .findByEtudiantIdAndPlageHoraireId(etudiantId, session.getPlageHoraire().getId())
                .orElseThrow(() -> new IllegalStateException("Appel non initialisé."));

        appel.marquerPresent(session.getEnseignant(), session.getMethode());
        appel.setLatitudeEtudiant(req.getLatitudeEtudiant());
        appel.setLongitudeEtudiant(req.getLongitudeEtudiant());
        appel.setDansLePerimetre(true);

        return appelsRepository.save(appel);
    }

    // ── HELPERS ──

    private void appliquerStatutDepuisRequest(Appels a, AppelsRequest req, Enseignant ens) {
        if (req.getStatut() == null) return;
        switch (req.getStatut()) {
            case PRESENT -> a.marquerPresent(ens,
                    req.getMethode() != null ? req.getMethode() : MethodeValidation.MANUELLE);
            case ABSENT  -> a.marquerAbsent(ens);
            case PARTIEL -> a.marquerPartiel(req.getNbHeuresPresent(), ens);
            case RETARD  -> {
                validerHeureArrivee(req.getHeureArrivee(), a.getPlageHoraire());
                a.marquerRetard(req.getHeureArrivee(), ens);
            }
        }
    }

    private void validerHeureArrivee(LocalTime heure, PlageHoraire ph) {
        if (heure == null) throw new IllegalArgumentException("Heure d'arrivée requise");
        if (heure.isBefore(ph.getHeureDebut()))
            throw new IllegalArgumentException("L'étudiant n'est pas en retard");
        if (heure.isAfter(ph.getHeureFin()))
            throw new IllegalArgumentException("Cours terminé, mettre Absent");
    }

    private Appels trouverDansListe(List<Appels> liste, Long etuId) {
        return liste.stream()
                .filter(a -> a.getEtudiant().getId().equals(etuId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Étudiant id=" + etuId + " non trouvé dans la liste d'appel"));
    }

    private void initialiserAppelsPourPlage(PlageHoraire ph, Enseignant ens, SessionAppel session) {
        etudiantRepository.findByClasseIdAndActiveTrue(ph.getClasse().getId()).forEach(etu -> {
            if (appelsRepository.findByEtudiantIdAndPlageHoraireId(etu.getId(), ph.getId()).isEmpty()) {
                appelsRepository.save(Appels.builder()
                        .etudiant(etu).plageHoraire(ph)
                        .enseignant(ens).sessionAppel(session)
                        .statut(EN_ATTENTE).build());
            }
        });
    }

    private SessionAppel getSessionActiveOrNull(Long phId) {
        try {
            return sessionAppelService.getSessionActive(phId);
        } catch (RuntimeException e) {
            log.debug("Aucune session active pour la plage {} : {}", phId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Erreur inattendue lors de la récupération de la session active pour la plage {} : {}",
                    phId, e.getMessage());
            return null;
        }
    }
}