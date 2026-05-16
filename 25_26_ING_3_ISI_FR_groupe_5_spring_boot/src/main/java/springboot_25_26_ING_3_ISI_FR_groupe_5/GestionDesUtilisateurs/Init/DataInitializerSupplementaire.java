package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.AnneeAcademiqueRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.CycleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.EcoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.FiliereRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.NiveauRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.SemestreRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.SpecialiteRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.UERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.ProgrammationUERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.PermissionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;

@Component
@Order(2)  // S'exécute APRÈS DataInitializer (@Order(1))
@RequiredArgsConstructor
public class DataInitializerSupplementaire implements ApplicationRunner {

    // ============ Repositories déjà peuplés par DataInitializer ============
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

    private Institut thies;
    private Institut bambey;
    private Institut ucad;
    private Institut ugb;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 DÉMARRAGE DE L'INITIALISATION SUPPLÉMENTAIRE — NOUVEAUX INSTITUTS, UE, ÉTUDIANTS");
        System.out.println("=".repeat(80));

        // Récupération des instituts existants
        ucad = institutRepository.findByNomIgnoreCase("Université Cheikh Anta Diop")
                .orElseThrow(() -> new RuntimeException("UCAD introuvable ! DataInitializer doit être exécuté en premier."));
        ugb = institutRepository.findByNomIgnoreCase("Université Gaston Berger")
                .orElseThrow(() -> new RuntimeException("UGB introuvable ! DataInitializer doit être exécuté en premier."));

        // Récupération des rôles existants
        Role roleAdminInstitut = roleRepository.findByNom("ADMIN_INSTITUT")
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN_INSTITUT introuvable !"));
        Role roleEnseignant = roleRepository.findByNom("ENSEIGNANT")
                .orElseThrow(() -> new RuntimeException("Rôle ENSEIGNANT introuvable !"));
        Role roleEtudiant = roleRepository.findByNom("ETUDIANT")
                .orElseThrow(() -> new RuntimeException("Rôle ETUDIANT introuvable !"));
        Role roleAssistant = roleRepository.findByNom("ASSISTANT")
                .orElseThrow(() -> new RuntimeException("Rôle ASSISTANT introuvable !"));

        // ============================================
        // 1. NOUVEAUX INSTITUTS
        // ============================================
        thies = createInstitut("Université de Thiès", "Thiès", "Thiès, Sénégal",
                "contact@univ-thies.sn", "+221 33 123 45 72", "Thiès");
        bambey = createInstitut("Université de Bambey", "Bambey", "Bambey, Sénégal",
                "contact@uadb.sn", "+221 33 123 45 73", "Bambey");
        System.out.println("   ✅ [INSTITUTS] Université de Thiès + Université de Bambey créés");

        // ============================================
        // 2. NOUVELLES ÉCOLES
        // ============================================
        Ecole ensa = createEcole("École Nationale Supérieure d'Agriculture", "Thiès, Sénégal",
                "contact@ensa.sn", "+221 33 123 45 74", thies);
        Ecole ensut = createEcole("École Supérieure Polytechnique", "Thiès, Sénégal",
                "contact@ensut.sn", "+221 33 123 45 75", thies);
        Ecole iut = createEcole("Institut Universitaire de Technologie", "Bambey, Sénégal",
                "contact@iut.sn", "+221 33 123 45 76", bambey);
        Ecole fshs = createEcole("Faculté des Sciences de la Santé", "Bambey, Sénégal",
                "contact@fshs.sn", "+221 33 123 45 77", bambey);
        System.out.println("   ✅ [ÉCOLES] ENSA, ENSUT, IUT, FSHS créées");

        // ============================================
        // 3. CYCLES (récupération des cycles existants)
        // ============================================
        Cycle ingenieur = cycleRepository.findByTypeCycle(TypeCycle.INGENIEUR)
                .orElseThrow(() -> new RuntimeException("Cycle INGENIEUR introuvable !"));
        Cycle licence = cycleRepository.findByTypeCycle(TypeCycle.LICENCE)
                .orElseThrow(() -> new RuntimeException("Cycle LICENCE introuvable !"));
        Cycle master = cycleRepository.findByTypeCycle(TypeCycle.MASTER)
                .orElseThrow(() -> new RuntimeException("Cycle MASTER introuvable !"));

