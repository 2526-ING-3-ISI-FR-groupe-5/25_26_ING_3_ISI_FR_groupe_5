package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsCheckManuelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelRetardRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.PlageHoraireService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/enseignant/appels")
@RequiredArgsConstructor
public class AppelsMvcController {

    private final AppelsService appelsService;
    private final PlageHoraireService plageHoraireService;
    private final SessionAppelService sessionAppelService;
    private final AppelsRepository appelsRepository;
    private final EnseignantRepository enseignantRepository;

    // ══════════════════════════════════════════
    // LISTE DES COURS DE L'ENSEIGNANT
    // GET /enseignant/appels
    // ══════════════════════════════════════════

    @GetMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String listeCours(
            Model model,
            Authentication authentication) {

        Utilisateur user = resolveUtilisateur(authentication);
        if (user == null) {
            log.error("Utilisateur introuvable dans le contexte d'authentification");
            return "error/500";
        }
        log.info("Liste des cours pour {}", user.getEmail());

        List<PlageHoraireResponse> plages = plageHoraireService.findByEnseignantId(user.getId());

        model.addAttribute("plages", plages);
        model.addAttribute("currentInstitutName",
                user.getInstitut() != null ? user.getInstitut().getNom() : "Global");

        // Compter les sessions actives — 1 seule requête (au lieu de N)
        long nbSessionsActives = sessionAppelService.countPlagesAvecSessionActive(user.getId());
        model.addAttribute("nbSessionsActives", nbSessionsActives);

        return "appel/liste-cours";
    }

    // ══════════════════════════════════════════
    // AFFICHAGE PAGE D'APPEL
    // GET /enseignant/appels/{id}/appel
    // ══════════════════════════════════════════

    @GetMapping("/{id}/appel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String afficherAppel(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        Utilisateur user = resolveUtilisateur(authentication);
        if (user == null) {
            log.error("Utilisateur introuvable dans le contexte d'authentification");
            return "error/500";
        }

        log.info("Affichage appel plage {} par {}", id, user.getEmail());

        PlageHoraireResponse plage = plageHoraireService.findById(id);
        model.addAttribute("plageHoraire", plage);

        var appels = appelsService.getByPlageHoraire(id);
        model.addAttribute("appels", appels);

        // 1 seule requête GROUP BY au lieu de 3 SELECT séparés
        var compteurs = new java.util.EnumMap<StatutPresence, Long>(StatutPresence.class);
        for (Object[] row : appelsRepository.countByPlageGroupedByStatut(id)) {
            compteurs.put((StatutPresence) row[0], (Long) row[1]);
        }
        model.addAttribute("nbPresents", compteurs.getOrDefault(StatutPresence.PRESENT, 0L));
        model.addAttribute("nbRetards", compteurs.getOrDefault(StatutPresence.RETARD, 0L));
        model.addAttribute("nbAbsents", compteurs.getOrDefault(StatutPresence.ABSENT, 0L));

        // Session normale active
        try {
            model.addAttribute("sessionActive", sessionAppelService.getSessionActive(id));
        } catch (Exception e) {
            log.debug("Aucune session active pour la plage {} : {}", id, e.getMessage());
            model.addAttribute("sessionActive", null);
        }

        // Session offline active pour cette plage
        try {
            if (plage.getClasse() != null && plage.getClasse().getId() != null) {
                model.addAttribute("sessionOffline",
                        sessionAppelService.getSessionOfflineActive(plage.getClasse().getId()));
            } else {
                model.addAttribute("sessionOffline", null);
            }
        } catch (Exception e) {
            log.debug("Aucune session offline pour la plage {} : {}", id, e.getMessage());
            model.addAttribute("sessionOffline", null);
        }

        model.addAttribute("sessions", sessionAppelService.getByPlage(id));

        LocalTime heureDebut = plage.getHeureDebut();
        boolean estPremierCoursMatin = heureDebut != null
                && (heureDebut.getHour() < 8
                || (heureDebut.getHour() == 8 && heureDebut.getMinute() <= 30));
        model.addAttribute("estPremierCoursMatin", estPremierCoursMatin);

        return "appel/appel_interface";
    }

    // ══════════════════════════════════════════
    // CHECK MANUEL DES PRESENCES
    // POST /enseignant/appels/{id}/check-manuel
    // ══════════════════════════════════════════

