/*
package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.*;

import java.util.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.SemestreRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.SpecialiteRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.UERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.ProgrammationUERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;

@Component
@Order(3)
@RequiredArgsConstructor
public class ProgrammationUEInitializer implements ApplicationRunner {

    private final InstitutRepository institutRepository;
    private final SemestreRepository semestreRepository;
    private final UERepository ueRepository;
    private final SpecialiteRepository specialiteRepository;
    private final EnseignantRepository enseignantRepository;
    private final ProgrammationUERepository programmationRepository;
private  final  ClassesRepository classeRepository;
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("\n" + "=".repeat(80));
        System.out.println("📚 DÉMARRAGE DE LA PROGRAMMATION DES UE PAR CLASSE");
        System.out.println("=".repeat(80));

        // Récupérer tous les instituts
        List<Institut> instituts = institutRepository.findAll();

        for (Institut institut : instituts) {
            programmerUEsPourInstitut(institut);
        }

        System.out.println("\n✅ ProgrammationUEInitializer — Toutes les UE ont été programmées !");
        System.out.println("=".repeat(80) + "\n");
    }

    private void programmerUEsPourInstitut(Institut institut) {
        System.out.println("\n🏛️ Institut : " + institut.getNom());

        // Récupérer les classes de l'institut
        List<Classe> classes = classeRepository.findAll().stream()
                .filter(c -> c.getNiveau().getFiliere().getEcole().getInstitut().getId().equals(institut.getId()))
                .toList();

        if (classes.isEmpty()) {
            System.out.println("   ⚠️ Aucune classe trouvée pour cet institut");
            return;
        }

        // Récupérer les semestres de l'institut
        List<Semestre> semestres = semestreRepository.findAll().stream()
                .filter(s -> s.getAnneeAcademique().getInstitut().getId().equals(institut.getId()))
                .toList();

        Semestre s1 = semestres.stream()
                .filter(s -> s.getTypeSemestre() == TypeSemestre.SEMESTRE_1 && s.isActive())
                .findFirst()
                .orElseGet(() -> semestres.stream()
                        .filter(s -> s.getTypeSemestre() == TypeSemestre.SEMESTRE_1)
                        .findFirst()
                        .orElse(null));

        Semestre s2 = semestres.stream()
                .filter(s -> s.getTypeSemestre() == TypeSemestre.SEMESTRE_2)
                .findFirst()
                .orElse(null);

        if (s1 == null && s2 == null) {
            System.out.println("   ⚠️ Aucun semestre trouvé pour cet institut");
            return;
        }

        // Récupérer les enseignants de l'institut
        List<Enseignant> enseignants = enseignantRepository.findAll().stream()
                .filter(e -> e.getInstitut() != null && e.getInstitut().getId().equals(institut.getId()))
                .toList();

        if (enseignants.isEmpty()) {
            System.out.println("   ⚠️ Aucun enseignant trouvé pour cet institut");
            return;
        }

        // Pour chaque classe, programmer ses UE
        int totalProgrammations = 0;
        for (Classe classe : classes) {
            int nbProgrammees = programmerUEsPourClasse(classe, s1, s2, enseignants);
            totalProgrammations += nbProgrammees;
        }

        System.out.println("   ✅ Total : " + totalProgrammations + " programmations créées pour " + institut.getNom());
    }

    private int programmerUEsPourClasse(Classe classe, Semestre s1, Semestre s2, List<Enseignant> enseignants) {
        Niveau niveau = classe.getNiveau();
        Specialite specialite = niveau.getSpecialite();
        Filiere filiere = niveau.getFiliere();

        // Récupérer les UE de la spécialité, ou de la filière, ou toutes
        List<UE> uesDeLaClasse = trouverUEsPourClasse(specialite, filiere);

        if (uesDeLaClasse.isEmpty()) {
            System.out.println("   ⚠️ " + classe.getNom() + " : aucune UE trouvée");
            return 0;
        }

        int nbProgrammees = 0;
        int indexEns = 0;

        for (int i = 0; i < uesDeLaClasse.size(); i++) {
            UE ue = uesDeLaClasse.get(i);

            // Rotation des enseignants
            if (indexEns >= enseignants.size()) {
                indexEns = 0;
            }
            Set<Long> ensIds = Set.of(enseignants.get(indexEns).getId());

            // Alternance S1 / S2
            Semestre semestreCible = (i % 2 == 0 && s1 != null) ? s1 : s2;
            if (semestreCible == null) {
                semestreCible = (s1 != null) ? s1 : s2;
            }
            if (semestreCible == null) {
                continue;
            }

            // Calcul des crédits et volume horaire selon le niveau
            long credits;
            long volumeHoraire;

            switch (niveau.getOrdre()) {
                case 4, 5 -> {
                    credits = 6L + (long) (Math.random() * 3);
                    volumeHoraire = credits * 10;
                }
                case 3 -> {
                    credits = 5L + (long) (Math.random() * 3);
                    volumeHoraire = credits * 10;
                }
                case 2 -> {
                    credits = 4L + (long) (Math.random() * 3);
                    volumeHoraire = credits * 12;
                }
                default -> {
                    credits = 4L + (long) (Math.random() * 2);
                    volumeHoraire = credits * 12;
                }
            }

            // Créer la programmation
            if (createProgrammationUE(ue, semestreCible, classe, volumeHoraire, credits, ensIds) != null) {
                nbProgrammees++;
            }

            indexEns++;
        }

        System.out.println("   ✅ " + classe.getNom() + " : " + nbProgrammees + " UE programmées");
        return nbProgrammees;
    }

    private List<UE> trouverUEsPourClasse(Specialite specialite, Filiere filiere) {
        List<UE> ues = new ArrayList<>();

        if (specialite != null) {
            // UE de la spécialité
            ues.addAll(ueRepository.findAll().stream()
                    .filter(ue -> ue.getSpecialite() != null && ue.getSpecialite().getId().equals(specialite.getId()))
                    .toList());
        }

        // Si pas assez d'UE, chercher dans les autres spécialités de la filière
        if (ues.size() < 3 && filiere != null) {
            List<Specialite> specialitesFiliere = specialiteRepository.findAll().stream()
                    .filter(s -> s.getFiliere() != null && s.getFiliere().getId().equals(filiere.getId()))
                    .filter(s -> specialite == null || !s.getId().equals(specialite.getId()))
                    .toList();

            for (Specialite spec : specialitesFiliere) {
                ues.addAll(ueRepository.findAll().stream()
                        .filter(ue -> ue.getSpecialite() != null && ue.getSpecialite().getId().equals(spec.getId()))
                        .toList());
            }
        }

        // Si toujours pas assez, prendre toutes les UE
        if (ues.isEmpty()) {
            ues = ueRepository.findAll();
        }

        return ues;
    }

    private ProgrammationUE createProgrammationUE(UE ue, Semestre semestre, Classe classe, Long dheure, Long nbrCredit, Set<Long> enseignantIds) {
        // Vérifier si la programmation existe déjà
        if (programmationRepository.existsByUeIdAndClasseIdAndSemestreId(ue.getId(), classe.getId(), semestre.getId())) {
            return null;
        }

        Set<Enseignant> enseignants = new HashSet<>();
        for (Long ensId : enseignantIds) {
            enseignantRepository.findById(ensId).ifPresent(enseignants::add);
        }

        if (enseignants.isEmpty()) {
            return null;
        }

        return programmationRepository.save(
                ProgrammationUE.builder()
                        .ue(ue)
                        .semestre(semestre)
                        .classe(classe)
                        .dheure(dheure)
                        .nbrCredit(nbrCredit)
                        .enseignants(enseignants)
                        .libelle(ue.getLibelle())
                        .libelleAnglais(ue.getLibelleAnglais())
                        .build()
        );
    }
}*/
