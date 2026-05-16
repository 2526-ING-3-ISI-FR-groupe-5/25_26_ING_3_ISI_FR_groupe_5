package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Résultat d'une opération de migration.
 * Trace tous les éléments traités par catégorie.
 */
@Getter
@ToString
public class MigrationResultat {

    // ── Étudiants ──────────────────────────────────────────────
    private final List<String> admis        = new ArrayList<>();
    private final List<String> redoublants  = new ArrayList<>();
    private final List<String> exclus       = new ArrayList<>();
    private final List<String> diplomes     = new ArrayList<>();
    private final List<String> ignores      = new ArrayList<>();

    // ── Personnels ─────────────────────────────────────────────
    private final List<String> enseignants  = new ArrayList<>();
    private final List<String> assistants   = new ArrayList<>();

    // ── Ressources académiques ─────────────────────────────────
    private final List<String> ues          = new ArrayList<>();

    // ═══════════════════════════════════════════════════════════
    // AJOUT PAR CATÉGORIE
    // ═══════════════════════════════════════════════════════════

    public void ajouterAdmis(String matricule)      { admis.add(matricule); }
    public void ajouterRedoublant(String matricule) { redoublants.add(matricule); }
    public void ajouterExclu(String matricule)      { exclus.add(matricule); }
    public void ajouterDiplome(String matricule)    { diplomes.add(matricule); }
    public void ajouterIgnore(String matricule)     { ignores.add(matricule); }
    public void ajouterEnseignant(String email)     { enseignants.add(email); }
    public void ajouterAssistant(String email)      { assistants.add(email); }
    public void ajouterUE(String code)              { ues.add(code); }

    // ═══════════════════════════════════════════════════════════
    // COMPTEURS (retournent int — compatibles Thymeleaf)
    // ═══════════════════════════════════════════════════════════

    public int getAdmis()       { return admis.size(); }
    public int getRedoublants() { return redoublants.size(); }
    public int getExclus()      { return exclus.size(); }
    public int getDiplomes()    { return diplomes.size(); }
    public int getIgnores()     { return ignores.size(); }
    public int getEnseignants() { return enseignants.size(); }
    public int getAssistants()  { return assistants.size(); }
    public int getUEs()         { return ues.size(); }

    // ═══════════════════════════════════════════════════════════
    // LISTES DÉTAILLÉES — utilisées par le controller / templates
    // ✅ AJOUTÉ — noms distincts des compteurs pour éviter ambiguïté Lombok
    // ═══════════════════════════════════════════════════════════

    public List<String> getAdmisList()       { return admis; }
    public List<String> getRedoublantsList() { return redoublants; }
    public List<String> getExclusList()      { return exclus; }
    public List<String> getDiplomesList()    { return diplomes; }
    public List<String> getIgnoresList()     { return ignores; }
    public List<String> getEnseignantsList() { return enseignants; }
    public List<String> getAssistantsList()  { return assistants; }
    public List<String> getUEsList()         { return ues; }

    // ═══════════════════════════════════════════════════════════
    // TOTAUX
    // ═══════════════════════════════════════════════════════════

    public int getTotalEtudiants() {
        return admis.size() + redoublants.size() + exclus.size() + diplomes.size();
    }

    public int getTotalMigre() {
        return getTotalEtudiants() + enseignants.size() + assistants.size() + ues.size();
    }

    public boolean isComplet() {
        return ignores.isEmpty();
    }

    public String resume() {
        return String.format(
                "Admis: %d | Redoublants: %d | Exclus: %d | Diplômés: %d | " +
                        "Ignorés: %d | Enseignants: %d | Assistants: %d | UEs: %d",
                getAdmis(), getRedoublants(), getExclus(), getDiplomes(),
                getIgnores(), getEnseignants(), getAssistants(), getUEs());
    }
}