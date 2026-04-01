package fr.cpe.service;

import fr.cpe.engine.GameManager;
import fr.cpe.engine.GameObserver;
import fr.cpe.engine.InputService;
import fr.cpe.model.Carte;
import jakarta.inject.Inject;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameService implements GameObserver {

    private final InputService inputService;
    private final GameManager gameManager;

    private Text textOrJ1;
    private Text textOrJ2;
    private FlowPane pyramidePane;

    @Inject
    public GameService(InputService inputService, GameManager gameManager) {
        this.inputService = inputService;
        this.gameManager = gameManager;
        this.gameManager.addObserver(this);
    }

    public void init(Pane gamePane) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 720);

        HBox header = new HBox(new Text("DUEL POUR LA TERRE DU MILIEU - CHAPITRE 1"));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px;");
        root.setTop(header);

        textOrJ1 = new Text("OR: 0");
        textOrJ2 = new Text("OR: 0");

        VBox joueur1Zone = new VBox(15);
        joueur1Zone.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 20px;");
        joueur1Zone.getChildren().addAll(
            new Text("JOUEUR 1 (Peuples Libres)"), textOrJ1,
            new Text("Alliances:"), new Rectangle(60, 20, Color.DARKBLUE),
            new Text("Cartes:"), new Rectangle(80, 120, Color.LIGHTBLUE)
        );
        root.setLeft(joueur1Zone);

        VBox joueur2Zone = new VBox(15);
        joueur2Zone.setStyle("-fx-background-color: #ffebee; -fx-padding: 20px;");
        joueur2Zone.getChildren().addAll(
            new Text("JOUEUR 2 (Sauron)"), textOrJ2,
            new Text("Alliances:"), new Rectangle(60, 20, Color.DARKRED),
            new Text("Cartes:"), new Rectangle(80, 120, Color.LIGHTCORAL)
        );
        root.setRight(joueur2Zone);

        VBox centreZone = new VBox(30);
        centreZone.setAlignment(Pos.CENTER);
        
        // Remplacement du Rectangle par un FlowPane pour organiser les cartes
        pyramidePane = new FlowPane(10, 10);
        pyramidePane.setAlignment(Pos.CENTER);
        pyramidePane.setMaxWidth(400);

        centreZone.getChildren().addAll(
            new Text("ZONE DE DRAFT - PYRAMIDE"), pyramidePane,
            new Text("PISTE DE L'ANNEAU (0-12)"), new Rectangle(600, 40, Color.DARKGRAY)
        );
        root.setCenter(centreZone);

        gamePane.getChildren().add(root);
        
        onGameStateChanged(); 
    }

    public void update(double width, double height) {
    }

    @Override
    public void onGameStateChanged() {
        if (textOrJ1 != null && textOrJ2 != null) {
            textOrJ1.setText("OR: " + gameManager.getJoueur1().getOr());
            textOrJ2.setText("OR: " + gameManager.getJoueur2().getOr());
        }

        if (pyramidePane != null) {
            pyramidePane.getChildren().clear();
            
            // Génération dynamique de l'affichage des cartes
            for (Carte carte : gameManager.getPyramide().getCartes()) {
                StackPane carteVue = new StackPane();
                boolean estLibre = gameManager.getPyramide().estLibre(carte);
                
                // Vert si on peut l'acheter, Gris si elle est bloquée
                Rectangle fond = new Rectangle(80, 120, estLibre ? Color.LIGHTGREEN : Color.GRAY);
                fond.setStroke(Color.BLACK);
                Text nom = new Text(carte.getNom());
                
                carteVue.getChildren().addAll(fond, nom);
                
                if (estLibre) {
                    carteVue.setOnMouseClicked(e -> {
                        // Pour le test, on dit que c'est toujours le J1 qui achète
                        gameManager.acheterCarte(carte, gameManager.getJoueur1());
                    });
                }
                
                pyramidePane.getChildren().add(carteVue);
            }
        }
    }
}