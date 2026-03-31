package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Joueur;
import fr.cpe.model.Pyramide;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class GameManager {

    private Joueur joueur1;
    private Joueur joueur2;
    private Pyramide pyramide;
    private List<GameObserver> observers = new ArrayList<>();
    private List<Specification<GameManager>> victorySpecs = new ArrayList<>();

    @Inject
    public GameManager() {
        this.joueur1 = null;
        this.joueur2 = null;
        this.pyramide = null;
    }

    public void acheterCarte(Carte c) {
    }

    public void notifierObservers() {
    }

    public void verifierVictoire() {
    }
}