package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Pyramide;
import java.util.Arrays;

public class PyramideFactory {

    public static Pyramide createChapitre1() {
        Pyramide pyramide = new Pyramide();

        Carte c1 = new Carte("Ferme", "Jaune", 0, "");
        Carte c2 = new Carte("Forge", "Gris", 0, "Dwarves");
        Carte c3 = new Carte("Caserne", "Rouge", 2, "");
        
        Carte c4 = new Carte("Temple", "Bleu", 1, "");
        Carte c5 = new Carte("Forêt", "Vert", 1, "Elves");
        
        Carte c6 = new Carte("Cité", "Bleu", 3, "Gondor");

        // Ligne 1 (Base - Libres)
        pyramide.ajouterCarte(c1, null);
        pyramide.ajouterCarte(c2, null);
        pyramide.ajouterCarte(c3, null);

        // Ligne 2 (Couvertes par la ligne 1)
        pyramide.ajouterCarte(c4, Arrays.asList(c1, c2));
        pyramide.ajouterCarte(c5, Arrays.asList(c2, c3));

        // Ligne 3 (Sommet - Couverte par la ligne 2)
        pyramide.ajouterCarte(c6, Arrays.asList(c4, c5));

        return pyramide;
    }
}