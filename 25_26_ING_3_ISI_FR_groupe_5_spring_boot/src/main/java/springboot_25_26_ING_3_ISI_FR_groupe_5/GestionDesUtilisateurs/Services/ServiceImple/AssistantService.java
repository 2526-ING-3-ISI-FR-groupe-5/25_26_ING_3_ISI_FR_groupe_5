package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.AssistantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private final AssistantRepository assistantRepo;
    private final ClassesService classeService;  // ✅ Correction : ClasseService au lieu de CycleService
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;

    // ══════════════════════════════════════════
    // CRÉER un assistant
    // ══════════════════════════════════════════
    @Transactional
    public AssistantPedagogique creer(AssistantPedagogique assistant, List<Long> classeIds) {

        if (assistantRepo.existsByEmail(assistant.getEmail())) {
            throw new RuntimeException("Un assistant avec cet email existe déjà");
        }

        // Encoder le mot de passe
        assistant.setPassword(passwordEncoder.encode(assistant.getPassword()));

        // Assigner le rôle
        Role role = roleRepo.findByNom("ASSISTANT")
                .orElseThrow(() -> new RuntimeException("Rôle ASSISTANT introuvable"));
        assistant.getRoles().add(role);

        // Assigner les classes
        for (Long classeId : classeIds) {
            Classe classe = classeService.findById(classeId);
            assistant.getClasses().add(classe);
        }

        return assistantRepo.save(assistant);
    }

    // ══════════════════════════════════════════
    // MODIFIER un assistant
    // ══════════════════════════════════════════
    @Transactional
    public AssistantPedagogique modifier(Long id, AssistantPedagogique data) {
        AssistantPedagogique assistant = findById(id);
        assistant.setNom(data.getNom());
        assistant.setPrenom(data.getPrenom());
        assistant.setTelephone(data.getTelephone());
        assistant.setFonction(data.getFonction());
        return assistantRepo.save(assistant);
    }

    // ══════════════════════════════════════════
    // AFFECTER des classes à un assistant
    // ══════════════════════════════════════════
    @Transactional
    public AssistantPedagogique affecterClasses(Long id, List<Long> classeIds) {
        AssistantPedagogique assistant = findById(id);

        // Vider les anciennes classes
        assistant.getClasses().clear();

        // Ajouter les nouvelles
        for (Long classeId : classeIds) {
            Classe classe = classeService.findById(classeId);
            assistant.getClasses().add(classe);
        }

        return assistantRepo.save(assistant);
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER
    // ══════════════════════════════════════════
    @Transactional
    public AssistantPedagogique toggleActif(Long id) {
        AssistantPedagogique assistant = findById(id);
        assistant.setActive(!assistant.isEnabled());
        return assistantRepo.save(assistant);
    }

    public AssistantPedagogique findById(Long id) {
        return assistantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Assistant introuvable"));
    }

    // ══════════════════════════════════════════
    // RECHERCHER
    // ══════════════════════════════════════════
    public Page<AssistantPedagogique> rechercher(String recherche, Pageable pageable) {
        return assistantRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                recherche, recherche, pageable
        );
    }

    // ══════════════════════════════════════════
    // RÉINITIALISER le mot de passe
    // ══════════════════════════════════════════
    @Transactional
    public void reinitialiserMotDePasse(Long id) {
        AssistantPedagogique assistant = findById(id);
        assistant.setPassword(passwordEncoder.encode(assistant.getEmail()));
        assistant.setFirstLogin(true);
        assistantRepo.save(assistant);
    }

    // ══════════════════════════════════════════
    // SUPPRIMER
    // ══════════════════════════════════════════
    @Transactional
    public void supprimer(Long id) {
        assistantRepo.delete(findById(id));
    }
}