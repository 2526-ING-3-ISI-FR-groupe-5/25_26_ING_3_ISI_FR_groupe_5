/*
package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeContrat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.ProgrammationUERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Permission;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.PermissionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    // ✅ Repositories
    private final InstitutContexteActifRepository institutContexteActifRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstitutRepository institutRepository;
    private final EcoleRepository ecoleRepository;
    private final CycleRepository cycleRepository;
    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final NiveauRepository niveauRepository;
    private final ClassesRepository classeRepository;
    private final UERepository ueRepository;
    private final SemestreRepository semestreRepository;
    private final AnneeAcademiqueRepository anneeRepository;
    private final ProgrammationUERepository programmationRepository;
    private final EnseignantRepository enseignantRepository;
    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;

    private Institut ucad;
    private Institut ugb;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 DÉMARRAGE DE L'INITIALISATION DES DONNÉES DU CARNET ROUGE");
        System.out.println("=".repeat(80));

        // ============================================
        // 1. PERMISSIONS
        // ============================================
        Permission pRead = createOrUpdatePermission("enseignant:read", "Voir les enseignants");
        Permission pWrite = createOrUpdatePermission("enseignant:write", "Modifier les enseignants");
        Permission pDelete = createOrUpdatePermission("enseignant:delete", "Supprimer les enseignants");

        Permission eRead = createOrUpdatePermission("etudiant:read", "Voir les étudiants");
        Permission eWrite = createOrUpdatePermission("etudiant:write", "Modifier les étudiants");
        Permission eDelete = createOrUpdatePermission("etudiant:delete", "Supprimer les étudiants");

        Permission nRead = createOrUpdatePermission("note:read", "Voir les notes");
        Permission nWrite = createOrUpdatePermission("note:write", "Saisir / modifier les notes");

        Permission institutRead = createOrUpdatePermission("institut:read", "Voir les instituts");
        Permission institutWrite = createOrUpdatePermission("institut:write", "Gérer les instituts");
        Permission institutDelete = createOrUpdatePermission("institut:delete", "Supprimer les instituts");

        Permission appelRead = createOrUpdatePermission("appel:read", "Voir les appels");
        Permission appelWrite = createOrUpdatePermission("appel:write", "Faire l'appel");
        Permission appelManage = createOrUpdatePermission("appel:manage", "Gérer les appels");

        Permission justifRead = createOrUpdatePermission("justificatif:read", "Voir les justificatifs");
        Permission justifWrite = createOrUpdatePermission("justificatif:write", "Soumettre un justificatif");
        Permission justifManage = createOrUpdatePermission("justificatif:manage", "Gérer les justificatifs");

        Permission presenceRead = createOrUpdatePermission("presence:read", "Voir ses présences");

        // ============================================
        // 2. ROLES
        // ============================================
        Role roleSuperAdmin = createOrUpdateRole("SUPER_ADMIN", "Super Administrateur - Tous les instituts",
                Set.of(pRead, pWrite, pDelete, eRead, eWrite, eDelete, nRead, nWrite,
                        institutRead, institutWrite, institutDelete,
                        appelRead, appelWrite, appelManage,
                        justifRead, justifWrite, justifManage, presenceRead));

        Role roleAdminInstitut = createOrUpdateRole("ADMIN_INSTITUT", "Administrateur d'institut",
                Set.of(pRead, pWrite, pDelete, eRead, eWrite, eDelete, nRead, nWrite,
                        appelRead, appelManage, justifRead, justifManage, presenceRead));

        Role roleEnseignant = createOrUpdateRole("ENSEIGNANT", "Enseignant",
                Set.of(eRead, nRead, nWrite, appelRead, appelWrite, appelManage,
                        justifRead, presenceRead));

        Role roleEtudiant = createOrUpdateRole("ETUDIANT", "Étudiant",
                Set.of(nRead, presenceRead, justifRead, justifWrite, appelRead));

        Role roleAssistant = createOrUpdateRole("ASSISTANT", "Assistant administratif / pédagogique",
                Set.of(eRead, nRead, justifRead, justifManage, presenceRead));

        // ============================================
        // 3. INSTITUTS
        // ============================================
        ucad = createInstitut("Université Cheikh Anta Diop", "Dakar", "Dakar, Sénégal",
                "contact@ucad.sn", "+221 33 123 45 67", "Fann");
        ugb = createInstitut("Université Gaston Berger", "Saint-Louis", "Saint-Louis, Sénégal",
                "contact@ugb.sn", "+221 33 123 45 68", "Sanar");

        // ============================================
        // 4. ECOLES
        // ============================================
        Ecole esi = createEcole("École Supérieure d'Informatique", "Dakar, Sénégal",
                "contact@esi.sn", "+221 33 123 45 69", ucad);
        Ecole fst = createEcole("Faculté des Sciences et Techniques", "Dakar, Sénégal",
                "contact@fst.sn", "+221 33 123 45 70", ucad);
        Ecole fsa = createEcole("Faculté des Sciences Appliquées", "Saint-Louis, Sénégal",
                "contact@fsa.sn", "+221 33 123 45 71", ugb);

        // ============================================
        // 5. CYCLES
        // ============================================
        Cycle licence = createCycle(TypeCycle.LICENCE);
        Cycle master = createCycle(TypeCycle.MASTER);
        Cycle doctorat = createCycle(TypeCycle.DOCTORAT);
        Cycle ingenieur = createCycle(TypeCycle.INGENIEUR);

        // ============================================
        // 6. FILIERES
        // ============================================
        Filiere genieInfo = createFiliere("Génie Informatique", "GI", "Formation en informatique générale", esi, ingenieur);
        Filiere genieLogiciel = createFiliere("Génie Logiciel", "GL", "Développement de logiciels", esi, ingenieur);
        Filiere reseaux = createFiliere("Réseaux et Télécommunications", "RT", "Infrastructures réseau", esi, ingenieur);
        Filiere maths = createFiliere("Mathématiques Appliquées", "MA", "Mathématiques et applications", fst, licence);
        Filiere physique = createFiliere("Physique", "PHY", "Physique fondamentale", fst, licence);
        Filiere chimie = createFiliere("Chimie", "CHM", "Chimie industrielle", fsa, master);

        // ============================================
        // 7. SPECIALITES
        // ============================================
        Specialite ia = createSpecialite("Intelligence Artificielle", "IA", "IA et Machine Learning", genieInfo);
        Specialite cybersecurite = createSpecialite("Cybersécurité", "CS", "Sécurité informatique", genieInfo);
        Specialite cloud = createSpecialite("Cloud Computing", "CC", "Infrastructure cloud", genieInfo);
        Specialite dataScience = createSpecialite("Data Science", "DS", "Analyse de données", genieInfo);

        // ============================================
        // 8. NIVEAUX
        // ============================================
        Niveau ing1 = createNiveau("Ingénieur 1", "ING1", 1, genieInfo, ia);
        Niveau ing2 = createNiveau("Ingénieur 2", "ING2", 2, genieInfo, ia);
        Niveau ing3 = createNiveau("Ingénieur 3", "ING3", 3, genieInfo, ia);
        Niveau ing4 = createNiveau("Ingénieur 4", "ING4", 4, genieInfo, ia);
        Niveau ing5 = createNiveau("Ingénieur 5", "ING5", 5, genieInfo, ia);
        Niveau licence1 = createNiveau("Licence 1", "L1", 1, maths, null);
        Niveau licence2 = createNiveau("Licence 2", "L2", 2, maths, null);
        Niveau licence3 = createNiveau("Licence 3", "L3", 3, maths, null);

        // ============================================
        // 9. CLASSES
        // ============================================
        Classe ing3A = createClasse("ING3-A", ing3);
        Classe ing3B = createClasse("ING3-B", ing3);
        Classe ing4A = createClasse("ING4-A", ing4);
        Classe ing4B = createClasse("ING4-B", ing4);
        Classe l3A = createClasse("L3-A", licence3);
        Classe l3B = createClasse("L3-B", licence3);

        // ============================================
        // 10. ANNEES ACADEMIQUES
        // ============================================
        // ✅ LIGNE DANGEREUSE SUPPRIMÉE : anneeRepository.findAll().forEach(a -> a.setActive(false));

        Annee_academique annee2024_UCAD = createAnneeAcademique("2024-2025",
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 6, 30), true, ucad);
        Annee_academique annee2025_UCAD = createAnneeAcademique("2025-2026",
                LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 30), false, ucad);

        Annee_academique annee2024_UGB = createAnneeAcademique("2024-2025",
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 6, 30), true, ugb);
        Annee_academique annee2025_UGB = createAnneeAcademique("2025-2026",
                LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 30), false, ugb);

        // ============================================
        // 11. SEMESTRES
        // ============================================
        Semestre s1_2024_UCAD = createSemestre(TypeSemestre.SEMESTRE_1,
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 1, 31), true, annee2024_UCAD);
        Semestre s2_2024_UCAD = createSemestre(TypeSemestre.SEMESTRE_2,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 30), false, annee2024_UCAD);

        Semestre s1_2024_UGB = createSemestre(TypeSemestre.SEMESTRE_1,
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 1, 31), true, annee2024_UGB);
        Semestre s2_2024_UGB = createSemestre(TypeSemestre.SEMESTRE_2,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 30), false, annee2024_UGB);

        // ✅ INITIALISATION DU CONTEXTE ACTIF (après création des années/semestres)
        initialiserContexteActif(ucad, annee2024_UCAD, s1_2024_UCAD);
        initialiserContexteActif(ugb, annee2024_UGB, s1_2024_UGB);

        // ============================================
        // 12. UE
        // ============================================
        UE algo = createUE("Algorithmique Avancée", "ALGO401", "Algorithmes et structures de données", "Advanced Algorithms", ia);
        UE java = createUE("Java EE", "JEE501", "Développement d'applications Java EE", "Java Enterprise Edition", ia);
        UE python = createUE("Python Avancé", "PYT402", "Programmation Python avancée", "Advanced Python", ia);
        UE adminsys = createUE("Administration Systeme sous LINUX", "RES301", "Administration Systeme sous LINUX", "Computer Networks", ia);
        UE sql = createUE("Bases de Données", "SQL302", "Bases de données relationnelles", "Relational Databases", ia);
        UE devWeb = createUE("Développement Web", "WEB403", "Développement web full-stack", "Full-stack Web Development", ia);

        // ============================================
        // 13. UTILISATEURS DE BASE
        // ============================================
        createSuperAdmin(roleSuperAdmin);
        createAdminInstitut(roleAdminInstitut, ucad);
        createAdminInstitut(roleAdminInstitut, ugb);
        createEnseignant(roleEnseignant, ucad);
        createEnseignant(roleEnseignant, ugb);
        createEtudiant(roleEtudiant, ucad);
        createEtudiant(roleEtudiant, ugb);

        // ============================================
        // 14. UTILISATEURS DE TEST
        // ============================================
        createEnseignantTest(roleEnseignant, ucad);
        createEtudiantTest(roleEtudiant, ucad);

        // ============================================
        // 15. UTILISATEURS FICTIFS
        // ============================================
        createFakeUsers(roleEnseignant, roleAssistant, ucad);
        createFakeUsers(roleEnseignant, roleAssistant, ugb);

        // ============================================
        // 16. PROGRAMMATIONS UE
        // ============================================
        List<Enseignant> enseignantsUCAD = enseignantRepository.findAll().stream()
                .filter(e -> e.getInstitut() != null && e.getInstitut().getId().equals(ucad.getId()))
                .toList();

        if (!enseignantsUCAD.isEmpty()) {
            Enseignant enseignant1 = enseignantsUCAD.get(0);
            Set<Long> enseignantIds = Set.of(enseignant1.getId());
            createProgrammationUE(algo, s1_2024_UCAD, ing4A, 45L, 6L, enseignantIds);
            createProgrammationUE(java, s1_2024_UCAD, ing4A, 60L, 8L, enseignantIds);
            createProgrammationUE(python, s2_2024_UCAD, ing4A, 45L, 6L, enseignantIds);
            createProgrammationUE(adminsys, s1_2024_UCAD, ing4B, 45L, 6L, enseignantIds);
            createProgrammationUE(sql, s2_2024_UCAD, ing4B, 30L, 4L, enseignantIds);
            createProgrammationUE(devWeb, s2_2024_UCAD, ing4B, 45L, 6L, enseignantIds);
        }

        // ============================================
        // 17. INSCRIPTIONS
        // ============================================
        List<Etudiant> etudiantsUCAD = etudiantRepository.findAll().stream()
                .filter(e -> e.getInstitut() != null && e.getInstitut().getId().equals(ucad.getId()))
                .toList();

        if (!etudiantsUCAD.isEmpty()) {
            Etudiant etudiant1 = etudiantsUCAD.get(0);
            createInscription(etudiant1, ing4A, annee2024_UCAD, StatutInscription.ACTIF, DecisionFinAnnee.ADMIS);
        }

        System.out.println("\n✅ DataInitializer — Toutes les données ont été initialisées avec succès !");
        System.out.println("=".repeat(80) + "\n");
    }

    // ============================================
    // MÉTHODES DE CRÉATION
    // ============================================

    private Permission createOrUpdatePermission(String nom, String description) {
        return permissionRepository.findByNom(nom)
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder().nom(nom).description(description).active(true).createdAt(LocalDateTime.now()).build()));
    }

    private Role createOrUpdateRole(String nom, String description, Set<Permission> permissions) {
        return roleRepository.findByNom(nom)
                .map(role -> { role.setPermissions(new HashSet<>(permissions)); role.setActive(true); return roleRepository.save(role); })
                .orElseGet(() -> roleRepository.save(
                        Role.builder().nom(nom).description(description).active(true).createdAt(LocalDateTime.now()).permissions(new HashSet<>(permissions)).build()));
    }

    private Institut createInstitut(String nom, String ville, String adresse, String email, String telephone, String localite) {
        return institutRepository.findByNomIgnoreCase(nom)
                .orElseGet(() -> institutRepository.save(
                        Institut.builder().nom(nom).ville(ville).adresse(adresse).email(email).telephone(telephone).localite(localite).build()));
    }

    private Ecole createEcole(String nom, String adresse, String email, String telephone, Institut institut) {
        return ecoleRepository.findByNomAndInstitut_Id(nom, institut.getId())
                .orElseGet(() -> ecoleRepository.save(
                        Ecole.builder()
                                .nom(nom)
                                .adresse(adresse)
                                .email(email)
                                .telephone(telephone)
                                .institut(institut)
                                .build()));
    }

    private Cycle createCycle(TypeCycle typeCycle) {
        return cycleRepository.findByTypeCycle(typeCycle)
                .orElseGet(() -> cycleRepository.save(Cycle.builder().typeCycle(typeCycle).build()));
    }

    private Filiere createFiliere(String nom, String code, String description, Ecole ecole, Cycle cycle) {
        return filiereRepository.findByNomAndEcoleId(nom, ecole.getId())
                .orElseGet(() -> filiereRepository.save(
                        Filiere.builder().nom(nom).code(code).description(description).ecole(ecole).cycle(cycle).build()));
    }

    private Specialite createSpecialite(String nom, String code, String description, Filiere filiere) {
        return specialiteRepository.findByNomAndFiliereId(nom, filiere.getId())
                .orElseGet(() -> specialiteRepository.save(
                        Specialite.builder().nom(nom).code(code).description(description).filiere(filiere).build()));
    }

    private Niveau createNiveau(String nom, String code, Integer ordre, Filiere filiere, Specialite specialite) {
        return niveauRepository.findByNomAndFiliereId(nom, filiere.getId())
                .orElseGet(() -> niveauRepository.save(
                        Niveau.builder().nom(nom).code(code).ordre(ordre).filiere(filiere).specialite(specialite).build()));
    }

    private Classe createClasse(String nom, Niveau niveau) {
        return classeRepository.findByNomAndNiveauId(nom, niveau.getId())
                .orElseGet(() -> classeRepository.save(Classe.builder().nom(nom).niveau(niveau).build()));
    }

    private Annee_academique createAnneeAcademique(String nom, LocalDate dateDebut, LocalDate dateFin, boolean active, Institut institut) {
        return anneeRepository.findByNomAndInstitutId(nom, institut.getId())
                .orElseGet(() -> anneeRepository.save(
                        Annee_academique.builder().nom(nom).dateDebut(dateDebut).dateFin(dateFin).active(active).institut(institut).build()));
    }

    private Semestre createSemestre(TypeSemestre typeSemestre, LocalDate dateDebut, LocalDate dateFin, boolean actif, Annee_academique annee) {
        return semestreRepository.findByAnneeAcademiqueIdAndTypeSemestre(annee.getId(), typeSemestre)
                .orElseGet(() -> semestreRepository.save(
                        Semestre.builder().typeSemestre(typeSemestre).dateDebut(dateDebut).dateFin(dateFin).active(actif).anneeAcademique(annee).build()));
    }

    private UE createUE(String nom, String code, String libelle, String libelleAnglais, Specialite specialite) {
        return ueRepository.findByCode(code)
                .orElseGet(() -> ueRepository.save(
                        UE.builder().nom(nom).code(code).libelle(libelle).libelleAnglais(libelleAnglais).specialite(specialite).build()));
    }

    private ProgrammationUE createProgrammationUE(UE ue, Semestre semestre, Classe classe, Long dheure, Long nbrCredit, Set<Long> enseignantIds) {
        if (programmationRepository.existsByUeIdAndClasseIdAndSemestreId(ue.getId(), classe.getId(), semestre.getId())) return null;
        Set<Enseignant> enseignants = new HashSet<>();
        for (Long ensId : enseignantIds) enseignantRepository.findById(ensId).ifPresent(enseignants::add);
        return programmationRepository.save(
                ProgrammationUE.builder().ue(ue).semestre(semestre).classe(classe).dheure(dheure).nbrCredit(nbrCredit)
                        .enseignants(enseignants).libelle(ue.getLibelle()).libelleAnglais(ue.getLibelleAnglais()).build());
    }

    private Inscription createInscription(Etudiant etudiant, Classe classe, Annee_academique annee, StatutInscription statut, DecisionFinAnnee decision) {
        if (inscriptionRepository.existsByEtudiantIdAndAnneeAcademiqueId(etudiant.getId(), annee.getId())) return null;
        return inscriptionRepository.save(
                Inscription.builder().etudiant(etudiant).classe(classe).anneeAcademique(annee).statut(statut).decisionFinAnnee(decision).build());
    }

    // ============================================
    // UTILISATEURS
    // ============================================

    private void createSuperAdmin(Role role) {
        String email = "superadmin@carnetrouge.com";
        if (utilisateurRepository.findByEmail(email).isEmpty()) {
            try {
                utilisateurRepository.save(Enseignant.builder()
                        .nom("Super").prenom("Admin").email(email)
                        .password(passwordEncoder.encode("Super123!")).telephone("0600000000")
                        .dateNaissance(LocalDate.of(1985, 1, 1)).active(true)
                        .grade("Super Administrateur").typeEnseignant("Permanent").createdAt(LocalDateTime.now())
                        .institut(null).roles(new HashSet<>(Set.of(role))).build());
                System.out.println("   ✅ [SUPER_ADMIN] " + email + " / Super123!");
            } catch (Exception e) {
                System.err.println("   ❌ Erreur création Super Admin : " + e.getMessage());
            }
        }
    }

    private void createAdminInstitut(Role role, Institut institut) {
        String email = "admin." + getCodeInstitut(institut) + "@carnetrouge.com";
        if (utilisateurRepository.findByEmail(email).isEmpty()) {
            try {
                utilisateurRepository.save(Enseignant.builder()
                        .nom("Admin").prenom(institut.getNom()).email(email)
                        .password(passwordEncoder.encode("Admin123!")).telephone("0600000001")
                        .dateNaissance(LocalDate.of(1990, 1, 1)).active(true)
                        .grade("Administrateur d'institut").typeEnseignant("Permanent").createdAt(LocalDateTime.now())
                        .institut(institut).roles(new HashSet<>(Set.of(role))).build());
                System.out.println("   ✅ [ADMIN_INSTITUT] " + email + " / Admin123! (Institut: " + institut.getNom() + ")");
            } catch (Exception e) {
                System.err.println("   ❌ Erreur création Admin Institut " + email + " : " + e.getMessage());
            }
        }
    }

    private void createEnseignant(Role role, Institut institut) {
        String email = "enseignant." + getCodeInstitut(institut) + "@carnetrouge.com";
        if (utilisateurRepository.findByEmail(email).isEmpty()) {
            try {
                utilisateurRepository.save(Enseignant.builder()
                        .nom("Martin").prenom("Sophie").email(email)
                        .password(passwordEncoder.encode("Ens123!")).telephone("0600000002")
                        .dateNaissance(LocalDate.of(1988, 9, 17)).active(true)
                        .grade("Maître de conférences").typeEnseignant("Permanent").createdAt(LocalDateTime.now())
                        .institut(institut).roles(new HashSet<>(Set.of(role))).build());
                System.out.println("   ✅ [ENSEIGNANT] " + email + " / Ens123!");
            } catch (Exception e) {
                System.err.println("   ❌ Erreur création Enseignant " + email + " : " + e.getMessage());
            }
        }
    }

    private void createEtudiant(Role role, Institut institut) {
        String email = "etudiant." + getCodeInstitut(institut) + "@carnetrouge.com";
        String code = getCodeInstitut(institut);
        if (utilisateurRepository.findByEmail(email).isEmpty()) {
            try {
                utilisateurRepository.save(Etudiant.builder()
                        .nom("Bernard").prenom("Lucas").email(email)
                        .password(passwordEncoder.encode("Etu123!")).telephone("0600000003")
                        .dateNaissance(LocalDate.of(2003, 11, 28)).active(true)
                        .matricule("ETU-" + code.toUpperCase().replace(".", "") + "-2024-001").createdAt(LocalDateTime.now())
                        .institut(institut).roles(new HashSet<>(Set.of(role))).build());
                System.out.println("   ✅ [ETUDIANT] " + email + " / Etu123!");
            } catch (Exception e) {
                System.err.println("   ❌ Erreur création Étudiant " + email + " : " + e.getMessage());
            }
        }
    }

    private void createEnseignantTest(Role role, Institut institut) {
        String email = "prof.test@carnetrouge.com";
        if (utilisateurRepository.findByEmail(email).isEmpty()) {
            try {
                utilisateurRepository.save(Enseignant.builder()
                        .nom("Dupont").prenom("Jean").email(email)
                        .password(passwordEncoder.encode("Prof123!")).telephone("0600000100")
                        .dateNaissance(LocalDate.of(1985, 5, 15)).active(true)
                        .grade("Professeur titulaire").typeEnseignant("Permanent")
                        .specialite("Informatique").createdAt(LocalDateTime.now())
                        .institut(institut).roles(new HashSet<>(Set.of(role))).build());
                System.out.println("   ✅ [ENSEIGNANT TEST] " + email + " / Prof123!");
            } catch (Exception e) {
                System.err.println("   ❌ Erreur création Enseignant Test : " + e.getMessage());
            }
        }
    }

    private void createEtudiantTest(Role role, Institut institut) {
        String email = "etu.test@carnetrouge.com";
        if (utilisateurRepository.findByEmail(email).isEmpty()) {
            try {
                utilisateurRepository.save(Etudiant.builder()
                        .nom("Moreau").prenom("Alice").email(email)
                        .password(passwordEncoder.encode("Etu123!")).telephone("0600000200")
                        .dateNaissance(LocalDate.of(2002, 9, 22)).active(true)
                        .matricule("ETU-TEST-2024-001").createdAt(LocalDateTime.now())
                        .institut(institut).roles(new HashSet<>(Set.of(role))).build());
                System.out.println("   ✅ [ETUDIANT TEST] " + email + " / Etu123!");
            } catch (Exception e) {
                System.err.println("   ❌ Erreur création Étudiant Test : " + e.getMessage());
            }
        }
    }

    private void createFakeUsers(Role roleEnseignant, Role roleAssistant, Institut institut) {
        String code = getCodeInstitut(institut);

        List<EnseignantData> enseignants = List.of(
                new EnseignantData("Kamga", "Paul", code + ".kamga@carnetrouge.com", "Professeur", "Permanent"),
                new EnseignantData("Nkomo", "Alice", code + ".nkomo@carnetrouge.com", "Maître de conférences", "Vacataire"),
                new EnseignantData("Biya", "Marc", code + ".biya@carnetrouge.com", "Docteur", "Permanent"));

        List<AssistantData> assistants = List.of(
                new AssistantData("Essama", "Julie", code + ".essama@carnetrouge.com", "Assistante administrative"),
                new AssistantData("Ateba", "Boris", code + ".ateba@carnetrouge.com", "Assistant pédagogique"));

        List<SurveillantData> surveillants = List.of(
                new SurveillantData("Essama", "Julie", code + ".essama2@carnetrouge.com", "CPGE", "CDI"),
                new SurveillantData("Dupont", "Marie", code + ".dupont@carnetrouge.com", "CPGE", "CDI"));

        // Créer les surveillants
        surveillants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                try {
                    TypeContrat typeContrat = "CDI".equals(data.typeContrat()) ? TypeContrat.CDI : TypeContrat.CDD;
                    utilisateurRepository.save(Surveillant.builder()
                            .nom(data.nom()).prenom(data.prenom()).email(data.email())
                            .password(passwordEncoder.encode("Ens123!")).telephone("05658498" + (10 + (int) (Math.random() * 90)))
                            .dateNaissance(LocalDate.of(2000, 10, 17)).active(true).createdAt(LocalDateTime.now())
                            .secteur(data.secteur()).typeContrat(typeContrat)
                            .institut(institut).roles(new HashSet<>(Set.of(roleEnseignant))).build());
                    System.out.println("   ✅ [SURVEILLANT] " + data.email());
                } catch (Exception e) {
                    System.err.println("   ❌ Erreur création Surveillant " + data.email() + " : " + e.getMessage());
                }
            }
        });

        // Créer les enseignants
        enseignants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                try {
                    utilisateurRepository.save(Enseignant.builder()
                            .nom(data.nom()).prenom(data.prenom()).email(data.email())
                            .password(passwordEncoder.encode("Ens123!")).telephone("06000000" + (10 + (int) (Math.random() * 90)))
                            .dateNaissance(LocalDate.of(2000, 9, 17)).active(true).grade(data.grade())
                            .typeEnseignant(data.type()).createdAt(LocalDateTime.now())
                            .institut(institut).roles(new HashSet<>(Set.of(roleEnseignant))).build());
                    System.out.println("   ✅ [ENSEIGNANT] " + data.email() + " (" + data.grade() + ")");
                } catch (Exception e) {
                    System.err.println("   ❌ Erreur création Enseignant " + data.email() + " : " + e.getMessage());
                }
            }
        });

        // Créer les assistants pédagogiques
        assistants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                try {
                    utilisateurRepository.save(AssistantPedagogique.builder()
                            .nom(data.nom()).prenom(data.prenom()).email(data.email())
                            .password(passwordEncoder.encode("Ass123!")).telephone("06000000" + (10 + (int) (Math.random() * 90)))
                            .dateNaissance(LocalDate.of(1988, 9, 17)).active(true).fonction(data.fonction())
                            .createdAt(LocalDateTime.now()).institut(institut)
                            .roles(new HashSet<>(Set.of(roleAssistant))).build());
                    System.out.println("   ✅ [ASSISTANT] " + data.email() + " (" + data.fonction() + ")");
                } catch (Exception e) {
                    System.err.println("   ❌ Erreur création Assistant " + data.email() + " : " + e.getMessage());
                }
            }
        });
    }

    private String getCodeInstitut(Institut institut) {
        return institut.getNom().toLowerCase()
                .replace("université ", "").replace("é", "e").replace("è", "e").replace("ê", "e")
                .replace("à", "a").replace("â", "a").replace("ô", "o").replace(" ", ".").replace("'", "");
    }

    // ============================================
    // ✅ NOUVELLE MÉTHODE : INITIALISATION CONTEXTE ACTIF
    // (Placée STRICTEMENT au niveau de la classe, après run())
    // ============================================
    private void initialiserContexteActif(Institut institut, Annee_academique annee, Semestre semestre) {
        if (semestre == null || annee == null || institut == null) return;

        var contexteOpt = institutContexteActifRepository.findByInstitutId(institut.getId());

        if (contexteOpt.isPresent()) {
            var ctx = contexteOpt.get();
            ctx.setAnneeAcademique(annee);
            ctx.setSemestre(semestre);
            ctx.setDerniereBascule(LocalDateTime.now());
            institutContexteActifRepository.save(ctx);
        } else {
            institutContexteActifRepository.save(InstitutContexteActif.builder()
                    .institut(institut)
                    .anneeAcademique(annee)
                    .semestre(semestre)
                    .derniereBascule(LocalDateTime.now())
                    .build());
        }
    }

    // ============================================
    // RECORDS POUR UTILISATEURS FICTIFS
    // ============================================
    private record EnseignantData(String nom, String prenom, String email, String grade, String type) {}
    private record AssistantData(String nom, String prenom, String email, String fonction) {}
    private record SurveillantData(String nom, String prenom, String email, String secteur, String typeContrat) {}
}*/
