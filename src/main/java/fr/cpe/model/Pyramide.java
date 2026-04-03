package fr.cpe.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pyramide {

    private final List<Carte> cartes = new ArrayList<>();
    private final Map<Carte, List<Carte>> couvertPar = new HashMap<>();

    public Pyramide() {
    }
    
    public List<Carte> getCartes() {
        return cartes;
    }

    public void ajouterCarte(Carte carte, List<Carte> cartesQuiLaCouvrent) {
        cartes.add(carte);
        if (cartesQuiLaCouvrent != null) {
            couvertPar.put(carte, new ArrayList<>(cartesQuiLaCouvrent));
        } else {
            couvertPar.put(carte, new ArrayList<>());
        }
    }

    public boolean estLibre(Carte c) {
        if (!cartes.contains(c)) {
            return false;
        }
        List<Carte> bloqueurs = couvertPar.get(c);
        return bloqueurs == null || bloqueurs.isEmpty();
    }

    public List<Carte> getCartesAccessibles() {
        List<Carte> accessibles = new ArrayList<>();
        for (Carte c : cartes) {
            if (estLibre(c)) {
                accessibles.add(c);
            }
        }
        return accessibles;
    }

    public List<Carte> getCartesRecouvertes() {
        List<Carte> recouvertes = new ArrayList<>();
        for (Carte c : cartes) {
            if (!estLibre(c)) {
                recouvertes.add(c);
            }
        }
        return recouvertes;
    }

    public void retirerCarte(Carte c) {
        if (cartes.remove(c)) {
            for (List<Carte> bloqueurs : couvertPar.values()) {
                if (bloqueurs != null) {
                    bloqueurs.remove(c);
                }
            }
        }
    }
}