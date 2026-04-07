package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepo;
    private final InscriptionRepository inscriptionRepo;
    private final ClassesService classesService;
    private final AnneeAcademiqueService anneeService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;

    // ══════════════════════════════════════════
    // CRÉER un étudiant + l'inscrire à l'année active
    // ══════════════════════════════════════════
    @Transactional
    public Etudiant creer(Etudiant etudiant, Long classeId) {

        if (etudiantRepo.existsByEmailContainingIgnoreCase(etudiant.getEmail())) {
            throw new RuntimeException("Un étudiant avec cet email existe déjà");
        }

        // Récupérer l'année active
        Annee_academique anneeActive = anneeService.getAnneeActive();

        // Encoder le mot de passe
        etudiant.setPassword(passwordEncoder.encode(etudiant.getPassword()));

        // Assigner le rôle étudiant
        Role role = roleRepo.findByNom("ETUDIANT")
                .orElseThrow(() -> new RuntimeException("Rôle ETUDIANT introuvable"));
        etudiant.getRoles().add(role);

        // Générer le matricule
        etudiant.setMatricule(genererMatricule());

        // Sauvegarder l'étudiant
        Etudiant saved = etudiantRepo.save(etudiant);

        // Créer l'inscription pour l'année active
        Classe classe = classesService.findById(classeId);
        Inscription inscription = new Inscription();
        inscription.setEtudiant(saved);
        inscription.setClasse(classe);
        inscription.setAnneeAcademique(anneeActive);
        inscriptionRepo.save(inscription);

        return saved;
    }

    // ══════════════════════════════════════════
    // MODIFIER un étudiant
    // ══════════════════════════════════════════
    @Transactional
    public Etudiant modifier(Long id, Etudiant data) {
        Etudiant etudiant = findById(id);
        etudiant.setNom(data.getNom());
        etudiant.setPrenom(data.getPrenom());
        etudiant.setTelephone(data.getTelephone());
        etudiant.setDateNaissance(data.getDateNaissance());
        etudiant.setNiveau(data.getNiveau());
        return etudiantRepo.save(etudiant);
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER un étudiant
    // ══════════════════════════════════════════
    @Transactional
    public Etudiant toggleActif(Long id) {
        Etudiant etudiant = findById(id);
        etudiant.setActive(!etudiant.isEnabled());
        return etudiantRepo.save(etudiant);
    }

    public Etudiant findById(Long id) {
        return etudiantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));
    }

    // ══════════════════════════════════════════
    // RECHERCHER avec filtre + année
    // ══════════════════════════════════════════
    public Page<Etudiant> rechercher(Long anneeId, String recherche, Pageable pageable) {
        anneeService.findById(anneeId);
        return etudiantRepo.searchByAnnee(anneeId, recherche, pageable);
    }

    // ══════════════════════════════════════════
    // GÉNÉRER le matricule
    // ══════════════════════════════════════════
    private String genererMatricule() {
        String year = String.valueOf(java.time.Year.now().getValue());
        long count = etudiantRepo.count() + 1;
        return String.format("ETU-%s-%05d", year, count);
    }

    // ══════════════════════════════════════════
    // RÉINITIALISER le mot de passe
    // ══════════════════════════════════════════
    @Transactional
    public void reinitialiserMotDePasse(Long id) {
        Etudiant etudiant = findById(id);
        // Mot de passe par défaut = matricule
        etudiant.setPassword(passwordEncoder.encode(etudiant.getMatricule()));
        etudiant.setFirstLogin(true);
        etudiantRepo.save(etudiant);
    }
}
