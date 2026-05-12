package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration;

import java.util.ArrayList;
import java.util.List;

/**
 * Résultat de migration (getters explicites pour compatibilité MapStruct)
 */
public class MigrationResultat {

    // Compteurs
    private int admisCount;
    private int redoublantsCount;
    private int exclusCount;
    private int diplomesCount;
    private int ignoresCount;

    // Listes (noms identiques à MigrationResponse → auto-mapping)
    private final List<String> admis = new ArrayList<>();
    private final List<String> redoublants = new ArrayList<>();
    private final List<String> exclus = new ArrayList<>();
    private final List<String> diplomes = new ArrayList<>();
    private final List<String> ignores = new ArrayList<>();

    // Méthodes d'ajout
    public void ajouterAdmis(String matricule) {
        admisCount++; if (matricule != null) admis.add(matricule);
    }
    public void ajouterRedoublant(String matricule) {
        redoublantsCount++; if (matricule != null) redoublants.add(matricule);
    }
    public void ajouterExclu(String matricule) {
        exclusCount++; if (matricule != null) exclus.add(matricule);
    }
    public void ajouterDiplome(String matricule) {
        diplomesCount++; if (matricule != null) diplomes.add(matricule);
    }
    public void ajouterIgnore(String matricule) {
        ignoresCount++; if (matricule != null) ignores.add(matricule);
    }

    // ✅ GETTERS EXPLICITES (MapStruct les voit immédiatement)
    public int getAdmisCount() { return admisCount; }
    public int getRedoublantsCount() { return redoublantsCount; }
    public int getExclusCount() { return exclusCount; }
    public int getDiplomesCount() { return diplomesCount; }
    public int getIgnoresCount() { return ignoresCount; }

    public int getTotalTraite() {
        return admisCount + redoublantsCount + exclusCount + diplomesCount + ignoresCount;
    }

    // Getters pour les listes (noms identiques → mapping automatique)
    public List<String> getAdmis() { return new ArrayList<>(admis); }
    public List<String> getRedoublants() { return new ArrayList<>(redoublants); }
    public List<String> getExclus() { return new ArrayList<>(exclus); }
    public List<String> getDiplomes() { return new ArrayList<>(diplomes); }
    public List<String> getIgnores() { return new ArrayList<>(ignores); }

    @Override
    public String toString() {
        return String.format("Admis: %d, Redoublants: %d, Exclus: %d, Diplômés: %d, Ignorés: %d",
                admisCount, redoublantsCount, exclusCount, diplomesCount, ignoresCount);
    }
}