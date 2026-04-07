package fr.cpe.model;

public class HautLieu {
    private final String nom;
    private final int coutOr;
    private final int coutForteresses;
    private final String effet;

    public HautLieu(String nom, int coutOr, int coutForteresses, String effet) {
        this.nom = nom;
        this.coutOr = coutOr;
        this.coutForteresses = coutForteresses;
        this.effet = effet;
    }

    public String getNom() { return nom; }
    public int getCoutOr() { return coutOr; }
    public int getCoutForteresses() { return coutForteresses; }
    public String getEffet() { return effet; }
}
