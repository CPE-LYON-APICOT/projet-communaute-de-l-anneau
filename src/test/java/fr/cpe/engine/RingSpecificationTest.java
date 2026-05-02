package fr.cpe.engine;

import fr.cpe.model.PisteAnneau;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste la condition de victoire par la Piste de l'Anneau :
 *  - la Communaute atteint la case 15 (Mont du Destin) -> Communaute gagne
 *  - OU les Nazguls atteignent la position de la Communaute -> Sauron gagne
 *
 * Etat initial : Communaute en case 5, Nazguls en case 0.
 */
class RingSpecificationTest {

    @Test
    void debutDePartie_nonSatisfaite() {
        // Communaute (5) > Nazguls (0), aucun n'a atteint la fin.
        assertFalse(new RingSpecification().isSatisfiedBy(new GameManager()));
    }

    @Test
    void communauteALaFin_satisfaite() {
        GameManager gm = new GameManager();
        PisteAnneau piste = gm.getPisteAnneau();
        // Communaute part de 5, doit avancer de 10 pour atteindre 15.
        piste.avancerCommunaute(10);

        assertTrue(new RingSpecification().isSatisfiedBy(gm));
    }

    @Test
    void nazgulsRattrapentCommunaute_satisfaite() {
        GameManager gm = new GameManager();
        PisteAnneau piste = gm.getPisteAnneau();
        // Nazguls partent de 0, doivent avancer de 5 pour rattraper.
        piste.avancerNazguls(5);

        assertTrue(new RingSpecification().isSatisfiedBy(gm));
    }

    @Test
    void nazgulsDepassentCommunaute_satisfaite() {
        GameManager gm = new GameManager();
        PisteAnneau piste = gm.getPisteAnneau();
        piste.avancerNazguls(8); // 8 > 5 -> les Nazguls depassent

        assertTrue(new RingSpecification().isSatisfiedBy(gm));
    }

    @Test
    void communauteAvanceLoinDevant_nonSatisfaite() {
        GameManager gm = new GameManager();
        PisteAnneau piste = gm.getPisteAnneau();
        piste.avancerCommunaute(5); // Communaute en 10
        piste.avancerNazguls(3);    // Nazguls en 3, loin derriere

        assertFalse(new RingSpecification().isSatisfiedBy(gm),
                "Si la Communaute mene et les Nazguls ne rattrapent pas, pas de victoire");
    }
}