        // ============================================
        // 4. NOUVELLES FILIÈRES
        // ============================================
        Filiere agroalimentaire = createFiliere("Agroalimentaire", "AGRO", "Industries agroalimentaires", ensa, ingenieur);
        Filiere genieCivil = createFiliere("Génie Civil", "GC", "Infrastructures et bâtiments", ensut, ingenieur);
        Filiere electrotechnique = createFiliere("Électrotechnique", "ELEC", "Systèmes électriques", ensut, ingenieur);
        Filiere informatique = createFiliere("Informatique Appliquée", "INFO", "Informatique de gestion", iut, licence);
        Filiere biologie = createFiliere("Biologie", "BIO", "Sciences biologiques", fshs, master);
        Filiere gestion = createFiliere("Gestion des Entreprises", "GE", "Management", iut, licence);
        System.out.println("   ✅ [FILIÈRES] 6 nouvelles filières créées");

        // ============================================
        // 5. NOUVELLES SPÉCIALITÉS
        // ============================================
        Specialite agroTransformation = createSpecialite("Agro-transformation", "AT", "Transformation des produits agricoles", agroalimentaire);
        Specialite structures = createSpecialite("Structures et Ouvrages", "SO", "Calcul de structures", genieCivil);
        Specialite securiteElectrique = createSpecialite("Sécurité Électrique", "SE", "Sécurité des installations électriques", electrotechnique);
        Specialite devMobile = createSpecialite("Développement Mobile", "DM", "Applications mobiles Android/iOS", informatique);
        Specialite santePublique = createSpecialite("Santé Publique", "SP", "Épidémiologie et prévention", biologie);
        System.out.println("   ✅ [SPÉCIALITÉS] 5 nouvelles spécialités créées");

        // ============================================
        // 6. NOUVEAUX NIVEAUX
        // ============================================
        Niveau ing1Thies = createNiveau("Ingénieur 1", "ING1", 1, genieCivil, structures);
        Niveau ing2Thies = createNiveau("Ingénieur 2", "ING2", 2, genieCivil, structures);
        Niveau l1Iut = createNiveau("Licence 1", "L1", 1, informatique, null);
        Niveau l2Iut = createNiveau("Licence 2", "L2", 2, informatique, null);
        Niveau m1Fshs = createNiveau("Master 1", "M1", 1, biologie, santePublique);
        Niveau m2Fshs = createNiveau("Master 2", "M2", 2, biologie, santePublique);
        System.out.println("   ✅ [NIVEAUX] 6 nouveaux niveaux créés");

        // ============================================
        // 7. NOUVELLES CLASSES
        // ============================================
        Classe ing1ThiesA = createClasse("ING1-GC-A", ing1Thies);
        Classe ing2ThiesA = createClasse("ING2-GC-A", ing2Thies);
        Classe l1IutA = createClasse("L1-INFO-A", l1Iut);
        Classe l1IutB = createClasse("L1-INFO-B", l1Iut);
        Classe m1FshsA = createClasse("M1-SP-A", m1Fshs);
        System.out.println("   ✅ [CLASSES] 5 nouvelles classes créées");

