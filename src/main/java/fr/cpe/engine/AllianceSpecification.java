package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Joueur;
import java.util.HashSet;
import java.util.Set;

/**
 * Condition de victoire : un joueur possede 6 cartes vertes apportant chacune
 * un symbole different (corne, fiole, marteau, pipe, coquillage, feuille).
 *
 * Un symbole est considere comme apporte par une carte verte des qu'il figure
 * dans sa liste de symbolesCompetences. On compte le nombre de symboles
 * DISTINCTS (Set) pour eviter les doublons - un joueur qui possede deux cartes
 * vertes donnant "fiole" ne progresse que d'un symbole.
 */
public class AllianceSpecification implements Specification<GameManager> {

    private static final int NB_SYMBOLES_VICTOIRE = 6;

    @Override
    public boolean isSatisfiedBy(GameManager gameManager) {
        return aSixSymbolesVertsDistincts(gameManager.getJoueur1())
            || aSixSymbolesVertsDistincts(gameManager.getJoueur2());
    }

    @Override
    public String getDescription() {
        return "6 symboles verts distincts";
    }

    private boolean aSixSymbolesVertsDistincts(Joueur joueur) {
        Set<String> symbolesUniques = new HashSet<>();
        for (Carte c : joueur.getCartes()) {
            if ("Vert".equalsIgnoreCase(c.getCouleur())) {
                symbolesUniques.addAll(c.getSymbolesCompetences());
            }
        }
        return symbolesUniques.size() >= NB_SYMBOLES_VICTOIRE;
    }
}
