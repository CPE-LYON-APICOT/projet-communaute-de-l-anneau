# Conception technique

> Ce document décrit l'architecture technique du projet "Duel pour la Terre du Milieu". Vous êtes dans le rôle du lead-dev / architecte. C'est un document technique destiné à des développeurs.

## Vue d'ensemble

L'application suit une architecture MVC simplifiée, adaptée au socle JavaFX/Canvas fourni :

1. **Model** — Les données pures et la logique métier isolée. `Joueur` stocke les ressources et symboles, `Carte` contient les coûts et effets, `Pyramide` gère la structure du draft.
2. **Service / Engine** — Le contrôleur central. `GameManager` orchestre les tours de jeu, valide les actions et vérifie les conditions de victoire.
3. **View** — Le rendu graphique via le `Canvas` JavaFX. Il écoute les changements d'état du modèle pour se redessiner.
