package fr.cpe.service;

import fr.cpe.model.Carte;
import java.util.ArrayList;
import java.util.List;

public class CardFactory {

    public List<Carte> createCartesChapitre(int chapitre) {
        List<Carte> deck = new ArrayList<>();
        
        if (chapitre == 1) {
            // Instanciation des cartes du chapitre 1
        } else if (chapitre == 2) {
            // Instanciation des cartes du chapitre 2
        } else if (chapitre == 3) {
            // Instanciation des cartes du chapitre 3
        }
        
        return deck;
    }
}