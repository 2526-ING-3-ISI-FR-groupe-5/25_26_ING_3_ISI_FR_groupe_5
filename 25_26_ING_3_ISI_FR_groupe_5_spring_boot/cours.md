# 📚 Cours Complet JPA — Spring Data JPA

> **Java Persistence API** — Tout ce qu'il faut savoir pour maîtriser la persistance en Java

---

## Table des matières

1. [C'est quoi JPA ?](#1-cest-quoi-jpa-)
2. [Les entités de base](#2-les-entités-de-base)
3. [Les relations entre classes — Lire un diagramme de classe](#3-les-relations-entre-classes--lire-un-diagramme-de-classe)
    - [@OneToOne](#31-onetoone)
    - [@OneToMany / @ManyToOne](#32-onetomany--manytoone)
    - [@ManyToMany](#33-manytomany)
4. [Les repositories — Construire les requêtes](#4-les-repositories--construire-les-requêtes)
    - [Requêtes dérivées](#41-requêtes-dérivées-derived-queries)
    - [Les mots-clés](#42-les-mots-clés)
    - [Pagination et tri](#43-pagination-et-tri)
5. [Requêtes personnalisées — @Query](#5-requêtes-personnalisées--query)
    - [JPQL](#51-jpql)
    - [SQL Natif](#52-sql-natif)
    - [Requêtes de modification](#53-requêtes-de-modification)
6. [Procédures stockées](#6-procédures-stockées)
7. [FetchType — LAZY vs EAGER](#7-fetchtype--lazy-vs-eager)
8. [CascadeType](#8-cascadetype)
9. [Auditing — CreatedDate, LastModifiedDate](#9-auditing--createddate-lastmodifieddate)
10. [Résumé final](#10-résumé-final)

---

## 1. C'est quoi JPA ?

JPA (Java Persistence API) est une **spécification** Java qui permet de **mapper des objets Java vers des tables de base de données**.

```
Classe Java   ←→   Table BDD
Attribut      ←→   Colonne
Instance      ←→   Ligne (enregistrement)
```

### La pile technologique

```
Ton code Java
      ↓
Spring Data JPA  ← simplifie tout (repositories, requêtes auto)
      ↓
JPA (spécification)
      ↓
Hibernate  ← implémentation concrète de JPA
      ↓
JDBC
      ↓
Base de données (PostgreSQL, MySQL, etc.)
```

> **Spring Data JPA** = Spring + JPA + Hibernate. Tu écris le minimum, Spring génère le reste.

---

## 2. Les entités de base

Une **entité** est une classe Java qui correspond à une table en base de données.

```java
@Entity                          // ← dit à JPA "cette classe = une table"
@Table(name = "enseignants")     // ← nom de la table (optionnel, sinon = nom de la classe)
public class Enseignant {

    @Id                                              // ← clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ← auto-increment
    private Long id;

    @Column(nullable = false)    // ← colonne NOT NULL
    private String nom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "type_enseignant") // ← nom de colonne personnalisé
    private String typeEnseignant;

    @Temporal(TemporalType.DATE)      // ← stocke seulement la date (pas l'heure)
    private Date dateNaissance;

    @Enumerated(EnumType.STRING)      // ← stocke le nom de l'enum en BDD
    private TypeNiveau niveau;

    @Transient                        // ← pas stocké en BDD
    private String champTemporaire;
}
```

### Les stratégies de génération d'ID

| Stratégie | Description |
|---|---|
| `IDENTITY` | Auto-increment BDD (PostgreSQL, MySQL) ✅ recommandé |
| `SEQUENCE` | Séquence BDD (Oracle, PostgreSQL) |
| `TABLE` | Table spéciale pour les IDs |
| `AUTO` | JPA choisit automatiquement |

---

## 3. Les relations entre classes — Lire un diagramme de classe

### Comment lire les relations en langage humain

Avant de coder, pose-toi toujours cette question :

```
"Un(e) [A] peut avoir combien de [B] ?"
"Un(e) [B] peut avoir combien de [A] ?"
```

---

### 3.1 @OneToOne

**Langage humain :** *"Un utilisateur a exactement un profil. Un profil appartient à exactement un utilisateur."*

**Diagramme de classe :**
```
Utilisateur 1 ────── 1 Profil
```

**Code :**

```java
// Côté propriétaire (celui qui a la clé étrangère en BDD)
@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profil_id", referencedColumnName = "id")
    private Profil profil;
}

// Côté inverse
@Entity
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    @OneToOne(mappedBy = "profil") // ← "mappedBy" = je suis le côté inverse
    private Utilisateur utilisateur;
}
```

**En base de données :**
```
Table: utilisateurs
┌────┬──────┬───────────┐
│ id │ nom  │ profil_id │  ← clé étrangère
└────┴──────┴───────────┘

Table: profils
┌────┬─────┐
│ id │ bio │
└────┴─────┘
```

---

### 3.2 @OneToMany / @ManyToOne

**Langage humain :** *"Un département peut avoir plusieurs enseignants. Un enseignant appartient à un seul département."*

**Diagramme de classe :**
```
Département 1 ────── * Enseignant
```

> Le `1` = @OneToMany côté Département
> Le `*` = @ManyToOne côté Enseignant

**Code :**

```java
// Côté "Many" — l'enseignant (qui a la clé étrangère)
@Entity
public class Enseignant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)    // ← plusieurs enseignants → un département
    @JoinColumn(name = "departement_id")  // ← clé étrangère dans la table enseignants
    private Departement departement;
}

// Côté "One" — le département
@Entity
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "departement",  // ← pointe vers le champ dans Enseignant
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Enseignant> enseignants = new ArrayList<>();
}
```

**En base de données :**
```
Table: departements          Table: enseignants
┌────┬──────┐                ┌────┬──────┬─────────────────┐
│ id │ nom  │                │ id │ nom  │ departement_id  │ ← clé étrangère
└────┴──────┘                └────┴──────┴─────────────────┘
```

> **Règle :** La clé étrangère est toujours du côté `@ManyToOne` (côté "Many").

---

### 3.3 @ManyToMany

**Langage humain :** *"Un rôle peut avoir plusieurs permissions. Une permission peut appartenir à plusieurs rôles."*

**Diagramme de classe :**
```
Role * ────── * Permission
```

> Les deux étoiles = @ManyToMany des deux côtés

**Code :**

```java
// Côté propriétaire (celui qui définit la table de jointure)
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",           // ← nom de la table de jointure
        joinColumns = @JoinColumn(name = "role_id"),        // ← clé vers Role
        inverseJoinColumns = @JoinColumn(name = "permission_id") // ← clé vers Permission
    )
    private Set<Permission> permissions = new HashSet<>();
}

// Côté inverse
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "permissions") // ← "mappedBy" = je suis le côté inverse
    private Set<Role> roles = new HashSet<>();
}
```

**En base de données :**
```
Table: roles              Table: role_permissions     Table: permissions
┌────┬──────┐             ┌─────────┬───────────────┐  ┌────┬──────┐
│ id │ name │             │ role_id │ permission_id │  │ id │ name │
└────┴──────┘             └─────────┴───────────────┘  └────┴──────┘
                                ↑ table de jointure
```

---

### Résumé des relations en langage humain

| Relation | Langage humain | Exemple |
|---|---|---|
| `@OneToOne` | "A a exactement un B" | Utilisateur → Profil |
| `@ManyToOne` | "Plusieurs A appartiennent à un B" | Enseignant → Département |
| `@OneToMany` | "Un A a plusieurs B" | Département → Enseignants |
| `@ManyToMany` | "Plusieurs A ont plusieurs B" | Rôle ↔ Permission |

### Comment lire un diagramme de classe

```
A  1 ────── 1  B    →  @OneToOne
A  1 ────── *  B    →  A: @OneToMany(B),  B: @ManyToOne(A)
A  * ────── 1  B    →  A: @ManyToOne(B),  B: @OneToMany(A)
A  * ────── *  B    →  @ManyToMany des deux côtés
```

### Qui a la clé étrangère ?

```
┌─────────────────────────────────────────────────────────┐
│  La clé étrangère est TOUJOURS du côté @ManyToOne       │
│  ou du côté qui définit @JoinColumn                     │
│  Le côté avec mappedBy n'a PAS de clé étrangère         │
└─────────────────────────────────────────────────────────┘
```

---

## 4. Les repositories — Construire les requêtes

### Interface de base

```java
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
    // JpaRepository<TypeEntité, TypeDeLaClePrimaire>
    // Fournit automatiquement : save, findById, findAll, delete, count, exists...
}
```

### Méthodes fournies gratuitement par JpaRepository

```java
// Sauvegarder / Mettre à jour
enseignantRepository.save(enseignant);
enseignantRepository.saveAll(liste);

// Trouver
enseignantRepository.findById(1L);
enseignantRepository.findAll();
enseignantRepository.findAll(pageable);

// Supprimer
enseignantRepository.deleteById(1L);
enseignantRepository.delete(enseignant);
enseignantRepository.deleteAll();

// Vérifier
enseignantRepository.existsById(1L);
enseignantRepository.count();
```

---

### 4.1 Requêtes dérivées (Derived Queries)

**Principe :** Tu écris le **nom de la méthode**, Spring génère le SQL automatiquement.

```
findBy + Propriété + Condition + [And/Or] + Propriété + Condition...
```

```java
// Spring génère → SELECT * FROM enseignants WHERE email = ?
Optional<Enseignant> findByEmail(String email);

// Spring génère → SELECT * FROM enseignants WHERE nom = ? AND active = true
List<Enseignant> findByNomAndActiveTrue(String nom);

// Spring génère → SELECT * FROM enseignants WHERE email = ? OR nom = ?
List<Enseignant> findByEmailOrNom(String email, String nom);
```

---

### 4.2 Les mots-clés

#### Préfixes

```java
findBy...        // SELECT
existsBy...      // SELECT → retourne boolean
countBy...       // SELECT COUNT → retourne long
deleteBy...      // DELETE
```

#### Conditions de comparaison

```java
// Égalité (défaut)
findByNom(String nom)                      // WHERE nom = ?

// Comparaison numérique
findByScoreGreaterThan(int score)           // WHERE score > ?
findByScoreLessThan(int score)             // WHERE score < ?
findByScoreGreaterThanEqual(int score)     // WHERE score >= ?
findByScoreLessThanEqual(int score)        // WHERE score <= ?
findByScoreBetween(int min, int max)       // WHERE score BETWEEN ? AND ?

// Texte
findByNomContaining(String mot)            // WHERE nom LIKE '%mot%'
findByNomStartingWith(String prefix)       // WHERE nom LIKE 'prefix%'
findByNomEndingWith(String suffix)         // WHERE nom LIKE '%suffix'
findByNomIgnoreCase(String nom)            // WHERE LOWER(nom) = LOWER(?)
findByNomContainingIgnoreCase(String mot)  // WHERE LOWER(nom) LIKE LOWER('%mot%')

// Null / Not Null
findByTelephoneIsNull()                    // WHERE telephone IS NULL
findByTelephoneIsNotNull()                 // WHERE telephone IS NOT NULL

// Boolean
findByActiveTrue()                         // WHERE active = true
findByActiveFalse()                        // WHERE active = false

// Collection
findByNomIn(List<String> noms)             // WHERE nom IN (?, ?, ?)
findByNomNotIn(List<String> noms)          // WHERE nom NOT IN (?, ?, ?)
```

#### Opérateurs logiques

```java
// ET
findByNomAndPrenom(String nom, String prenom)
// WHERE nom = ? AND prenom = ?

// OU
findByNomOrEmail(String nom, String email)
// WHERE nom = ? OR email = ?

// Combinaison
findByActiveTrueAndNomContainingIgnoreCase(String nom)
// WHERE active = true AND LOWER(nom) LIKE LOWER('%nom%')
```

#### Tri

```java
findByActiveTrueOrderByNomAsc()            // ORDER BY nom ASC
findByActiveTrueOrderByNomDesc()           // ORDER BY nom DESC
findByActiveTrueOrderByNomAscPrenomDesc()  // ORDER BY nom ASC, prenom DESC
```

#### Limite

```java
findFirstByOrderByCreatedAtDesc()          // LIMIT 1
findTop3ByOrderByScoreDesc()               // LIMIT 3
findTop10ByActiveTrueOrderByNomAsc()       // LIMIT 10 WHERE active = true
```

---

### 4.3 Pagination et tri

```java
// Repository
Page<Enseignant> findByActiveTrue(Pageable pageable);
Page<Enseignant> findByNomContainingIgnoreCase(String nom, Pageable pageable);

// ⚠️ Pageable doit TOUJOURS être le DERNIER paramètre
```

```java
// Utilisation dans le Service
public Page<Enseignant> lister(int page, int size, String tri) {

    Pageable pageable = PageRequest.of(
        page,                         // numéro de page (commence à 0)
        size,                         // nombre d'éléments par page
        Sort.by(tri).ascending()      // tri
    );

    return enseignantRepository.findByActiveTrue(pageable);
}

// Infos disponibles sur l'objet Page
Page<Enseignant> resultat = enseignantRepository.findAll(pageable);

resultat.getContent();          // List<Enseignant> → les données
resultat.getTotalElements();    // nombre total d'éléments
resultat.getTotalPages();       // nombre total de pages
resultat.getNumber();           // numéro page actuelle (0-based)
resultat.getSize();             // taille de la page
resultat.isFirst();             // première page ?
resultat.isLast();              // dernière page ?
resultat.hasNext();             // page suivante disponible ?
resultat.hasPrevious();         // page précédente disponible ?
```

---

## 5. Requêtes personnalisées — @Query

Quand les requêtes dérivées ne suffisent pas, tu écris toi-même la requête.

### 5.1 JPQL

JPQL = **Java Persistence Query Language**. C'est comme SQL mais tu parles des **classes Java** et non des tables.

```java
// ⚠️ Différence JPQL vs SQL
// SQL natif  → SELECT * FROM enseignants WHERE email = ?
// JPQL       → SELECT e FROM Enseignant e WHERE e.email = :email
//                            ↑ nom de la classe Java, pas de la table
```

```java
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

    // Requête simple
    @Query("SELECT e FROM Enseignant e WHERE e.email = :email")
    Optional<Enseignant> trouverParEmail(@Param("email") String email);

    // Avec plusieurs conditions
    @Query("SELECT e FROM Enseignant e WHERE e.active = true AND e.grade = :grade ORDER BY e.nom ASC")
    List<Enseignant> trouverActifsParGrade(@Param("grade") String grade);

    // Avec jointure
    @Query("SELECT e FROM Enseignant e JOIN e.roles r WHERE r.name = :roleName")
    List<Enseignant> trouverParRole(@Param("roleName") String roleName);

    // Avec COUNT
    @Query("SELECT COUNT(e) FROM Enseignant e WHERE e.active = true AND e.typeEnseignant = :type")
    long compterParType(@Param("type") String type);

    // Avec LIKE
    @Query("SELECT e FROM Enseignant e WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :mot, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :mot, '%'))")
    Page<Enseignant> rechercherParMotCle(@Param("mot") String mot, Pageable pageable);

    // Sélectionner des champs spécifiques (projection)
    @Query("SELECT e.nom, e.email FROM Enseignant e WHERE e.active = true")
    List<Object[]> nomEtEmailActifs();
}
```

---

### 5.2 SQL Natif

Quand tu veux écrire du SQL pur (fonctions spécifiques à PostgreSQL, etc.) :

```java
// nativeQuery = true → SQL pur
@Query(
    value = "SELECT * FROM enseignants WHERE type_enseignant = :type ORDER BY nom",
    nativeQuery = true
)
List<Enseignant> trouverParTypeNatif(@Param("type") String type);

// Avec pagination en SQL natif
@Query(
    value = "SELECT * FROM enseignants WHERE active = true",
    countQuery = "SELECT COUNT(*) FROM enseignants WHERE active = true",
    nativeQuery = true
)
Page<Enseignant> trouverActifsNatif(Pageable pageable);

// Avec fonctions PostgreSQL
@Query(
    value = "SELECT * FROM enseignants WHERE DATE_PART('year', AGE(date_naissance)) > :age",
    nativeQuery = true
)
List<Enseignant> trouverParAge(@Param("age") int age);
```

---

### 5.3 Requêtes de modification

Pour les UPDATE et DELETE personnalisés :

```java
// ⚠️ @Modifying obligatoire pour UPDATE et DELETE
// ⚠️ @Transactional obligatoire pour les modifications

@Modifying
@Transactional
@Query("UPDATE Enseignant e SET e.active = false WHERE e.id = :id")
int desactiverEnseignant(@Param("id") Long id);

@Modifying
@Transactional
@Query("UPDATE Enseignant e SET e.locked = true WHERE e.loginAttempts >= :max")
int verrouillerComptes(@Param("max") int maxTentatives);

@Modifying
@Transactional
@Query("DELETE FROM Enseignant e WHERE e.active = false AND e.createdAt < :date")
int supprimerInactifsAvant(@Param("date") LocalDateTime date);

// Avec SQL natif
@Modifying
@Transactional
@Query(value = "UPDATE enseignants SET login_attempts = 0 WHERE id = :id", nativeQuery = true)
int reinitialiserTentatives(@Param("id") Long id);
```

---

## 6. Procédures stockées

### Créer la procédure en PostgreSQL

```sql
-- Exemple : procédure pour obtenir les stats des enseignants
CREATE OR REPLACE FUNCTION get_enseignant_stats(p_type VARCHAR)
RETURNS TABLE(total BIGINT, actifs BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*) as total,
        COUNT(CASE WHEN active = true THEN 1 END) as actifs
    FROM enseignants
    WHERE type_enseignant = p_type;
END;
$$ LANGUAGE plpgsql;
```

### Méthode 1 — @NamedStoredProcedureQuery sur l'entité

```java
@Entity
@NamedStoredProcedureQuery(
    name = "Enseignant.getStats",
    procedureName = "get_enseignant_stats",
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_type", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "total",  type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "actifs", type = Long.class)
    }
)
public class Enseignant {
    // ...
}

// Dans le repository
@Procedure(name = "Enseignant.getStats")
Map<String, Object> getStats(@Param("p_type") String type);
```

### Méthode 2 — @Procedure directement dans le repository ✅ (recommandée)

```java
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

    // Appel simple
    @Procedure(procedureName = "get_enseignant_stats")
    void appellerProcedure(String pType);

    // Avec valeur de retour
    @Procedure("get_enseignant_stats")
    Long getTotalParType(String pType);
}
```

### Méthode 3 — EntityManager (le plus flexible)

```java
@Service
@RequiredArgsConstructor
public class EnseignantService {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Long> getStatsParType(String type) {

        StoredProcedureQuery query = entityManager
            .createStoredProcedureQuery("get_enseignant_stats")
            .registerStoredProcedureParameter("p_type",  String.class, ParameterMode.IN)
            .registerStoredProcedureParameter("total",   Long.class,   ParameterMode.OUT)
            .registerStoredProcedureParameter("actifs",  Long.class,   ParameterMode.OUT);

        query.setParameter("p_type", type);
        query.execute();

        Map<String, Long> stats = new HashMap<>();
        stats.put("total",  (Long) query.getOutputParameterValue("total"));
        stats.put("actifs", (Long) query.getOutputParameterValue("actifs"));

        return stats;
    }
}
```

### Procédure sans paramètre de sortie (juste une action)

```sql
-- Procédure PostgreSQL
CREATE OR REPLACE PROCEDURE reinitialiser_tentatives(p_id BIGINT)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE enseignants SET login_attempts = 0 WHERE id = p_id;
END;
$$;
```

```java
// Appel via @Query natif (plus simple)
@Modifying
@Transactional
@Query(value = "CALL reinitialiser_tentatives(:id)", nativeQuery = true)
void reinitialiserTentatives(@Param("id") Long id);
```

---

## 7. FetchType — LAZY vs EAGER

### LAZY — chargement différé

```java
@ManyToMany(fetch = FetchType.LAZY)  // ← par défaut pour @ManyToMany et @OneToMany
private Set<Role> roles;
```

**Comportement :**
```
Quand tu charges un Utilisateur → les rôles NE SONT PAS chargés
Quand tu accèdes à utilisateur.getRoles() → Hibernate fait une requête SQL à ce moment
```

**Avantage :** Moins de données chargées, meilleures performances
**Inconvénient :** `LazyInitializationException` si tu accèdes aux données hors d'une session Hibernate

### EAGER — chargement immédiat

```java
@ManyToMany(fetch = FetchType.EAGER)  // ← par défaut pour @ManyToOne et @OneToOne
private Set<Role> roles;
```

**Comportement :**
```
Quand tu charges un Utilisateur → les rôles SONT chargés immédiatement
```

**Avantage :** Toujours disponible, pas de risque de LazyInitializationException
**Inconvénient :** Peut charger beaucoup de données inutiles

### Règles par défaut

| Annotation | FetchType par défaut |
|---|---|
| `@OneToOne` | EAGER |
| `@ManyToOne` | EAGER |
| `@OneToMany` | LAZY |
| `@ManyToMany` | LAZY |

### Quand utiliser quoi ?

```
EAGER  → données toujours nécessaires (ex: rôles d'un utilisateur pour la sécurité)
LAZY   → données rarement nécessaires (ex: liste de tous les étudiants d'un cours)
```

---

## 8. CascadeType

Définit ce qui se passe sur les entités **liées** quand tu agis sur l'entité **principale**.

```java
@OneToMany(cascade = CascadeType.ALL)
private List<Adresse> adresses;
```

| CascadeType | Effet |
|---|---|
| `PERSIST` | Si tu sauvegardes A → sauvegarde aussi B |
| `MERGE` | Si tu modifies A → modifie aussi B |
| `REMOVE` | Si tu supprimes A → supprime aussi B ⚠️ dangereux |
| `REFRESH` | Si tu rafraîchis A → rafraîchit aussi B |
| `DETACH` | Si tu détaches A → détache aussi B |
| `ALL` | Tout ce qui précède |

### Exemples concrets

```java
// Utilisateur → Adresses (l'adresse n'existe pas sans utilisateur)
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
private List<Adresse> adresses;
// orphanRemoval = true → supprime les adresses sans utilisateur

// Utilisateur → Rôles (les rôles existent indépendamment)
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private Set<Role> roles;
// ⚠️ Pas de REMOVE → supprimer un utilisateur ne supprime pas les rôles

// Département → Enseignants
// ⚠️ Ne pas mettre REMOVE ici si les enseignants peuvent changer de département
@OneToMany(mappedBy = "departement", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private List<Enseignant> enseignants;
```

---

## 9. Auditing — CreatedDate, LastModifiedDate

Pour tracer automatiquement les dates de création et modification :

```java
// Dans ta classe principale
@SpringBootApplication
@EnableJpaAuditing  // ✅ active l'auditing
public class Application { }

// Dans ton entité
@Entity
@EntityListeners(AuditingEntityListener.class)  // ✅ obligatoire
public class Enseignant {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;    // rempli automatiquement à la création

    @LastModifiedDate
    private LocalDateTime updatedAt;    // mis à jour automatiquement à chaque modification

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;           // qui a créé (nécessite AuditorAware)

    @LastModifiedBy
    private String updatedBy;           // qui a modifié (nécessite AuditorAware)
}
```

---

## 10. Résumé final

### Les relations en une phrase

```
@OneToOne   → "A possède exactement un B"           → clé étrangère chez A ou B
@ManyToOne  → "Plusieurs A appartiennent à un B"    → clé étrangère chez A
@OneToMany  → "Un A possède plusieurs B"             → clé étrangère chez B
@ManyToMany → "Plusieurs A ont plusieurs B"          → table de jointure
```

### La règle du mappedBy

```
mappedBy = "nomDuChampDansLAutreClasse"

→ Le côté avec mappedBy = côté INVERSE (pas de clé étrangère, pas de @JoinColumn)
→ Le côté SANS mappedBy = côté PROPRIÉTAIRE (a la clé étrangère ou @JoinTable)
```

### Construction des requêtes

```
Cas simple     → Requête dérivée  (findByNomAndActiveTrue)
Cas complexe   → @Query JPQL      (SELECT e FROM Enseignant e WHERE ...)
Fonction BDD   → @Query natif     (nativeQuery = true)
Procédure      → @Procedure ou EntityManager
Modification   → @Modifying + @Transactional
```

### Les pièges à éviter

| Piège | Solution |
|---|---|
| `LazyInitializationException` | Passer en EAGER ou utiliser `@Transactional` sur le service |
| Double table de jointure | Utiliser `mappedBy` sur le côté inverse |
| `CascadeType.REMOVE` sur ManyToMany | Éviter, ça peut supprimer des données partagées |
| Pageable pas en dernier paramètre | Toujours mettre Pageable en dernier |
| `@Modifying` sans `@Transactional` | Toujours les mettre ensemble |
| `@Builder` avec valeurs par défaut | Utiliser `@Builder.Default` |

---

*Cours rédigé pour CarnetRouge — Spring Boot 3.x + Spring Data JPA + PostgreSQL*