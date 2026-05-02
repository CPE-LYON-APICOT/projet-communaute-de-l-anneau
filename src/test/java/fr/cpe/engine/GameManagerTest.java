package fr.cpe.engine;

import fr.cpe.model.Carte;
import fr.cpe.model.HautLieu;
import fr.cpe.model.Joueur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'integration du GameManager : couvre le cycle achat / defausse,
 * la reduction de cout par competence, l'avance de la Piste de l'Anneau,
 * la transition de chapitre, le pattern Observer et la victoire par Anneau.
 */
class GameManagerTest {

    private GameManager gm;

    @BeforeEach
    void setUp() {
        gm = new GameManager();
    }

    // --------- Etat initial ---------

    @Test
    void etatInitial_cohérent() {
        assertSame(gm.getJoueur1(), gm.getJoueurCourant());
        assertEquals(1, gm.getChapitreCourant());
        assertFalse(gm.estPartieTerminee());
        assertEquals(4, gm.getHautsLieuxDisponibles().size());
        assertEquals(5, gm.getPisteAnneau().getPositionCommunaute(),
                "La Communaute demarre en case 5 (Comte/Bree)");
        assertEquals(0, gm.getPisteAnneau().getPositionNazguls(),
                "Les Nazguls demarrent en case 0");
        assertFalse(gm.getPyramide().getCartes().isEmpty(), "Chapitre 1 pre-rempli");
    }

    // --------- Acheter / defausser ---------

    @Test
    void acheterCarteGratuite_changeDeTourEtRetireLaCarte() {
        Carte c = premiereCarteAccessible();
        int nbAvant = gm.getPyramide().getCartes().size();

        gm.acheterCarte(c); // c.coutOr == 0 car c'est une carte de base

        assertSame(gm.getJoueur2(), gm.getJoueurCourant(), "changement de tour");
        assertEquals(nbAvant - 1, gm.getPyramide().getCartes().size());
        assertTrue(gm.getJoueur1().getCartes().contains(c));
    }

    @Test
    void acheterCarte_paieLeCoutEnOr() {
        // On achete une carte couteuse : il faut donner de l'or au joueur courant.
        // Le chapitre 1 contient des cartes a cout 1 (ex: carte_1_17 bleu).
        Carte cible = carteParCout(1);
        assertNotNull(cible, "Le chapitre 1 contient au moins une carte a cout 1");

        gm.getJoueur1().ajouterOr(5);
        libererCarte(cible);

        // Le joueur ne possede aucune des ressources requises -> penalite eventuelle.
        int penalite = cible.getSymbolesRequis().size();
        gm.acheterCarte(cible);

        assertEquals(5 - 1 - penalite, gm.getJoueur1().getOr(),
                "or debite = coutOr + 1 par ressource manquante");
    }

    @Test
    void acheterCarte_penaliteRessourcesManquantes() {
        Carte cible = carteAvecRequisNonNull();
        assertNotNull(cible, "Chapitre 1 contient au moins une carte avec symboleRequis");

        Joueur j1 = gm.getJoueur1();
        j1.ajouterOr(10); // Assez d'or pour tout payer
        libererCarte(cible);

        int orAvant = j1.getOr();
        int ressourcesRequises = cible.getSymbolesRequis().size();
        
        gm.acheterCarte(cible);

        // Le joueur n'avait aucune des ressources requises.
        // Il doit donc payer coutOr + (1 * ressourcesRequises)
        int coutAttendu = cible.getCoutOr() + ressourcesRequises;
        assertEquals(orAvant - coutAttendu, j1.getOr(),
                "cout final = coutOr + 1 or par ressource manquante");
        assertTrue(j1.getCartes().contains(cible));
    }

    @Test
    void acheterCarte_sansPenaliteSiRessourcesPossedees() {
        Carte cible = carteAvecRequisNonNull();
        assertNotNull(cible, "Chapitre 1 contient au moins une carte avec symboleRequis");

        Joueur j1 = gm.getJoueur1();
        j1.ajouterOr(10);
        // Ajout des ressources pour eviter la penalite
        for (String req : cible.getSymbolesRequis()) {
            j1.ajouterSymboleCompetence(req);
        }
        libererCarte(cible);

        int orAvant = j1.getOr();
        gm.acheterCarte(cible);

        // Pas de pénalité, paie juste le coutOr de base
        assertEquals(orAvant - cible.getCoutOr(), j1.getOr(),
                "cout final = coutOr (pas de penalite)");
    }

    @Test
    void defausserCarte_ajoute2Or() {
        Carte c = premiereCarteAccessible();
        int orAvant = gm.getJoueur1().getOr();

        gm.defausserCarte(c);

        assertEquals(orAvant + 2, gm.getJoueur1().getOr());
        assertFalse(gm.getPyramide().getCartes().contains(c));
    }

    // --------- Piste de l'Anneau ---------

    @Test
    void acheterCarteAvecAvance_J1_avanceCommunaute() {
        Carte avecAvance = carteAvecAvanceAnneau();
        assertNotNull(avecAvance, "Au moins une carte avec avanceAnneau > 0 dans ch.1");

        gm.getJoueur1().ajouterOr(10);
        libererCarte(avecAvance);
        int avance = avecAvance.getAvanceAnneau();

        gm.acheterCarte(avecAvance);

        // Communaute partait de 5, donc nouvelle position = 5 + avance.
        assertEquals(5 + avance, gm.getPisteAnneau().getPositionCommunaute());
        assertEquals(0, gm.getPisteAnneau().getPositionNazguls());
    }

