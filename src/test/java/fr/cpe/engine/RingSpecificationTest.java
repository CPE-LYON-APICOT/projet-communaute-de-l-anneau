package fr.cpe.engine;

import fr.cpe.model.PisteAnneau;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste la condition de victoire par la Piste de l'Anneau :
 *  - la Communaute atteint la fin (case 9)
 *  - OU les Nazguls rattrapent la Communaute (si elle a deja bouge)
 */
class RingSpecificationTest {

    @Test
    void debutDePartie_nonSatisfaite() {
        assertFalse(new RingSpecification().isSatisfiedBy(new GameManager()));
    }

    @Test
    void communauteALaFin_satisfaite() {
        GameManager gm = new GameManager();
        PisteAnneau piste = gm.getPisteAnneau();
        piste.avancerCommunaute(9);

        assertTrue(new RingSpecification().isSatisfiedBy(gm));
    }

    @Test
    void nazgulsRattrapentCommunauteEnMouvement_satisfaite() {
        GameManager gm = new GameManager();
        PisteAnneau piste = gm.getPisteAnneau();
        piste.avancerCommunaute(3);
        piste.avancerNazguls(3); // rattrapage exact

        assertTrue(new RingSpecification().isSatisfiedBy(gm));
    }

    @Test
    void nazgulsDepassentCommunaute_satisfaite() {
        GameManager gm = new GameManager();
        PisteAnneau piste = gm.getPisteAnneau();
        piste.avancerCommunaute(2);
        piste.avancerNazguls(5);

        assertTrue(new RingSpecification().isSatisfiedBy(gm));
    }

    @Test
    void nazgulsA0EtCommunauteA0_nonSatisfaite() {
        // Cas limite : personne n'a bouge, les deux sont a 0 -> pas de victoire.
        GameManager gm = new GameManager();
        assertFalse(new RingSpecification().isSatisfiedBy(gm));
    }
}
