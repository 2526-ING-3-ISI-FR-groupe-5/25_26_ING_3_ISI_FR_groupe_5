package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.InterfaceEnseignant;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnseignantService implements InterfaceEnseignant {

    private final EnseignantRepository enseignantRepo;
    private final AnneeAcademiqueService anneeService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;

    // ══════════════════════════════════════════
    // CRÉER un enseignant
    // ══════════════════════════════════════════
    @Transactional
    public Enseignant creer(Enseignant enseignant) {

        if (enseignantRepo.existsByEmail(enseignant.getEmail())) {
            throw new RuntimeException("Un enseignant avec cet email existe déjà");
        }

        // Encoder le mot de passe
        enseignant.setPassword(passwordEncoder.encode(enseignant.getPassword()));

        // Assigner le rôle
        Role role = roleRepo.findByNom("ENSEIGNANT")
                .orElseThrow(() -> new RuntimeException("Rôle ENSEIGNANT introuvable"));
        enseignant.getRoles().add(role);

        return enseignantRepo.save(enseignant);
    }

    // ══════════════════════════════════════════
    // MODIFIER un enseignant
    // ══════════════════════════════════════════
    @Transactional
    public Enseignant modifier(Long id, Enseignant data) {
        Enseignant enseignant = findById(id);
        enseignant.setNom(data.getNom());
        enseignant.setPrenom(data.getPrenom());
        enseignant.setTelephone(data.getTelephone());
        enseignant.setGrade(data.getGrade());
        enseignant.setTypeEnseignant(data.getTypeEnseignant());
        return enseignantRepo.save(enseignant);
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER
    // ══════════════════════════════════════════
    @Transactional
    public Enseignant toggleActif(Long id) {
        Enseignant enseignant = findById(id);
        enseignant.setActive(!enseignant.isEnabled());
        return enseignantRepo.save(enseignant);
    }

    public Enseignant findById(Long id) {
        return enseignantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));
    }

    // ══════════════════════════════════════════
    // RECHERCHER avec filtre + année
    // ══════════════════════════════════════════
    public Page<Enseignant> rechercher(Long anneeId, String recherche, Pageable pageable) {
        anneeService.findById(anneeId);
        return enseignantRepo.searchByAnnee(anneeId, recherche, pageable);
    }

    // ══════════════════════════════════════════
    // RECHERCHER par type
    // ══════════════════════════════════════════
    public Page<Enseignant> getByType(String type, Pageable pageable) {
        return enseignantRepo.findByTypeEnseignant(type, pageable);
    }

    // ══════════════════════════════════════════
    // RÉINITIALISER le mot de passe
    // ══════════════════════════════════════════
    @Transactional
    public void reinitialiserMotDePasse(Long id) {
        Enseignant enseignant = findById(id);
        // Mot de passe par défaut = email
        enseignant.setPassword(passwordEncoder.encode(enseignant.getEmail()));
        enseignant.setFirstLogin(true);
        enseignantRepo.save(enseignant);
    }

    // ══════════════════════════════════════════
    // SUPPRIMER un enseignant
    // ══════════════════════════════════════════
    @Transactional
    public void supprimer(Long id) {
        Enseignant enseignant = findById(id);
        if (!enseignant.getProgrammations().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer : cet enseignant est lié à des programmations"
            );
        }
        enseignantRepo.delete(enseignant);
    }

    public List<Enseignant> getAll() {
        return enseignantRepo.findAll();
    }
}
