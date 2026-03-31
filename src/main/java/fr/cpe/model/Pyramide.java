package fr.cpe.model;

import java.util.ArrayList;
import java.util.List;

public class Pyramide {

    private List<Carte> cartes = new ArrayList<>();

    public boolean estLibre(Carte c) {
        return true;
    }

    public void retirerCarte(Carte c) {
        cartes.remove(c);
    }
}