package fr.cpe.model;

public class Carte {

    private final String nom;
    private final String couleur;
    private final int coutOr;
    private final String symboleAlliance;

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance) {
        this.nom = nom;
        this.couleur = couleur;
        this.coutOr = coutOr;
        this.symboleAlliance = symboleAlliance;
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
}