        // ============================================
        // 8. ANNÉES ACADÉMIQUES
        // ============================================
        Annee_academique annee2024_THIES = createAnneeAcademique("2024-2025",
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 6, 30), true, thies);
        Annee_academique annee2025_THIES = createAnneeAcademique("2025-2026",
                LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 30), false, thies);
        Annee_academique annee2024_BAMBEY = createAnneeAcademique("2024-2025",
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 6, 30), true, bambey);
        Annee_academique annee2025_BAMBEY = createAnneeAcademique("2025-2026",
                LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 30), false, bambey);
        System.out.println("   ✅ [ANNÉES ACADÉMIQUES] 2024-2025 & 2025-2026 pour Thiès et Bambey");

        // ============================================
        // 9. SEMESTRES
        // ============================================
        Semestre s1_2024_THIES = createSemestre(TypeSemestre.SEMESTRE_1,
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 1, 31), true, annee2024_THIES);
        Semestre s2_2024_THIES = createSemestre(TypeSemestre.SEMESTRE_2,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 30), false, annee2024_THIES);
        Semestre s1_2024_BAMBEY = createSemestre(TypeSemestre.SEMESTRE_1,
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 1, 31), true, annee2024_BAMBEY);
        Semestre s2_2024_BAMBEY = createSemestre(TypeSemestre.SEMESTRE_2,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 30), false, annee2024_BAMBEY);
        System.out.println("   ✅ [SEMESTRES] S1 & S2 pour Thiès et Bambey");

        // ============================================
        // 10. NOUVELLES UE (25 UE)
        // ============================================
        // Récupération de spécialités existantes dans UCAD
        Specialite ia = specialiteRepository.findByNomAndFiliereId("Intelligence Artificielle",
                filiereRepository.findByNomAndEcoleId("Génie Informatique",
                        ecoleRepository.findByNomAndInstitut_Id("École Supérieure d'Informatique", ucad.getId())
                                .orElseThrow().getId()).orElseThrow().getId()).orElse(null);
        Specialite cybersecurite = specialiteRepository.findByNomAndFiliereId("Cybersécurité",
                filiereRepository.findByNomAndEcoleId("Génie Informatique",
                        ecoleRepository.findByNomAndInstitut_Id("École Supérieure d'Informatique", ucad.getId())
                                .orElseThrow().getId()).orElseThrow().getId()).orElse(null);
        Specialite cloud = specialiteRepository.findByNomAndFiliereId("Cloud Computing",
                filiereRepository.findByNomAndEcoleId("Génie Informatique",
                        ecoleRepository.findByNomAndInstitut_Id("École Supérieure d'Informatique", ucad.getId())
                                .orElseThrow().getId()).orElseThrow().getId()).orElse(null);
        Specialite dataScience = specialiteRepository.findByNomAndFiliereId("Data Science",
                filiereRepository.findByNomAndEcoleId("Génie Informatique",
                        ecoleRepository.findByNomAndInstitut_Id("École Supérieure d'Informatique", ucad.getId())
                                .orElseThrow().getId()).orElseThrow().getId()).orElse(null);
        Specialite genieLogicielSpec = specialiteRepository.findByNomAndFiliereId(
                "Génie Logiciel", // Note : ici le code est GL, mais c'est une spécialité non créée dans DataInitializer
                filiereRepository.findByNomAndEcoleId("Génie Logiciel",
                        ecoleRepository.findByNomAndInstitut_Id("École Supérieure d'Informatique", ucad.getId())
                                .orElseThrow().getId()).orElseThrow().getId()).orElse(null);

        // Créons les UE pour les spécialités existantes (UCAD)
        if (ia != null) {
            createUE("Machine Learning", "ML701", "Apprentissage automatique avancé", "Advanced ML", ia);
            createUE("Deep Learning", "DL702", "Réseaux de neurones profonds", "Deep Neural Networks", ia);
            createUE("Traitement du Langage Naturel", "NLP703", "NLP et text mining", "Natural Language Processing", ia);
            createUE("Vision par Ordinateur", "CV705", "Computer vision avancée", "Advanced Computer Vision", ia);
        }
        if (dataScience != null) {
            createUE("Big Data", "BD704", "Technologies Big Data", "Big Data Technologies", dataScience);
        }
        if (cybersecurite != null) {
            createUE("Sécurité Réseau", "SEC601", "Sécurité des réseaux informatiques", "Network Security", cybersecurite);
            createUE("Cryptographie", "CRY602", "Cryptographie appliquée", "Applied Cryptography", cybersecurite);
            createUE("Audit de Sécurité", "AUD603", "Audit et tests d'intrusion", "Security Audit", cybersecurite);
        }
        if (cloud != null) {
            createUE("Virtualisation", "VIR801", "Virtualisation et conteneurisation", "Virtualization", cloud);
            createUE("DevOps", "DEV802", "Intégration et déploiement continus", "DevOps Practices", cloud);
            createUE("Architecture Cloud", "ARC803", "Architectures cloud natives", "Cloud Architecture", cloud);
        }
        if (genieLogicielSpec != null) {
            createUE("Modélisation UML", "UML501", "Conception orientée objet", "UML Modeling", genieLogicielSpec);
            createUE("Test Logiciel", "TST502", "Tests unitaires et intégration", "Software Testing", genieLogicielSpec);
            createUE("Méthodes Agiles", "AGI503", "Scrum, Kanban, XP", "Agile Methods", genieLogicielSpec);
        }

        // UE pour les nouvelles spécialités (Thiès)
        createUE("Chimie Alimentaire", "CHM201", "Biochimie des aliments", "Food Chemistry", agroTransformation);
        createUE("Microbiologie Alimentaire", "MIC202", "Microbiologie alimentaire", "Food Microbiology", agroTransformation);
        createUE("Résistance des Matériaux", "RDM301", "Calcul de structures", "Strength of Materials", structures);
        createUE("Béton Armé", "BA302", "Calcul béton armé", "Reinforced Concrete", structures);
        createUE("Circuits Électriques", "CEL401", "Circuits et systèmes électriques", "Electric Circuits", securiteElectrique);
        createUE("Automatisme Industriel", "AUT402", "Automatismes industriels", "Industrial Automation", securiteElectrique);

        // UE pour les nouvelles spécialités (Bambey)
        createUE("Bureautique Avancée", "BUR101", "Outils bureautiques", "Office Tools", devMobile);
        createUE("Réseaux Informatiques", "RES102", "Introduction aux réseaux", "Network Basics", devMobile);
        createUE("Programmation C", "PRG103", "Programmation en langage C", "C Programming", devMobile);
        createUE("Épidémiologie", "EPI901", "Épidémiologie fondamentale", "Basic Epidemiology", santePublique);
        createUE("Biostatistiques", "BST902", "Statistiques pour la santé", "Biostatistics", santePublique);

        System.out.println("   ✅ [UE] 25 nouvelles UE créées");

        // ============================================
        // 11. UTILISATEURS POUR LES NOUVEAUX INSTITUTS
        // ============================================
        createAdminInstitut(roleAdminInstitut, thies);
        createAdminInstitut(roleAdminInstitut, bambey);
        createEnseignant(roleEnseignant, thies);
        createEnseignant(roleEnseignant, bambey);
        createEtudiant(roleEtudiant, thies);
        createEtudiant(roleEtudiant, bambey);
        createFakeUsers(roleEnseignant, roleAssistant, thies);
        createFakeUsers(roleEnseignant, roleAssistant, bambey);
        System.out.println("   ✅ [UTILISATEURS] Admins, enseignants, étudiants, assistants pour Thiès et Bambey");

        // ============================================
        // 12. ÉTUDIANTS SUPPLÉMENTAIRES UCAD
        // ============================================
        List<String[]> etudiantsDataUCAD = List.of(
                new String[]{"Diop", "Fatou", "ETU-UCAD-2024-002", "0600000101", "2001-05-12"},
                new String[]{"Fall", "Mamadou", "ETU-UCAD-2024-003", "0600000102", "2002-08-23"},
                new String[]{"Sow", "Aminata", "ETU-UCAD-2024-004", "0600000103", "2003-01-15"},
                new String[]{"Seck", "Ibrahima", "ETU-UCAD-2024-005", "0600000104", "2001-11-30"},
                new String[]{"Ndiaye", "Adama", "ETU-UCAD-2024-006", "0600000105", "2002-03-07"},
                new String[]{"Mbaye", "Bineta", "ETU-UCAD-2024-007", "0600000106", "2003-07-19"},
                new String[]{"Gueye", "Ousmane", "ETU-UCAD-2024-008", "0600000107", "2001-09-25"},
                new String[]{"Diallo", "Khadija", "ETU-UCAD-2024-009", "0600000108", "2002-12-11"},
                new String[]{"Thiam", "Serigne", "ETU-UCAD-2024-010", "0600000109", "2003-04-03"},
                new String[]{"Ba", "Sokhna", "ETU-UCAD-2024-011", "0600000110", "2001-06-28"},
                new String[]{"Kane", "Moussa", "ETU-UCAD-2024-012", "0600000111", "2002-02-14"},
                new String[]{"Faye", "Aby", "ETU-UCAD-2024-013", "0600000112", "2003-10-08"},
                new String[]{"Lo", "Cheikh", "ETU-UCAD-2024-014", "0600000113", "2001-08-17"},
                new String[]{"Sarr", "Mariama", "ETU-UCAD-2024-015", "0600000114", "2002-05-22"},
                new String[]{"Cisse", "Babacar", "ETU-UCAD-2024-016", "0600000115", "2003-11-09"}
        );

        Annee_academique annee2024_UCAD = anneeRepository.findByNomAndInstitutId("2024-2025", ucad.getId())
                .orElseThrow();

        for (String[] data : etudiantsDataUCAD) {
            String email = data[0].toLowerCase() + "." + data[1].toLowerCase() + "@carnetrouge.com";
            if (utilisateurRepository.findByEmail(email).isEmpty()) {
                try {
                    Etudiant etu = Etudiant.builder()
                            .nom(data[0]).prenom(data[1]).email(email)
                            .password(passwordEncoder.encode("Etu123!")).telephone(data[3])
                            .dateNaissance(LocalDate.parse(data[4])).active(true)
                            .matricule(data[2]).createdAt(LocalDateTime.now())
                            .institut(ucad).roles(new HashSet<>(Set.of(roleEtudiant)))
                            .build();
                    utilisateurRepository.save(etu);
                    System.out.println("   ✅ [ÉTUDIANT UCAD] " + email);

                    // Inscription aléatoire dans une classe UCAD
                    List<Classe> classesUCAD = classeRepository.findAll().stream()
                            .filter(c -> c.getNiveau().getFiliere().getEcole().getInstitut().getId().equals(ucad.getId()))
                            .toList();
                    if (!classesUCAD.isEmpty()) {
                        Classe classeChoisie = classesUCAD.get((int) (Math.random() * classesUCAD.size()));
                        createInscription(etu, classeChoisie, annee2024_UCAD, StatutInscription.ACTIF, DecisionFinAnnee.ADMIS);
                    }
                } catch (Exception e) {
                    System.err.println("   ❌ Erreur création étudiant " + email + " : " + e.getMessage());
                }
            }
        }

        // ============================================
        // 13. ÉTUDIANTS THIÈS
        // ============================================
        List<String[]> etudiantsDataTHIES = List.of(
                new String[]{"Dieng", "Moustapha", "ETU-THIES-2024-001", "0600000201", "2002-04-10"},
                new String[]{"Ngom", "Rokhaya", "ETU-THIES-2024-002", "0600000202", "2001-07-15"},
                new String[]{"Sy", "Abdoulaye", "ETU-THIES-2024-003", "0600000203", "2003-01-20"},
                new String[]{"Ly", "Fatima", "ETU-THIES-2024-004", "0600000204", "2002-06-05"},
                new String[]{"Sene", "Modou", "ETU-THIES-2024-005", "0600000205", "2001-12-18"}
        );

        for (String[] data : etudiantsDataTHIES) {
            String email = data[0].toLowerCase() + "." + data[1].toLowerCase() + "@carnetrouge.com";
            if (utilisateurRepository.findByEmail(email).isEmpty()) {
                try {
                    Etudiant etu = Etudiant.builder()
                            .nom(data[0]).prenom(data[1]).email(email)
                            .password(passwordEncoder.encode("Etu123!")).telephone(data[3])
                            .dateNaissance(LocalDate.parse(data[4])).active(true)
                            .matricule(data[2]).createdAt(LocalDateTime.now())
                            .institut(thies).roles(new HashSet<>(Set.of(roleEtudiant)))
                            .build();
                    utilisateurRepository.save(etu);
                    System.out.println("   ✅ [ÉTUDIANT THIÈS] " + email);

                    List<Classe> classesTHIES = classeRepository.findAll().stream()
                            .filter(c -> c.getNiveau().getFiliere().getEcole().getInstitut().getId().equals(thies.getId()))
                            .toList();
                    if (!classesTHIES.isEmpty()) {
                        createInscription(etu, classesTHIES.get(0), annee2024_THIES, StatutInscription.ACTIF, DecisionFinAnnee.ADMIS);
                    }
                } catch (Exception e) {
                    System.err.println("   ❌ Erreur création étudiant " + email + " : " + e.getMessage());
                }
            }
        }

        // ============================================
        // 14. ÉTUDIANTS BAMBEY
        // ============================================
        List<String[]> etudiantsDataBAMBEY = List.of(
                new String[]{"Toure", "Aissatou", "ETU-BAMBEY-2024-001", "0600000301", "2003-03-05"},
                new String[]{"Camara", "Lamine", "ETU-BAMBEY-2024-002", "0600000302", "2002-09-12"},
                new String[]{"Keita", "Mariame", "ETU-BAMBEY-2024-003", "0600000303", "2001-11-25"},
                new String[]{"Barry", "Alpha", "ETU-BAMBEY-2024-004", "0600000304", "2002-04-30"},
                new String[]{"Sow", "Hawa", "ETU-BAMBEY-2024-005", "0600000305", "2003-07-14"}
        );

        for (String[] data : etudiantsDataBAMBEY) {
            String email = data[0].toLowerCase() + "." + data[1].toLowerCase() + "@carnetrouge.com";
            if (utilisateurRepository.findByEmail(email).isEmpty()) {
                try {
                    Etudiant etu = Etudiant.builder()
                            .nom(data[0]).prenom(data[1]).email(email)
                            .password(passwordEncoder.encode("Etu123!")).telephone(data[3])
                            .dateNaissance(LocalDate.parse(data[4])).active(true)
                            .matricule(data[2]).createdAt(LocalDateTime.now())
                            .institut(bambey).roles(new HashSet<>(Set.of(roleEtudiant)))
                            .build();
                    utilisateurRepository.save(etu);
                    System.out.println("   ✅ [ÉTUDIANT BAMBEY] " + email);

                    List<Classe> classesBAMBEY = classeRepository.findAll().stream()
                            .filter(c -> c.getNiveau().getFiliere().getEcole().getInstitut().getId().equals(bambey.getId()))
                            .toList();
                    if (!classesBAMBEY.isEmpty()) {
                        createInscription(etu, classesBAMBEY.get(0), annee2024_BAMBEY, StatutInscription.ACTIF, DecisionFinAnnee.ADMIS);
                    }
                } catch (Exception e) {
                    System.err.println("   ❌ Erreur création étudiant " + email + " : " + e.getMessage());
                }
            }
        }

        System.out.println("   ✅ [ÉTUDIANTS] 25 étudiants supplémentaires créés au total");

        // ============================================
        // 15. PROGRAMMATIONS UE (THIÈS)
        // ============================================
        List<Enseignant> enseignantsTHIES = enseignantRepository.findAll().stream()
                .filter(e -> e.getInstitut() != null && e.getInstitut().getId().equals(thies.getId()))
                .toList();

        if (!enseignantsTHIES.isEmpty()) {
            UE rdm = ueRepository.findByCode("RDM301").orElse(null);
            UE ba = ueRepository.findByCode("BA302").orElse(null);
            UE cel = ueRepository.findByCode("CEL401").orElse(null);

            if (rdm != null) createProgrammationUE(rdm, s1_2024_THIES, ing1ThiesA, 45L, 6L, Set.of(enseignantsTHIES.get(0).getId()));
            if (ba != null) createProgrammationUE(ba, s2_2024_THIES, ing1ThiesA, 60L, 8L, Set.of(enseignantsTHIES.get(0).getId()));
            if (cel != null) createProgrammationUE(cel, s1_2024_THIES, ing1ThiesA, 45L, 6L, Set.of(enseignantsTHIES.get(0).getId()));
        }

        // ============================================
        // 16. PROGRAMMATIONS UE (BAMBEY)
        // ============================================
        List<Enseignant> enseignantsBAMBEY = enseignantRepository.findAll().stream()
                .filter(e -> e.getInstitut() != null && e.getInstitut().getId().equals(bambey.getId()))
                .toList();

        if (!enseignantsBAMBEY.isEmpty()) {
            UE bur = ueRepository.findByCode("BUR101").orElse(null);
            UE res = ueRepository.findByCode("RES102").orElse(null);
            UE prg = ueRepository.findByCode("PRG103").orElse(null);
            UE epi = ueRepository.findByCode("EPI901").orElse(null);
            UE bst = ueRepository.findByCode("BST902").orElse(null);

            if (bur != null) createProgrammationUE(bur, s1_2024_BAMBEY, l1IutA, 30L, 4L, Set.of(enseignantsBAMBEY.get(0).getId()));
            if (res != null) createProgrammationUE(res, s1_2024_BAMBEY, l1IutB, 45L, 6L, Set.of(enseignantsBAMBEY.get(0).getId()));
            if (prg != null) createProgrammationUE(prg, s2_2024_BAMBEY, l1IutA, 60L, 8L, Set.of(enseignantsBAMBEY.get(0).getId()));
            if (epi != null) createProgrammationUE(epi, s1_2024_BAMBEY, m1FshsA, 45L, 6L, Set.of(enseignantsBAMBEY.get(0).getId()));
            if (bst != null) createProgrammationUE(bst, s2_2024_BAMBEY, m1FshsA, 30L, 4L, Set.of(enseignantsBAMBEY.get(0).getId()));
        }

        System.out.println("   ✅ [PROGRAMMATIONS] UE programmées pour Thiès et Bambey");

        System.out.println("\n✅ DataInitializerSupplémentaire — Deuxième vague d'initialisation terminée !");
        System.out.println("=".repeat(80) + "\n");
    }

    // ============================================
    // MÉTHODES DE CRÉATION (identiques à DataInitializer)
    // ============================================

    private Institut createInstitut(String nom, String ville, String adresse, String email, String telephone, String localite) {
        return institutRepository.findByNomIgnoreCase(nom)
                .orElseGet(() -> institutRepository.save(
                        Institut.builder().nom(nom).ville(ville).adresse(adresse).email(email).telephone(telephone).localite(localite).build()));
    }

    private Ecole createEcole(String nom, String adresse, String email, String telephone, Institut institut) {
        return ecoleRepository.findByNomAndInstitut_Id(nom, institut.getId())
                .orElseGet(() -> ecoleRepository.save(
                        Ecole.builder().nom(nom).adresse(adresse).email(email).telephone(telephone).institut(institut).build()));
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
    // UTILISATEURS (identiques à DataInitializer)
    // ============================================

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
                System.out.println("   ✅ [ADMIN_INSTITUT] " + email + " / Admin123!");
            } catch (Exception e) {
                System.err.println("   ❌ Erreur création Admin " + email + " : " + e.getMessage());
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

    private void createFakeUsers(Role roleEnseignant, Role roleAssistant, Institut institut) {
        String code = getCodeInstitut(institut);
        List<EnseignantData> enseignants = List.of(
                new EnseignantData("Kamga", "Paul", code + ".kamga@carnetrouge.com", "Professeur", "Permanent"),
                new EnseignantData("Nkomo", "Alice", code + ".nkomo@carnetrouge.com", "Maître de conférences", "Vacataire"),
                new EnseignantData("Biya", "Marc", code + ".biya@carnetrouge.com", "Docteur", "Permanent"));
        List<AssistantData> assistants = List.of(
                new AssistantData("Essama", "Julie", code + ".essama@carnetrouge.com", "Assistante administrative"),
                new AssistantData("Ateba", "Boris", code + ".ateba@carnetrouge.com", "Assistant pédagogique"));

        enseignants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                try {
                    utilisateurRepository.save(Enseignant.builder()
                            .nom(data.nom()).prenom(data.prenom()).email(data.email())
                            .password(passwordEncoder.encode("Ens123!")).telephone("06000000" + (10 + (int) (Math.random() * 90)))
                            .dateNaissance(LocalDate.of(2000, 9, 17)).active(true).grade(data.grade())
                            .typeEnseignant(data.type()).createdAt(LocalDateTime.now())
                            .institut(institut).roles(new HashSet<>(Set.of(roleEnseignant))).build());
                    System.out.println("   ✅ [ENSEIGNANT] " + data.email());
                } catch (Exception e) {
                    System.err.println("   ❌ Erreur création Enseignant " + data.email() + " : " + e.getMessage());
                }
            }
        });
        assistants.forEach(data -> {
            if (utilisateurRepository.findByEmail(data.email()).isEmpty()) {
                try {
                    utilisateurRepository.save(AssistantPedagogique.builder()
                            .nom(data.nom()).prenom(data.prenom()).email(data.email())
                            .password(passwordEncoder.encode("Ass123!")).telephone("06000000" + (10 + (int) (Math.random() * 90)))
                            .dateNaissance(LocalDate.of(1988, 9, 17)).active(true).fonction(data.fonction())
                            .createdAt(LocalDateTime.now()).institut(institut)
                            .roles(new HashSet<>(Set.of(roleAssistant))).build());
                    System.out.println("   ✅ [ASSISTANT] " + data.email());
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

    private record EnseignantData(String nom, String prenom, String email, String grade, String type) {}
    private record AssistantData(String nom, String prenom, String email, String fonction) {}
}
