package fr.cpe.model;

import java.util.Collections;
import java.util.List;

public class Carte {

    private final String nom;
    private final String couleur;
    private final int coutOr;
    private final String symboleAlliance;
    private final List<String> symbolesCompetences;
    private final List<String> symbolesRequis;
    private final int avanceAnneau;
    private final String cheminImage;  // ex: "carte_1_05" -> chargera /cartes/carte_1_05.png
    private final int orDonne;

    /**
     * Constructeur complet (8 args).
     * @param cheminImage nom du fichier image SANS extension (recherche dans /cartes/),
     *                    ou null si pas d'image (fallback rectangle colore).
     */
    public Carte(String nom, String couleur, int coutOr, String symboleAlliance,
                 List<String> symbolesCompetences, List<String> symbolesRequis, int orDonne, int avanceAnneau,
                 String cheminImage) {
        this.nom = nom;
        this.couleur = couleur;
        this.coutOr = coutOr;
        this.symboleAlliance = symboleAlliance;
        this.symbolesCompetences = symbolesCompetences == null ? Collections.emptyList() : List.copyOf(symbolesCompetences);
        this.symbolesRequis = symbolesRequis == null ? Collections.emptyList() : List.copyOf(symbolesRequis);
        this.orDonne = orDonne;
        this.avanceAnneau = avanceAnneau;
        this.cheminImage = cheminImage;
    }

    // Constructeur 7-args (sans image) - retro-compatibilite avec les tests existants
    public Carte(String nom, String couleur, int coutOr, String symboleAlliance,
                 List<String> symbolesCompetences, List<String> symbolesRequis, int avanceAnneau) {
        this(nom, couleur, coutOr, symboleAlliance, symbolesCompetences, symbolesRequis, 0, avanceAnneau, null);
    }

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance,
                 List<String> symbolesCompetences, List<String> symbolesRequis) {
        this(nom, couleur, coutOr, symboleAlliance, symbolesCompetences, symbolesRequis, 0, 0, null);
    }

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance) {
        this(nom, couleur, coutOr, symboleAlliance, Collections.emptyList(), Collections.emptyList(), 0, 0, null);
    }

    public String getNom() {
        return nom;
    }

    public String getCouleur() {
        return couleur;
    }

    public int getCoutOr() {
        return coutOr;
    }

    public String getSymboleAlliance() {
        return symboleAlliance;
    }

    public List<String> getSymbolesCompetences() {
        return symbolesCompetences;
    }

    public List<String> getSymbolesRequis() {
        return symbolesRequis;
    }

    public int getAvanceAnneau() {
        return avanceAnneau;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public int getOrDonne() {
        return orDonne;
    }
}
