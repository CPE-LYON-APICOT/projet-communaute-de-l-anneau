package fr.cpe.service;

import fr.cpe.engine.GameManager;
import fr.cpe.engine.InputService;
import jakarta.inject.Inject;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameService {

    private final InputService inputService;
    private final GameManager gameManager;

    @Inject
    public GameService(InputService inputService, GameManager gameManager) {
        this.inputService = inputService;
        this.gameManager = gameManager;
    }

    public void init(Pane gamePane) {
        // Conteneur principal qui organise l'écran en 5 zones
        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 720);

        // 1. EN-TÊTE (Haut)
        HBox header = new HBox(new Text("DUEL POUR LA TERRE DU MILIEU - CHAPITRE 1"));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px;");
        root.setTop(header);

        // 2. JOUEUR 1 (Gauche)
        VBox joueur1Zone = new VBox(15);
        joueur1Zone.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 20px;");
        joueur1Zone.getChildren().addAll(
            new Text("JOUEUR 1 (Peuples Libres)"),
            new Text("OR: 0"),
            new Text("Alliances:"),
            new Rectangle(60, 20, Color.DARKBLUE), // Placeholder Alliances
            new Text("Cartes:"),
            new Rectangle(80, 120, Color.LIGHTBLUE) // Placeholder Tableau de cartes
        );
        root.setLeft(joueur1Zone);

        // 3. JOUEUR 2 (Droite)
        VBox joueur2Zone = new VBox(15);
        joueur2Zone.setStyle("-fx-background-color: #ffebee; -fx-padding: 20px;");
        joueur2Zone.getChildren().addAll(
            new Text("JOUEUR 2 (Sauron)"),
            new Text("OR: 0"),
            new Text("Alliances:"),
            new Rectangle(60, 20, Color.DARKRED), // Placeholder Alliances
            new Text("Cartes:"),
            new Rectangle(80, 120, Color.LIGHTCORAL) // Placeholder Tableau de cartes
        );
        root.setRight(joueur2Zone);

        // 4. ZONE CENTRALE (Milieu : Pyramide + Anneau)
        VBox centreZone = new VBox(30);
        centreZone.setAlignment(Pos.CENTER);
        
        Rectangle pyramidePlaceholder = new Rectangle(500, 300, Color.GRAY);
        Rectangle anneauPlaceholder = new Rectangle(600, 40, Color.DARKGRAY);
        
        centreZone.getChildren().addAll(
            new Text("ZONE DE DRAFT - PYRAMIDE"),
            pyramidePlaceholder,
            new Text("PISTE DE L'ANNEAU (0-12)"),
            anneauPlaceholder
        );
        root.setCenter(centreZone);

        // Ajout du layout complet au Pane du jeu
        gamePane.getChildren().add(root);
    }

    public void update(double width, double height) {
    }
}