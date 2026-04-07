package fr.cpe.model;

public class Carte {

    private final String nom;
    private final String couleur;
    private final int coutOr;
    private final String symboleAlliance;
    private final String symboleCompetence;
    private final String symboleRequis;

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance, String symboleCompetence, String symboleRequis) {
        this.nom = nom;
        this.couleur = couleur;
        this.coutOr = coutOr;
        this.symboleAlliance = symboleAlliance;
        this.symboleCompetence = symboleCompetence;
        this.symboleRequis = symboleRequis;
    }

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance) {
        this(nom, couleur, coutOr, symboleAlliance, null, null);
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

    public String getSymboleCompetence() {
        return symboleCompetence;
    }

    public String getSymboleRequis() {
        return symboleRequis;
    }
}