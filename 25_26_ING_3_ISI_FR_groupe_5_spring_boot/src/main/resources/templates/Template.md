<!DOCTYPE html>
<html lang="fr" data-theme="dark" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>CarnetRouge — Gestion Utilisateurs</title>
    <link rel="manifest" href="/manifest.json">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=DM+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" />

    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        bgdeep:       'var(--color-bgdeep)',
                        bgcard:       'var(--color-bgcard)',
                        bgsurface:    'var(--color-bgsurface)',
                        bgsubtle:     'var(--color-bgsubtle)',
                        primary:      'var(--color-primary)',
                        primaryhover: 'var(--color-primaryhover)',
                        textmain:     'var(--color-textmain)',
                        textmuted:    'var(--color-textmuted)',
                        bordercolor:  'var(--color-border)',
                    },
                    fontFamily: {
                        sans: ['"DM Sans"', 'sans-serif'],
                        serif: ['"Playfair Display"', 'serif'],
                        mono: ['"JetBrains Mono"', 'monospace'],
                    }
                }
            }
        }
    </script>

    <style>
        :root[data-theme="light"] {
            --color-bgdeep:       #f0f2f5;
            --color-bgcard:       #ffffff;
            --color-bgsurface:    #f7f8fa;
            --color-bgsubtle:     #e8eaf0;
            --color-primary:      #4f7ef8;
            --color-primaryhover: #3a6ae8;
            --color-textmain:     #1c2230;
            --color-textmuted:    #6b7280;
            --color-border:       #e2e5ec;
        }
        :root[data-theme="dark"] {
            --color-bgdeep:       #0a0b0f;
            --color-bgcard:       #111318;
            --color-bgsurface:    #181b22;
            --color-bgsubtle:     #1e2230;
            --color-primary:      #4f7ef8;
            --color-primaryhover: #6b94ff;
            --color-textmain:     #f0f0f2;
            --color-textmuted:    #9aa3b2;
            --color-border:       #1e2230;
        }

        body {
            background-color: var(--color-bgdeep);
            color: var(--color-textmain);
            transition: background-color .25s ease, color .25s ease;
        }

        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-thumb { background: var(--color-bgsubtle); border-radius: 4px; }
        ::-webkit-scrollbar-track { background: transparent; }

        @keyframes fadeInUp {
            0%   { opacity: 0; transform: translateY(10px); }
            100% { opacity: 1; transform: translateY(0); }
        }
        .stagger { animation: fadeInUp .35s ease-out forwards; opacity: 0; }

        /* ── Toast ── */
        .toast {
            position: fixed;
            bottom: 24px; left: 50%;
            transform: translate(-50%, 24px);
            display: flex; align-items: center; gap: 10px;
            padding: 12px 20px; border-radius: 10px;
            font-size: .85rem; font-weight: 500;
            background-color: var(--color-bgcard);
            border: 1px solid var(--color-border);
            color: var(--color-textmain);
            opacity: 0; pointer-events: none;
            transition: all .3s cubic-bezier(.4,0,.2,1);
            z-index: 100; box-shadow: 0 10px 30px -10px rgba(0,0,0,.4);
        }
        .toast.show { opacity: 1; transform: translate(-50%, 0); pointer-events: auto; }
        .toast.success { border-color: rgba(34,197,94,.35); }
        .toast.success .toast-i { color: #4ade80; }
        .toast.error { border-color: rgba(239,68,68,.35); }
        .toast.error .toast-i { color: #f87171; }

        @media (prefers-reduced-motion: reduce) {
            *, .stagger { animation: none !important; transition: none !important; opacity: 1 !important; }
        }
    </style>
</head>
<body class="font-sans text-[15px] min-h-screen">

<div class="min-h-screen bg-bgdeep">

    <!-- ════════════ NAVBAR ════════════ -->
    <nav class="sticky top-0 z-30 backdrop-blur-md border-b border-bordercolor"
         style="background-color: color-mix(in srgb, var(--color-bgdeep) 80%, transparent);">
        <div class="max-w-[1400px] mx-auto px-5 sm:px-8 h-16 flex items-center justify-between gap-4">
            <a th:href="@{/dashboard}" class="flex items-center gap-3 no-underline">
                <div class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0"
                     style="background: linear-gradient(135deg, var(--color-primary), var(--color-primaryhover));">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                         fill="none" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H19a1 1 0 0 1 1 1v18a1 1 0 0 1-1 1H6.5a1 1 0 0 1 0-5H20"/>
                    </svg>
                </div>
                <span class="font-serif text-lg font-bold tracking-tight text-textmain">
                    Carnet<span style="color: var(--color-primary);">Rouge</span>
                </span>
            </a>
            <div class="flex items-center gap-3">
                <div class="hidden sm:flex items-center gap-2 text-sm text-textmuted">
                    <div class="w-8 h-8 rounded-full flex items-center justify-center font-mono text-xs font-semibold text-white"
                         style="background-color: var(--color-primary);">A</div>
                    <span sec:authentication="name">Admin</span>
                </div>
                <span class="hidden sm:inline-flex text-[10px] font-mono uppercase tracking-wider px-2 py-1 rounded-full"
                      style="background-color: color-mix(in srgb, var(--color-primary) 18%, transparent); color: var(--color-primary);">Admin</span>
                <a th:href="@{/logout}"
                   class="text-sm font-medium px-3 py-2 rounded-lg border border-bordercolor hover:border-primary transition-colors flex items-center gap-2 text-textmain no-underline">
                    <i class="bi bi-box-arrow-right"></i> <span class="hidden sm:inline">Déconnexion</span>
                </a>
            </div>
        </div>
    </nav>

    <!-- ════════════ MAIN ════════════ -->
    <main class="max-w-[1400px] mx-auto px-5 sm:px-8 py-8">

        <!-- Page Header -->
        <div class="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-8">
            <div>
                <p class="text-xs uppercase tracking-[0.2em] font-mono mb-2 text-primary">CarnetRouge — Administration</p>
                <h1 class="font-serif text-3xl sm:text-4xl font-bold tracking-tight text-textmain">
                    Personnel <span style="color: var(--color-primary);">Enseignant</span><br>&amp; Assistant
                </h1>
                <p class="text-textmuted text-sm mt-2 max-w-xl">Gérez les accès et les informations du personnel académique.</p>
            </div>
            <div class="flex items-center gap-3">
                <div class="rounded-xl border border-bordercolor bg-bgcard px-4 py-3 flex items-center gap-3">
                    <div class="w-10 h-10 rounded-lg flex items-center justify-center text-lg"
                         style="background-color: color-mix(in srgb, var(--color-primary) 15%, transparent); color: var(--color-primary);">
                        <i class="bi bi-people-fill"></i>
                    </div>
                    <div>
                        <p class="text-xs text-textmuted">Total Utilisateurs</p>
                        <p class="text-2xl font-serif font-bold text-textmain" th:text="${utilisateurs.totalElements}">0</p>
                    </div>
                </div>
                <a th:href="@{/admin/utilisateurs/creer}"
                   class="text-sm font-medium px-4 py-2.5 rounded-lg text-white transition-colors bg-primary hover:bg-primaryhover flex items-center gap-2 no-underline shrink-0">
                    <i class="bi bi-person-plus-fill"></i> <span class="hidden sm:inline">Nouvel utilisateur</span>
                </a>
            </div>
        </div>

        <!-- ════════════ BARRE EXPORT / IMPORT ════════════ -->
        <div class="rounded-xl border border-bordercolor bg-bgcard px-4 py-3 mb-4">
            <div class="flex items-center justify-between flex-wrap gap-3">
                <span class="text-[11px] font-mono uppercase tracking-wider text-textmuted flex items-center gap-2">
                    <i class="bi bi-file-earmark-arrow-down"></i> Export / Import
                </span>
                <div class="flex items-center gap-2 flex-wrap">

                    <!-- Modèle -->
                    <a th:href="@{/admin/export-import/modele/utilisateur}"
                       class="text-xs font-semibold px-3 py-2 rounded-lg border border-bordercolor text-textmuted hover:border-primary hover:text-textmain transition-colors flex items-center gap-2 no-underline">
                        <i class="bi bi-download"></i> Modèle
                    </a>

                    <!-- Import -->
                    <button type="button" onclick="document.getElementById('import-file').click()"
                            class="text-xs font-semibold px-3 py-2 rounded-lg border border-bordercolor text-textmuted hover:border-primary hover:text-textmain transition-colors flex items-center gap-2 cursor-pointer bg-transparent">
                        <i class="bi bi-upload"></i> Importer
                    </button>
                    <form id="import-form" th:action="@{/admin/export-import/import/utilisateurs}" method="post" enctype="multipart/form-data" class="hidden">
                        <input type="file" id="import-file" name="fichier" accept=".xlsx" onchange="document.getElementById('import-form').submit()"/>
                    </form>

                    <!-- Export -->
                    <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg border border-bordercolor bg-bgsurface">
                        <select id="export-format"
                                class="bg-transparent border-none text-xs font-semibold text-textmuted outline-none cursor-pointer">
                            <option value="excel">Excel</option>
                            <option value="pdf">PDF</option>
                        </select>
                        <label class="flex items-center gap-1.5 text-[11px] text-textmuted cursor-pointer whitespace-nowrap">
                            <input type="checkbox" id="export-annee" checked
                                   class="w-3.5 h-3.5 accent-[color:var(--color-primary)]"/>
                            Année active
                        </label>
                        <button type="button" onclick="exporter()"
                                class="text-xs font-bold px-3 py-1.5 rounded-md text-white bg-primary hover:bg-primaryhover transition-colors flex items-center gap-1.5">
                            <i class="bi bi-file-earmark-arrow-down"></i> Exporter
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- ════════════ FILTRES ════════════ -->
        <div class="rounded-xl border border-bordercolor bg-bgcard p-4 mb-4">
            <form th:action="@{/admin/utilisateurs}" method="get">
                <div class="grid grid-cols-1 sm:grid-cols-[1fr_220px_auto] gap-3 items-end">
                    <div>
                        <label class="text-xs font-mono text-textmuted block mb-1.5">Recherche</label>
                        <div class="relative">
                            <i class="bi bi-search absolute left-3 top-1/2 -translate-y-1/2 text-textmuted text-sm"></i>
                            <input type="text" name="recherche"
                                   th:value="${recherche ?: ''}"
                                   placeholder="Nom, email, grade…"
                                   class="w-full text-sm rounded-lg pl-9 pr-3 py-2.5 border outline-none bg-bgsurface border-bordercolor focus:ring-2 focus:ring-primary text-textmain placeholder-textmuted/50">
                        </div>
                    </div>
                    <div>
                        <label class="text-xs font-mono text-textmuted block mb-1.5">Type d'utilisateur</label>
                        <div class="relative">
                            <select name="type"
                                    class="w-full text-sm rounded-lg pl-3 pr-8 py-2.5 border outline-none bg-bgsurface border-bordercolor focus:ring-2 focus:ring-primary text-textmain appearance-none cursor-pointer">
                                <option value="TOUS" th:selected="${typeSelectionne == null or typeSelectionne == 'TOUS'}">Tous les types</option>
                                <option value="ENS"  th:selected="${typeSelectionne == 'ENS'}">Enseignants</option>
                                <option value="AST"  th:selected="${typeSelectionne == 'AST'}">Assistants</option>
                                <option value="SUR"  th:selected="${typeSelectionne == 'SUR'}">Surveillants</option>
                            </select>
                            <i class="bi bi-chevron-down absolute right-3 top-1/2 -translate-y-1/2 text-textmuted text-xs pointer-events-none"></i>
                        </div>
                    </div>
                    <div class="flex gap-2">
                        <button type="submit"
                                class="text-sm font-medium px-4 py-2.5 rounded-lg text-white bg-primary hover:bg-primaryhover transition-colors flex items-center gap-2 whitespace-nowrap">
                            <i class="bi bi-funnel-fill"></i> Filtrer
                        </button>
                        <a th:href="@{/admin/utilisateurs}" title="Réinitialiser"
                           class="w-[42px] h-[42px] shrink-0 rounded-lg border border-bordercolor hover:border-primary transition-colors flex items-center justify-center text-textmuted no-underline">
                            <i class="bi bi-arrow-clockwise"></i>
                        </a>
                    </div>
                </div>
            </form>
        </div>

        <!-- ════════════ MESSAGES FLASH ════════════ -->
        <div th:if="${success}" class="toast-success flex items-center gap-2 px-4 py-3 mb-4 rounded-lg"
             style="background-color: rgba(34,197,94,.1); border: 1px solid rgba(34,197,94,.2); color: #4ade80;">
            <i class="bi bi-check-circle-fill"></i> <span th:text="${success}"></span>
        </div>
        <div th:if="${error}" class="toast-error flex items-center gap-2 px-4 py-3 mb-4 rounded-lg"
             style="background-color: rgba(239,68,68,.1); border: 1px solid rgba(239,68,68,.2); color: #f87171;">
            <i class="bi bi-exclamation-triangle-fill"></i> <span th:text="${error}"></span>
        </div>

        <!-- Erreurs import -->
        <div th:if="${importErreurs != null and not #lists.isEmpty(importErreurs)}"
             class="mb-4 px-4 py-3 rounded-lg max-h-[200px] overflow-y-auto"
             style="background-color: rgba(239,68,68,.08); border: 1px solid rgba(239,68,68,.2);">
            <p class="text-xs font-bold mb-2" style="color:#f87171;">Erreurs d'import :</p>
            <ul class="text-xs space-y-1" style="color:#fca5a5;">
                <li th:each="err : ${importErreurs}" th:text="${err}"></li>
            </ul>
        </div>

        <!-- ════════════ TABLE / LISTE ════════════ -->
        <div class="rounded-xl border border-bordercolor bg-bgcard overflow-hidden">
            <div class="flex items-center justify-between px-5 py-4 border-b border-bordercolor">
                <h2 class="font-serif text-xl font-semibold text-textmain">Liste du Personnel</h2>
                <span class="text-xs font-mono text-textmuted" th:text="${utilisateurs.totalElements + ' résultats'}">0 résultats</span>
            </div>

            <!-- ── Table desktop ── -->
            <div class="hidden md:block overflow-x-auto">
                <table class="w-full text-sm">
                    <thead>
                    <tr class="border-b border-bordercolor text-left">
                        <th class="px-5 py-3 text-xs font-mono uppercase tracking-wider text-textmuted">Utilisateur</th>
                        <th class="px-5 py-3 text-xs font-mono uppercase tracking-wider text-textmuted">Contact</th>
                        <th class="px-5 py-3 text-xs font-mono uppercase tracking-wider text-textmuted text-center">Type</th>
                        <th class="px-5 py-3 text-xs font-mono uppercase tracking-wider text-textmuted">Spécifications</th>
                        <th class="px-5 py-3 text-xs font-mono uppercase tracking-wider text-textmuted">Rôles</th>
                        <th class="px-5 py-3 text-xs font-mono uppercase tracking-wider text-textmuted">Statut</th>
                        <th class="px-5 py-3 text-xs font-mono uppercase tracking-wider text-textmuted text-right">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr th:each="u : ${utilisateurs.content}" class="border-b border-bordercolor last:border-0 hover:bg-bgsurface transition-colors">
                        <td class="px-5 py-3.5">
                            <div class="flex items-center gap-3">
                                <div class="w-9 h-9 rounded-lg flex items-center justify-center font-mono text-sm font-semibold shrink-0"
                                     style="background-color: var(--color-bgsubtle); color: var(--color-primary);"
                                     th:text="${#strings.substring(u.nom,0,1)}">U</div>
                                <div>
                                    <p class="font-medium text-textmain" th:text="${u.nom + ' ' + u.prenom}">Nom Prenom</p>
                                    <p class="text-xs font-mono text-textmuted" th:text="${'#' + u.id}">#000</p>
                                </div>
                            </div>
                        </td>
                        <td class="px-5 py-3.5 text-textmuted" th:text="${u.email}">email@exemple.com</td>
                        <td class="px-5 py-3.5 text-center">
                            <span th:if="${u.type == 'ENS'}" class="text-[10px] font-mono uppercase tracking-wider px-2 py-1 rounded-full inline-flex items-center gap-1"
                                  style="background-color: color-mix(in srgb, var(--color-primary) 15%, transparent); color: var(--color-primary);">
                                <i class="bi bi-mortarboard-fill"></i> ENS</span>
                            <span th:if="${u.type == 'AST'}" class="text-[10px] font-mono uppercase tracking-wider px-2 py-1 rounded-full inline-flex items-center gap-1"
                                  style="background-color: var(--color-bgsubtle); color: var(--color-textmuted);">
                                <i class="bi bi-person-workspace"></i> AST</span>
                            <span th:if="${u.type == 'SUR'}" class="text-[10px] font-mono uppercase tracking-wider px-2 py-1 rounded-full inline-flex items-center gap-1"
                                  style="background-color: color-mix(in srgb, #f59e0b 15%, transparent); color: #f59e0b;">
                                <i class="bi bi-shield-fill-check"></i> SUR</span>
                        </td>
                        <td class="px-5 py-3.5">
                            <div th:if="${u.type == 'ENS'}" class="space-y-0.5 text-xs">
                                <div class="flex gap-2"><span class="text-textmuted w-12 shrink-0">Grade</span><span class="font-medium text-textmain" th:text="${u.grade ?: '—'}">—</span></div>
                                <div class="flex gap-2"><span class="text-textmuted w-12 shrink-0">Cat.</span><span class="font-medium text-textmain" th:text="${u.typeEnseignant ?: '—'}">—</span></div>
                            </div>
                            <div th:if="${u.type == 'AST'}" class="space-y-0.5 text-xs">
                                <div class="flex gap-2"><span class="text-textmuted w-12 shrink-0">Fonct.</span><span class="font-medium text-textmain" th:text="${u.fonction ?: '—'}">—</span></div>
                            </div>
                            <div th:if="${u.type == 'SUR'}" class="space-y-0.5 text-xs">
                                <div class="flex gap-2"><span class="text-textmuted w-12 shrink-0">Contrat</span><span class="font-medium text-textmain" th:text="${u.typeContrat ?: '—'}">—</span></div>
                                <div class="flex gap-2"><span class="text-textmuted w-12 shrink-0">Secteur</span><span class="font-medium text-textmain" th:text="${u.secteur ?: '—'}">—</span></div>
                            </div>
                        </td>
                        <td class="px-5 py-3.5">
                            <div class="flex flex-wrap gap-1">
                                <span th:each="role : ${u.roles}" th:text="${role.nom}"
                                      class="text-[10px] font-mono font-semibold px-2 py-0.5 rounded-md"
                                      style="background-color: color-mix(in srgb, var(--color-primary) 10%, transparent); color: var(--color-primary); border: 1px solid color-mix(in srgb, var(--color-primary) 20%, transparent);">
                                </span>
                            </div>
                        </td>
                        <td class="px-5 py-3.5">
                            <div th:if="${u.active}" class="inline-flex items-center gap-1.5 text-xs font-medium" style="color:#4ade80;">
                                <span class="w-1.5 h-1.5 rounded-full" style="background-color:#4ade80;"></span> Actif
                            </div>
                            <div th:unless="${u.active}" class="inline-flex items-center gap-1.5 text-xs font-medium text-textmuted">
                                <span class="w-1.5 h-1.5 rounded-full bg-textmuted"></span> Inactif
                            </div>
                        </td>
                        <td class="px-5 py-3.5">
                            <div class="flex items-center justify-end gap-1.5">
                                <a th:href="@{/admin/utilisateurs/{id}(id=${u.id})}"
                                   title="Voir détails"
                                   class="w-8 h-8 rounded-lg flex items-center justify-center border border-bordercolor hover:border-primary hover:text-primary transition-colors text-textmuted no-underline">
                                    <i class="bi bi-eye-fill text-sm"></i>
                                </a>
                                <form th:action="@{/admin/utilisateurs/{id}/toggle-active(id=${u.id})}" method="post" class="inline">
                                    <input type="hidden" name="active" th:value="${!u.active}" />
                                    <button type="submit" title="Activer/Désactiver"
                                            class="w-8 h-8 rounded-lg flex items-center justify-center border border-bordercolor hover:border-primary hover:text-primary transition-colors text-textmuted bg-transparent cursor-pointer">
                                        <i class="bi bi-power text-sm"></i>
                                    </button>
                                </form>
                                <form th:action="@{/admin/utilisateurs/{id}/supprimer(id=${u.id})}" method="post" class="inline"
                                      onsubmit="return confirm('Confirmer la suppression ?')">
                                    <button type="submit" title="Supprimer"
                                            class="w-8 h-8 rounded-lg flex items-center justify-center border border-bordercolor hover:border-red-500 hover:text-red-500 transition-colors text-textmuted bg-transparent cursor-pointer">
                                        <i class="bi bi-trash3 text-sm"></i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- ── Cards mobile ── -->
            <div class="md:hidden divide-y divide-bordercolor">
                <div th:each="u : ${utilisateurs.content}" class="p-4 space-y-3">

                    <!-- En-tête -->
                    <div class="flex items-start gap-3">
                        <div class="w-10 h-10 rounded-lg flex items-center justify-center font-mono text-sm font-semibold shrink-0"
                             style="background-color: var(--color-bgsubtle); color: var(--color-primary);"
                             th:text="${#strings.substring(u.nom,0,1)}">U</div>
                        <div class="flex-1 min-w-0">
                            <p class="font-medium text-textmain truncate" th:text="${u.nom + ' ' + u.prenom}">Nom Prénom</p>
                            <p class="text-xs text-textmuted truncate" th:text="${u.email}">email@exemple.com</p>
                        </div>
                        <span th:if="${u.type == 'ENS'}" class="text-[10px] font-mono uppercase tracking-wider px-2 py-1 rounded-full inline-flex items-center gap-1 shrink-0"
                              style="background-color: color-mix(in srgb, var(--color-primary) 15%, transparent); color: var(--color-primary);">
                            <i class="bi bi-mortarboard-fill"></i> ENS</span>
                        <span th:if="${u.type == 'AST'}" class="text-[10px] font-mono uppercase tracking-wider px-2 py-1 rounded-full inline-flex items-center gap-1 shrink-0"
                              style="background-color: var(--color-bgsubtle); color: var(--color-textmuted);">
                            <i class="bi bi-person-workspace"></i> AST</span>
                        <span th:if="${u.type == 'SUR'}" class="text-[10px] font-mono uppercase tracking-wider px-2 py-1 rounded-full inline-flex items-center gap-1 shrink-0"
                              style="background-color: color-mix(in srgb, #f59e0b 15%, transparent); color: #f59e0b;">
                            <i class="bi bi-shield-fill-check"></i> SUR</span>
                    </div>

                    <!-- Méta -->
                    <div class="flex items-center justify-between text-xs rounded-lg px-3 py-2" style="background-color: var(--color-bgsurface);">
                        <div>
                            <span class="text-textmuted">Statut · </span>
                            <span th:if="${u.active}" class="font-medium inline-flex items-center gap-1" style="color:#4ade80;">
                                <span class="w-1.5 h-1.5 rounded-full" style="background-color:#4ade80;"></span> Actif</span>
                            <span th:unless="${u.active}" class="font-medium inline-flex items-center gap-1 text-textmuted">
                                <span class="w-1.5 h-1.5 rounded-full bg-textmuted"></span> Inactif</span>
                        </div>
                        <span class="font-mono text-textmuted" th:text="${'#' + u.id}">#000</span>
                    </div>

                    <!-- Specs -->
                    <div class="flex flex-wrap gap-1.5 text-xs">
                        <th:block th:if="${u.type == 'ENS'}">
                            <span class="px-2 py-1 rounded-md border border-bordercolor text-textmain"><strong class="text-textmuted">Grade</strong> <th:block th:text="${u.grade ?: '—'}"></th:block></span>
                            <span class="px-2 py-1 rounded-md border border-bordercolor text-textmain"><strong class="text-textmuted">Cat.</strong> <th:block th:text="${u.typeEnseignant ?: '—'}"></th:block></span>
                        </th:block>
                        <th:block th:if="${u.type == 'AST'}">
                            <span class="px-2 py-1 rounded-md border border-bordercolor text-textmain"><strong class="text-textmuted">Fonction</strong> <th:block th:text="${u.fonction ?: '—'}"></th:block></span>
                        </th:block>
                        <th:block th:if="${u.type == 'SUR'}">
                            <span class="px-2 py-1 rounded-md border border-bordercolor text-textmain"><strong class="text-textmuted">Secteur</strong> <th:block th:text="${u.secteur ?: '—'}"></th:block></span>
                            <span class="px-2 py-1 rounded-md border border-bordercolor text-textmain"><strong class="text-textmuted">Contrat</strong> <th:block th:text="${u.typeContrat ?: '—'}"></th:block></span>
                        </th:block>
                    </div>

                    <!-- Rôles -->
                    <div class="flex flex-wrap gap-1">
                        <span th:each="role : ${u.roles}" th:text="${role.nom}"
                              class="text-[10px] font-mono font-semibold px-2 py-0.5 rounded-md"
                              style="background-color: color-mix(in srgb, var(--color-primary) 10%, transparent); color: var(--color-primary); border: 1px solid color-mix(in srgb, var(--color-primary) 20%, transparent);">
                        </span>
                    </div>

                    <!-- Actions -->
                    <div class="flex items-center gap-2 pt-1">
                        <a th:href="@{/admin/utilisateurs/{id}(id=${u.id})}"
                           class="w-9 h-9 rounded-lg flex items-center justify-center border border-bordercolor text-textmuted no-underline">
                            <i class="bi bi-eye-fill"></i>
                        </a>
                        <form th:action="@{/admin/utilisateurs/{id}/toggle-active(id=${u.id})}" method="post">
                            <input type="hidden" name="active" th:value="${!u.active}" />
                            <button type="submit" class="w-9 h-9 rounded-lg flex items-center justify-center border border-bordercolor text-textmuted bg-transparent">
                                <i class="bi bi-power"></i>
                            </button>
                        </form>
                        <form th:action="@{/admin/utilisateurs/{id}/supprimer(id=${u.id})}" method="post"
                              onsubmit="return confirm('Confirmer la suppression ?')">
                            <button type="submit" class="w-9 h-9 rounded-lg flex items-center justify-center border border-bordercolor text-red-500 bg-transparent">
                                <i class="bi bi-trash3"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- ════════════ PAGINATION ════════════ -->
        <div th:if="${utilisateurs.totalPages > 1}" class="flex items-center justify-center gap-1.5 mt-6">
            <a th:if="${utilisateurs.hasPrevious()}"
               th:href="@{/admin/utilisateurs(page=${utilisateurs.number - 1},recherche=${recherche},type=${typeSelectionne})}"
               class="w-9 h-9 rounded-lg flex items-center justify-center border border-bordercolor hover:border-primary text-textmuted no-underline transition-colors">
                <i class="bi bi-chevron-left"></i>
            </a>
            <a th:each="i : ${#numbers.sequence(0, utilisateurs.totalPages - 1)}"
               th:href="@{/admin/utilisateurs(page=${i},recherche=${recherche},type=${typeSelectionne})}"
               th:text="${i + 1}"
               th:classappend="${i == utilisateurs.number} ? 'text-white border-primary' : 'text-textmuted border-bordercolor hover:border-primary'"
               class="w-9 h-9 rounded-lg flex items-center justify-center border text-sm font-mono no-underline transition-colors"
               th:style="${i == utilisateurs.number} ? 'background-color: var(--color-primary);' : ''"></a>
            <a th:if="${utilisateurs.hasNext()}"
               th:href="@{/admin/utilisateurs(page=${utilisateurs.number + 1},recherche=${recherche},type=${typeSelectionne})}"
               class="w-9 h-9 rounded-lg flex items-center justify-center border border-bordercolor hover:border-primary text-textmuted no-underline transition-colors">
                <i class="bi bi-chevron-right"></i>
            </a>
        </div>

    </main>
</div>

<!-- Toast -->
<div class="toast" id="toast">
    <i class="bi toast-i" id="toast-i"></i>
    <span id="toast-msg"></span>
</div>

<script th:inline="javascript">
    /* ── Toast ── */
    var _tt = null;
    function showToast(type, icon, msg) {
        var t  = document.getElementById('toast');
        var ti = document.getElementById('toast-i');
        var tm = document.getElementById('toast-msg');
        if (_tt) clearTimeout(_tt);
        t.className  = 'toast ' + type;
        ti.className = 'bi ' + icon + ' toast-i';
        tm.textContent = msg;
        t.classList.add('show');
        _tt = setTimeout(function() { t.classList.remove('show'); }, 3500);
    }

    document.addEventListener('DOMContentLoaded', function() {
        var success = document.querySelector('.toast-success');
        var error   = document.querySelector('.toast-error');
        if (success) {
            showToast('success', 'bi-check-circle-fill', success.textContent.trim());
            setTimeout(function() { success.style.display = 'none'; }, 3500);
        }
        if (error) {
            showToast('error', 'bi-exclamation-triangle-fill', error.textContent.trim());
            setTimeout(function() { error.style.display = 'none'; }, 3500);
        }
    });

    /* ── Export ── */
    function exporter() {
        const format = document.getElementById('export-format').value;
        const avecAnnee = document.getElementById('export-annee').checked;
        let url = `/admin/export-import/${format}/utilisateurs`;
        if (avecAnnee) {
            const anneeId = /*[[${anneeActive?.id}]]*/ null;
            if (anneeId) url += `?anneeId=${anneeId}`;
        }
        window.location.href = url;
    }
</script>

</body>
</html>