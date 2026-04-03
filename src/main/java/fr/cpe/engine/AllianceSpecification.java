package fr.cpe.engine;

import fr.cpe.model.Joueur;
import java.util.HashSet;

public class AllianceSpecification implements Specification<GameManager> {

    private static final int NB_SYMBOLES_VICTOIRE = 6;

    @Override
    public boolean isSatisfiedBy(GameManager gameManager) {
        return hasSixDifferentSymbols(gameManager.getJoueur1()) || 
               hasSixDifferentSymbols(gameManager.getJoueur2());
    }

    private boolean hasSixDifferentSymbols(Joueur joueur) {
        return new HashSet<>(joueur.getSymbolesAlliance()).size() >= NB_SYMBOLES_VICTOIRE;
    }
}