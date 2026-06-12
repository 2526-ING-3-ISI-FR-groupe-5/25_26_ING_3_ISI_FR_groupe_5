Pour filtrer uniquement les erreurs lors de l'exécution de Maven, voici plusieurs approches :

## Solution 1 : Rediriger les erreurs uniquement (recommandé)

```bash
mvn spring-boot:run 2>> error.md
```

**Explication :** `2>>` redirige uniquement la sortie d'erreur standard (stderr) vers le fichier, sans inclure la sortie normale (stdout).

## Solution 2 : Filtrer avec `grep` (Windows PowerShell)

```bash
mvn spring-boot:run 2>&1 | Select-String -Pattern "ERROR|WARN|Caused by|Exception" > error.md
```

## Solution 3 : Filtrer avec `findstr` (CMD Windows)

```bash
mvn spring-boot:run 2>&1 | findstr /I "ERROR WARN Caused Exception Failed" > error.md
```

## Solution 4 : Script bash complet (Linux/Mac/Git Bash)

```bash
mvn spring-boot:run 2>&1 | grep -E "ERROR|WARN|Caused by|Exception|Failed|BUILD" > error.md
```

## Solution 5 : N'afficher que les erreurs Spring Boot

```bash
mvn spring-boot:run -q 2>&1 | grep -A 5 "ERROR" > error.md
```

L'option `-q` (quiet) réduit la verbosité de Maven, et `grep -A 5` montre 5 lignes après chaque "ERROR".

## Solution 6 : Approche complète avec niveaux de log

```bash
mvn spring-boot:run -Dlogging.level.root=ERROR 2>&1 | grep -v "INFO" > error.md
```

## Recommandation pour votre cas

La **Solution 1** est la plus simple et la plus efficace :
```bash
mvn spring-boot:run 2>> error.md
```

Cela n'enregistrera que les messages d'erreur dans le fichier, rendant l'analyse beaucoup plus facile.

**Note :** Contrairement à `>>` qui ajoute à la fin du fichier, `2>` écraserait le fichier à chaque exécution. Utilisez `2>` pour un nouveau fichier à chaque fois, ou `2>>` pour conserver l'historique.