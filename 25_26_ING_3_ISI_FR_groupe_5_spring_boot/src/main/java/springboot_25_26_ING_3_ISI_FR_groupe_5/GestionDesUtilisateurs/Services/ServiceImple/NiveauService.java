package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.NiveauRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NiveauService {

    private final NiveauRepository niveauRepo;
    private final FiliereService filiereService;

    @Transactional
    public Niveau creer(Niveau niveau, Long filiereId, Long specialiteId) {
        Filiere filiere = filiereService.findById(filiereId);

        if (niveauRepo.existsByCodeAndFiliereId(niveau.getCode(), filiereId)) {
            throw new RuntimeException("Un niveau avec ce code existe déjà dans cette filière");
        }

        niveau.setFiliere(filiere);
        return niveauRepo.save(niveau);
    }

    @Transactional
    public List<Niveau> getAll() {
        return niveauRepo.findAll();
    }

    @Transactional
    public Niveau modifier(Long id, Niveau data) {
        Niveau niveau = findById(id);
        niveau.setNom(data.getNom());
        niveau.setCode(data.getCode());
        niveau.setOrdre(data.getOrdre());
        return niveauRepo.save(niveau);
    }

    public Niveau findById(Long id) {
        return niveauRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Niveau introuvable"));
    }

    public List<Niveau> getByFiliere(Long filiereId) {
        filiereService.findById(filiereId);
        return niveauRepo.findByFiliereIdOrderByOrdreAsc(filiereId);
    }

    // ✅ NOUVELLE MÉTHODE : Récupérer les niveaux par spécialité
    public List<Niveau> getBySpecialite(Long specialiteId) {
        return niveauRepo.findBySpecialiteId(specialiteId);
    }

    public Optional<Niveau> getNiveauSuperieur(Niveau actuel) {
        return niveauRepo.findByFiliereIdAndOrdre(
                actuel.getFiliere().getId(),
                actuel.getOrdre() + 1
        );
    }

    @Transactional
    public void supprimer(Long id) {
        Niveau niveau = findById(id);
        if (!niveau.getClasses().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : ce niveau contient des classes");
        }
        niveauRepo.delete(niveau);
    }
}