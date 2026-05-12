const CACHE_NAME = 'carnet-rouge-v1';

// Ne mettre QUE les ressources statiques et pages publiques
// Pas les routes protégées par JWT
const ASSETS = [
    '/login',
    '/css/output.css',
    '/manifest.json',
    '/icon-512.png'
];

self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            return Promise.allSettled(
                ASSETS.map(url =>
                    cache.add(url).catch(err =>
                        console.warn(`Échec cache: ${url}`, err)
                    )
                )
            );
        })
    );
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then(keys =>
            Promise.all(
                keys.filter(key => key !== CACHE_NAME)
                    .map(key => caches.delete(key))
            )
        )
    );
    self.clients.claim();
});

self.addEventListener('fetch', (event) => {
    const url = event.request.url;

    // ★ Ignorer tout ce qui n'est pas http/https (chrome-extension, etc.)
    if (!url.startsWith('http')) return;

    // Ignorer les requêtes non-GET (POST, PUT, DELETE...)
    if (event.request.method !== 'GET') return;

    // Ignorer les requêtes vers d'autres domaines
    if (!url.startsWith(self.location.origin)) return;

    event.respondWith(
        fetch(event.request)
            .then(response => {
                // Ne mettre en cache que les réponses valides (status 200)
                if (response && response.status === 200) {
                    const clone = response.clone();
                    caches.open(CACHE_NAME).then(cache =>
                        cache.put(event.request, clone)
                    );
                }
                return response;
            })
            .catch(() => caches.match(event.request))
    );
});