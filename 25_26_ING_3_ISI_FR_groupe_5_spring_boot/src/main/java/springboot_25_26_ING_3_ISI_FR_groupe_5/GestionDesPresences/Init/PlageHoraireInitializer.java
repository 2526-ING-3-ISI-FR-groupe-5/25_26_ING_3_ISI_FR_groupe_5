package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSeance;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.PlageHoraireRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.ProgrammationUERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(5)
@RequiredArgsConstructor
public class PlageHoraireInitializer implements ApplicationRunner {

    private final ProgrammationUERepository programmationRepository;
    private final PlageHoraireRepository plageHoraireRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("\n" + "=".repeat(80));
        System.out.println("📅 CRÉATION DES PLAGES HORAIRES");
        System.out.println("=".repeat(80));

        List<ProgrammationUE> programmations = programmationRepository.findAll();

        if (programmations.isEmpty()) {
            System.out.println("⚠️ Aucune programmation trouvée.");
            return;
        }

        int totalPlages = 0;

        for (ProgrammationUE prog : programmations) {
            Semestre semestre = prog.getSemestre();
            if (semestre == null || !semestre.isActive()) continue;

            LocalDate debut = semestre.getDateDebut();
            LocalDate fin = semestre.getDateFin();
            if (debut == null || fin == null) continue;

            Classe classe = prog.getClasse();
            Set<Enseignant> enseignants = prog.getEnseignants();
            if (enseignants.isEmpty()) continue;

            int plagesCreees = 0;
            LocalDate current = debut;

            while (!current.isAfter(fin)) {
                if (current.getDayOfWeek() != DayOfWeek.SATURDAY
                        && current.getDayOfWeek() != DayOfWeek.SUNDAY) {

                    final LocalDate jour = current;
                    boolean existe = plageHoraireRepository
                            .findByClasseIdAndSemestreId(classe.getId(), semestre.getId())
                            .stream()
                            .anyMatch(p -> p.getJour().equals(jour));

                    if (!existe) {
                        PlageHoraire plage = PlageHoraire.builder()
                                .jour(jour)
                                .heureDebut(LocalTime.of(8, 0))
                                .heureFin(LocalTime.of(10, 0))
                                .salle("Salle " + (100 + plagesCreees % 10))
                                .typeSeance(TypeSeance.CM)
                                .classe(classe)
                                .semestre(semestre)
                                .programmationUE(prog)
                                .enseignants(new HashSet<>(enseignants))
                                .build();

                        plageHoraireRepository.save(plage);
                        plagesCreees++;
                    }
                }
                current = current.plusDays(7);
            }

            if (plagesCreees > 0) {
                System.out.println("   ✅ " + prog.getUe().getNom() + " — " + classe.getNom()
                        + " : " + plagesCreees + " plages");
            }
            totalPlages += plagesCreees;
        }

        System.out.println("\n✅ PlageHoraireInitializer — " + totalPlages + " plages horaires créées !");
        System.out.println("=".repeat(80) + "\n");
    }
}