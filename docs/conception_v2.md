# Conception v2 — Duel pour la Terre du Milieu

Ce document complète `conception.md` (rendu initial) en décrivant le code
effectivement produit après le rendu. Il est volontairement factuel : pas
de répétition de la théorie des design patterns, on décrit ce que fait
chaque classe et pourquoi.

---

## 1. Architecture

Le projet suit une séparation classique en trois packages :

- **`fr.cpe.model`** — entités métier sans logique de jeu (Carte, Joueur,
  Pyramide, PisteAnneau, HautLieu).
- **`fr.cpe.engine`** — moteur de jeu, règles, conditions de victoire,
  factory (GameManager, PyramideFactory, AllianceSpecification,
  RingSpecification, Specification, GameObserver).
- **`fr.cpe.service`** — couche de présentation et orchestration JavaFX
  (GameService).

Plus deux fichiers fournis par le squelette et marqués comme intouchables :
`GameEngine.java` (boucle de jeu à 60 fps) et `InputService.java` /
`KeyObserver.java` (gestion clavier — non utilisée dans ce jeu mais
conservée).

L'injection de dépendances est gérée par **Google Guice** via
`AppModule.java`. `GameManager` est annoté `@Singleton`, `GameService` reçoit
le manager via `@Inject`.

---

## 2. Modèle (`fr.cpe.model`)

### `Carte`

Représente une carte du jeu, immuable. Champs :

| Champ | Type | Rôle |
|---|---|---|
| `nom` | String | Identifiant lisible (par convention = nom du fichier image) |
| `couleur` | String | `Jaune`, `Gris`, `Vert`, `Bleu`, etc. |
| `coutOr` | int | Coût de base en or |
| `symboleAlliance` | String | Conservé pour évolution future, non utilisé actuellement |
| `symbolesCompetences` | `List<String>` | Symboles donnés par la carte (compétences ou symboles verts) |
| `symbolesRequis` | `List<String>` | Symboles attendus pour réduire le coût |
| `orDonne` | int | Or immédiatement crédité à l'achat |
| `avanceAnneau` | int | Cases à avancer sur la Piste de l'Anneau |
| `cheminImage` | String | Nom de fichier `carte_C_NN` (sans extension) chargé depuis `/cartes/` |

Quatre constructeurs surchargés permettent la rétro-compatibilité avec les
tests anciens. Les listes sont défensivement copiées en `List.copyOf` pour
garantir l'immutabilité.

### `Joueur`

État d'un joueur : or, cartes possédées, symboles d'alliance, symboles de
compétence, hauts-lieux. Méthodes utilitaires :

- `compterSymboleCompetence(String)` retourne le nombre d'occurrences (utile
  pour la réduction de coût).
- `reset()` remet le joueur à zéro pour la fonction « Rejouer ».

### `Pyramide`

Stocke les cartes du chapitre courant et leurs relations de couvrement
(`Map<Carte, List<Carte>> couvertPar`). Méthodes clés :

- `ajouterCarte(c, bloqueurs)` ajoute une carte avec sa liste de cartes qui
  la couvrent.
- `estLibre(c)` retourne vrai si la liste de bloqueurs est vide.
- `retirerCarte(c)` retire la carte ET la supprime de toutes les listes de
  bloqueurs (libère les cartes du dessus en cascade).
- `definirLignes(...)` / `getLignes()` mémorise la structure visuelle (lignes
  empilées) pour que `GameService` puisse afficher la pyramide en
  quinconce.

### `PisteAnneau`

Deux entiers : `positionCommunaute` (initialisée à 5) et `positionNazguls`
(initialisée à 0). La piste va jusqu'à 15 (Mont du Destin). Méthode
`reset()` pour rejouer.

### `HautLieu`

