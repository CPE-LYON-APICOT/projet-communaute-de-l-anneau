package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Joueur;
import fr.cpe.model.PisteAnneau;
import fr.cpe.model.Pyramide;
import fr.cpe.model.HautLieu;
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
    private final List<HautLieu> hautsLieuxDisponibles = new ArrayList<>();

    @Inject
    public GameManager() {
        this.joueur1 = new Joueur();
        this.joueur2 = new Joueur();
        this.joueurCourant = this.joueur1;
        this.pyramide = PyramideFactory.createChapitre1();
        this.pisteAnneau = new PisteAnneau();
        this.victorySpecs.add(new AllianceSpecification());
        this.victorySpecs.add(new RingSpecification());
        this.hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 1", 2, 1, "Effet 1"));
        this.hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 2", 3, 2, "Effet 2"));
        this.hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 3", 4, 3, "Effet 3"));
        this.hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 4", 5, 4, "Effet 4"));
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
        
        int reduction = (int) joueurCourant.compterSymboleCompetence(c.getSymboleRequis());
        int coutFinal = Math.max(0, c.getCoutOr() - reduction);

        if (pyramide.estLibre(c) && joueurCourant.getOr() >= coutFinal) {
            joueurCourant.payerOr(coutFinal);
            joueurCourant.ajouterCarte(c);
            joueurCourant.ajouterSymboleAlliance(c.getSymboleAlliance());
            joueurCourant.ajouterSymboleCompetence(c.getSymboleCompetence());
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

    public void reclamerHautLieu(HautLieu hl) {
        if (partieTerminee || !hautsLieuxDisponibles.contains(hl)) return;
        
        long nbForteresses = joueurCourant.compterSymboleAlliance("Forteresse");
        
        if (joueurCourant.getOr() >= hl.getCoutOr() && nbForteresses >= hl.getCoutForteresses()) {
            joueurCourant.payerOr(hl.getCoutOr());
            joueurCourant.ajouterHautLieu(hl);
            hautsLieuxDisponibles.remove(hl);
            
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