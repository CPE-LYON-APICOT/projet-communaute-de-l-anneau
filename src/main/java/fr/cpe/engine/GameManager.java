package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Joueur;
import fr.cpe.model.PisteAnneau;
import fr.cpe.model.Pyramide;
import fr.cpe.engine.PyramideFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class GameManager {

    private final Joueur joueur1;
    private final Joueur joueur2;
    private Joueur joueurCourant;
    private Pyramide pyramide;
    private final PisteAnneau pisteAnneau;
    private int chapitreCourant = 1;
    private boolean partieTerminee = false;
    
    private final List<GameObserver> observers = new ArrayList<>();
    private final List<Specification<GameManager>> victorySpecs = new ArrayList<>();

    @Inject
    public GameManager() {
        this.joueur1 = new Joueur();
        this.joueur2 = new Joueur();
        this.joueurCourant = this.joueur1;
        this.pyramide = PyramideFactory.createChapitre1();
        this.pisteAnneau = new PisteAnneau();
        this.victorySpecs.add(new AllianceSpecification());
        this.victorySpecs.add(new RingSpecification());
    }

    public PisteAnneau getPisteAnneau() {
        return pisteAnneau;
    }

    public Joueur getJoueur1() {
        return joueur1;
    }

    public Joueur getJoueur2() {
        return joueur2;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public Pyramide getPyramide() {
        return pyramide;
    }

    private void changerTour() {
        joueurCourant = (joueurCourant == joueur1) ? joueur2 : joueur1;
    }

    public void addObserver(GameObserver observer) {
        this.observers.add(observer);
    }

    public void notifierObservers() {
        for (GameObserver observer : observers) {
            observer.onGameStateChanged();
        }
    }

    public void acheterCarte(Carte c) {
        if (partieTerminee) return;
        if (pyramide.estLibre(c) && joueurCourant.getOr() >= c.getCoutOr()) {
            joueurCourant.payerOr(c.getCoutOr());
            joueurCourant.ajouterCarte(c);
            joueurCourant.ajouterSymboleAlliance(c.getSymboleAlliance());
            pyramide.retirerCarte(c);
            
            verifierEtChangerChapitre();
            verifierVictoire();
            changerTour();
            notifierObservers();
        }
    }

    public void defausserCarte(Carte c) {
        if (partieTerminee) return;
        if (pyramide.estLibre(c)) {
            joueurCourant.ajouterOr(2);
            pyramide.retirerCarte(c);
            
            verifierEtChangerChapitre();
            verifierVictoire();
            changerTour();
            notifierObservers();
        }
    }

    public boolean verifierVictoire() {
        if (partieTerminee) {
            return true;
        }
        for (Specification<GameManager> spec : victorySpecs) {
            if (spec.isSatisfiedBy(this)) {
                return true; // Une condition de victoire a été atteinte
            }
        }
        return false;
    }

    private void verifierEtChangerChapitre() {
        if (pyramide.getCartes().isEmpty()) {
            if (chapitreCourant == 1) {
                chapitreCourant = 2;
                pyramide = PyramideFactory.createChapitre2();
            } else if (chapitreCourant == 2) {
                chapitreCourant = 3;
                pyramide = PyramideFactory.createChapitre3();
            } else if (chapitreCourant == 3) {
                partieTerminee = true;
            }
        }
    }
    
}