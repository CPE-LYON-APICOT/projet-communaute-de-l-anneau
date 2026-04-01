package fr.cpe.model;

import java.util.ArrayList;
import java.util.List;

public class Joueur {

    private int or;
    private final List<String> symbolesAlliance = new ArrayList<>();
    private final List<Carte> cartes = new ArrayList<>();

    public Joueur() {
        this.or = 0;
    }

    public int getOr() {
        return or;
    }

    public void ajouterOr(int montant) {
        this.or += montant;
    }

    public void payerOr(int montant) {
        this.or -= montant;
    }

    public List<String> getSymbolesAlliance() {
        return symbolesAlliance;
    }

    public void ajouterSymboleAlliance(String symbole) {
        if (symbole != null && !symbole.isEmpty()) {
            this.symbolesAlliance.add(symbole);
        }
    }

    public List<Carte> getCartes() {
        return cartes;
    }

    public void ajouterCarte(Carte carte) {
        this.cartes.add(carte);
    }
}