package fr.cpe.service;

import fr.cpe.engine.GameManager;
import fr.cpe.engine.GameObserver;
import jakarta.inject.Inject;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameService implements GameObserver {

    private final GameManager gameManager;
    private Text textTour;
    private Text textOrJ1;
    private Text textOrJ2;
    private HBox cartesJ1Box;
    private HBox cartesJ2Box;
    private VBox pyramideBox;

    @Inject
    public GameService(GameManager gameManager) {
        this.gameManager = gameManager;
        this.gameManager.addObserver(this);
    }

    public void init(Pane gamePane) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 720);

        HBox topZone = new HBox(10);
        topZone.setAlignment(Pos.CENTER);
        topZone.setStyle("-fx-padding: 20px; -fx-background-color: #e0f7fa;");
        textTour = new Text("Tour : Joueur 1");
        textOrJ1 = new Text("Or Joueur 1: 0");
        cartesJ1Box = new HBox(5);
        topZone.getChildren().addAll(textTour, textOrJ1, cartesJ1Box);
        root.setTop(topZone);

        pyramideBox = new VBox(20);
        pyramideBox.setAlignment(Pos.CENTER);
        pyramideBox.getChildren().add(new Text("ZONE PYRAMIDE"));
        root.setCenter(pyramideBox);

        HBox bottomZone = new HBox(10);
        bottomZone.setAlignment(Pos.CENTER);
        bottomZone.setStyle("-fx-padding: 20px; -fx-background-color: #ffebee;");
        textOrJ2 = new Text("Or Joueur 2: 0");
        cartesJ2Box = new HBox(5);
        bottomZone.getChildren().addAll(textOrJ2, cartesJ2Box);
        root.setBottom(bottomZone);

        gamePane.getChildren().add(root);
        
        onGameStateChanged();
    }

    private void mettreAJourPyramide() {
        pyramideBox.getChildren().clear();
        for (fr.cpe.model.Carte carte : gameManager.getPyramide().getCartesAccessibles()) {
            javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane();
            
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(100, 150);
            javafx.scene.paint.Color couleur;
            switch (carte.getCouleur().toLowerCase()) {
                case "jaune": couleur = javafx.scene.paint.Color.YELLOW; break;
                case "gris": couleur = javafx.scene.paint.Color.GRAY; break;
                case "rouge": couleur = javafx.scene.paint.Color.RED; break;
                case "bleu": couleur = javafx.scene.paint.Color.LIGHTBLUE; break;
                case "vert": couleur = javafx.scene.paint.Color.LIGHTGREEN; break;
                default:
                    try {
                        couleur = javafx.scene.paint.Color.web(carte.getCouleur());
                    } catch (Exception e) {
                        couleur = javafx.scene.paint.Color.WHITE;
                    }
            }
            rect.setFill(couleur);
            rect.setStroke(javafx.scene.paint.Color.BLACK);
            
            Text text = new Text(carte.getNom() + "\nCoût: " + carte.getCoutOr());
            text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            
            pane.getChildren().addAll(rect, text);
            pane.setOnMouseClicked(e -> {
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    gameManager.acheterCarte(carte);
                } else if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                    gameManager.defausserCarte(carte);
                }
            });
            
            pyramideBox.getChildren().add(pane);
        }
    }

    private void mettreAJourInventaires() {
        if (cartesJ1Box == null || cartesJ2Box == null) return;
        
        cartesJ1Box.getChildren().clear();
        for (fr.cpe.model.Carte carte : gameManager.getJoueur1().getCartes()) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(30, 40);
            javafx.scene.paint.Color couleur;
            switch (carte.getCouleur().toLowerCase()) {
                case "jaune": couleur = javafx.scene.paint.Color.YELLOW; break;
                case "gris": couleur = javafx.scene.paint.Color.GRAY; break;
                case "rouge": couleur = javafx.scene.paint.Color.RED; break;
                case "bleu": couleur = javafx.scene.paint.Color.LIGHTBLUE; break;
                case "vert": couleur = javafx.scene.paint.Color.LIGHTGREEN; break;
                default:
                    try { couleur = javafx.scene.paint.Color.web(carte.getCouleur()); } 
                    catch (Exception e) { couleur = javafx.scene.paint.Color.WHITE; }
            }
            rect.setFill(couleur);
            rect.setStroke(javafx.scene.paint.Color.BLACK);
            cartesJ1Box.getChildren().add(rect);
        }

        cartesJ2Box.getChildren().clear();
        for (fr.cpe.model.Carte carte : gameManager.getJoueur2().getCartes()) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(30, 40);
            javafx.scene.paint.Color couleur;
            switch (carte.getCouleur().toLowerCase()) {
                case "jaune": couleur = javafx.scene.paint.Color.YELLOW; break;
                case "gris": couleur = javafx.scene.paint.Color.GRAY; break;
                case "rouge": couleur = javafx.scene.paint.Color.RED; break;
                case "bleu": couleur = javafx.scene.paint.Color.LIGHTBLUE; break;
                case "vert": couleur = javafx.scene.paint.Color.LIGHTGREEN; break;
                default:
                    try { couleur = javafx.scene.paint.Color.web(carte.getCouleur()); } 
                    catch (Exception e) { couleur = javafx.scene.paint.Color.WHITE; }
            }
            rect.setFill(couleur);
            rect.setStroke(javafx.scene.paint.Color.BLACK);
            cartesJ2Box.getChildren().add(rect);
        }
    }

    public void update(double width, double height) {
    }

    @Override
    public void onGameStateChanged() {
        if (textTour != null) {
            textTour.setText("Tour : Joueur " + (gameManager.getJoueurCourant() == gameManager.getJoueur1() ? "1" : "2"));
        }
        if (textOrJ1 != null && textOrJ2 != null) {
            textOrJ1.setText("Or Joueur 1: " + gameManager.getJoueur1().getOr());
            textOrJ2.setText("Or Joueur 2: " + gameManager.getJoueur2().getOr());
        }
        if (pyramideBox != null && gameManager.getPyramide() != null) {
            mettreAJourPyramide();
        }
        mettreAJourInventaires();
    }
}