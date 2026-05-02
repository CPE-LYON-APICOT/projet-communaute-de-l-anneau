package fr.cpe.model;

public class PisteAnneau {
    /** La Communaute part de la case 5 (Comte / Bree), avec 10 cases pour
     *  atteindre la case 15 (Mont du Destin). Les Nazguls partent de la case 0. */
    private int positionCommunaute = 5;
    private int positionNazguls = 0;

    public int getPositionCommunaute() {
        return positionCommunaute;
    }

    public void avancerCommunaute(int cases) {
        this.positionCommunaute += cases;
    }

    public int getPositionNazguls() {
        return positionNazguls;
    }

    public void avancerNazguls(int cases) {
        this.positionNazguls += cases;
    }

    /** Remet les pions a leurs positions initiales (5 / 0). */
    public void reset() {
        this.positionCommunaute = 5;
        this.positionNazguls = 0;
    }
}
