package fr.cpe.model;

public class Carte {

    private final String nom;
    private final String couleur;
    private final int coutOr;
    private final String symboleAlliance;
    private final String symboleCompetence;
    private final String symboleRequis;
    private final int avanceAnneau;

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance,
                 String symboleCompetence, String symboleRequis, int avanceAnneau) {
        this.nom = nom;
        this.couleur = couleur;
        this.coutOr = coutOr;
        this.symboleAlliance = symboleAlliance;
        this.symboleCompetence = symboleCompetence;
        this.symboleRequis = symboleRequis;
        this.avanceAnneau = avanceAnneau;
    }

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance,
                 String symboleCompetence, String symboleRequis) {
        this(nom, couleur, coutOr, symboleAlliance, symboleCompetence, symboleRequis, 0);
    }

    public Carte(String nom, String couleur, int coutOr, String symboleAlliance) {
        this(nom, couleur, coutOr, symboleAlliance, null, null, 0);
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

    public int getAvanceAnneau() {
        return avanceAnneau;
    }
}