Quatre instances créées par `GameManager` au démarrage. La mécanique de
réclamation est temporairement désactivée (les cartes militaires nécessaires
n'ont pas été incluses). Ils sont affichés mais inutilisables pour
l'instant.

---

## 3. Moteur (`fr.cpe.engine`)

### `Specification<T>` (interface générique)

```java
public interface Specification<T> {
    boolean isSatisfiedBy(T item);
    default String getDescription() { return getClass().getSimpleName(); }
}
```

Le `getDescription()` par défaut retourne le nom de classe ; chaque
implémentation l'override pour fournir un texte humain affiché sur l'écran
de fin (« 6 symboles verts distincts », « Piste de l'Anneau »).

### `AllianceSpecification`

Condition de victoire : un joueur possède 6 cartes vertes apportant chacune
un symbole différent (parmi `corne`, `fiole`, `marteau`, `pipe`,
`coquillage`, `feuille`). Implémentation par `Set<String>` rempli avec les
`symbolesCompetences` des cartes vertes du joueur.

### `RingSpecification`

Deux cas de victoire :
1. La Communauté atteint la case 15 (`FIN_DE_PISTE`).
2. Les Nazguls atteignent ou dépassent la position de la Communauté
   (rattrapage).

### `GameManager` (`@Singleton`)

Orchestrateur principal du jeu. Contient deux `Joueur`, la `Pyramide`
courante, la `PisteAnneau`, la liste des `HautLieu`, et la liste des
`Specification` à vérifier.

Méthodes principales :

- **`acheterCarte(Carte c)`** : calcule la pénalité d'or par ressource
  manquante (différence entre `symbolesRequis` de la carte et
  `compterSymboleCompetence` du joueur), débite l'or, ajoute les
  compétences/or donnés, retire la carte de la pyramide, fait avancer le
  pion correspondant sur la piste, vérifie victoire et chapitre, change de
  tour, notifie les observers.
- **`defausserCarte(Carte c)`** : crédite +2 or, retire la carte, change de
  tour.
- **`reclamerHautLieu(HautLieu hl)`** : actuellement désactivé
  (`nbForteresses = 0` forcé), conservé pour évolution future.
- **`verifierVictoire()`** : itère sur les `victorySpecs`, identifie celle
  qui a déclenché, construit `vainqueurFinal` au format
  `« <Communauté|Sauron> — victoire par <description> »`. Pour
  `RingSpecification` un test additionnel détermine qui gagne (si Nazguls
  rattrapent, c'est Sauron même si la Communauté venait de jouer).
- **`verifierEtChangerChapitre()`** : passe au chapitre suivant si la
  pyramide est vide. À la fin du chapitre 3, déclare le vainqueur au score
  ou égalité.
- **`reset()`** : remet à zéro les deux joueurs, la piste, le chapitre, les
  hauts-lieux, recrée la pyramide chapitre 1 et notifie les observers.
- **`nomJoueur(Joueur j)`** : retourne « Communauté » ou « Sauron » pour
  l'affichage.

### `PyramideFactory`

Charge `cartes.csv` au démarrage (bloc `static`) et met chaque carte en
cache dans une `Map<String, Carte>`. À la création d'un chapitre :

1. Récupère les cartes attendues depuis le cache.
2. **Mélange** la liste avec `Collections.shuffle` (ordre différent à chaque
   partie).
3. Place les cartes dans une grille selon la structure du chapitre.
4. Calcule les relations de couvrement pour chaque cellule.
5. Mémorise la structure en lignes pour l'affichage.

Structures :

| Chapitre | Cartes | Lignes (base → sommet) |
|---|---|---|
| 1 | 20 | 6-5-4-3-2 |
| 2 | 15 | 5-4-3-2-1 |
| 3 | 14 | 5-4-3-2 (les cartes militaires ont été retirées) |

### `GameObserver` (interface)

Une seule méthode : `onGameStateChanged()`. `GameService` l'implémente pour
rafraîchir l'UI à chaque changement notifié par `GameManager`.

---

## 4. Présentation (`fr.cpe.service.GameService`)

Classe centrale d'affichage et d'interaction. Implémente `GameObserver`
pour réagir aux changements d'état.

### Layout

`StackPane` racine contenant un `BorderPane` :

- **Top** : zone Communauté (bleu) — tour, chapitre, or, symboles verts,
  compétences, inventaire des cartes possédées.
- **Center** : sous-`BorderPane` avec piste de l'Anneau en haut, pyramide
  centrée, colonne droite (panneau musique + Hauts-Lieux).
- **Bottom** : zone Sauron (rouge) — symétrique de la zone Communauté.

Tout est responsive grâce à `prefWidthProperty().bind(parent.width)`.

### Affichage de la pyramide

`mettreAJourPyramide()` parcourt `Pyramide.getLignes()` du sommet vers la
base et construit, pour chaque carte :

- Si la carte est libre → vignette cliquable (clic gauche = acheter, clic
  droit = défausser).
- Si la carte est couverte → image de dos (`dos_carte_chapitre_X.png`).
- Si la carte a déjà été prise → espace transparent pour préserver
  l'alignement.

Les images sont chargées depuis le classpath via
`getClass().getResourceAsStream("/cartes/" + cheminImage + ".png")` et mises
en cache dans une `HashMap<String, Image>` pour éviter de relire les PNG à
chaque rafraîchissement.

### Inventaire

Petites vignettes 36×48 colorées avec symboles condensés (◆ compétence,
↯ avance Anneau, ⚑ Forteresse). **Clic** sur une vignette ouvre un overlay
plein écran avec le scan de la carte (zoom). Un autre clic ferme l'overlay.

### Piste de l'Anneau

16 cases (positions 0 à 15) affichées en ligne. La case 15 est dorée (Mont
du Destin). Pion bleu pour la Communauté, pion noir bordé rouge pour les
Nazguls.

### Panneau musique

`HBox` avec ComboBox de pistes (Terre du Milieu, Gondor, Khazad-dûm,
Moria), Slider de volume, ToggleButton pause/play, Button stop. Utilise
`javafx.scene.media.MediaPlayer` avec `setCycleCount(INDEFINITE)` pour la
boucle. Changer de piste libère le `MediaPlayer` précédent.

### Écran de fin

Overlay sombre par-dessus le `rootStack` quand `partieTerminee == true`.
Affiche le titre, le vainqueur (avec la cause), et deux boutons :

- **Rejouer** (vert) → `gameManager.reset()` + relance la musique.
- **Quitter** (rouge) → `Platform.exit()`.

La musique est mise en pause à l'apparition de l'écran.

---

## 5. Format des données

### `cartes.csv`

Fichier semicolon-separated, encodage UTF-8, dans `src/main/resources/`.
Une ligne par carte, 8 colonnes :

```
fichier;couleur;cout_or;cout_symbole;alliance;competence;or_donne;avance_anneau
```

- `cout_symbole` et `competence` peuvent contenir plusieurs valeurs séparées
  par virgule (ex: `Pierre,Pierre` pour exiger 2 Pierres).
- Une cellule vide signifie « pas de valeur ».

Le parser dans `PyramideFactory.chargerCartes()` est minimaliste (split sur
`;` puis sur `,`) et tolère les colonnes vides via `cols[i].trim().isEmpty()`.

### Images

- `src/main/resources/cartes/carte_C_NN.png` — 49 cartes scannées
  (20 ch1 + 15 ch2 + 14 ch3), orientation portrait.
- `src/main/resources/objets/dos_carte_chapitre_X.png` — 3 dos.

Toutes les images ont été découpées automatiquement à partir des planches
`scan_jeux/CarteFact*.jpg` via un script Pillow utilisant la détection des
bandes blanches pour trouver les frontières entre cartes.

### Audio

- `src/main/resources/audio/*.mp3` — 4 musiques libres de droits (Terre du
  Milieu par défaut, Gondor, Khazad-dûm, Moria).

---

## 6. Patterns appliqués

| Pattern | Implémentation | Endroit |
|---|---|---|
| **Singleton** | `@Singleton` Guice + `@Inject` constructeur | `GameManager`, `InputService` |
| **Factory** | Classe statique qui produit des `Pyramide` peuplées avec mélange | `PyramideFactory` |
| **Observer** | Interface `GameObserver`, `GameManager` notifie les observers à chaque mutation, `GameService` y réagit | `GameObserver` ↔ `GameService` |
| **Specification** | Interface générique `Specification<T>` avec `isSatisfiedBy` et `getDescription`, deux implémentations encapsulant les règles de victoire | `AllianceSpecification`, `RingSpecification` |
| **Dependency Injection** | Module Guice qui binde les classes ; constructeurs annotés `@Inject` | `AppModule`, `GameManager`, `GameService` |

---

## 7. Tests

27 tests JUnit 5 répartis en quatre classes :

- **`AllianceSpecificationTest`** (6 tests) : nouvelle partie non
  satisfaite, 6 symboles verts distincts par J1 ou J2 satisfont, 5 symboles
  ne suffisent pas, doublons ignorés, symboles non-verts ignorés.
- **`RingSpecificationTest`** (5 tests) : début de partie non satisfait
  (5 vs 0), Communauté atteint 15 = victoire, Nazguls atteignent 5 = défaite,
  Nazguls dépassent = défaite, Communauté avance loin devant = pas de
  victoire.
- **`PyramideTest`** (4 tests) : carte ajoutée sans couvreur libre, carte
  couverte non libre, retirer une carte libère celles qu'elle bloquait,
  filtrage `getCartesAccessibles`.
- **`GameManagerTest`** (12 tests) : état initial, achat avec/sans
  ressources, défausse, avance Anneau J1/J2, Hauts-Lieux désactivés,
  Observer, transition de chapitre.

Tous passants.

---

## 8. Limitations connues et choix volontaires

- **Hauts-Lieux désactivés** : la mécanique repose sur des cartes militaires
  qui n'ont pas été ajoutées au jeu (faute de temps). Le code et l'UI sont
  conservés pour une évolution future.
- **Cartes militaires retirées** : 6 cartes du chapitre 3 (`carte_3_04`,
  `_07`, `_08`, `_09`, `_11`, `_18` originales) ont été retirées car elles
  utilisaient le système d'arme. La structure du chapitre 3 a été réduite
  de 20 à 14 cartes.
- **`symboleAlliance` dormant** : le champ existe encore sur `Carte` et
  `Joueur` mais n'est plus utilisé dans la mécanique de jeu. Conservé pour
  la même raison que les Hauts-Lieux.
- **Pas de sauvegarde de partie** : non demandé, non implémenté.
- **Pas de mode solo / IA** : le jeu est en local 2 joueurs uniquement.

---

## 9. Comment lancer

Depuis le panneau Gradle de l'IDE : **Tasks > application > run**. La
fenêtre s'ouvre avec la pyramide du chapitre 1 mélangée aléatoirement et
la musique « Terre du Milieu » qui démarre automatiquement.
