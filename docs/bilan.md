# Fiche rendu projet

> Ce document est un bilan destiné au client. Présentez ce qui a été livré, ce qui fonctionne, et tournez habilement ce qui manque. Pas de jargon technique — on parle de fonctionnalités et de valeur perçue.

## Rappel du projet

*Duel pour la Terre du Milieu* est une adaptation du jeu de société 7 Wonders Duel dans l'univers du Seigneur des Anneaux. Deux joueurs s'affrontent en Hotseat sur le même écran : la **Communauté de l'Anneau** cherche à détruire l'Anneau au Mont du Destin, tandis que **Sauron** envoie ses Nazguls les intercepter. Chacun construit sa puissance en achetant des cartes réparties sur trois chapitres, avec trois façons distinctes de gagner la partie.

## Ce qui a été livré

### Fonctionnalité 1 — Interface graphique immersive

L'application s'ouvre directement sur une interface thématique : fond sombre dégradé, typographie médiévale-fantastique, couleurs or et bordeaux. Les zones joueurs (bleu pour la Communauté en haut, rouge pour Sauron en bas) encadrent la zone de jeu centrale. Aucun écran de chargement, aucune configuration à faire — on lance, on joue.

### Fonctionnalité 2 — Pyramide de cartes sur 3 chapitres

La pyramide centrale contient les cartes disponibles à l'achat. Seules les cartes en bas (non couvertes) sont accessibles ; les autres affichent le dos du chapitre en cours. Quand toutes les cartes d'un chapitre sont jouées, la pyramide suivante s'installe automatiquement. Les cartes sont représentées par leurs vraies illustrations scannées du jeu physique. Un clic sur une carte de l'inventaire l'affiche en grand pour lire les détails.

### Fonctionnalité 3 — Acheter ou défausser

À chaque tour, le joueur actif peut effectuer **une** action sur n'importe quelle carte accessible : **clic gauche** pour l'acheter (son coût est déduit, ses effets s'appliquent immédiatement), **clic droit** pour la défausser et récupérer 2 pièces d'or. Le jeu passe automatiquement la main à l'adversaire après chaque action.

### Fonctionnalité 4 — Piste de l'Anneau

La barre horizontale en haut de la zone centrale matérialise la piste. Le pion bleu (Communauté, démarre en case 5) progresse vers le Mont du Destin (case 15) à chaque carte bleue achetée par le joueur 1. Le pion noir (Nazguls, démarre en case 0) avance de son côté quand le joueur 2 achète des cartes bleues. Les deux pions sont visibles à tout moment, numérotés case par case.

### Fonctionnalité 5 — Trois conditions de victoire

La partie peut se terminer de trois façons : la Communauté atteint la case 15 (Mont du Destin), les Nazguls rattrapent la Communauté sur la piste, ou un joueur réunit 6 symboles verts distincts (les 6 peuples de la Terre du Milieu). Dès qu'une condition est remplie, un écran de fin s'affiche avec le vainqueur et la raison de la victoire. Un bouton **Rejouer** relance une nouvelle partie sans fermer l'application.

### Fonctionnalité 6 — Musique de fond

Quatre pistes musicales inspirées de l'univers Tolkien accompagnent la partie : *Terre du Milieu*, *Gondor*, *Khazad-dûm* et *Moria*. Un panneau discret en haut à droite permet de choisir la piste, régler le volume, mettre en pause ou arrêter la musique sans interrompre la partie.

### Fonctionnalité 7 — Règles intégrées

Un bouton **📖 Règles** ouvre un overlay explicatif directement dans l'application : but du jeu, actions disponibles, calcul des coûts, signification des couleurs de cartes, fonctionnement de la pyramide et des chapitres. Un clic n'importe où referme l'overlay.

## Ce qui n'a pas été livré (et pourquoi)

De manière générale, le projet a demandé beaucoup plus de temps que prévu, ce qui a contraint à revoir les ambitions initiales à la baisse. L'objectif de départ était plus complet ; les fonctionnalités ci-dessous en sont les principales victimes.

À l'origine, le jeu devait fonctionner en réseau local : deux joueurs sur deux machines différentes se connectent l'un à l'autre pour rejoindre une même partie. Cette vision a été abandonnée faute de temps — l'implémentation du multijoueur réseau (synchronisation de l'état de jeu, gestion de la connexion, tour par tour à distance) représentait un chantier trop important à mener en parallèle du reste. Le mode Hotseat actuel (deux joueurs sur le même écran) est une solution de repli qui permet quand même de jouer, mais ce n'était pas l'intention initiale.

### Pyramide — Forme simplifiée

La pyramide affichée à l'écran ne respecte pas la disposition exacte du jeu physique. Dans 7 Wonders Duel, certaines cartes sont décalées et se chevauchent pour former une vraie pyramide visuelle avec des positions précises. Faute de temps, la disposition implémentée est une grille en lignes de tailles décroissantes — fonctionnelle pour le jeu mais visuellement éloignée de l'original.

### Hauts-Lieux — Interface en place, effets à finaliser

Les quatre Hauts-Lieux sont visibles dans la colonne de droite avec leur coût affiché. La base est là : l'interface, la logique de réclamation, le passage de main après achat. Ce qui manque, c'est la mécanique de Forteresse — un type de symbole porté par les cartes militaires — qui conditionne le déblocage des Hauts-Lieux. Cette mécanique a été identifiée tard dans le développement, après que les cartes militaires avaient déjà été intégrées sans ce symbole. Plutôt que de livrer des Hauts-Lieux dont les conditions d'accès seraient impossibles à remplir, on a préféré les garder visibles en attendant la mise à jour des cartes correspondantes. La feature est à 70% — elle n'attend que les cartes militaires avec les bons attributs.

## Perspectives

### Court terme

- **Hauts-Lieux fonctionnels** : ajouter le symbole Forteresse aux cartes militaires bleues et activer le mécanisme de réclamation. C'est le principal chantier ouvert.
- **Revenus de début de tour** : selon les règles officielles de 7 Wonders Duel, chaque joueur reçoit 7 pièces d'or au début de son tour (en sus des effets de cartes). L'implémenter rendrait l'économie du jeu plus fidèle à l'original.

### Moyen terme

- **Vrais noms et effets pour les Hauts-Lieux** : remplacer les placeholders actuels par les lieux réels de la Terre du Milieu (Fondcombe, Lórien…) avec leurs effets distincts.
- **Cartes progrès scientifiques** : 7 Wonders Duel inclut des jetons progrès qui s'activent en combinant des paires de symboles scientifiques. Ce serait une troisième condition de victoire et un axe stratégique supplémentaire.

### Plus loin

- **Mode en réseau** : permettre une partie entre deux ordinateurs distants plutôt qu'en Hotseat.
- **Éditeur de cartes** : un outil pour créer des cartes personnalisées et les intégrer dans la pyramide, sans modifier le code.
- **Militaire** : implémenter les cartes militaires avec le plateau de forteresse pour faire toutes les conditions de victoire et de déblocage des hauts lieux.
- **Jetons Progrès** : implémenter les jetons progrès scientifiques pour faire toutes les conditions de victoire et de déblocage des hauts lieux.
