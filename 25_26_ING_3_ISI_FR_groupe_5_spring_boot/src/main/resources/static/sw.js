// ═══════════════════════════════════════════════════════════
// SERVICE WORKER — CarnetRouge PWA
// Gère le cache offline + Background Sync pour la validation
// de présence quand le réseau est indisponible.
// ═══════════════════════════════════════════════════════════

const CACHE_NAME = 'carnet-rouge-v2';
const SYNC_TAG   = 'sync-presence';
const DB_NAME    = 'carnet-rouge-db';
const DB_VERSION = 1;
const STORE_NAME = 'pending-presences';

// Pages et ressources à mettre en cache pour le mode offline
const CACHED_URLS = [
    '/etudiant/valider-presence',
    '/etudiant/mon-espace',
    '/css/output.css',
    '/favicon/icon-192x192.png',
    '/favicon/icon-512x512.png',
    '/manifest.json',
];

// ═══════════════════════════════════════════════════════════
// INSTALLATION — mise en cache des ressources statiques
// ═══════════════════════════════════════════════════════════

self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => {
            console.log('[SW] Mise en cache des ressources offline');
            // On utilise {cache: 'no-cache'} pour toujours avoir la version fraîche
            return Promise.allSettled(
                CACHED_URLS.map(url =>
                    cache.add(new Request(url, { cache: 'no-cache' }))
                )
            );
        })
    );
    // Prendre le contrôle immédiatement sans attendre le rechargement
    self.skipWaiting();
});

// ═══════════════════════════════════════════════════════════
// ACTIVATION — nettoyage des anciens caches
// ═══════════════════════════════════════════════════════════

self.addEventListener('activate', event => {
    event.waitUntil(
        caches.keys().then(keys =>
            Promise.all(
                keys
                    .filter(key => key !== CACHE_NAME)
                    .map(key => {
                        console.log('[SW] Suppression ancien cache :', key);
                        return caches.delete(key);
                    })
            )
        )
    );
    self.clients.claim();
});

// ═══════════════════════════════════════════════════════════
// FETCH — stratégie Network First avec fallback cache
// ═══════════════════════════════════════════════════════════

self.addEventListener('fetch', event => {
    const url = new URL(event.request.url);

    // Ne pas intercepter les requêtes non-GET sauf /valider-presence
    if (event.request.method !== 'GET') return;

    // Stratégie Network First pour les pages dynamiques
    if (url.pathname.startsWith('/etudiant/') || url.pathname.startsWith('/enseignant/')) {
        event.respondWith(networkFirst(event.request));
        return;
    }

    // Stratégie Cache First pour les ressources statiques
    if (url.pathname.startsWith('/css/') ||
        url.pathname.startsWith('/js/')  ||
        url.pathname.startsWith('/favicon/')) {
        event.respondWith(cacheFirst(event.request));
        return;
    }
});

async function networkFirst(request) {
    try {
        const response = await fetch(request);
        // Mettre à jour le cache avec la réponse fraîche
        const cache = await caches.open(CACHE_NAME);
        cache.put(request, response.clone());
        return response;
    } catch {
        // Pas de réseau → servir depuis le cache
        const cached = await caches.match(request);
        if (cached) return cached;
        // Fallback sur la page offline
        return caches.match('/etudiant/valider-presence');
    }
}

async function cacheFirst(request) {
    const cached = await caches.match(request);
    if (cached) return cached;
    try {
        const response = await fetch(request);
        const cache = await caches.open(CACHE_NAME);
        cache.put(request, response.clone());
        return response;
    } catch {
        return new Response('Ressource non disponible offline', { status: 503 });
    }
}

// ═══════════════════════════════════════════════════════════
// BACKGROUND SYNC — synchronisation des présences en attente
// ═══════════════════════════════════════════════════════════

self.addEventListener('sync', event => {
    if (event.tag === SYNC_TAG) {
        console.log('[SW] Background Sync déclenché — envoi des présences en attente');
        event.waitUntil(syncPresences());
    }
});

/**
 * Récupère toutes les présences stockées dans IndexedDB
 * et les envoie au serveur une par une.
 */
async function syncPresences() {
    const db = await openDB();
    const pendings = await getAllPending(db);

    console.log(`[SW] ${pendings.length} presence(s) a synchroniser`);

    for (const pending of pendings) {
        try {
            const response = await fetch('/etudiant/valider-presence', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-CSRF-TOKEN': pending.csrfToken,
                },
                body: new URLSearchParams({
                    sessionAppelId:    pending.sessionAppelId,
                    codeSaisi:         pending.codeSaisi,
                    latitudeEtudiant:  pending.latitude,
                    longitudeEtudiant: pending.longitude,
                    _csrf:             pending.csrfToken,
                }),
                credentials: 'include',
            });

            if (response.ok || response.redirected) {
                // Succès → supprimer de IndexedDB
                await deletePending(db, pending.id);
                console.log('[SW] Presence synchronisee :', pending.id);

                // Notifier l'étudiant
                await self.registration.showNotification('CarnetRouge', {
                    body: 'Votre presence a ete enregistree !',
                    icon: '/favicon/icon-192x192.png',
                    badge: '/favicon/icon-192x192.png',
                    tag: 'presence-ok',
                });
            } else {
                console.warn('[SW] Echec sync presence :', response.status, response.statusText);

                // Si code expiré ou session fermée → supprimer quand même (irrécupérable)
                if (response.status === 400 || response.status === 404) {
                    await deletePending(db, pending.id);

                    await self.registration.showNotification('CarnetRouge', {
                        body: 'Presence non enregistree : session expiree ou code invalide.',
                        icon: '/favicon/icon-192x192.png',
                        tag: 'presence-echec',
                    });
                }
                // Autres erreurs → garder pour réessayer au prochain sync
            }
        } catch (err) {
            // Réseau encore indisponible → on réessaiera
            console.warn('[SW] Reseau indisponible, reessai ulterieur :', err.message);
        }
    }
}

// ═══════════════════════════════════════════════════════════
// INDEXEDDB — stockage des présences en attente
// ═══════════════════════════════════════════════════════════

function openDB() {
    return new Promise((resolve, reject) => {
        const req = indexedDB.open(DB_NAME, DB_VERSION);
        req.onupgradeneeded = e => {
            const db = e.target.result;
            if (!db.objectStoreNames.contains(STORE_NAME)) {
                db.createObjectStore(STORE_NAME, { keyPath: 'id', autoIncrement: true });
            }
        };
        req.onsuccess = e => resolve(e.target.result);
        req.onerror   = e => reject(e.target.error);
    });
}

function getAllPending(db) {
    return new Promise((resolve, reject) => {
        const tx    = db.transaction(STORE_NAME, 'readonly');
        const store = tx.objectStore(STORE_NAME);
        const req   = store.getAll();
        req.onsuccess = e => resolve(e.target.result);
        req.onerror   = e => reject(e.target.error);
    });
}

function deletePending(db, id) {
    return new Promise((resolve, reject) => {
        const tx    = db.transaction(STORE_NAME, 'readwrite');
        const store = tx.objectStore(STORE_NAME);
        const req   = store.delete(id);
        req.onsuccess = () => resolve();
        req.onerror   = e => reject(e.target.error);
    });
}

// Exposé pour être appelé depuis la page PWA
self.addEventListener('message', event => {
    if (event.data && event.data.type === 'STORE_PRESENCE') {
        openDB().then(db => {
            const tx    = db.transaction(STORE_NAME, 'readwrite');
            const store = tx.objectStore(STORE_NAME);
            store.add(event.data.payload);
            console.log('[SW] Presence stockee en attente de sync');
        });
    }
});