    @Test
    void acheterCarteAvecAvance_J2_avanceNazguls() {
        // On force le tour a J2 en faisant J1 defausser une carte banale.
        Carte gratuite = premiereCarteAccessible();
        gm.defausserCarte(gratuite);
        assertSame(gm.getJoueur2(), gm.getJoueurCourant());

        Carte avecAvance = carteAvecAvanceAnneau();
        if (avecAvance == null) return; // pyramide vide, scenario non pertinent
        gm.getJoueur2().ajouterOr(10);
        libererCarte(avecAvance);
        int avance = avecAvance.getAvanceAnneau();

        gm.acheterCarte(avecAvance);

        // Verification : si l'avance > 5, les Nazguls atteignent la position de la Communaute (5)
        // -> RingSpecification declenche la victoire de Sauron, et la partie peut s'arreter.
        // On verifie juste que la position des Nazguls a bien augmente.
        assertEquals(avance, gm.getPisteAnneau().getPositionNazguls());
        assertEquals(5, gm.getPisteAnneau().getPositionCommunaute(),
                "La Communaute n'a pas bouge depuis sa position initiale");
    }

    // --------- Hauts-Lieux ---------

    @Test
    void reclamerHautLieu_echoueSansForteresse() {
        HautLieu hl = gm.getHautsLieuxDisponibles().get(0);
        gm.getJoueur1().ajouterOr(20);

        int tailleAvant = gm.getHautsLieuxDisponibles().size();
        gm.reclamerHautLieu(hl);

        assertEquals(tailleAvant, gm.getHautsLieuxDisponibles().size(),
                "Pas de forteresse => reclamation refusee, haut-lieu toujours dispo");
        assertFalse(gm.getJoueur1().getHautsLieux().contains(hl));
    }

    @Test
    void reclamerHautLieu_estDesactive() {
        // Le mecanisme d'alliance ayant ete retire, les Hauts-Lieux ne sont
        // plus jamais reclamables (les cartes militaires necessaires n'existent
        // pas encore). On verifie que meme avec beaucoup d'or, la reclamation
        // echoue silencieusement.
        HautLieu hl = gm.getHautsLieuxDisponibles().get(0);
        Joueur j1 = gm.getJoueur1();
        j1.ajouterOr(20);

        int tailleAvant = gm.getHautsLieuxDisponibles().size();
        gm.reclamerHautLieu(hl);

        assertEquals(tailleAvant, gm.getHautsLieuxDisponibles().size(),
                "Hauts-Lieux desactives : la reclamation doit echouer");
        assertFalse(j1.getHautsLieux().contains(hl));
    }

    // --------- Observer ---------

    @Test
    void observer_notifieSurAchat() {
        AtomicInteger compteur = new AtomicInteger();
        gm.addObserver(() -> compteur.incrementAndGet());

        gm.acheterCarte(premiereCarteAccessible());

        assertEquals(1, compteur.get());
    }

    // --------- Chapitre ---------

    @Test
    void pyramideVideAuChapitre1_transitionVersChapitre2() {
        // On defausse les cartes du chapitre 1 jusqu'a la transition vers le ch2.
        int garde = 50; // filet anti-boucle infinie
        while (gm.getChapitreCourant() == 1 && garde-- > 0) {
            if (gm.getPyramide().getCartesAccessibles().isEmpty()) break;
            gm.defausserCarte(gm.getPyramide().getCartesAccessibles().get(0));
            if (gm.estPartieTerminee()) break;
        }
        assertEquals(2, gm.getChapitreCourant(), "on doit etre passe au chapitre 2");
        assertFalse(gm.getPyramide().getCartes().isEmpty(), "chapitre 2 doit etre pre-rempli");
    }

    // --------- Helpers ---------

    private Carte premiereCarteAccessible() {
        return gm.getPyramide().getCartesAccessibles().get(0);
    }

    private Carte carteParCout(int cout) {
        for (Carte c : gm.getPyramide().getCartes()) {
            if (c.getCoutOr() == cout) return c;
        }
        return null;
    }

    private Carte carteAvecRequisNonNull() {
        for (Carte c : gm.getPyramide().getCartes()) {
            if (!c.getSymbolesRequis().isEmpty()) return c;
        }
        return null;
    }

    private Carte carteAvecAvanceAnneau() {
        for (Carte c : gm.getPyramide().getCartes()) {
            if (c.getAvanceAnneau() > 0) return c;
        }
        return null;
    }

    /**
     * Vide la pyramide des cartes qui bloquent la cible en les defaussant,
     * SANS acheter la cible elle-meme.
     * Filet anti-boucle : 20 iterations max.
     */
    private void libererCarte(Carte cible) {
        int garde = 20;
        while (!gm.getPyramide().estLibre(cible) && garde-- > 0) {
            List<Carte> libres = gm.getPyramide().getCartesAccessibles();
            Carte aRetirer = null;
            for (Carte l : libres) {
                if (l != cible) { aRetirer = l; break; }
            }
            if (aRetirer == null) return;
            // On contourne la logique metier en retirant directement.
            gm.getPyramide().retirerCarte(aRetirer);
        }
    }
}
