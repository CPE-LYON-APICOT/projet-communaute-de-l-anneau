package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.Joueur;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste la condition de victoire par symboles verts (6 symboles distincts
 * apportes par cartes vertes possedees).
 *
 * On valide ici le pattern Specification : la classe ne depend que de l'etat
 * exposé via getJoueur1/getJoueur2, et la regle est isolee dans isSatisfiedBy.
 */
class AllianceSpecificationTest {

    private GameManager manager() {
        return new GameManager();
    }

    private Carte carteVerte(String symbole) {
        return new Carte("test_" + symbole, "Vert", 0, null,
                List.of(symbole), Collections.emptyList(), 0, 0, null);
    }

    private Carte carteAutreCouleur(String couleur, String symbole) {
        return new Carte("test_" + symbole, couleur, 0, null,
                List.of(symbole), Collections.emptyList(), 0, 0, null);
    }

    @Test
    void nouvellePartie_nonSatisfaite() {
        assertFalse(new AllianceSpecification().isSatisfiedBy(manager()));
    }

    @Test
    void joueur1AvecSixSymbolesVertsDistincts_satisfaite() {
        GameManager gm = manager();
        Joueur j1 = gm.getJoueur1();
        for (String s : Arrays.asList("corne", "fiole", "marteau", "pipe", "coquillage", "feuille")) {
            j1.ajouterCarte(carteVerte(s));
        }
        assertTrue(new AllianceSpecification().isSatisfiedBy(gm));
    }

    @Test
    void joueur2AvecSixSymbolesVertsDistincts_satisfaite() {
        GameManager gm = manager();
        Joueur j2 = gm.getJoueur2();
        for (String s : Arrays.asList("corne", "fiole", "marteau", "pipe", "coquillage", "feuille")) {
            j2.ajouterCarte(carteVerte(s));
        }
        assertTrue(new AllianceSpecification().isSatisfiedBy(gm));
    }

    @Test
    void cinqSymbolesDistincts_pasDeVictoire() {
        GameManager gm = manager();
        Joueur j1 = gm.getJoueur1();
        for (String s : Arrays.asList("corne", "fiole", "marteau", "pipe", "coquillage")) {
            j1.ajouterCarte(carteVerte(s));
        }
        assertFalse(new AllianceSpecification().isSatisfiedBy(gm));
    }

    @Test
    void doublons_neComptentPas() {
        GameManager gm = manager();
        Joueur j1 = gm.getJoueur1();
        // 6 cartes vertes mais seulement 3 symboles distincts
        for (int i = 0; i < 2; i++) {
            j1.ajouterCarte(carteVerte("corne"));
            j1.ajouterCarte(carteVerte("fiole"));
            j1.ajouterCarte(carteVerte("marteau"));
        }
        assertFalse(new AllianceSpecification().isSatisfiedBy(gm));
    }

    @Test
    void symbolesNonVerts_neComptentPas() {
        GameManager gm = manager();
        Joueur j1 = gm.getJoueur1();
        // 6 cartes avec 6 symboles distincts mais sur cartes non vertes
        j1.ajouterCarte(carteAutreCouleur("Gris", "corne"));
        j1.ajouterCarte(carteAutreCouleur("Gris", "fiole"));
        j1.ajouterCarte(carteAutreCouleur("Bleu", "marteau"));
        j1.ajouterCarte(carteAutreCouleur("Bleu", "pipe"));
        j1.ajouterCarte(carteAutreCouleur("Jaune", "coquillage"));
        j1.ajouterCarte(carteAutreCouleur("Jaune", "feuille"));
        assertFalse(new AllianceSpecification().isSatisfiedBy(gm),
                "Seules les cartes vertes doivent etre comptees");
    }
}
