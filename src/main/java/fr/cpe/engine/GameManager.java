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
    private String vainqueurFinal = null;
    
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

    public String getVainqueurFinal() {
        return vainqueurFinal;
    }

    public List<HautLieu> getHautsLieuxDisponibles() {
        return hautsLieuxDisponibles;
    }

    public int getChapitreCourant() {
        return chapitreCourant;
    }

    public boolean estPartieTerminee() {
        return partieTerminee;
    }

    /**
     * Reinitialise la partie pour rejouer : remet les deux joueurs, la piste,
     * le chapitre et les hauts-lieux a leur etat initial, et regenere une
     * nouvelle pyramide chapitre 1 (les cartes seront melangees).
     */
    public void reset() {
        joueur1.reset();
        joueur2.reset();
        joueurCourant = joueur1;
        pisteAnneau.reset();
        chapitreCourant = 1;
        partieTerminee = false;
        vainqueurFinal = null;
        pyramide = PyramideFactory.createChapitre1();
        hautsLieuxDisponibles.clear();
        hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 1", 2, 1, "Effet 1"));
        hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 2", 3, 2, "Effet 2"));
        hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 3", 4, 3, "Effet 3"));
        hautsLieuxDisponibles.add(new HautLieu("Haut-Lieu 4", 5, 4, "Effet 4"));
        notifierObservers();
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
        
        // Calcul des ressources manquantes
        int ressourcesManquantes = 0;
        if (c.getSymbolesRequis() != null) {
            java.util.Map<String, Long> requisMap = c.getSymbolesRequis().stream()
                .collect(java.util.stream.Collectors.groupingBy(s -> s, java.util.stream.Collectors.counting()));
                
            for (java.util.Map.Entry<String, Long> entry : requisMap.entrySet()) {
                long possede = joueurCourant.compterSymboleCompetence(entry.getKey());
                if (possede < entry.getValue()) {
                    ressourcesManquantes += (entry.getValue() - possede);
                }
            }
        }

        int coutFinal = c.getCoutOr() + ressourcesManquantes;

        if (pyramide.estLibre(c) && joueurCourant.getOr() >= coutFinal) {
            joueurCourant.payerOr(coutFinal);
            joueurCourant.ajouterCarte(c);
            // Le mecanisme d'alliance a ete retire : la victoire par 6 symboles
            // distincts compte directement les symbolesCompetences des cartes vertes.

            if (c.getSymbolesCompetences() != null) {
                for (String comp : c.getSymbolesCompetences()) {
                    joueurCourant.ajouterSymboleCompetence(comp);
                }
            }
            
            if (c.getOrDonne() > 0) {
                joueurCourant.ajouterOr(c.getOrDonne());
            }
            
            pyramide.retirerCarte(c);

            // Avance sur la Piste de l'Anneau :
            // J1 = Communauté (avance vers la fin), J2 = Sauron/Nazguls (rattrape).
            int avance = c.getAvanceAnneau();
            if (avance > 0) {
                if (joueurCourant == joueur1) {
                    pisteAnneau.avancerCommunaute(avance);
                } else {
                    pisteAnneau.avancerNazguls(avance);
                }
            }

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
        // Hauts-Lieux desactives : ils s'appuyaient sur le mecanisme d'alliance
        // (Forteresse) qui a ete retire. Les cartes militaires necessaires
        // n'etant pas encore introduites, les Hauts-Lieux restent affiches mais
        // ne peuvent pas etre reclames.
        if (partieTerminee || !hautsLieuxDisponibles.contains(hl)) return;

        long nbForteresses = 0; // mecanisme retire
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
                partieTerminee = true;
                String nomGagnant = nomJoueur(joueurCourant);
                // Cas particulier de la Piste de l'Anneau : si les Nazguls
                // rattrapent la Communaute, c'est Sauron qui gagne meme si
                // la Communaute etait le joueur courant juste avant.
                if (spec instanceof RingSpecification) {
                    int posCom = pisteAnneau.getPositionCommunaute();
                    int posNaz = pisteAnneau.getPositionNazguls();
                    if (posNaz >= posCom && posCom > 0) {
                        nomGagnant = "Sauron";
                    } else {
                        nomGagnant = "Communauté";
                    }
                }
                vainqueurFinal = nomGagnant + " — victoire par " + spec.getDescription();
                return true;
            }
        }
        return false;
    }

    /** Retourne le nom thematique d'un joueur (Communaute pour J1, Sauron pour J2). */
    public String nomJoueur(Joueur j) {
        return (j == joueur1) ? "Communauté" : "Sauron";
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
                if (!verifierVictoire()) {
                    partieTerminee = true;
                    int posCom = pisteAnneau.getPositionCommunaute();
                    int posNaz = pisteAnneau.getPositionNazguls();
                    if (posCom > posNaz) {
                        vainqueurFinal = "Communauté — victoire au score (fin du chapitre 3)";
                    } else if (posNaz > posCom) {
                        vainqueurFinal = "Sauron — victoire au score (fin du chapitre 3)";
                    } else {
                        vainqueurFinal = "Égalité — fin du chapitre 3";
                    }
                }
            }
        }
    }
    
}