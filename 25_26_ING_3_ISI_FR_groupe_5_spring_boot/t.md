
---


---

### Contexte

Je développe une **PWA de gestion académique** (Spring Boot + JPA + Thymeleaf + Tailwind CSS + DaisyUI). L'application est **multi-instituts**. Je travaille actuellement sur le **système de gestion des appels de présence**.

### Architecture du système d'appel

#### Entités principales

```java
// PlageHoraire.java (créneau de cours)
@Entity
public class PlageHoraire extends Auditable {
    private Long id;
    private LocalDate jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
    private String titre;           // "CM Algorithmique"
    private Long nbHeure;           // 4 (durée totale)
    private String typeSeance;      // CM, TD, TP
    private String codePin;         // Code PIN généré pour l'appel
    private String qrCodeData;      // Données QR code
    private LocalDateTime codeExpiration;
    private Double latitudeEnseignant;  // Géolocalisation
    private Double longitudeEnseignant;
    private Integer perimetreMetres;
    private boolean appelEnCours;
    private boolean coursTermine;

    @ManyToOne private Classe classe;
    @ManyToMany private Set<Enseignant> enseignants;
    @OneToMany(mappedBy = "plageHoraire") private Set<Appels> appels;
}
```

```java
// Appels.java (présence d'un étudiant à un cours)
@Entity
public class Appels {
    private Long id;
    private boolean present = false;      // true = présent tout le cours
    private int nbHeuresPresent = 0;      // 0 à nbHeure du cours
    private MethodeValidation methode;    // MANUELLE, QR_CODE, CODE_PIN
    private String codeUtilise;
    private LocalDateTime dateValidation;
    private Double latitudeEtudiant;
    private Double longitudeEtudiant;
    private boolean dansLePerimetre;
    private StatutPresence statut;        // EN_ATTENTE, PRESENT, PARTIEL, ABSENT, JUSTIFIE
    private String commentaire;
    private boolean synchronise = true;   // PWA offline

    @ManyToOne private Etudiant etudiant;
    @ManyToOne private PlageHoraire plageHoraire;
    @ManyToOne private Enseignant enseignant;
    @ManyToOne private SessionAppel sessionAppel;
    @ManyToOne private Justificatif justificatif;
}
```

```java
// SessionAppel.java (une session d'appel lancée par l'enseignant)
@Entity
public class SessionAppel extends Auditable {
    private Long id;
    private MethodeValidation methode;    // MANUELLE, QR_CODE, CODE_PIN
    private String code;
    private LocalDateTime dateGeneration;
    private LocalDateTime dateExpiration;
    private boolean actif = true;
    private Double latitudeEnseignant;
    private Double longitudeEnseignant;
    private Integer perimetreMetres;
    private LocalDateTime heureFinReelle;
    private boolean coursTermine = false;

    @ManyToOne private PlageHoraire plageHoraire;
    @ManyToOne private Enseignant enseignant;
}
```

```java
// Classe.java
@Entity
public class Classe extends Auditable {
    private Long id;
    private String nom;

    @ManyToOne private Niveau niveau;
    @OneToMany(mappedBy = "classe") private Set<PlageHoraire> plagesHoraires;
    @OneToMany(mappedBy = "classe") private Set<Inscription> inscriptions;

    public List<Etudiant> getEtudiantsActifs() { ... }
    public int getNombreEtudiants() { ... }
    public Institut getInstitut() { ... }
}
```

```java
// Énumérations
public enum MethodeValidation { MANUELLE, QR_CODE, CODE_PIN }
public enum StatutPresence { EN_ATTENTE, PRESENT, PARTIEL, ABSENT, JUSTIFIE }
```

---

### Problématique

L'enseignant arrive sur la **page de sa classe** (`/enseignant/classes/{id}`). Sur cette page, il doit pouvoir :

1. **Voir la liste des étudiants** de la classe (ceux qui ont une inscription active)
2. **Voir les statistiques** : nombre de présents, absents, justifiés
3. **Lancer un appel** : soit par QR Code, soit par Code PIN (les deux sont mutuellement exclusifs)
4. **Faire l'appel manuel** : pour chaque étudiant, il peut :
    - Cocher "Présent" → l'étudiant est présent tout le cours (ex: 4h/4h)
    - Utiliser des boutons +/- pour ajuster le nombre d'heures de présence (0 à nbHeure)
5. **Voir l'état de chaque étudiant** : Présent (vert), Partiel (orange), Absent (rouge), Justifié (bleu)
6. **Voir une session d'appel active** (QR ou PIN en cours) avec le code, la durée restante, et un bouton pour arrêter
7. **Terminer le cours** avec un bouton qui enregistre l'heure de fin

---

### Contraintes techniques

- **Design system** : Dark mode, palette de couleurs :
    - `--color-bgdeep: #0a0b0f` (fond)
    - `--color-bgcard: #111318` (cartes)
    - `--color-bgsurface: #181b22` (inputs)
    - `--color-primary: #4f7ef8` (boutons, accents)
    - `--color-primaryhover: #6b94ff`
- **Polices** : `Playfair Display` (titres) + `DM Sans` (corps)
- **Responsive** : Tableau sur desktop, Cards sur mobile
- **DaisyUI** disponible pour les composants (btn, input, badge)
- **Pas de CDN Bootstrap** - utiliser des SVG inline pour les icônes

---

### Ce que je veux

Une **page HTML + Tailwind CSS + DaisyUI** (sans Thymeleaf) qui représente la page de détail d'une classe avec :

1. **En-tête** : nom de la classe, filière, niveau, nombre d'étudiants
2. **Stats** : 4 cartes (étudiants, présents, absents, heures)
3. **Section appel** : deux cartes côte à côte (QR Code / Code PIN) avec :
    - Input pour la durée (minutes)
    - Bouton "Lancer"
    - **Quand l'un est lancé, l'autre est grisé/désactivé**
4. **Bandeau "Appel en cours"** (caché par défaut) qui affiche :
    - Le type (QR ou PIN)
    - Le code (PIN visible, QR masqué)
    - La durée restante
    - Boutons "Arrêter" et "Terminer le cours"
5. **Liste des étudiants** avec pour chaque étudiant :
    - Initiales, nom, prénom, matricule
    - Si présent tout le cours : badge vert "✅ Présent 4h"
    - Si partiel : boutons +/- avec compteur (ex: "2 /4h") et badge orange "Partiel"
    - Si absent : compteur à 0 et badge rouge "Absent"
    - Si justifié : badge bleu "📎 Justifié"
6. **Bouton "Enregistrer les présences"** en bas
7. **Barre de recherche** dans la liste
8. **Comportement JavaScript** :
    - `lancerAppel(methode)` : active l'appel, désactive l'autre méthode
    - `arreterAppel()` : désactive l'appel, réactive les deux méthodes
    - `terminerCours()` : arrête tout + enregistre
    - `adjustHeures(btn, delta)` : ajuste les heures +/- avec couleur dynamique
    - `updateStats()` : met à jour les compteurs présents/absents

---

### Informations complémentaires

- La classe a **42 étudiants** et **4 heures** de cours
- Le cours affiché est "Algorithmique Avancée" (CM), Lundi 15/04/2025, 08:00-12:00, Salle B12
- L'étudiant "Alice Moreau" est présent 4h/4h
- L'étudiant "Bob Dupont" est présent 2h/4h
- L'étudiant "Claire Dubois" est absent 0h/4h
- L'étudiant "Emma Fabre" a un justificatif

---

### Question

Peux-tu me générer cette page HTML complète avec Tailwind CSS + DaisyUI, en respectant le design system et toutes les contraintes ci-dessus ?

---

