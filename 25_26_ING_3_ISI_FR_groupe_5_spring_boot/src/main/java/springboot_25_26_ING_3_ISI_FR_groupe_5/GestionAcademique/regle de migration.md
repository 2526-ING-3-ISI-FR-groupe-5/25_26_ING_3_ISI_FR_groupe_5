## 📚 Documentation — Règles de Migration Académique

---

### 1. Principe général

La migration consiste à créer les inscriptions et programmations de l'année **N+1** à partir des données de l'année **N**, sans modifier les données de l'année N.

---

### 2. Cycle de vie d'une migration

```
EN_EXECUTION ──► TERMINEE ──► PUBLIEE (définitif)
                    │
                    └──► ANNULE (rollback)

ÉCHEC (en cas d'erreur)
```

| Statut | Description | Rollback possible |
|--------|-------------|-------------------|
| `EN_EXECUTION` | Migration en cours | ❌ |
| `TERMINEE` | Migration terminée, en attente de validation | ✅ |
| `PUBLIEE` | Migration validée définitivement | ❌ |
| `ANNULE` | Rollback effectué | ❌ |
| `ECHEC` | Erreur lors de l'exécution | ❌ |

---

### 3. Arbre hiérarchique des entités

```
Institut (1)
  └── École (2)
        └── Filière (3)
              └── Spécialité (4)
                    └── Niveau (5)
                          └── Classe (6)
                                └── Étudiant (7)

Entités indépendantes :
  └── Enseignant (8)
  └── Assistant Pédagogique (9)
  └── UE (10)
```

---

### 4. Règles d'indépendance

**Aucune dépendance entre les niveaux. Chaque entité est migrable individuellement.**

Les entités (Institut, École, Filière, Spécialité, Niveau, Classe) sont **permanentes** — elles n'appartiennent pas à une année académique. Seules les **inscriptions** et **programmations** sont liées à une année.

> **Règle :** On peut migrer une Classe sans avoir migré sa Filière ou son École au préalable.

---

### 5. Comportement par type d'entité

#### 5.1 Entités hiérarchiques (migration en cascade)

La sélection d'une entité migre cette entité et tout son contenu descendant.

| Entité sélectionnée | Périmètre migré |
|---------------------|-----------------|
| **Institut** | Écoles → Filières → Spécialités → Niveaux → Classes → Inscriptions |
| **École** | Filières → Spécialités → Niveaux → Classes → Inscriptions |
| **Filière** | Spécialités → Niveaux → Classes → Inscriptions |
| **Spécialité** | Niveaux → Classes → Inscriptions |
| **Niveau** | Classes → Inscriptions |
| **Classe** | Inscriptions des étudiants |

#### 5.2 Entités feuilles (migration directe)

| Entité sélectionnée | Action |
|---------------------|--------|
| **Étudiant** | Crée une inscription N+1 pour cet étudiant uniquement |
| **Enseignant** | Duplique ses programmations UE vers N+1 |
| **Assistant Pédagogique** | Conserve ses affectations de classes pour N+1 |
| **UE** | Duplique ses programmations vers N+1 |

---

### 6. Règles métier pour les inscriptions étudiantes

| Décision de fin d'année | Comportement |
|--------------------------|---------------|
| `ADMIS` | L'étudiant passe au **niveau supérieur**. Affecté à la classe la moins remplie du niveau supérieur ayant la même spécialité. |
| `REDOUBLANT` | L'étudiant reste dans la **même classe**. |
| `EXCLU` | L'étudiant est **désactivé**. Une inscription avec statut `EXCLU` est créée. |
| `DIPLOME` | Aucune inscription créée. L'étudiant a terminé son cursus. |
| *(Sans décision)* | **Ignoré**. L'étudiant doit avoir une décision avant migration. |

---

### 7. Règles de rollback

- Possible uniquement si le batch est au statut `TERMINEE`
- **Les données N+1 sont conservées** (les inscriptions et programmations créées restent en base)
- L'état N est restauré :
    - Les étudiants retrouvent leur classe d'origine (année N)
    - Les étudiants exclus sont réactivés
    - Le contexte actif est restauré sur l'année N
- Impossible après `PUBLIEE`

---

### 8. Règles de publication

- Possible uniquement si le batch est au statut `TERMINEE`
- Action **irréversible**
- Le batch passe en `PUBLIEE`, le rollback devient impossible

---

### 9. Types de migration disponibles

| Type | Déclencheur | Description |
|------|-------------|-------------|
| `COMPLETE` | Dashboard → "Migration complète" | Migre tout l'institut |
| `SIMULATION` | Dashboard → "Simuler" | Dry-run sans modification |
| `SELECTIVE` | Dashboard → carte entité | Migration d'une sélection d'entités |

---

### 10. Rôles et permissions

| Action | ASSISTANT | SUPER_ADMIN |
|--------|-----------|-------------|
| Lancer une migration | ✅ | ✅ |
| Simuler une migration | ✅ | ✅ |
| Publier une migration | ✅ | ✅ |
| Rollback une migration | ✅ | ✅ |
| Consulter l'historique | ✅ | ✅ |

---