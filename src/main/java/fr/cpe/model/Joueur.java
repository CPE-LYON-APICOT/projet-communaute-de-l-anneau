package fr.cpe.model;

import java.util.HashSet;
import java.util.Set;

public class Joueur {
    
    private int or = 0;
    private int positionAnneau = 0;
    private final Set<String> alliances = new HashSet<>();

    public Joueur() {
    }

    public int getOr() { return or; }
    public void ajouterOr(int montant) { this.or += montant; }
    public void depenserOr(int montant) { this.or -= montant; }

    public int getPositionAnneau() { return positionAnneau; }
    public void avancerAnneau(int cases) { this.positionAnneau += cases; }

    public Set<String> getAlliances() { return alliances; }
    public void ajouterAlliance(String symbole) { this.alliances.add(symbole); }
}