    @PostMapping("/{id}/check-manuel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String checkManuel(
            @PathVariable Long id,
            @RequestParam(required = false) List<Long> etudiantIdsPresents,
            Authentication authentication,
            RedirectAttributes ra) {
        try {
            AppelsCheckManuelRequest req = new AppelsCheckManuelRequest();
            req.setPlageHoraireId(id);
            req.setEtudiantIdsPresents(etudiantIdsPresents != null ? etudiantIdsPresents : List.of());
            Utilisateur user = resolveUtilisateur(authentication);
            if (user instanceof Enseignant e) req.setEnseignantId(e.getId());
            appelsService.enregistrerAppelManuel(req);
            ra.addFlashAttribute("succes", "Presences enregistrees avec succes !");
        } catch (Exception e) {
            log.error("Erreur check manuel plage {}", id, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // RETARD
    // POST /enseignant/appels/{id}/marquer-retard
    // ══════════════════════════════════════════

    @PostMapping("/{id}/marquer-retard")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String marquerRetard(
            @PathVariable Long id,
            @RequestParam Long etudiantId,
            @RequestParam LocalTime heureArrivee,
            Authentication authentication,
            RedirectAttributes ra) {
        try {
            AppelRetardRequest req = new AppelRetardRequest();
            req.setEtudiantId(etudiantId);
            req.setPlageHoraireId(id);
            req.setHeureArrivee(heureArrivee);
            Utilisateur user = resolveUtilisateur(authentication);
            if (user instanceof Enseignant e) req.setEnseignantId(e.getId());
            appelsService.marquerRetard(req);
            ra.addFlashAttribute("succes", "Retard enregistre !");
        } catch (Exception e) {
            log.error("Erreur retard plage {} etudiant {}", id, etudiantId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // SESSION QR / PIN — NORMALE
    // POST /enseignant/appels/{id}/lancer-session
    // ══════════════════════════════════════════

    @PostMapping("/{id}/lancer-session")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String lancerSession(
            @PathVariable Long id,
            @RequestParam String methode,
            @RequestParam(defaultValue = "3") int dureeMinutes,
            Authentication authentication,
            RedirectAttributes ra) {
        try {
            MethodeValidation methodeEnum;
            try {
                methodeEnum = MethodeValidation.valueOf(methode);
            } catch (IllegalArgumentException ex) {
                ra.addFlashAttribute("erreur",
                        "Methode de validation invalide : " + methode
                                + ". Valeurs attendues : MANUELLE, QR_CODE, CODE_PIN.");
                return "redirect:/enseignant/appels/" + id + "/appel";
            }

            SessionAppelRequest req = new SessionAppelRequest();
            req.setPlageHoraireId(id);
            req.setMethode(methodeEnum);
            req.setDureeMinutes(dureeMinutes);
            Utilisateur user = resolveUtilisateur(authentication);
            if (user == null) throw new RuntimeException("Utilisateur introuvable");
            sessionAppelService.creer(req, user.getId());
            ra.addFlashAttribute("succes", "Session " + methode + " lancee !");
        } catch (Exception e) {
            log.error("Erreur lancement session plage {}", id, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // SESSION OFFLINE
    // POST /enseignant/appels/{id}/lancer-session-offline
    // ══════════════════════════════════════════

    @PostMapping("/{id}/lancer-session-offline")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String lancerSessionOffline(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes ra) {
        try {
            Utilisateur user = resolveUtilisateur(authentication);
            if (user == null) throw new RuntimeException("Utilisateur introuvable");
            sessionAppelService.creerSessionOffline(id, user.getId());
            ra.addFlashAttribute("succes",
                    "Session offline lancee ! Dictez le code aux etudiants sans reseau.");
        } catch (Exception e) {
            log.error("Erreur lancement session offline plage {}", id, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // RENOUVELER LE CODE QR/PIN
    // POST /enseignant/appels/{id}/renouveler-code/{sid}
    // ══════════════════════════════════════════

    @PostMapping("/{id}/renouveler-code/{sid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String renouvelerCode(
            @PathVariable Long id,
            @PathVariable Long sid,
            @RequestParam(defaultValue = "3") int dureeMinutes,
            Authentication authentication,
            RedirectAttributes ra) {
        try {
            Utilisateur user = resolveUtilisateur(authentication);
            if (user == null) throw new RuntimeException("Utilisateur introuvable");
            sessionAppelService.renouvelerCode(sid, dureeMinutes, user.getId());
            ra.addFlashAttribute("succes", "Code renouvele avec succes !");
        } catch (Exception e) {
            log.error("Erreur renouvellement code session {}", sid, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // ARRETER / TERMINER
    // POST /enseignant/appels/{id}/arreter-session/{sid}
    // POST /enseignant/appels/{id}/terminer/{sid}
    // ══════════════════════════════════════════

    /**
     * ✅ CORRIGÉ — Transmission du user.getId() pour validation d'identité.
     */
    @PostMapping("/{id}/arreter-session/{sid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String arreterSession(
            @PathVariable Long id,
            @PathVariable Long sid,
            Authentication authentication,
            RedirectAttributes ra) {
        try {
            Utilisateur user = resolveUtilisateur(authentication);
            if (user == null) throw new RuntimeException("Utilisateur introuvable");
            sessionAppelService.arreterSession(sid, user.getId());
            ra.addFlashAttribute("succes", "Session arretee.");
        } catch (Exception e) {
            log.error("Erreur arret session {}", sid, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    /**
     * ✅ CORRIGÉ — Transmission du user.getId() pour validation d'identité.
     */
    @PostMapping("/{id}/terminer/{sid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String terminerCours(
            @PathVariable Long id,
            @PathVariable Long sid,
            Authentication authentication,
            RedirectAttributes ra) {
        try {
            Utilisateur user = resolveUtilisateur(authentication);
            if (user == null) throw new RuntimeException("Utilisateur introuvable");
            sessionAppelService.terminerCours(sid, user.getId());
            ra.addFlashAttribute("succes", "Cours termine. Les absences ont ete enregistrees.");
        } catch (Exception e) {
            log.error("Erreur terminaison cours session {}", sid, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // AJUSTEMENT HEURES
    // POST /enseignant/appels/{id}/ajuster-heures/{aid}
    // ══════════════════════════════════════════

    @PostMapping("/{id}/ajuster-heures/{aid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String ajusterHeures(
            @PathVariable Long id,
            @PathVariable Long aid,
            @RequestParam int nbHeuresPresent,
            RedirectAttributes ra) {
        try {
            appelsService.ajusterHeures(aid, nbHeuresPresent);
            ra.addFlashAttribute("succes", "Heures ajustees.");
        } catch (Exception e) {
            log.error("Erreur ajustement heures appel {}", aid, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }


    // ══════════════════════════════════════════
    // UTIL
    // ══════════════════════════════════════════
    private Utilisateur resolveUtilisateur(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Utilisateur) return (Utilisateur) principal;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return enseignantRepository.findByEmail(username).orElse(null);
        }
        return null;
    }
}