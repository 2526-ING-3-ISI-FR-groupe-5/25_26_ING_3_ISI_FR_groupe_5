package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.NiveauRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.INiveauService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NiveauService implements INiveauService {

    private final NiveauRepository niveauRepo;
    private final FiliereService filiereService;
    private final SpecialiteService specialiteService;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public Niveau creer(Niveau niveau, Long filiereId, Long specialiteId, Utilisateur acteur) {
        try {
            Filiere filiere = filiereService.findById(filiereId);

            if (niveauRepo.existsByCodeAndFiliereId(niveau.getCode(), filiereId)) {
                throw new RuntimeException("Un niveau avec ce code existe déjà dans cette filière");
            }

            niveau.setFiliere(filiere);

            if (specialiteId != null) {
                Specialite specialite = specialiteService.findById(specialiteId);
                niveau.setSpecialite(specialite);
            }

            Niveau saved = niveauRepo.save(niveau);

            // ✅ Journalisation
            journalService.journaliserSucces(acteur, TypeAction.NIVEAU_CREE,
                    "Niveau", saved.getId(), "Niveau créé : " + saved.getNom());

            return saved;

        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.NIVEAU_CREE,
                    "Niveau", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création du niveau", e);
        }
    }

    @Override
    @Transactional
    public Niveau modifier(Long id, Niveau data, Long specialiteId, Utilisateur acteur) {
        try {
            Niveau niveau = findById(id);
            niveau.setNom(data.getNom());
            niveau.setCode(data.getCode());
            niveau.setOrdre(data.getOrdre());

            if (specialiteId != null) {
                Specialite specialite = specialiteService.findById(specialiteId);
                niveau.setSpecialite(specialite);
            } else {
                niveau.setSpecialite(null);
            }

            Niveau saved = niveauRepo.save(niveau);

            // ✅ Journalisation
            journalService.journaliserSucces(acteur, TypeAction.NIVEAU_MODIFIE,
                    "Niveau", saved.getId(), "Niveau modifié : " + saved.getNom());

            return saved;

        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.NIVEAU_MODIFIE,
                    "Niveau", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification du niveau", e);
        }
    }

    @Override
    public Niveau findById(Long id) {
        return niveauRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Niveau", "id", id));
    }

    @Override
    public List<Niveau> getAll() {
        return niveauRepo.findAll();
    }

    @Override
    public List<Niveau> getByFiliere(Long filiereId) {
        filiereService.findById(filiereId);
        return niveauRepo.findByFiliereIdOrderByOrdreAsc(filiereId);
    }

    @Override
    public List<Niveau> getBySpecialite(Long specialiteId) {
        return niveauRepo.findBySpecialiteId(specialiteId);
    }

    @Override
    public Optional<Niveau> getNiveauSuperieur(Niveau actuel) {
        return niveauRepo.findByFiliereIdAndOrdre(
                actuel.getFiliere().getId(),
                actuel.getOrdre() + 1
        );
    }

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur acteur) {
        try {
            Niveau niveau = findById(id);
            if (!niveau.getClasses().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer : ce niveau contient des classes");
            }
            niveauRepo.delete(niveau);

            // ✅ Journalisation
            journalService.journaliserSucces(acteur, TypeAction.NIVEAU_SUPPRIME,
                    "Niveau", id, "Niveau supprimé : " + niveau.getNom());

        } catch (RuntimeException e) {
            journalService.journaliserEchec(acteur, TypeAction.NIVEAU_SUPPRIME,
                    "Niveau", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.NIVEAU_SUPPRIME,
                    "Niveau", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du niveau", e);
        }
    }
}