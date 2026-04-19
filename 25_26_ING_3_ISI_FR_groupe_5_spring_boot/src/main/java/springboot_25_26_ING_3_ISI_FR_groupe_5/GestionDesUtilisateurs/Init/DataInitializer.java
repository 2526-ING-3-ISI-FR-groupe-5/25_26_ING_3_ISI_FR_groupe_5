package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeContrat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    // Repositories pour les entités métier
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

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("🚀 Début de l'initialisation des données...");

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

        // ============================================
        // 2. ROLES
        // ============================================
        Role roleAdmin = createOrUpdateRole("ADMIN", "Administrateur", true,
                Set.of(pRead, pWrite, pDelete, eRead, eWrite, eDelete, nRead, nWrite));
        Role roleEnseignant = createOrUpdateRole("ENSEIGNANT", "Enseignant", true,
                Set.of(eRead, nRead, nWrite));
        Role roleEtudiant = createOrUpdateRole("ETUDIANT", "Étudiant", true,
                Set.of(nRead));
        Role roleAssistant = createOrUpdateRole("ASSISTANT", "Assistant administratif / pédagogique", true,
                Set.of(eRead, nRead));

        // ============================================
        // 3. INSTITUTS
        // ============================================
        Institut ucad = createInstitut("Université Cheikh Anta Diop", "Dakar", "Dakar, Sénégal",
                "contact@ucad.sn", "+221 33 123 45 67", "Fann");
        Institut ugb = createInstitut("Université Gaston Berger", "Saint-Louis", "Saint-Louis, Sénégal",
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
        // 10. ANNEES ACADEMIQUES (une seule active)
        // ============================================
        // Désactiver toute année existante
        anneeRepository.findAll().forEach(a -> a.setActive(false));

        Annee_academique annee2024 = createAnneeAcademique("2024-2025",
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 6, 30), true);
        Annee_academique annee2025 = createAnneeAcademique("2025-2026",
                LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 30), false);

        // ============================================
        // 11. SEMESTRES
        // ============================================
        Semestre s1_2024 = createSemestre(TypeSemestre.SEMESTRE_1,
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 1, 31), true, annee2024);
        Semestre s2_2024 = createSemestre(TypeSemestre.SEMESTRE_2,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 30), false, annee2024);

        // ============================================
        // 12. UE (Unités d'Enseignement)
        // ============================================
        UE algo = createUE("Algorithmique Avancée", "ALGO401", "Algorithmes et structures de données",
                "Advanced Algorithms", ia);
        UE java = createUE("Java EE", "JEE501", "Développement d'applications Java EE",
                "Java Enterprise Edition", ia);
        UE python = createUE("Python Avancé", "PYT402", "Programmation Python avancée",
                "Advanced Python", ia);
        UE adminsys = createUE("Administration Systeme sous LINUX", "RES301", "Administration Systeme sous LINUX",
                "Computer Networks", ia);
        UE sql = createUE("Bases de Données", "SQL302", "Bases de données relationnelles",
                "Relational Databases", ia);
        UE devWeb = createUE("Développement Web", "WEB403", "Développement web full-stack",
                "Full-stack Web Development", ia);

        // ============================================
        // 13. PROGRAMMATIONS UE
        // ============================================
        // Récupérer les enseignants existants ou en créer
        List<Enseignant> enseignants = enseignantRepository.findAll();
        if (!enseignants.isEmpty()) {
            Enseignant enseignant1 = enseignants.get(0);
            Set<Long> enseignantIds = Set.of(enseignant1.getId());

            createProgrammationUE(algo, s1_2024, ing4A, 45L, 6L, enseignantIds);
            createProgrammationUE(java, s1_2024, ing4A, 60L, 8L, enseignantIds);
            createProgrammationUE(python, s2_2024, ing4A, 45L, 6L, enseignantIds);
            createProgrammationUE(adminsys, s1_2024, ing4B, 45L, 6L, enseignantIds);
            createProgrammationUE(sql, s2_2024, ing4B, 30L, 4L, enseignantIds);
            createProgrammationUE(devWeb, s2_2024, ing4B, 45L, 6L, enseignantIds);
        }

        // ============================================
        // 14. UTILISATEURS DE BASE
        // ============================================
        createAdminUser(roleAdmin);
        createEnseignant(roleEnseignant);
        createEtudiant(roleEtudiant);

        // ============================================
        // 15. INSCRIPTIONS
        // ============================================
        List<Etudiant> etudiants = etudiantRepository.findAll();
        if (!etudiants.isEmpty()) {
            Etudiant etudiant1 = etudiants.get(0);
            createInscription(etudiant1, ing4A, annee2024, StatutInscription.ACTIF, null);
        }

        // ============================================
        // 16. UTILISATEURS FICTIFS
        // ============================================
        createFakeUsers(roleEnseignant, roleAssistant);

        System.out.println("✅ DataInitializer — Toutes les données ont été initialisées avec succès !");
    }

    // ============================================
    // MÉTHODES DE CRÉATION
    // ============================================

    private Permission createOrUpdatePermission(String nom, String description) {
        return permissionRepository.findByNom(nom)
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder()
                                .nom(nom)
                                .description(description)
                                .active(true)
                                .creatAt(LocalDateTime.now())
                                .build()
                ));
    }

    private Role createOrUpdateRole(String nom, String description, boolean active, Set<Permission> permissions) {
        return roleRepository.findByNom(nom)
                .map(role -> {
                    role.setPermissions(new HashSet<>(permissions));
                    role.setActive(active);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .nom(nom)
                                .description(description)
                                .active(active)
                                .creatAt(LocalDateTime.now())
                                .permissions(new HashSet<>(permissions))
                                .build()
                ));
    }

    private Institut createInstitut(String nom, String ville, String adresse, String email, String telephone, String localite) {
        return institutRepository.findByNomIgnoreCase(nom)
                .orElseGet(() -> institutRepository.save(
                        Institut.builder()
                                .nom(nom)
                                .ville(ville)
                                .adresse(adresse)
                                .email(email)
                                .telephone(telephone)
                                .localite(localite)
                                .build()
                ));
    }

    private Ecole createEcole(String nom, String adresse, String email, String telephone, Institut institut) {
        return ecoleRepository.findByNomAndInstitutId(nom, institut.getId())
                .orElseGet(() -> ecoleRepository.save(
                        Ecole.builder()
                                .nom(nom)
                                .adresse(adresse)
                                .email(email)
                                .telephone(telephone)
                                .institut(institut)
                                .build()
                ));
    }

    private Cycle createCycle(TypeCycle typeCycle) {
        return cycleRepository.findByTypeCycle(typeCycle)
                .orElseGet(() -> cycleRepository.save(
                        Cycle.builder()
                                .typeCycle(typeCycle)
                                .build()
                ));
    }

    private Filiere createFiliere(String nom, String code, String description, Ecole ecole, Cycle cycle) {
        return filiereRepository.findByNomAndEcoleId(nom, ecole.getId())
                .orElseGet(() -> filiereRepository.save(
                        Filiere.builder()
                                .nom(nom)
                                .code(code)
                                .description(description)
                                .ecole(ecole)
                                .cycle(cycle)
                                .build()
                ));
    }

    private Specialite createSpecialite(String nom, String code, String description, Filiere filiere) {
        return specialiteRepository.findByNomAndFiliereId(nom, filiere.getId())
                .orElseGet(() -> specialiteRepository.save(
                        Specialite.builder()
                                .nom(nom)
                                .code(code)
                                .description(description)
                                .filiere(filiere)
                                .build()
                ));
    }

    private Niveau createNiveau(String nom, String code, Integer ordre, Filiere filiere, Specialite specialite) {
        return niveauRepository.findByNomAndFiliereId(nom, filiere.getId())
                .orElseGet(() -> niveauRepository.save(
                        Niveau.builder()
                                .nom(nom)
                                .code(code)
                                .ordre(ordre)
                                .filiere(filiere)
                                .specialite(specialite)
                                .build()
                ));
    }

    private Classe createClasse(String nom, Niveau niveau) {
        return classeRepository.findByNomAndNiveauId(nom, niveau.getId())
                .orElseGet(() -> classeRepository.save(
                        Classe.builder()
                                .nom(nom)
                                .niveau(niveau)
                                .build()
                ));
    }

    private Annee_academique createAnneeAcademique(String nom, LocalDate dateDebut, LocalDate dateFin, boolean active) {
        return anneeRepository.findByNom(nom)
                .orElseGet(() -> anneeRepository.save(
                        Annee_academique.builder()
                                .nom(nom)
                                .dateDebut(dateDebut)
                                .dateFin(dateFin)
                                .active(active)
                                .build()
                ));
    }

    private Semestre createSemestre(TypeSemestre typeSemestre, LocalDate dateDebut, LocalDate dateFin, boolean actif, Annee_academique annee) {
        return semestreRepository.findByAnneeAcademiqueIdAndTypeSemestre(annee.getId(), typeSemestre)
                .orElseGet(() -> semestreRepository.save(
                        Semestre.builder()
                                .typeSemestre(typeSemestre)
                                .dateDebut(dateDebut)
                                .dateFin(dateFin)
                                .actif(actif)
                                .anneeAcademique(annee)
                                .build()
                ));
    }

    private UE createUE(String nom, String code, String libelle, String libelleAnglais, Specialite specialite) {
        return ueRepository.findByCode(code)
                .orElseGet(() -> ueRepository.save(
                        UE.builder()
                                .nom(nom)
                                .code(code)
                                .libelle(libelle)
                                .libelleAnglais(libelleAnglais)
                                .specialite(specialite)
                                .build()
                ));
    }

    private ProgrammationUE createProgrammationUE(UE ue, Semestre semestre, Classe classe, Long dheure, Long nbrCredit, Set<Long> enseignantIds) {
        if (programmationRepository.existsByUeIdAndClasseIdAndSemestreId(ue.getId(), classe.getId(), semestre.getId())) {
            return null;
        }

        Set<Enseignant> enseignants = new HashSet<>();
        for (Long ensId : enseignantIds) {
            enseignantRepository.findById(ensId).ifPresent(enseignants::add);
        }

        ProgrammationUE programmation = ProgrammationUE.builder()
                .ue(ue)
                .semestre(semestre)
                .classe(classe)
                .dheure(dheure)
                .nbrCredit(nbrCredit)
                .enseignants(enseignants)
                .libelle(ue.getLibelle())
                .libelleAnglais(ue.getLibelleAnglais())
                .build();

        return programmationRepository.save(programmation);
    }

    private Inscription createInscription(Etudiant etudiant, Classe classe, Annee_academique annee, StatutInscription statut, String decisionFinAnnee) {
        if (inscriptionRepository.existsByEtudiantIdAndAnneeAcademiqueId(etudiant.getId(), annee.getId())) {
            return null;
        }

        Inscription inscription = Inscription.builder()
                .etudiant(etudiant)
                .classe(classe)
                .anneeAcademique(annee)
                .statut(statut)
                .decisionFinAnnee(decisionFinAnnee)
                .build();

        return inscriptionRepository.save(inscription);
    }

    // ============================================
    // UTILISATEURS
    // ============================================

    private void createAdminUser(Role role) {
        if (utilisateurRepository.findByEmail("admin@carnetrouge.com").isEmpty()) {
            Enseignant adminUser = Enseignant.builder()
                    .nom("Dupont")
                    .prenom("Jean")
                    .email("admin@carnetrouge.com")
                    .password(passwordEncoder.encode("Admin123!"))
                    .telephone("0600000001")
                    .dateNaissance(LocalDate.of(1990, 9, 17))
                    .active(true)
                    .grade("Administrateur")
                    .typeEnseignant("Permanent")
                    .createdAt(LocalDateTime.now())
                    .roles(new HashSet<>(Set.of(role)))
                    .build();
            utilisateurRepository.save(adminUser);
        }
    }

    private void createEnseignant(Role role) {
        if (utilisateurRepository.findByEmail("enseignant@carnetrouge.com").isEmpty()) {
            Enseignant enseignant = Enseignant.builder()
                    .nom("Martin")
                    .prenom("Sophie")
                    .email("enseignant@carnetrouge.com")
                    .password(passwordEncoder.encode("Ens123!"))
                    .telephone("0600000002")
                    .dateNaissance(LocalDate.of(1988, 9, 17))
                    .active(true)
                    .grade("Maître de conférences")
                    .typeEnseignant("Permanent")
                    .createdAt(LocalDateTime.now())
                    .roles(new HashSet<>(Set.of(role)))
                    .build();
            utilisateurRepository.save(enseignant);
        }
    }

    private void createEtudiant(Role role) {
        if (utilisateurRepository.findByEmail("etudiant@carnetrouge.com").isEmpty()) {
            Etudiant etudiant = Etudiant.builder()
                    .nom("Bernard")
                    .prenom("Lucas")
                    .email("etudiant@carnetrouge.com")
                    .password(passwordEncoder.encode("Etu123!"))
                    .telephone("0600000003")
                    .dateNaissance(LocalDate.of(2003, 11, 28))
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .roles(new HashSet<>(Set.of(role)))
                    .build();
            utilisateurRepository.save(etudiant);
        }
    }

    private void createFakeUsers(Role roleEnseignant, Role roleAssistant) {
        // Enseignants fictifs
        record EnseignantData(String nom, String prenom, String email, String grade, String type) {}
        List<EnseignantData> enseignants = List.of(
                new EnseignantData("Kamga", "Paul", "kamga@carnetrouge.com", "Professeur", "Permanent"),
                new EnseignantData("Nkomo", "Alice", "nkomo@carnetrouge.com", "Maître de conférences", "Vacataire"),
                new EnseignantData("Biya", "Marc", "biya@carnetrouge.com", "Docteur", "Permanent"),
                new EnseignantData("Fouda", "Claire", "fouda@carnetrouge.com", "Maître assistant", "Permanent"),
                new EnseignantData("Mbarga", "Samuel", "mbarga@carnetrouge.com", "Professeur agrégé", "Vacataire"),
                new EnseignantData("Billong", "Jean", "billong@carnetrouge.com", "Docteur", "Permanent"),
                new EnseignantData("Pessa", "Marie", "pessa@carnetrouge.com", "Maître assistant", "Permanent"),
                new EnseignantData("Mballa", "Paul", "mballa@carnetrouge.com", "Professeur", "Permanent"),
                new EnseignantData("Nitcheu", "Alice", "nitcheu@carnetrouge.com", "Maître de conférences", "Vacataire"),
                new EnseignantData("Mboa", "Jean", "mboa@carnetrouge.com", "Docteur", "Permanent"),
                new EnseignantData("Biamou", "Marc", "biamou@carnetrouge.com", "Maître assistant", "Permanent")
        );

        // Assistants fictifs
        record AssistantData(String nom, String prenom, String email, String fonction) {}
        List<AssistantData> assistants = List.of(
                new AssistantData("Essama", "Julie", "essama@carnetrouge.com", "Assistante administrative"),
                new AssistantData("Ateba", "Boris", "ateba@carnetrouge.com", "Assistant pédagogique"),
                new AssistantData("Ondoa", "Grace", "ondoa@carnetrouge.com", "Assistante de direction"),
                new AssistantData("Mbassi", "Kevin", "mbassi@carnetrouge.com", "Assistant informatique"),
                new AssistantData("Zang", "Nadine", "zang@carnetrouge.com", "Assistante RH")
        );

        // Surveillants fictifs
        record SurveillantData(String nom, String prenom, String email, String secteur, String typeContrat) {}
        List<SurveillantData> surveillants = List.of(
                new SurveillantData("Essama", "Julie", "essama2@carnetrouge.com", "CPGE", "CDI"),
                new SurveillantData("Dupont", "Marie", "marie.dupont@carnetrouge.com", "CPGE", "CDI"),
                new SurveillantData("Ndiaye", "Amadou", "amadou.ndiaye@carnetrouge.com", "Terminale", "CDD"),
                new SurveillantData("Kamdem", "Paul", "paul.kamdem@carnetrouge.com", "Seconde", "CDI")
        );

        surveillants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                String tel = "05658498" + (10 + (int) (Math.random() * 90));
                utilisateurRepository.save(
                        Surveillant.builder()
                                .nom(data.nom())
                                .prenom(data.prenom())
                                .email(data.email())
                                .password(passwordEncoder.encode("Ens123!"))
                                .telephone(tel)
                                .dateNaissance(LocalDate.of(2000, 10, 17))
                                .active(true)
                                .createdAt(LocalDateTime.now())
                                .secteur(data.secteur())
                                .typeContrat(TypeContrat.valueOf(data.typeContrat()))
                                .roles(new HashSet<>(Set.of(roleEnseignant)))
                                .build()
                );
            }
        });

        enseignants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                String tel = "06000000" + (10 + (int) (Math.random() * 90));
                utilisateurRepository.save(
                        Enseignant.builder()
                                .nom(data.nom())
                                .prenom(data.prenom())
                                .email(data.email())
                                .password(passwordEncoder.encode("Ens123!"))
                                .telephone(tel)
                                .dateNaissance(LocalDate.of(2000, 9, 17))
                                .active(true)
                                .grade(data.grade())
                                .typeEnseignant(data.type())
                                .createdAt(LocalDateTime.now())
                                .roles(new HashSet<>(Set.of(roleEnseignant)))
                                .build()
                );
            }
        });

        assistants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                String tel = "06000000" + (10 + (int) (Math.random() * 90));
                utilisateurRepository.save(
                        AssistantPedagogique.builder()
                                .nom(data.nom())
                                .prenom(data.prenom())
                                .email(data.email())
                                .password(passwordEncoder.encode("Ass123!"))
                                .telephone(tel)
                                .dateNaissance(LocalDate.of(1988, 9, 17))
                                .active(true)
                                .fonction(data.fonction())
                                .createdAt(LocalDateTime.now())
                                .roles(new HashSet<>(Set.of(roleAssistant)))
                                .build()
                );
            }
        });
    }
}