package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Pyramide;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Construit la pyramide complete d'un chapitre, en utilisant les cartes
 * définies dans cartes.csv.
 *
 * Structure :
 *  - Chapitre 1 : 20 cartes en 6-5-4-3-2
 *  - Chapitre 2 : 15 cartes en 5-4-3-2-1
 *  - Chapitre 3 : 14 cartes en 5-4-3-2 (apres retrait des cartes militaires)
 */
public class PyramideFactory {

    private static final Map<String, Carte> CARTES_CACHE = new HashMap<>();

    static {
        chargerCartes();
    }

    private static void chargerCartes() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                PyramideFactory.class.getResourceAsStream("/cartes.csv"), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(";", -1);
                
                String fichier = cols[0].trim();
                String couleur = cols[1].trim();
                int coutOr = cols[2].trim().isEmpty() ? 0 : Integer.parseInt(cols[2].trim());
                List<String> competencesRequises = cols[3].trim().isEmpty() 
                        ? null : Arrays.asList(cols[3].trim().split("\\s*,\\s*"));
                String alliance = cols[4].trim().isEmpty() ? null : cols[4].trim();
                List<String> competencesDonnees = cols[5].trim().isEmpty() 
                        ? null : Arrays.asList(cols[5].trim().split("\\s*,\\s*"));
                int orDonne = cols[6].trim().isEmpty() ? 0 : Integer.parseInt(cols[6].trim());
                int avanceAnneau = cols[7].trim().isEmpty() ? 0 : Integer.parseInt(cols[7].trim());

                Carte c = new Carte(fichier, couleur, coutOr, alliance, competencesDonnees, competencesRequises, orDonne, avanceAnneau, fichier);
                CARTES_CACHE.put(fichier, c);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de cartes.csv : " + e.getMessage());
        }
    }

    public static Pyramide createChapitre1() {
        return creerPyramide(1, new int[]{6, 5, 4, 3, 2});
    }

    public static Pyramide createChapitre2() {
        return creerPyramide(2, new int[]{5, 4, 3, 2, 1});
    }

    public static Pyramide createChapitre3() {
        return creerPyramide(3, new int[]{5, 4, 3, 2});
    }

    /**
     * Construit une pyramide pour un chapitre donne avec une structure de lignes.
     * tailleParLigne[0] = nb cartes a la BASE (libres), tailleParLigne[N-1] = SOMMET.
     */
    private static Pyramide creerPyramide(int chapitre, int[] tailleParLigne) {
        Pyramide pyramide = new Pyramide();

        // 1. Recuperation et MELANGE des cartes du chapitre.
        //    On collecte toutes les cartes dans une liste puis on shuffle pour
        //    avoir une disposition differente a chaque partie.
        int total = 0;
        for (int t : tailleParLigne) total += t;
        List<Carte> cartesChapitre = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            cartesChapitre.add(creerCarte(chapitre, i));
        }
        java.util.Collections.shuffle(cartesChapitre);

        Carte[][] grille = new Carte[tailleParLigne.length][];
        int idx = 0;
        for (int r = 0; r < tailleParLigne.length; r++) {
            grille[r] = new Carte[tailleParLigne[r]];
            for (int c = 0; c < tailleParLigne[r]; c++) {
                grille[r][c] = cartesChapitre.get(idx);
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

    /** Genere ou recupere une carte selon son numero. */
    private static Carte creerCarte(int chapitre, int numero) {
        String chemin = String.format("carte_%d_%02d", chapitre, numero);
        if (CARTES_CACHE.containsKey(chemin)) {
            return CARTES_CACHE.get(chemin);
        }
        return new Carte(chemin, "Jaune", 0, null, null, null, 0, 0, chemin);
    }
}
