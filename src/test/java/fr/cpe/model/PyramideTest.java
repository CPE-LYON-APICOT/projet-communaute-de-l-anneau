package fr.cpe.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste la mecanique de pyramide : cartes libres / couvertes, et
 * liberation automatique des cartes superieures quand une carte est retiree.
 */
class PyramideTest {

    @Test
    void carteAjouteeSansCouvreur_estLibre() {
        Pyramide p = new Pyramide();
        Carte c = new Carte("A", "Jaune", 0, "");
        p.ajouterCarte(c, null);

        assertTrue(p.estLibre(c));
    }

    @Test
    void carteCouverte_nEstPasLibre() {
        Pyramide p = new Pyramide();
        Carte base = new Carte("Base", "Jaune", 0, "");
        Carte haute = new Carte("Haute", "Bleu", 2, "");

        p.ajouterCarte(base, null);
        p.ajouterCarte(haute, null);
        // ajout APRES : on couvre base par haute
        // On re-ajoute la relation via la carte haute comme bloqueur de base
        // -> On cree une pyramide ou base est sous une carte
        Pyramide p2 = new Pyramide();
        Carte racine = new Carte("Racine", "Jaune", 0, "");
        Carte sommet = new Carte("Sommet", "Bleu", 2, "");
        p2.ajouterCarte(racine, Arrays.asList(sommet));
        p2.ajouterCarte(sommet, null);

        assertFalse(p2.estLibre(racine));
        assertTrue(p2.estLibre(sommet));
    }

    @Test
    void retirerCarte_libereCellesQuElleBloquait() {
        // Pyramide 2-1 : c1 et c2 a la base, c3 couverte par les deux.
        Pyramide p = new Pyramide();
        Carte c1 = new Carte("c1", "Jaune", 0, "");
        Carte c2 = new Carte("c2", "Gris",  0, "");
        Carte c3 = new Carte("c3", "Bleu",  2, "");

        p.ajouterCarte(c1, null);
        p.ajouterCarte(c2, null);
        p.ajouterCarte(c3, Arrays.asList(c1, c2));

        assertFalse(p.estLibre(c3));
        p.retirerCarte(c1);
        assertFalse(p.estLibre(c3), "c3 reste couverte tant que c2 est la");
        p.retirerCarte(c2);
        assertTrue(p.estLibre(c3), "c3 libre quand ses deux bloqueurs sont partis");
    }

    @Test
    void getCartesAccessibles_filtreCorrectement() {
        Pyramide p = new Pyramide();
        Carte c1 = new Carte("c1", "Jaune", 0, "");
        Carte c2 = new Carte("c2", "Gris",  0, "");
        Carte c3 = new Carte("c3", "Bleu",  2, "");

        p.ajouterCarte(c1, null);
        p.ajouterCarte(c2, null);
        p.ajouterCarte(c3, Arrays.asList(c1, c2));

        List<Carte> libres = p.getCartesAccessibles();
        assertEquals(2, libres.size());
        assertTrue(libres.contains(c1));
        assertTrue(libres.contains(c2));
        assertFalse(libres.contains(c3));
    }
}
