package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enums.TypeSexe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.*;

import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final AdministrateurRepository administrateurRepository;
    private final EnseignantRepository enseignantRepository;
    private final SurveillantRepository surveillantRepository;

    public DataInitializer(
            UtilisateurRepository utilisateurRepository,
            RoleRepository roleRepository,
            AdministrateurRepository administrateurRepository,
            EnseignantRepository enseignantRepository,
            SurveillantRepository surveillantRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.administrateurRepository = administrateurRepository;
        this.enseignantRepository = enseignantRepository;
        this.surveillantRepository = surveillantRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si des données existent déjà
        if (utilisateurRepository.count() == 0) {
            // Créer des rôles
            Role adminRole = new Role();
            adminRole.setNom("ROLE_ADMIN");
            adminRole.setDescription("Rôle administrateur");
            adminRole.setActive(true);
            adminRole.setDateCreation(new Date());

            Role enseignantRole = new Role();
            enseignantRole.setNom("ROLE_ENSEIGNANT");
            enseignantRole.setDescription("Rôle enseignant");
            enseignantRole.setActive(true);
            enseignantRole.setDateCreation(new Date());

            Role surveillantRole = new Role();
            surveillantRole.setNom("ROLE_SURVEILLANT");
            surveillantRole.setDescription("Rôle surveillant");
            surveillantRole.setActive(true);
            surveillantRole.setDateCreation(new Date());

            roleRepository.saveAll(Arrays.asList(adminRole, enseignantRole, surveillantRole));

            // Créer 5 administrateurs
            for (int i = 1; i <= 5; i++) {
                Administrateur admin = new Administrateur();
                admin.setNom("Admin" + i);
                admin.setPrenom("User" + i);
                admin.setEmail("admin" + i + "@example.com");
                admin.setPassword("password" + i);
                admin.setSexe(TypeSexe.M);
                admin.setActive(true);
                admin.setDateCreation(new Date());
                admin.setFonction("Directeur");
                admin.setRoles(Set.of(adminRole));
                administrateurRepository.save(admin);
            }

            // Créer 10 enseignants
            for (int i = 1; i <= 10; i++) {
                Enseignant enseignant = new Enseignant();
                enseignant.setNom("Enseignant" + i);
                enseignant.setPrenom("User" + i);
                enseignant.setEmail("enseignant" + i + "@example.com");
                enseignant.setPassword("password" + i);
                enseignant.setSexe(TypeSexe.M);
                enseignant.setActive(true);
                enseignant.setDateCreation(new Date());
                enseignant.setGrade("Professeur");
                enseignant.setSpecialite("Informatique");
                enseignant.setTypeEnseignant("Permanent");
                enseignant.setRoles(Set.of(enseignantRole));
                enseignantRepository.save(enseignant);
            }

            // Créer 5 surveillants
            for (int i = 1; i <= 5; i++) {
                Surveillant surveillant = new Surveillant();
                surveillant.setNom("Surveillant" + i);
                surveillant.setPrenom("User" + i);
                surveillant.setEmail("surveillant" + i + "@example.com");
                surveillant.setPassword("password" + i);
                surveillant.setSexe(TypeSexe.M);
                surveillant.setActive(true);
                surveillant.setDateCreation(new Date());
                surveillant.setFonction("Surveillant général");
                surveillant.setRoles(Set.of(surveillantRole));
                surveillantRepository.save(surveillant);
            }

            System.out.println("20 utilisateurs ont été enregistrés avec succès.");
        } else {
            System.out.println("Des utilisateurs existent déjà. Aucune donnée n'a été ajoutée.");
        }
    }
}

