package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ActivePermissionRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ActiveRoleDTORequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.SurveillantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeContrat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.InterfaceServiceAdmin;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Notification.Services.EmailService;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UtilisateurService implements InterfaceServiceAdmin {

    private final EnseignantRepository enseignantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AssistantRepository assistantRepository;
    private final SurveillantRepository surveillantRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final EnseignantMapper enseignantMapper;
    private final AssistantMapper assistantMapper;
    private final SurveillantMapper surveillantMapper;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final EmailService emailService;
    private final ClassesRepository classesRepository;
    private final InstitutSecurityService securityService;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ══════════════════════════════════════════════
    // LISTER
    // ══════════════════════════════════════════════

    @Override
    public Page<UtilisateurResponse> listeTous(String recherche, String type, int page, int size) {
        Long institutId = securityService.getInstitutIdCourant();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return utilisateurRepository.searchWithFilters(recherche, type,  pageable)
                .map(utilisateurMapper::toDTO);
    }

    // ══════════════════════════════════════════════
    // TROUVER PAR ID
    // ══════════════════════════════════════════════

    @Override
    public UtilisateurResponse findById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé : " + id));
        return utilisateurMapper.toDTO(utilisateur);
    }

    // ══════════════════════════════════════════════
    // SUPPRIMER
    // ══════════════════════════════════════════════

    @Transactional
    @Override
    public void deleteUtilisateur(Long id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
    }

    // ══════════════════════════════════════════════
    // ACTIVER / DÉSACTIVER
    // ══════════════════════════════════════════════

    @Transactional
    @Override
    public void activerDesactiverUtilisateur(Long id, boolean activer) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + id));
        utilisateur.setActive(activer);
        utilisateurRepository.save(utilisateur);
    }

    // ══════════════════════════════════════════════
    // ENSEIGNANT
    // ══════════════════════════════════════════════

    @Override
    public Enseignant getById(Long id) {
        return enseignantRepository.findById(id)
                .orElseThrow(() -> new EnseignantNotFoundException("Enseignant non trouvé avec l'ID : " + id));
    }

    @Transactional
    @Override
    public Enseignant save(EnseignantRequest request) {
        if (enseignantRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("L'email est déjà utilisé : " + request.getEmail());
        }
        Enseignant enseignant = enseignantMapper.toEntity(request);
        enseignant.setInstitut(securityService.getInstitutCourant());
        return enseignantRepository.save(enseignant);
    }

    @Override
    public EnseignantResponseDetails EnsDetails(Long id) {
        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inexistant avec l'id : " + id));
        return enseignantMapper.toDtoDetails(enseignant);
    }

    // ══════════════════════════════════════════════
    // ASSISTANT
    // ══════════════════════════════════════════════

    @Transactional
    @Override
    public AssistantPedagogique saveAssistant(AssistantRequest request) {
        if (assistantRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("L'email est déjà utilisé : " + request.getEmail());
        }
        AssistantPedagogique assistant = assistantMapper.toEntity(request);
        String encodedPassword = passwordEncoder().encode(request.getPassword());
        assistant.setPassword(encodedPassword);
        assistant.setInstitut(securityService.getInstitutCourant());
        return assistantRepository.save(assistant);
    }

    @Override
    public AssistantResponseDetails AssDetails(Long id) {
        AssistantPedagogique assistant = assistantRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inexistant avec l'id : " + id));
        return assistantMapper.toDtoDetails(assistant);
    }

    // ══════════════════════════════════════════════
    // SURVEILLANT
    // ══════════════════════════════════════════════

    @Override
    public SurveillantResponseDetails SurDetails(Long id) {
        Surveillant surveillant = surveillantRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Surveillant inexistant avec l'id : " + id));
        return surveillantMapper.toDtoDetails(surveillant);
    }

    // ══════════════════════════════════════════════
    // RÔLES
    // ══════════════════════════════════════════════

    @Transactional
    @Override
    public ActiveRoleDTORequest activeRole(Long id, ActiveRoleDTORequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleIsNotExisteException("Le rôle n'existe pas"));
        roleMapper.updateRoleFromDTO(request, role);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toActiveRoleDTORequest(savedRole);
    }

    // ══════════════════════════════════════════════
    // PERMISSIONS
    // ══════════════════════════════════════════════

    @Transactional
    @Override
    public ActivePermissionRequest activePermissionRequest(Long id, ActivePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotExistException("Cette permission n'existe pas"));
        permissionMapper.updatePermission(request, permission);
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toActivePermmission(savedPermission);
    }

    // ══════════════════════════════════════════════
    // CRÉER UTILISATEUR
    // ══════════════════════════════════════════════

    @Transactional
    @Override
    public void creerUtilisateur(UtilisateurRequest request) {

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("L'email est déjà utilisé : " + request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RoleIsNotExisteException("Rôle introuvable : " + request.getRoleId()));

        if (request.getPermissionsDesactivees() != null) {
            role.getPermissions().forEach(p -> {
                if (request.getPermissionsDesactivees().contains(p.getId())) {
                    p.setActive(false);
                }
            });
        }

        String motDePasseBrut = genererMotDePasse();
        String motDePasseEncode = passwordEncoder().encode(motDePasseBrut);

        Utilisateur utilisateur = switch (request.getTypeUtilisateur()) {

            case "ENS" -> {
                Enseignant ens = new Enseignant();
                ens.setGrade(request.getGrade());
                ens.setTypeEnseignant(request.getTypeEnseignant());
                yield ens;
            }

            case "AST" -> {
                AssistantPedagogique ast = new AssistantPedagogique();
                ast.setFonction(request.getFonction());
                if (request.getClassesIds() != null && !request.getClassesIds().isEmpty()) {
                    List<Classe> classes = classesRepository.findAllById(request.getClassesIds());
                    ast.setClasses(classes);
                }
                yield ast;
            }

            case "SUR" -> {
                Surveillant sur = new Surveillant();
                sur.setSecteur(request.getSecteur());
                sur.setTypeContrat(TypeContrat.valueOf(request.getTypeContrat()));
                yield sur;
            }

            default -> throw new IllegalArgumentException("Type utilisateur invalide : " + request.getTypeUtilisateur());
        };

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setDateNaissance(request.getDateNaissance());
        utilisateur.setPassword(motDePasseEncode);
        utilisateur.setActive(true);
        utilisateur.setFirstLogin(true);
        utilisateur.setInstitut(securityService.getInstitutCourant());
        utilisateur.setRoles(new HashSet<>(Set.of(role)));

        utilisateurRepository.save(utilisateur);

        emailService.envoyerEmailBienvenue(
                request.getEmail(),
                request.getPrenom(),
                request.getNom(),
                motDePasseBrut,
                role.getNom()
        );
    }

    // ══════════════════════════════════════════════
    // GÉNÉRATION MOT DE PASSE
    // ══════════════════════════════════════════════

    private String genererMotDePasse() {
        String majuscules = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minuscules = "abcdefghijklmnopqrstuvwxyz";
        String chiffres = "0123456789";
        String speciaux = "@#$!%?&";
        String tous = majuscules + minuscules + chiffres + speciaux;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        sb.append(majuscules.charAt(random.nextInt(majuscules.length())));
        sb.append(minuscules.charAt(random.nextInt(minuscules.length())));
        sb.append(chiffres.charAt(random.nextInt(chiffres.length())));
        sb.append(speciaux.charAt(random.nextInt(speciaux.length())));

        for (int i = 4; i < 10; i++) {
            sb.append(tous.charAt(random.nextInt(tous.length())));
        }

        List<Character> chars = new ArrayList<>();
        for (char c : sb.toString().toCharArray()) chars.add(c);
        Collections.shuffle(chars, random);

        StringBuilder resultat = new StringBuilder();
        for (char c : chars) resultat.append(c);
        return resultat.toString();
    }
}