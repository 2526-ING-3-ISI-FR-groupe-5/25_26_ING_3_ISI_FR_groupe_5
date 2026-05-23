

## Architecture existante

### Entités principales

**`SessionAppel`** — représente une session d'appel ouverte par un enseignant :
```java
- id
- plageHoraire      // le créneau de cours
- enseignant        // qui a lancé l'appel
- methode           // QR_CODE ou CODE_PIN (MethodeValidation enum)
- code              // le code généré (UUID pour QR, 6 chiffres pour PIN)
- dateGeneration
- dateExpiration    // 3 minutes par défaut
- typeSession       // NORMALE ou OFFLINE (TypeSession enum)
- actif
- coursTermine
- latitudeEnseignant / longitudeEnseignant / perimetreMetres  // géolocalisation
```

**`Appels`** — représente la présence d'un étudiant sur un créneau :
```java
- etudiant
- plageHoraire
- sessionAppel
- statut            // EN_ATTENTE, PRESENT, RETARD, PARTIEL, ABSENT, JUSTIFIE
- methode           // comment l'étudiant a validé
- nbHeuresPresent
- latitudeEtudiant / longitudeEtudiant / dansLePerimetre
- synchronise       // pour la sync offline
```

### Enums existants

```java
// MethodeValidation.java
public enum MethodeValidation {
    QR_CODE,
    CODE_PIN,
    MANUELLE
}

// TypeSession.java
public enum TypeSession {
    NORMALE,  // code 6 chiffres, expire en 3 min
    OFFLINE   // code 8 caractères alphanumériques, expire à la fin du cours
}
```

### Services existants

**`SessionAppelService`** :
```java
// Crée une session normale (PIN 6 chiffres, 3 min)
public SessionAppel creer(SessionAppelRequest req, Long enseignantId)

// Crée une session offline (code 8 caractères, durée du cours)
public SessionAppel creerSessionOffline(Long plageHoraireId, Long enseignantId)

// Génération des codes
private String genererCode(MethodeValidation methode) {
    return switch (methode) {
        case QR_CODE  -> UUID.randomUUID().toString();  // UUID pour QR
        case CODE_PIN -> String.format("%06d", new SecureRandom().nextInt(999999)); // 6 chiffres
        default -> null;
    };
}

// Validation isValide() tient compte du TypeSession
public boolean isValide() {
    if (!actif || coursTermine) return false;
    if (typeSession == TypeSession.OFFLINE) return true; // pas d'expiration par temps
    return !isExpire();
}
```

**`AppelsService.validerParCode()`** :
```java
public Appels validerParCode(AppelsRequest req, Long etudiantId) {
    SessionAppel session = sessionAppelService.findById(req.getSessionAppelId());
    if (!session.isValide()) throw new RuntimeException("Session expirée ou fermée.");
    if (!session.getCode().equals(req.getCodeSaisi())) throw new RuntimeException("Code invalide.");
    // Vérification périmètre GPS si défini
    if (session.getPerimetreMetres() != null) {
        boolean ok = session.estDansLePerimetre(req.getLatitudeEtudiant(), req.getLongitudeEtudiant());
        if (!ok) throw new RuntimeException("Vous n'êtes pas dans l'enceinte.");
    }
    // Marquer présent
    appel.marquerPresent(session.getEnseignant(), session.getMethode());
    return appelsRepository.save(appel);
}
```

### Controller PWA étudiant

**`EspaceEtudiantController`** :
```java
// GET — affiche la page avec session normale ET session offline
@GetMapping("/valider-presence")
public String afficherValidation(Model model, ...) {
    model.addAttribute("session", sessionAppelService.getSessionActivePourClasse(classeId));
    model.addAttribute("sessionOffline", sessionAppelService.getSessionOfflineActive(classeId));
    return "etudiant/valider-presence";
}

// POST — valide la présence
@PostMapping("/valider-presence")
public String validerPresence(@ModelAttribute AppelsRequest req, ...) {
    // GPS obligatoire sauf pour les sessions offline
    appelsService.validerParCode(req, etudiant.getId());
}
```

### Template PWA actuel

La page `valider-presence.html` affiche **deux blocs séparés** :
- Bloc session normale → champ PIN 6 chiffres
- Bloc session offline → champ code 8 caractères

---

## Ce que je veux implémenter

Mon ami m'a suggéré d'**insérer le code PIN directement dans le QR Code**. L'idée est la suivante :

Au lieu d'avoir deux codes distincts (UUID pour QR et PIN numérique séparés), le QR Code encode une **URL complète** contenant le PIN :

```
https://monapp.com/etudiant/valider-presence?session=123&pin=847291
```

Ainsi l'étudiant a **deux façons équivalentes** de valider avec le **même code** :
- 📱 **Scanner le QR** → le navigateur ouvre l'URL → formulaire pré-rempli → validation automatique
- ⌨️ **Saisir le PIN manuellement** → tape les 6 chiffres à la main

---

## Ce que j'attends de toi

Explique-moi et génère :

1. **La modification de `genererCode()`** dans `SessionAppelService` — le QR Code doit encoder une URL contenant le PIN au lieu d'un UUID

2. **La modification de `SessionAppel`** — un seul code (PIN 6 chiffres) pour les deux méthodes. Plus besoin de générer un UUID séparé pour QR_CODE

3. **La modification de `AppelsMvcController`** — la route `lancer-session` doit générer le QR Code sous forme d'image (base64) à afficher dans le template enseignant

4. **La modification du template enseignant `appel_interface.html`** — afficher le QR Code comme image + afficher le PIN en dessous pour ceux qui ne peuvent pas scanner

5. **La modification du template étudiant `valider-presence.html`** — gérer le cas où l'étudiant arrive via URL QR (paramètres `session` et `pin` dans l'URL) pour pré-remplir le formulaire automatiquement

6. **La bibliothèque Java recommandée** pour générer le QR Code côté serveur (ex: `zxing`)

---

## Contraintes importantes

- Le projet utilise **Spring Boot** + **Thymeleaf** + **Tailwind CSS**
- La PWA doit fonctionner **offline** avec **Background Sync**
- La géolocalisation GPS est obligatoire pour les sessions normales
- Le code PIN expire en **3 minutes** pour les sessions normales
- Le code offline expire à la **fin du cours** (`coursTermine = true`)
- L'architecture multi-tenant (multi-instituts) doit être respectée
- Ne pas casser le système de **rollback de migration** existant

---

