package fr.cpe.engine;

import fr.cpe.model.PisteAnneau;

public class RingSpecification implements Specification<GameManager> {

    /** Case finale (Mont du Destin) : si la Communaute l'atteint, l'Anneau est detruit. */
    private static final int FIN_DE_PISTE = 15;

    @Override
    public boolean isSatisfiedBy(GameManager gameManager) {
        PisteAnneau piste = gameManager.getPisteAnneau();
        boolean communauteALaFin = piste.getPositionCommunaute() >= FIN_DE_PISTE;
        // Les Nazguls rattrapent : leur position atteint celle de la Communaute.
        // (Plus besoin de la garde "Communaute > 0" puisque la Communaute demarre a 5.)
        boolean nazgulsRattrapent = piste.getPositionNazguls() >= piste.getPositionCommunaute();

        return communauteALaFin || nazgulsRattrapent;
    }

    @Override
    public String getDescription() {
        return "Piste de l'Anneau";
    }
}