package fr.cpe.engine;

import fr.cpe.model.PisteAnneau;

public class RingSpecification implements Specification<GameManager> {

    private static final int FIN_DE_PISTE = 9;

    @Override
    public boolean isSatisfiedBy(GameManager gameManager) {
        PisteAnneau piste = gameManager.getPisteAnneau();
        boolean communauteALaFin = piste.getPositionCommunaute() >= FIN_DE_PISTE;
        boolean nazgulsRattrapent = piste.getPositionNazguls() >= piste.getPositionCommunaute() 
                                    && piste.getPositionCommunaute() > 0;
        
        return communauteALaFin || nazgulsRattrapent;
    }
}