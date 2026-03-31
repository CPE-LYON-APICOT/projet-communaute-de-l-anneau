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

    private final Joueur joueur1;
    private final Joueur joueur2;
    private final Pyramide pyramide;
    
    private final List<GameObserver> observers = new ArrayList<>();
    private final List<Specification<GameManager>> victorySpecs = new ArrayList<>();

    @Inject
    public GameManager() {
        this.joueur1 = new Joueur();
        this.joueur2 = new Joueur();
        this.pyramide = new Pyramide();
        
        this.victorySpecs.add(new AllianceSpecification());
        this.victorySpecs.add(new RingSpecification());
    }

    public Joueur getJoueur1() {
        return joueur1;
    }

    public Joueur getJoueur2() {
        return joueur2;
    }

    public Pyramide getPyramide() {
        return pyramide;
    }

    public void addObserver(GameObserver observer) {
        this.observers.add(observer);
    }

    public void notifierObservers() {
        for (GameObserver observer : observers) {
            observer.onGameStateChanged();
        }
    }

    public void acheterCarte(Carte c, Joueur acheteur) {
        if (pyramide.estLibre(c)) {
            pyramide.retirerCarte(c);
            
            // TODO: Ajouter la logique d'application des effets de la carte au joueur
            
            verifierVictoire();
            notifierObservers();
        }
    }

    public boolean verifierVictoire() {
        for (Specification<GameManager> spec : victorySpecs) {
            if (spec.isSatisfiedBy(this)) {
                return true; // Une condition de victoire a été atteinte
            }
        }
        return false;
    }
}