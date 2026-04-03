package fr.cpe.model;

public class PisteAnneau {
    private int positionCommunaute = 0;
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
}
