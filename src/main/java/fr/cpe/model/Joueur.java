package fr.cpe.model;

import java.util.ArrayList;
import java.util.List;

public class Joueur {

    private int or;
    private final List<String> symbolesAlliance = new ArrayList<>();
    private final List<Carte> cartes = new ArrayList<>();
    private final List<String> symbolesCompetence = new ArrayList<>();
    private final List<HautLieu> hautsLieux = new ArrayList<>();

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

    public void ajouterSymboleCompetence(String symbole) {
        if (symbole != null && !symbole.isEmpty()) {
            this.symbolesCompetence.add(symbole);
        }
    }

    public long compterSymboleCompetence(String symbole) {
        if (symbole == null || symbole.isEmpty()) return 0;
        return this.symbolesCompetence.stream().filter(s -> s.equals(symbole)).count();
    }

    public long compterSymboleAlliance(String symbole) {
        if (symbole == null || symbole.isEmpty()) return 0;
        return this.symbolesAlliance.stream().filter(s -> s.equals(symbole)).count();
    }

    public void ajouterHautLieu(HautLieu hl) {
        this.hautsLieux.add(hl);
    }
    
    public List<HautLieu> getHautsLieux() {
        return hautsLieux;
    }

    /** Remet le joueur a son etat initial (0 or, aucune carte ni symbole). */
    public void reset() {
        this.or = 0;
        this.symbolesAlliance.clear();
        this.cartes.clear();
        this.symbolesCompetence.clear();
        this.hautsLieux.clear();
    }
}