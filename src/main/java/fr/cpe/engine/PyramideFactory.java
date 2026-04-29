package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Pyramide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Construit la pyramide complete d'un chapitre, en utilisant TOUTES les cartes
 * scannees disponibles (carte_C_NN.png).
 *
 * Structure (sens "base = libre, sommet = couvert", convention du code initial) :
 *  - Chapitre 1 : 20 cartes en 6-5-4-3-2  (6 cartes a la base, 2 au sommet)
 *  - Chapitre 2 : 15 cartes en 5-4-3-2-1
 *  - Chapitre 3 : 20 cartes en 6-5-4-3-2
 *
 * Couvrement :
 *  - Carte de la ligne r (taille T_r) est COUVERTE par les 2 cartes de la ligne
 *    r-1 (taille T_{r-1}) immediatement en-dessous d'elle, soit [r-1][c] et
 *    [r-1][c+1] (la ligne du dessous est plus large).
 *  - Quand les 2 cartes du dessous sont prises, la carte du dessus devient libre.
 *
 * Les attributs de jeu (couleur, alliance, competence, avance Anneau) sont
 * generes selon une rotation pour avoir de la diversite. Pour un equilibrage
 * fidele du jeu original, il faudrait les renseigner carte par carte.
 */
public class PyramideFactory {

    private static final String[] COULEURS    = {"Jaune", "Gris", "Vert", "Rouge", "Bleu"};
    private static final String[] ALLIANCES   = {"Hobbits", "Elves", "Dwarves", "Ents", "Gondor", "Rohan"};
    private static final String[] COMPETENCES = {"Nourriture", "Bois", "Pierre", "Acier", "Magie"};

    public static Pyramide createChapitre1() {
        return creerPyramide(1, new int[]{6, 5, 4, 3, 2});
    }

    public static Pyramide createChapitre2() {
        return creerPyramide(2, new int[]{5, 4, 3, 2, 1});
    }

    public static Pyramide createChapitre3() {
        return creerPyramide(3, new int[]{6, 5, 4, 3, 2});
    }

    /**
     * Construit une pyramide pour un chapitre donne avec une structure de lignes.
     * tailleParLigne[0] = nb cartes a la BASE (libres), tailleParLigne[N-1] = SOMMET.
     */
    private static Pyramide creerPyramide(int chapitre, int[] tailleParLigne) {
        Pyramide pyramide = new Pyramide();
        Carte[][] grille = new Carte[tailleParLigne.length][];
        int idx = 0;

        // 1. Creation des cartes
        for (int r = 0; r < tailleParLigne.length; r++) {
            grille[r] = new Carte[tailleParLigne[r]];
            for (int c = 0; c < tailleParLigne[r]; c++) {
                grille[r][c] = creerCarte(chapitre, idx + 1, r);
                idx++;
            }
        }

        // 2. Ajout dans la pyramide avec relations couvertPar
        //    Carte [r][c] (r > 0) est COUVERTE par [r-1][c] et [r-1][c+1] (ligne du dessous, plus large)
        for (int r = 0; r < tailleParLigne.length; r++) {
            for (int c = 0; c < tailleParLigne[r]; c++) {
                List<Carte> bloqueurs = new ArrayList<>();
                if (r > 0) {
                    bloqueurs.add(grille[r - 1][c]);
                    if (c + 1 < tailleParLigne[r - 1]) {
                        bloqueurs.add(grille[r - 1][c + 1]);
                    }
                }
                pyramide.ajouterCarte(grille[r][c], bloqueurs.isEmpty() ? null : bloqueurs);
            }
        }

        // 3. Memorise la structure pour l'affichage en GameService
        List<List<Carte>> lignesAsList = new ArrayList<>();
        for (Carte[] ligne : grille) {
            lignesAsList.add(Arrays.asList(ligne));
        }
        pyramide.definirLignes(lignesAsList);

        return pyramide;
    }

    /** Genere les attributs d'une carte selon sa position dans la pyramide. */
    private static Carte creerCarte(int chapitre, int numero, int ligne) {
        String chemin = String.format("carte_%d_%02d", chapitre, numero);

        // Couleur cyclique pour varier la palette
        String couleur = COULEURS[(numero - 1) % COULEURS.length];

        // Alliance : meme cycle, plus "Forteresse" sur les rouges (cartes-Anneau)
        String alliance;
        if ("Rouge".equals(couleur)) {
            alliance = "Forteresse";
        } else {
            alliance = ALLIANCES[(numero - 1) % ALLIANCES.length];
        }

        // Cout : cartes du bas (ligne 0) gratuites, augmente vers le sommet
        int cout = ligne * (chapitre);
        if ("Jaune".equals(couleur)) cout = 0; // les jaunes sont gratuites (or)

        // Competence : seulement sur les jaunes/gris/verts (cartes ressources)
        String competence = null;
        if ("Jaune".equals(couleur) || "Gris".equals(couleur) || "Vert".equals(couleur)) {
            competence = COMPETENCES[(numero - 1) % COMPETENCES.length];
        }

        // Symbole requis pour reduction (jamais sur le bas, parfois sur les autres)
        String requis = null;
        if (ligne >= 2 && (numero % 3 == 0)) {
            requis = COMPETENCES[(numero) % COMPETENCES.length];
        }

        // Avance Anneau : sur les rouges et certaines bleues
        int avanceAnneau = 0;
        if ("Rouge".equals(couleur)) {
            avanceAnneau = chapitre; // ch1=+1, ch2=+2, ch3=+3
        } else if ("Bleu".equals(couleur) && numero % 4 == 0) {
            avanceAnneau = 1;
        }

        return new Carte(chemin, couleur, cout, alliance, competence, requis, avanceAnneau, chemin);
    }
}
