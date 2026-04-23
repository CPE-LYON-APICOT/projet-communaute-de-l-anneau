package fr.cpe.service;

import fr.cpe.engine.GameManager;
import fr.cpe.engine.GameObserver;
import fr.cpe.model.Carte;
import fr.cpe.model.HautLieu;
import fr.cpe.model.Joueur;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GameService implements GameObserver {

    private static final int TAILLE_PISTE = 10; // cases 0..9

    private final GameManager gameManager;

    // Textes dynamiques
    private Text textTour;
    private Text textChapitre;
    private Text textOrJ1;
    private Text textOrJ2;
    private Text textSymbolesJ1;
    private Text textSymbolesJ2;

    // Zones dynamiques
    private HBox cartesJ1Box;
    private HBox cartesJ2Box;
    private VBox pyramideBox;
    private HBox pisteBox;
    private VBox hautsLieuxBox;

    // Overlay de fin de partie (affiché sur le root StackPane)
    private StackPane rootStack;
    private VBox ecranFin;

    @Inject
    public GameService(GameManager gameManager) {
        this.gameManager = gameManager;
        this.gameManager.addObserver(this);
    }

    public void init(Pane gamePane) {
        rootStack = new StackPane();
        rootStack.setPrefSize(1280, 720);

        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 720);

        // -------- TOP : Joueur 1 (Communauté) --------
        root.setTop(buildJoueurZone(true));

        // -------- CENTER : piste + pyramide + Hauts-Lieux --------
        BorderPane centre = new BorderPane();
        centre.setPadding(new Insets(10));

        pisteBox = new HBox(4);
        pisteBox.setAlignment(Pos.CENTER);
        pisteBox.setPadding(new Insets(10));
        centre.setTop(pisteBox);

        pyramideBox = new VBox(10);
        pyramideBox.setAlignment(Pos.CENTER);
        centre.setCenter(pyramideBox);

        hautsLieuxBox = new VBox(8);
        hautsLieuxBox.setAlignment(Pos.TOP_CENTER);
        hautsLieuxBox.setPadding(new Insets(10));
        hautsLieuxBox.setStyle("-fx-background-color: #fff8e1; -fx-border-color: #bfa44a;");
        Text titreHL = new Text("Hauts-Lieux");
        titreHL.setFont(Font.font("System", FontWeight.BOLD, 14));
        hautsLieuxBox.getChildren().add(titreHL);
        centre.setRight(hautsLieuxBox);

        root.setCenter(centre);

        // -------- BOTTOM : Joueur 2 (Sauron) --------
        root.setBottom(buildJoueurZone(false));

        rootStack.getChildren().add(root);
        gamePane.getChildren().add(rootStack);

        onGameStateChanged();
    }

    private VBox buildJoueurZone(boolean j1) {
        VBox zone = new VBox(6);
        zone.setAlignment(Pos.CENTER);
        zone.setPadding(new Insets(10));
        zone.setStyle(j1
                ? "-fx-background-color: #e0f7fa;"
                : "-fx-background-color: #ffebee;");

        HBox ligneInfo = new HBox(20);
        ligneInfo.setAlignment(Pos.CENTER);

        if (j1) {
            textTour = new Text("Tour : Joueur 1");
            textTour.setFont(Font.font("System", FontWeight.BOLD, 14));
            textChapitre = new Text("Chapitre 1");
            textChapitre.setFont(Font.font("System", FontWeight.BOLD, 14));
            textOrJ1 = new Text("Or J1 : 0");
            textSymbolesJ1 = new Text("Alliances : -");
            ligneInfo.getChildren().addAll(textTour, textChapitre, textOrJ1, textSymbolesJ1);

            cartesJ1Box = new HBox(4);
            cartesJ1Box.setAlignment(Pos.CENTER);
            zone.getChildren().addAll(ligneInfo, cartesJ1Box);
        } else {
            textOrJ2 = new Text("Or J2 : 0");
            textSymbolesJ2 = new Text("Alliances : -");
            ligneInfo.getChildren().addAll(textOrJ2, textSymbolesJ2);

            cartesJ2Box = new HBox(4);
            cartesJ2Box.setAlignment(Pos.CENTER);
            zone.getChildren().addAll(ligneInfo, cartesJ2Box);
        }

        return zone;
    }

    // =========================================================================
    // Mises à jour
    // =========================================================================

    @Override
    public void onGameStateChanged() {
        if (textTour != null) {
            textTour.setText("Tour : Joueur "
                    + (gameManager.getJoueurCourant() == gameManager.getJoueur1() ? "1" : "2"));
        }
        if (textChapitre != null) {
            textChapitre.setText("Chapitre " + gameManager.getChapitreCourant());
        }
        if (textOrJ1 != null && textOrJ2 != null) {
            textOrJ1.setText("Or J1 : " + gameManager.getJoueur1().getOr());
            textOrJ2.setText("Or J2 : " + gameManager.getJoueur2().getOr());
        }
        if (textSymbolesJ1 != null) {
            textSymbolesJ1.setText(formatAlliances(gameManager.getJoueur1()));
        }
        if (textSymbolesJ2 != null) {
            textSymbolesJ2.setText(formatAlliances(gameManager.getJoueur2()));
        }
        if (pyramideBox != null && gameManager.getPyramide() != null) {
            mettreAJourPyramide();
        }
        mettreAJourInventaires();
        mettreAJourPiste();
        mettreAJourHautsLieux();
        mettreAJourEcranFin();
    }

    private String formatAlliances(Joueur j) {
        if (j.getSymbolesAlliance().isEmpty()) return "Alliances : -";
        return "Alliances : " + String.join(", ", j.getSymbolesAlliance());
    }

    private void mettreAJourPyramide() {
        pyramideBox.getChildren().clear();
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER);
        for (Carte carte : gameManager.getPyramide().getCartesAccessibles()) {
            ligne.getChildren().add(construireVignetteCarte(carte, true));
        }
        pyramideBox.getChildren().add(ligne);

        // Les cartes couvertes sont affichées grisées au-dessous pour indiquer
        // ce qui arrive, sans être cliquables.
        if (!gameManager.getPyramide().getCartesRecouvertes().isEmpty()) {
            HBox ligneCouvertes = new HBox(6);
            ligneCouvertes.setAlignment(Pos.CENTER);
            for (Carte carte : gameManager.getPyramide().getCartesRecouvertes()) {
                ligneCouvertes.getChildren().add(construireVignetteCarte(carte, false));
            }
            pyramideBox.getChildren().add(new Text("(Couvertes)"));
            pyramideBox.getChildren().add(ligneCouvertes);
        }
    }

    private StackPane construireVignetteCarte(Carte carte, boolean cliquable) {
        StackPane pane = new StackPane();

        double w = cliquable ? 110 : 70;
        double h = cliquable ? 150 : 90;
        Rectangle rect = new Rectangle(w, h);
        Color fond = couleurDe(carte.getCouleur());
        if (!cliquable) {
            // Griser les cartes couvertes
            fond = fond.deriveColor(0, 0.3, 1, 0.6);
        }
        rect.setFill(fond);
        rect.setStroke(Color.BLACK);

        StringBuilder label = new StringBuilder(carte.getNom());
        label.append("\nCoût: ").append(carte.getCoutOr());
        if (carte.getSymboleRequis() != null) {
            label.append(" (").append(carte.getSymboleRequis()).append(")");
        }
        if (carte.getSymboleAlliance() != null && !carte.getSymboleAlliance().isEmpty()) {
            label.append("\n⚑ ").append(carte.getSymboleAlliance());
        }
        if (carte.getSymboleCompetence() != null) {
            label.append("\n◆ ").append(carte.getSymboleCompetence());
        }
        if (carte.getAvanceAnneau() > 0) {
            label.append("\n↯ Anneau +").append(carte.getAvanceAnneau());
        }
        Text text = new Text(label.toString());
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font("System", cliquable ? 11 : 9));

        pane.getChildren().addAll(rect, text);

        if (cliquable) {
            pane.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    gameManager.acheterCarte(carte);
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    gameManager.defausserCarte(carte);
                }
            });
        }
        return pane;
    }

    private void mettreAJourInventaires() {
        if (cartesJ1Box == null || cartesJ2Box == null) return;
        remplirInventaire(cartesJ1Box, gameManager.getJoueur1());
        remplirInventaire(cartesJ2Box, gameManager.getJoueur2());
    }

    private void remplirInventaire(HBox box, Joueur j) {
        box.getChildren().clear();
        for (Carte carte : j.getCartes()) {
            StackPane vignette = new StackPane();
            Rectangle rect = new Rectangle(36, 48);
            rect.setFill(couleurDe(carte.getCouleur()));
            rect.setStroke(Color.BLACK);

            StringBuilder eff = new StringBuilder();
            if (carte.getSymboleCompetence() != null) eff.append("◆");
            if (carte.getAvanceAnneau() > 0) eff.append("↯");
            if ("Forteresse".equals(carte.getSymboleAlliance())) eff.append("⚑");

            Text t = new Text(eff.toString());
            t.setFont(Font.font("System", FontWeight.BOLD, 10));
            vignette.getChildren().addAll(rect, t);
            box.getChildren().add(vignette);
        }
    }

    private void mettreAJourPiste() {
        if (pisteBox == null) return;
        pisteBox.getChildren().clear();

        Text titre = new Text("Piste de l'Anneau : ");
        titre.setFont(Font.font("System", FontWeight.BOLD, 13));
        pisteBox.getChildren().add(titre);

        int posCom = gameManager.getPisteAnneau().getPositionCommunaute();
        int posNaz = gameManager.getPisteAnneau().getPositionNazguls();

        for (int i = 0; i < TAILLE_PISTE; i++) {
            StackPane case_ = new StackPane();
            Rectangle rect = new Rectangle(34, 34);
            if (i == TAILLE_PISTE - 1) {
                rect.setFill(Color.GOLD);
            } else {
                rect.setFill(Color.WHEAT);
            }
            rect.setStroke(Color.BLACK);
            case_.getChildren().add(rect);

            // Numéro de case
            Text num = new Text(String.valueOf(i));
            num.setFont(Font.font("System", 9));
            StackPane.setAlignment(num, Pos.TOP_LEFT);
            case_.getChildren().add(num);

            HBox pions = new HBox(2);
            pions.setAlignment(Pos.CENTER);
            if (i == posCom) {
                Circle pionCom = new Circle(6, Color.DODGERBLUE);
                pionCom.setStroke(Color.BLACK);
                pions.getChildren().add(pionCom);
            }
            if (i == posNaz) {
                Circle pionNaz = new Circle(6, Color.BLACK);
                pionNaz.setStroke(Color.DARKRED);
                pions.getChildren().add(pionNaz);
            }
            case_.getChildren().add(pions);

            pisteBox.getChildren().add(case_);
        }
    }

    private void mettreAJourHautsLieux() {
        if (hautsLieuxBox == null) return;

        // On garde le titre (1er enfant) et on remplace le reste.
        while (hautsLieuxBox.getChildren().size() > 1) {
            hautsLieuxBox.getChildren().remove(1);
        }

        for (HautLieu hl : gameManager.getHautsLieuxDisponibles()) {
            VBox carte = new VBox(2);
            carte.setAlignment(Pos.CENTER);
            carte.setPadding(new Insets(6));
            carte.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #8b6f1c;");

            Text nom = new Text(hl.getNom());
            nom.setFont(Font.font("System", FontWeight.BOLD, 12));
            Text cout = new Text(hl.getCoutOr() + " or, " + hl.getCoutForteresses() + " ⚑");
            cout.setFont(Font.font("System", 11));
            Text effet = new Text(hl.getEffet());
            effet.setFont(Font.font("System", 10));

            carte.getChildren().addAll(nom, cout, effet);

            // Clic gauche = réclamer (le GameManager vérifie les conditions).
            carte.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    gameManager.reclamerHautLieu(hl);
                }
            });

            hautsLieuxBox.getChildren().add(carte);
        }

        if (gameManager.getHautsLieuxDisponibles().isEmpty()) {
            Text vide = new Text("(Tous réclamés)");
            vide.setFont(Font.font("System", 10));
            hautsLieuxBox.getChildren().add(vide);
        }
    }

    private void mettreAJourEcranFin() {
        if (rootStack == null) return;

        if (gameManager.estPartieTerminee()) {
            if (ecranFin == null) {
                ecranFin = new VBox(12);
                ecranFin.setAlignment(Pos.CENTER);
                ecranFin.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
                Text titre = new Text("FIN DE LA PARTIE");
                titre.setFill(Color.WHITE);
                titre.setFont(Font.font("System", FontWeight.BOLD, 36));
                Text vainqueur = new Text();
                vainqueur.setFill(Color.GOLD);
                vainqueur.setFont(Font.font("System", FontWeight.BOLD, 22));
                vainqueur.setId("vainqueurText");
                ecranFin.getChildren().addAll(titre, vainqueur);
                rootStack.getChildren().add(ecranFin);
            }
            // Mettre à jour le texte du vainqueur
            for (javafx.scene.Node n : ecranFin.getChildren()) {
                if ("vainqueurText".equals(n.getId()) && n instanceof Text) {
                    String v = gameManager.getVainqueurFinal();
                    ((Text) n).setText(v != null ? "Vainqueur : " + v : "Partie terminée");
                }
            }
        } else if (ecranFin != null) {
            rootStack.getChildren().remove(ecranFin);
            ecranFin = null;
        }
    }

    public void update(double width, double height) {
    }

    // =========================================================================
    // Utilitaire
    // =========================================================================
    private static Color couleurDe(String nomCouleur) {
        if (nomCouleur == null) return Color.WHITE;
        switch (nomCouleur.toLowerCase()) {
            case "jaune": return Color.YELLOW;
            case "gris":  return Color.LIGHTGRAY;
            case "rouge": return Color.INDIANRED;
            case "bleu":  return Color.LIGHTBLUE;
            case "vert":  return Color.LIGHTGREEN;
            default:
                try { return Color.web(nomCouleur); }
                catch (Exception e) { return Color.WHITE; }
        }
    }
}
