package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MigrationResultat {

    private List<String> admis = new ArrayList<>();
    private List<String> redoublants = new ArrayList<>();
    private List<String> exclus = new ArrayList<>();
    private List<String> diplomes = new ArrayList<>();
    private List<String> ignores = new ArrayList<>();

    public void ajouterAdmis(String matricule) {
        admis.add(matricule);
    }

    public void ajouterRedoublant(String matricule) {
        redoublants.add(matricule);
    }

    public void ajouterExclu(String matricule) {
        exclus.add(matricule);
    }

    public void ajouterDiplome(String matricule) {
        diplomes.add(matricule);
    }

    public void ajouterIgnore(String matricule) {
        ignores.add(matricule);
    }

    public int getTotal() {
        return admis.size() + redoublants.size() + exclus.size() + diplomes.size() + ignores.size();
    }

    @Override
    public String toString() {
        return String.format("Migration terminée - Admis: %d, Redoublants: %d, Exclus: %d, Diplômés: %d, Ignorés: %d",
                admis.size(), redoublants.size(), exclus.size(), diplomes.size(), ignores.size());
    }
}