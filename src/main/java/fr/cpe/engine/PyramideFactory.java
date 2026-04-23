package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Pyramide;
import java.util.Arrays;

public class PyramideFactory {

    // =========================================================================
    // CHAPITRE 1 - Fondations : cartes bon marché, peu de Forteresse, 1 avance
    // =========================================================================
    public static Pyramide createChapitre1() {
        Pyramide pyramide = new Pyramide();

        // Ligne 1 (base - libres) : cartes à 0 or, compétences de base
        Carte c1 = new Carte("Ferme de la Comté", "Jaune", 0, "Hobbits", "Nourriture", null, 0);
        Carte c2 = new Carte("Mine de Moria",     "Gris",  0, "Dwarves", "Pierre",    null, 0);
        Carte c3 = new Carte("Clairière",         "Vert",  0, "Ents",    "Bois",      null, 0);

        // Ligne 2 (couverte par ligne 1) : demandent une ressource
        Carte c4 = new Carte("Caserne d'Osgiliath", "Rouge", 2, "Forteresse", null,    null,   1);
        Carte c5 = new Carte("Temple de Fondcombe", "Bleu",  1, "Elves",      "Magie", "Bois", 0);

        // Ligne 3 (sommet)
        Carte c6 = new Carte("Cité de Bree",        "Bleu",  2, "Rohan",      null,    "Pierre", 0);

        pyramide.ajouterCarte(c1, null);
        pyramide.ajouterCarte(c2, null);
        pyramide.ajouterCarte(c3, null);
        pyramide.ajouterCarte(c4, Arrays.asList(c1, c2));
        pyramide.ajouterCarte(c5, Arrays.asList(c2, c3));
        pyramide.ajouterCarte(c6, Arrays.asList(c4, c5));

        return pyramide;
    }

    // =========================================================================
    // CHAPITRE 2 - Alliances : cartes plus chères, 2 Forteresse, +3 anneau
    // =========================================================================
    public static Pyramide createChapitre2() {
        Pyramide pyramide = new Pyramide();

        Carte c1 = new Carte("Grenier de Bree",   "Jaune", 1, "Hobbits",    "Nourriture", null,      0);
        Carte c2 = new Carte("Forge Naine",       "Gris",  1, "Dwarves",    "Acier",      "Pierre",  0);
        Carte c3 = new Carte("Scriptorium elfe",  "Vert",  2, "Elves",      "Magie",      "Bois",    0);

        Carte c4 = new Carte("Garnison du Gondor","Rouge", 3, "Forteresse", null,         "Acier",   1);
        Carte c5 = new Carte("Tour de Garde",     "Rouge", 3, "Forteresse", null,         "Acier",   2);

        Carte c6 = new Carte("Palais Royal",      "Bleu",  4, "Gondor",     null,         "Magie",   0);

        pyramide.ajouterCarte(c1, null);
        pyramide.ajouterCarte(c2, null);
        pyramide.ajouterCarte(c3, null);
        pyramide.ajouterCarte(c4, Arrays.asList(c1, c2));
        pyramide.ajouterCarte(c5, Arrays.asList(c2, c3));
        pyramide.ajouterCarte(c6, Arrays.asList(c4, c5));

        return pyramide;
    }

    // =========================================================================
    // CHAPITRE 3 - Grandes œuvres : cartes puissantes, 2 Forteresse, +5 anneau
    // =========================================================================
    public static Pyramide createChapitre3() {
        Pyramide pyramide = new Pyramide();

        Carte c1 = new Carte("Forêt de Fangorn",   "Vert",  3, "Ents",       "Bois",   "Nourriture", 0);
        Carte c2 = new Carte("Khazad-dûm",         "Gris",  4, "Dwarves",    "Acier",  "Pierre",     0);
        Carte c3 = new Carte("Caserne du Rohan",   "Rouge", 4, "Forteresse", null,     "Acier",      2);

        Carte c4 = new Carte("Tour de Mordor",     "Rouge", 5, "Forteresse", null,     "Magie",      3);
        Carte c5 = new Carte("Fondcombe",          "Bleu",  5, "Elves",      "Magie",  "Bois",       0);

        Carte c6 = new Carte("Minas Tirith",       "Bleu",  5, "Gondor",     null,     "Pierre",     0);

        pyramide.ajouterCarte(c1, null);
        pyramide.ajouterCarte(c2, null);
        pyramide.ajouterCarte(c3, null);
        pyramide.ajouterCarte(c4, Arrays.asList(c1, c2));
        pyramide.ajouterCarte(c5, Arrays.asList(c2, c3));
        pyramide.ajouterCarte(c6, Arrays.asList(c4, c5));

        return pyramide;
    }
}
