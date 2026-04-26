package fr.cpe.engine;

import fr.cpe.model.Joueur;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste la condition de victoire par alliance (6 symboles d'alliance distincts).
 * Valide au passage le pattern Specification : la classe ne dépend que de ce
 * que GameManager expose via getJoueur1/getJoueur2.
 */
class AllianceSpecificationTest {

    private GameManager manager() {
        return new GameManager();
    }

    @Test
    void nouvellePartie_nonSatisfaite() {
        assertFalse(new AllianceSpecification().isSatisfiedBy(manager()));
    }

    @Test
    void joueur1AvecSixSymbolesDistincts_satisfaite() {
        GameManager gm = manager();
        Joueur j1 = gm.getJoueur1();
        j1.ajouterSymboleAlliance("Hobbits");
        j1.ajouterSymboleAlliance("Elves");
        j1.ajouterSymboleAlliance("Dwarves");
        j1.ajouterSymboleAlliance("Ents");
        j1.ajouterSymboleAlliance("Gondor");
        j1.ajouterSymboleAlliance("Rohan");

        assertTrue(new AllianceSpecification().isSatisfiedBy(gm));
    }

    @Test
    void joueur2AvecSixSymbolesDistincts_satisfaite() {
        GameManager gm = manager();
        Joueur j2 = gm.getJoueur2();
        j2.ajouterSymboleAlliance("Hobbits");
        j2.ajouterSymboleAlliance("Elves");
        j2.ajouterSymboleAlliance("Dwarves");
        j2.ajouterSymboleAlliance("Ents");
        j2.ajouterSymboleAlliance("Gondor");
        j2.ajouterSymboleAlliance("Forteresse");

        assertTrue(new AllianceSpecification().isSatisfiedBy(gm));
    }

    @Test
    void doublons_neComptentPas() {
        GameManager gm = manager();
        Joueur j1 = gm.getJoueur1();
        // 10 symboles, mais seulement 5 distincts -> pas de victoire
        for (int i = 0; i < 2; i++) {
            j1.ajouterSymboleAlliance("Hobbits");
            j1.ajouterSymboleAlliance("Elves");
            j1.ajouterSymboleAlliance("Dwarves");
            j1.ajouterSymboleAlliance("Ents");
            j1.ajouterSymboleAlliance("Gondor");
        }
        assertFalse(new AllianceSpecification().isSatisfiedBy(gm));
    }
}
