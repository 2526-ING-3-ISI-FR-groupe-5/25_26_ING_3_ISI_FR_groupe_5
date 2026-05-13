# 🔍 Guide de Débogage - Erreur 403 sur /admin/inscriptions et /admin/migration

## 🚨 Le Problème
L'utilisateur se conecte mais obtient un **403 Forbidden** sur:
- `http://localhost:8080/admin/inscriptions`
- `http://localhost:8080/admin/migration`

## 🎯 Diagnostic en 3 étapes

### ÉTAPE 1: Vérifier le rôle actuel

1. Assurez-vous d'être **connecté** au système
2. Accédez à: `http://localhost:8080/debug/user-info`
3. **Observez les informations affichées:**

#### Vous verrez ceci:
```
👤 Informations Utilisateur
Email: superadmin@carnetrouge.com
Nom: Super
Prénom: Admin
Type: Enseignant
Actif: OUI
Institut: AUCUN INSTITUT ASSIGNÉ

🗂️ Rôles en Base de Données
Rôles: SUPER_ADMIN

🔐 Authorities Spring Security
Authorities: ROLE_SUPER_ADMIN, ...

✅ Vérifications d'Accès
hasRole('SUPER_ADMIN'): OUI
hasAuthority('ROLE_SUPER_ADMIN'): OUI
hasRole('ADMIN_INSTITUT'): NON
hasAuthority('ROLE_ADMIN_INSTITUT'): NON
```

### ÉTAPE 2: Interpréter les Résultats

**✅ SI vous voyez:**
- `hasRole('SUPER_ADMIN'): OUI` **OU**
- `hasRole('ADMIN_INSTITUT'): OUI`

**→ Le rôle est correct en base de données**

**Action:** Vider les cookies et vous reconnecter
```
1. Appuyez sur F12 (DevTools)
2. Onglet "Application" ou "Storage"
3. Sup primez les cookies: JWT_TOKEN, REFRESH_TOKEN
4. Rafraîchissez la page et reconnectez-vous
5. Essayez à nouveau /admin/inscriptions
```

---

**❌ SI vous voyez:**
- `hasRole('SUPER_ADMIN'): NON` **ET**
- `hasRole('ADMIN_INSTITUT'): NON`

**→ Le rôle n'est PAS assigné en base de données**

**Action:** Modifier la base de données (voir ÉTAPE 3)

---

**❌ SI vous voyez:**
- `Actif: NON`

**→ L'utilisateur est INACTIF** (active=false en base de données)

**Action:** Modifier en base de données:
```sql
UPDATE utilisateur SET active = true WHERE email = 'votre.email@carnetrouge.com';
```

---

### ÉTAPE 3: Corriger en Base de Données (PostgreSQL)

#### Option A: Assigner le rôle SUPER_ADMIN

Ouvrez un client PostgreSQL (pgAdmin, DBeaver, ou ligne de commande) et exécutez:

```sql
-- 1. Vérifier que le rôle existe
SELECT id, nom FROM role WHERE nom = 'SUPER_ADMIN';

-- 2. Vérifier l'utilisateur
SELECT id, email FROM utilisateur WHERE email = 'superadmin@carnetrouge.com';

-- 3. Assigner le rôle (remplacez les ID par ceux obtenus ci-dessus)
INSERT INTO utilisateurs_role (utilisateur_id, role_id)
VALUES (1, 1)  -- Remplacez par les vrais IDs
ON CONFLICT DO NOTHING;

-- 4. Vérifier
SELECT u.email, r.nom FROM utilisateur u
JOIN utilisateurs_role ur ON u.id = ur.utilisateur_id
JOIN role r ON ur.role_id = r.id
WHERE u.email = 'superadmin@carnetrouge.com';
```

#### Option B: Assigner le rôle ADMIN_INSTITUT

```sql
INSERT INTO utilisateurs_role (utilisateur_id, role_id)
SELECT u.id, r.id
FROM utilisateur u, role r
WHERE u.email = 'admin.ucad@carnetrouge.com'
  AND r.nom = 'ADMIN_INSTITUT'
  AND NOT EXISTS (
    SELECT 1 FROM utilisateurs_role ur
    WHERE ur.utilisateur_id = u.id AND ur.role_id = r.id
  );
```

#### Option C: Créer un nouvel utilisateur SUPER_ADMIN (mode test)

Si aucun super admin n'existe, relancez DataInitializer:

```sql
-- Supprimer les anciennes données
DELETE FROM utilisateurs_role WHERE utilisateur_id IN (
  SELECT id FROM utilisateur WHERE email = 'superadmin@carnetrouge.com'
);
DELETE FROM utilisateur WHERE email = 'superadmin@carnetrouge.com';

-- Ensuite redémarrez l'application
-- DataInitializer va automatiquement créer:
-- Email: superadmin@carnetrouge.com
-- Mot de passe: Super123!
```

## 📋 Dépannage Supplémentaire

### Le 403 persiste après correction?

**Causes possibles:**

1. **Cache du navigateur**
   - Videz les cookies (voir ÉTAPE 2)
   - Videz le cache (F12 → Application → Clear All)

2. **Cache de Spring Security**
   - Redémarrez l'application Spring Boot
   - Attendez que les logs montrent: `🚀 DÉMARRAGE DE L'INITIALISATION`

3. **Rôle inactif**
   ```sql
   SELECT * FROM role WHERE nom = 'SUPER_ADMIN' AND active = false;
   -- Si le rowcount est > 0:
   UPDATE role SET active = true WHERE nom = 'SUPER_ADMIN';
   ```

4. **Utilisateur inactif**
   ```sql
   SELECT email, active FROM utilisateur WHERE email = 'superadmin@carnetrouge.com';
   -- Si active = false:
   UPDATE utilisateur SET active = true WHERE email = 'superadmin@carnetrouge.com';
   ```

### Vérifications Finales

Après chaque modification:

1. **Redémarrez Spring Boot** (obligatoire)
2. **Videz les cookies** (F12 → Application → Clear All)
3. **Reconnectez-vous**
4. **Allez à** `/debug/user-info` pour vérifier
5. **Essayez** `/admin/inscriptions`

## 🆘 Si Rien Ne Fonctionne

Exécutez ce diagnostic complet:

```sql
-- Diagnostic complet
SELECT 
  u.id as user_id,
  u.email,
  u.active as user_active,
  u.institut_id,
  r.id as role_id,
  r.nom as role_nom,
  r.active as role_active
FROM utilisateur u
LEFT JOIN utilisateurs_role ur ON u.id = ur.utilisateur_id
LEFT JOIN role r ON ur.role_id = r.id
WHERE u.email = 'superadmin@carnetrouge.com'
ORDER BY u.id, r.nom;
```

**Résultat attendu pour SUPER_ADMIN:**
```
user_id | email | user_active | institution_id | role_id | role_nom | role_active
1       |superadmin@... | true | NULL | 1 | SUPER_ADMIN | true
```

## 💡 Notes Importantes

- **SUPER_ADMIN** peut avoir `institut_id = NULL`
- **ADMIN_INSTITUT** DOIT avoir un `institut_id` non-null
- Les rôles doivent avoir `active = true`
- L'utilisateur doit avoir `active = true`
- Après modification: **redémarrez Spring** (pas juste reload)

---

## 📞 Support

Si le problème persiste:
1. Allez à `/debug/user-info`
2. Vérifiez chaque ligne du diagnostic
3. Exécutez les commandes SQL proposées
4. Redémarrez l'application
5. Testez à nouveau

