package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.AnneeAcademiqueRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Initialisation de 50 étudiants pour tester la fonctionnalité d'APPEL.
 * S'exécute APRÈS DataInitializer (@Order(2)).
 */
@Component
@Order(3)
@RequiredArgsConstructor
public class StudentAppelDataInitializer implements ApplicationRunner {

    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final ClassesRepository classeRepository;
    private final AnneeAcademiqueRepository anneeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Données réalistes (contexte Sénégal/UCAD/UGB)
    private static final String[] PRENOMS = {
            "Moussa", "Aminata", "Ibrahima", "Fatou", "Ousmane", "Mariama", "Cheikh", "Awa",
            "Abdoulaye", "Ndèye", "Pape", "Khady", "Mamadou", "Sokhna", "Alioune", "Ramatoulaye",
            "Babacar", "Coumba", "Modou", "Aïssatou"
    };
    private static final String[] NOMS = {
            "Diallo", "Ndiaye", "Sow", "Diop", "Seck", "Mbaye", "Fall", "Gueye", "Faye", "Sy",
            "Ba", "Touré", "Camara", "Kane", "Ndour", "Sarr", "Diagne", "Basse", "Cissé", "Tall"
    };

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("\n🎓 DÉMARRAGE : Initialisation de 50 étudiants pour les appels...");

        // 1. Récupération des données existantes
        List<Classe> classes = classeRepository.findAll();
        if (classes.isEmpty()) {
            System.err.println("⚠️ Aucune classe trouvée. L'initialisation des étudiants est annulée.");
            return;
        }

        // ✅ CORRECTION ICI : On gère le cas où aucune année n'est active
        Annee_academique anneeActive = anneeRepository.findAll().stream()
                .filter(Annee_academique::isActive)
                .findFirst()
                .orElse(null);

        if (anneeActive == null) {
            System.err.println("⚠️ Aucune année académique active trouvée. Les inscriptions ne seront pas créées et '2025' sera utilisé pour les matricules.");
        } else {
            System.out.println("📅 Année cible active : " + anneeActive.getNom());
        }

        Role roleEtudiant = roleRepository.findByNom("ETUDIANT").orElse(null);
        if (roleEtudiant == null) {
            System.err.println("⚠️ Rôle ETUDIANT introuvable.");
            return;
        }

        int createdCount = 0;
        int skippedCount = 0;

        // Pré-calcul du préfixe de l'année pour éviter le NullPointerException plus bas
        String anneePrefix = (anneeActive != null) ? anneeActive.getNom().substring(0, 4) : "2025";

        // 2. Création des 50 étudiants
        for (int i = 0; i < 50; i++) {
            String prenom = PRENOMS[ThreadLocalRandom.current().nextInt(PRENOMS.length)];
            String nom = NOMS[ThreadLocalRandom.current().nextInt(NOMS.length)];
            String email = prenom.toLowerCase() + "." + nom.toLowerCase() + i + "@carnetrouge.com";

            // ✅ Utilisation du préfixe sécurisé ici
            String matricule = "ETU-" + anneePrefix + "-" + String.format("%03d", i + 1);

            // Évite les doublons
            if (etudiantRepository.findByEmail(email).isPresent()) {
                skippedCount++;
                continue;
            }

            // Répartition équitable dans les classes (round-robin)
            Classe classeCible = classes.get(i % classes.size());

            // Création Étudiant
            Etudiant etudiant = Etudiant.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .email(email)
                    .password(passwordEncoder.encode("Etu123!"))
                    .telephone("78" + ThreadLocalRandom.current().nextInt(1000000, 9999999))
                    .dateNaissance(LocalDate.of(
                            1998 + ThreadLocalRandom.current().nextInt(8), // 1998-2005
                            ThreadLocalRandom.current().nextInt(1, 13),
                            ThreadLocalRandom.current().nextInt(1, 29)
                    ))
                    .active(true)
                    .matricule(matricule)
                    .createdAt(LocalDateTime.now())
                    .institut(classeCible.getNiveau().getFiliere().getEcole().getInstitut())
                    .classe(classeCible)
                    .roles(new HashSet<>(Set.of(roleEtudiant)))
                    .build();

            etudiantRepository.save(etudiant);

            // 3. Création Inscription (si année active)
            if (anneeActive != null) {
                if (!inscriptionRepository.existsByEtudiantIdAndAnneeAcademiqueId(etudiant.getId(), anneeActive.getId())) {
                    inscriptionRepository.save(Inscription.builder()
                            .etudiant(etudiant)
                            .classe(classeCible)
                            .anneeAcademique(anneeActive)
                            .statut(StatutInscription.ACTIF)
                            .decisionFinAnnee(null) // Sera défini à la fin de l'année
                            .build());
                }
            }

            createdCount++;
        }

        System.out.println("✅ " + createdCount + " étudiants créés pour les tests d'appel.");
        System.out.println("⏭️ " + skippedCount + " étudiants déjà existants (ignorés).");
        System.out.println("🔑 Mot de passe par défaut : Etu123!");
    }
}