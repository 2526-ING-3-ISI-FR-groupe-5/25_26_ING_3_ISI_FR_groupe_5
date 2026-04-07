package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.SurveillantRepository;

@Service
@RequiredArgsConstructor
public class SurveillantService {

    private final SurveillantRepository surveillantRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;

    // ══════════════════════════════════════════
    // CRÉER un surveillant
    // ══════════════════════════════════════════
    @Transactional
    public Surveillant creer(Surveillant surveillant) {

        if (surveillantRepo.existsByEmail(surveillant.getEmail())) {
            throw new RuntimeException("Un surveillant avec cet email existe déjà");
        }

        // Encoder le mot de passe
        surveillant.setPassword(passwordEncoder.encode(surveillant.getPassword()));

        // Assigner le rôle
        Role role = roleRepo.findByNom("SURVEILLANT")
                .orElseThrow(() -> new RuntimeException("Rôle SURVEILLANT introuvable"));
        surveillant.getRoles().add(role);

        return surveillantRepo.save(surveillant);
    }

    // ══════════════════════════════════════════
    // MODIFIER un surveillant
    // ══════════════════════════════════════════
    @Transactional
    public Surveillant modifier(Long id, Surveillant data) {
        Surveillant surveillant = findById(id);
        surveillant.setNom(data.getNom());
        surveillant.setPrenom(data.getPrenom());
        surveillant.setTelephone(data.getTelephone());
        surveillant.setSecteur(data.getSecteur());
        surveillant.setTypeContrat(data.getTypeContrat());
        return surveillantRepo.save(surveillant);
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER
    // ══════════════════════════════════════════
    @Transactional
    public Surveillant toggleActif(Long id) {
        Surveillant surveillant = findById(id);
        surveillant.setActive(!surveillant.isEnabled());
        return surveillantRepo.save(surveillant);
    }
@Transactional
    public Page<Surveillant> getAll(Pageable pageable) {
        return surveillantRepo.findAll(pageable);
    }

    public Surveillant findById(Long id) {
        return surveillantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Surveillant introuvable"));
    }

    // ══════════════════════════════════════════
    // RÉINITIALISER le mot de passe
    // ══════════════════════════════════════════
    @Transactional
    public void reinitialiserMotDePasse(Long id) {
        Surveillant surveillant = findById(id);
        surveillant.setPassword(passwordEncoder.encode(surveillant.getEmail()));
        surveillant.setFirstLogin(true);
        surveillantRepo.save(surveillant);
    }

    // ══════════════════════════════════════════
    // SUPPRIMER
    // ══════════════════════════════════════════
    @Transactional
    public void supprimer(Long id) {
        surveillantRepo.delete(findById(id));
    }
}