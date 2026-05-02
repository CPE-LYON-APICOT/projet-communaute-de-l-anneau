package fr.cpe.service;

import fr.cpe.engine.GameManager;
import fr.cpe.engine.GameObserver;
import fr.cpe.model.Carte;
import fr.cpe.model.HautLieu;
import fr.cpe.model.Joueur;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GameService implements GameObserver {

    private static final int TAILLE_PISTE = 16; // cases 0..15 (la 15 = Mont du Destin)

    private final GameManager gameManager;

    // Textes dynamiques
    private Text textTour;
    private Text textChapitre;
    private Text textOrJ1;
    private Text textOrJ2;
    private Text textSymbolesJ1;
    private Text textSymbolesJ2;
    private Text textCompetencesJ1;
    private Text textCompetencesJ2;

    // Zones dynamiques
    private HBox cartesJ1Box;
    private HBox cartesJ2Box;
    private VBox pyramideBox;
    private HBox pisteBox;
    private VBox hautsLieuxBox;

    // Overlay de fin de partie (affiché sur le root StackPane)
    private StackPane rootStack;
    private VBox ecranFin;

    // Musique de fond
    private MediaPlayer mediaPlayer;
    private double volumeMusique = 0.3;
    private boolean musiqueActive = true;
    private static final java.util.LinkedHashMap<String, String> PISTES = new java.util.LinkedHashMap<>();
    static {
        PISTES.put("Terre du Milieu", "/audio/terre_du_milieu.mp3");
        PISTES.put("Gondor",          "/audio/gondor.mp3");
        PISTES.put("Khazad-dûm",      "/audio/khazad_dum.mp3");
        PISTES.put("Moria",           "/audio/moria.mp3");
    }

    @Inject
    public GameService(GameManager gameManager) {
        this.gameManager = gameManager;
        this.gameManager.addObserver(this);
    }

    public void init(Pane gamePane) {
        rootStack = new StackPane();
        // Le rootStack suit la taille du gamePane parent (responsive)
        rootStack.setMinSize(0, 0);
        rootStack.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        rootStack.prefWidthProperty().bind(gamePane.widthProperty());
        rootStack.prefHeightProperty().bind(gamePane.heightProperty());
        // Theme Tolkien : fond sombre degrade
        rootStack.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1410, #2a1f15);");

        BorderPane root = new BorderPane();

        // -------- TOP : Joueur 1 (Communaute) --------
        root.setTop(buildJoueurZone(true));

        // -------- CENTER : piste + pyramide + Hauts-Lieux --------
        BorderPane centre = new BorderPane();
        centre.setPadding(new Insets(15));

        pisteBox = new HBox(4);
        pisteBox.setAlignment(Pos.CENTER);
        pisteBox.setPadding(new Insets(8));
        centre.setTop(pisteBox);

        pyramideBox = new VBox(14);
        pyramideBox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(pyramideBox, Pos.CENTER);
        centre.setCenter(pyramideBox);

        hautsLieuxBox = new VBox(10);
        hautsLieuxBox.setAlignment(Pos.TOP_CENTER);
        hautsLieuxBox.setPadding(new Insets(12));
        hautsLieuxBox.setMinWidth(180);
        hautsLieuxBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #3d2f1d, #2a1f15);"
                + " -fx-border-color: #bfa44a; -fx-border-width: 2;"
                + " -fx-background-radius: 8; -fx-border-radius: 8;");
        Text titreHL = new Text("Hauts-Lieux");
        titreHL.setFont(Font.font("Serif", FontWeight.BOLD, 18));
        titreHL.setFill(Color.GOLD);
        hautsLieuxBox.getChildren().add(titreHL);

        // Conteneur droit : panneau musique au-dessus, Hauts-Lieux en dessous
        VBox colonneDroite = new VBox(10);
        colonneDroite.setAlignment(Pos.TOP_CENTER);
        colonneDroite.getChildren().addAll(construirePanneauMusique(), hautsLieuxBox);
        BorderPane.setMargin(colonneDroite, new Insets(0, 10, 0, 10));
        centre.setRight(colonneDroite);

        root.setCenter(centre);

        // -------- BOTTOM : Joueur 2 (Sauron) --------
        root.setBottom(buildJoueurZone(false));

        rootStack.getChildren().add(root);
        gamePane.getChildren().add(rootStack);

        // Demarrage de la musique de fond
        chargerEtJouerMusique("Terre du Milieu");

        onGameStateChanged();
    }

    /** Construit la barre de controle audio (selection piste + volume + on/off). */
    private HBox construirePanneauMusique() {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle("-fx-background-color: rgba(0,0,0,0.55);"
                + " -fx-background-radius: 8; -fx-border-color: #bfa44a;"
                + " -fx-border-width: 1; -fx-border-radius: 8;");

        Text labelMusique = new Text("♪");
        labelMusique.setFill(Color.GOLD);
        labelMusique.setFont(Font.font("System", FontWeight.BOLD, 16));

        ComboBox<String> selecteur = new ComboBox<>();
        selecteur.getItems().addAll(PISTES.keySet());
        selecteur.getSelectionModel().selectFirst();
        selecteur.setPrefWidth(140);
        selecteur.setOnAction(e -> chargerEtJouerMusique(selecteur.getValue()));

        Slider sliderVolume = new Slider(0, 1, volumeMusique);
        sliderVolume.setPrefWidth(100);
        sliderVolume.valueProperty().addListener((obs, oldV, newV) -> {
            volumeMusique = newV.doubleValue();
            if (mediaPlayer != null) mediaPlayer.setVolume(volumeMusique);
        });

        ToggleButton boutonPause = new ToggleButton("⏸");
        boutonPause.setSelected(true);
        boutonPause.setTooltip(new javafx.scene.control.Tooltip("Pause / Reprendre"));
        boutonPause.setOnAction(e -> {
            musiqueActive = boutonPause.isSelected();
            boutonPause.setText(musiqueActive ? "⏸" : "▶");
            if (mediaPlayer != null) {
                if (musiqueActive) mediaPlayer.play();
                else mediaPlayer.pause();
            }
        });

        javafx.scene.control.Button boutonStop = new javafx.scene.control.Button("⏹");
        boutonStop.setTooltip(new javafx.scene.control.Tooltip("Arreter la musique"));
        boutonStop.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                musiqueActive = false;
                boutonPause.setSelected(false);
                boutonPause.setText("▶");
            }
        });

        box.getChildren().addAll(labelMusique, selecteur, sliderVolume, boutonPause, boutonStop);
        return box;
    }

    /** Charge la piste demandee et la lit en boucle. Remplace toute musique en cours. */
    private void chargerEtJouerMusique(String nomPiste) {
        String chemin = PISTES.get(nomPiste);
        if (chemin == null) return;
        java.net.URL url = getClass().getResource(chemin);
        if (url == null) {
            System.err.println("Musique introuvable : " + chemin);
            return;
        }
        // Stoppe et libere la piste precedente
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        try {
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(volumeMusique);
            if (musiqueActive) mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Erreur chargement musique : " + e.getMessage());
        }
    }

    private VBox buildJoueurZone(boolean j1) {
        VBox zone = new VBox(6);
        zone.setAlignment(Pos.CENTER);
        zone.setPadding(new Insets(12));
        zone.setStyle(j1
                ? "-fx-background-color: linear-gradient(to bottom, #1a4d6e, #0d2638);"
                  + " -fx-border-color: #4a90c0; -fx-border-width: 0 0 2 0;"
                : "-fx-background-color: linear-gradient(to top, #5c1a1a, #2d0d0d);"
                  + " -fx-border-color: #c04a4a; -fx-border-width: 2 0 0 0;");

        HBox ligneInfo = new HBox(25);
        ligneInfo.setAlignment(Pos.CENTER);

        if (j1) {
            textTour = labelClair("Tour : Communauté", 16, true);
            textChapitre = labelClair("Chapitre 1", 16, true);
            textOrJ1 = labelClair("Or : 0", 14, false);
            textSymbolesJ1 = labelClair("Symboles verts : 0/6", 13, false);
            textCompetencesJ1 = labelClair("Compétences : -", 12, false);
            ligneInfo.getChildren().addAll(textTour, textChapitre, textOrJ1,
                    textSymbolesJ1, textCompetencesJ1);

            cartesJ1Box = new HBox(4);
            cartesJ1Box.setAlignment(Pos.CENTER);
            zone.getChildren().addAll(ligneInfo, cartesJ1Box);
        } else {
            textOrJ2 = labelClair("Or : 0", 14, false);
            textSymbolesJ2 = labelClair("Symboles verts : 0/6", 13, false);
            textCompetencesJ2 = labelClair("Compétences : -", 12, false);
            ligneInfo.getChildren().addAll(textOrJ2, textSymbolesJ2, textCompetencesJ2);

            cartesJ2Box = new HBox(4);
            cartesJ2Box.setAlignment(Pos.CENTER);
            zone.getChildren().addAll(cartesJ2Box, ligneInfo);
        }

        return zone;
    }

    private static Text labelClair(String texte, int taille, boolean gras) {
        Text t = new Text(texte);
        t.setFill(Color.WHITE);
        t.setFont(Font.font("Serif", gras ? FontWeight.BOLD : FontWeight.NORMAL, taille));
        return t;
    }

    // =========================================================================
    // Mises à jour
    // =========================================================================

    @Override
    public void onGameStateChanged() {
        if (textTour != null) {
            textTour.setText("Tour : " + gameManager.nomJoueur(gameManager.getJoueurCourant()));
        }
        if (textChapitre != null) {
            textChapitre.setText("Chapitre " + gameManager.getChapitreCourant());
        }
        if (textOrJ1 != null && textOrJ2 != null) {
            textOrJ1.setText("Or : " + gameManager.getJoueur1().getOr());
            textOrJ2.setText("Or : " + gameManager.getJoueur2().getOr());
        }
        if (textSymbolesJ1 != null) {
            textSymbolesJ1.setText(formatAlliances(gameManager.getJoueur1()));
        }
        if (textSymbolesJ2 != null) {
            textSymbolesJ2.setText(formatAlliances(gameManager.getJoueur2()));
        }
        if (textCompetencesJ1 != null) {
            textCompetencesJ1.setText(formatCompetences(gameManager.getJoueur1()));
        }
        if (textCompetencesJ2 != null) {
            textCompetencesJ2.setText(formatCompetences(gameManager.getJoueur2()));
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
        // Compte les symboles distincts apportes par les cartes vertes du joueur.
        java.util.Set<String> symbolesVerts = new java.util.HashSet<>();
        for (Carte c : j.getCartes()) {
            if ("Vert".equalsIgnoreCase(c.getCouleur())) {
                symbolesVerts.addAll(c.getSymbolesCompetences());
            }
        }
        if (symbolesVerts.isEmpty()) {
            return "Symboles verts : 0/6";
        }
        return "Symboles verts : " + symbolesVerts.size() + "/6 ("
                + String.join(", ", symbolesVerts) + ")";
    }

    /** Detail des competences du joueur, comptees a partir de cartes non vertes
     *  (les cartes vertes ne donnent que des symboles, pas des competences classiques). */
    private String formatCompetences(Joueur j) {
        java.util.Map<String, Integer> compteur = new java.util.LinkedHashMap<>();
        // Ordre fixe : 5 competences classiques en premier, puis le reste.
        for (String c : new String[]{"assassin", "roi", "erudit", "barbare", "forgeron"}) {
            compteur.put(c, 0);
        }
        for (Carte c : j.getCartes()) {
            // On ne compte que les competences sur cartes NON vertes (les cartes
            // vertes apportent des symboles, pas des competences au sens metier).
            if ("Vert".equalsIgnoreCase(c.getCouleur())) continue;
            for (String comp : c.getSymbolesCompetences()) {
                compteur.merge(comp, 1, Integer::sum);
            }
        }
        StringBuilder sb = new StringBuilder("Compétences : ");
        boolean premier = true;
        for (java.util.Map.Entry<String, Integer> e : compteur.entrySet()) {
            if (e.getValue() == 0) continue;
            if (!premier) sb.append(", ");
            sb.append(e.getKey()).append(" x").append(e.getValue());
            premier = false;
        }
        if (premier) sb.append("-");
        return sb.toString();
    }

    private void mettreAJourPyramide() {
        pyramideBox.getChildren().clear();

        java.util.List<java.util.List<Carte>> lignes = gameManager.getPyramide().getLignes();

        // Fallback si la structure n'est pas definie (tests, etc.)
        if (lignes == null || lignes.isEmpty()) {
            HBox ligne = new HBox(10);
            ligne.setAlignment(Pos.CENTER);
            for (Carte carte : gameManager.getPyramide().getCartesAccessibles()) {
                ligne.getChildren().add(construireVignetteCarte(carte, true));
            }
            pyramideBox.getChildren().add(ligne);
            return;
        }

        // Affichage de la pyramide visuelle : sommet en haut, base en bas.
        // Les lignes sont stockees dans l'ordre [base, ..., sommet] -> on parcourt en sens inverse.
        Image dos = chargerImageDosChapitre(gameManager.getChapitreCourant());
        java.util.List<Carte> presentes = gameManager.getPyramide().getCartes();

        for (int r = lignes.size() - 1; r >= 0; r--) {
            HBox ligneBox = new HBox(14);  // +espacement pour laisser respirer le hover
            ligneBox.setAlignment(Pos.CENTER);
            for (Carte carte : lignes.get(r)) {
                if (!presentes.contains(carte)) {
                    // Carte deja prise : espace vide pour preserver la pyramide
                    ligneBox.getChildren().add(construireVignetteVide());
                } else if (gameManager.getPyramide().estLibre(carte)) {
                    ligneBox.getChildren().add(construireVignetteCarte(carte, true));
                } else {
                    // Couverte : afficher le dos du chapitre
                    ligneBox.getChildren().add(construireVignetteDos(dos));
                }
            }
            pyramideBox.getChildren().add(ligneBox);
        }
    }

    private StackPane construireVignetteDos(Image dos) {
        StackPane pane = new StackPane();
        double w = TAILLE_VIGNETTE_W, h = TAILLE_VIGNETTE_H;
        if (dos != null) {
            ImageView iv = new ImageView(dos);
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            pane.getChildren().add(iv);
        } else {
            Rectangle rect = new Rectangle(w, h);
            rect.setFill(Color.DARKGOLDENROD);
            rect.setStroke(Color.BLACK);
            Text t = new Text("?");
            t.setFill(Color.WHITE);
            t.setFont(Font.font("System", FontWeight.BOLD, 28));
            pane.getChildren().addAll(rect, t);
        }
        return pane;
    }

    private StackPane construireVignetteVide() {
        StackPane pane = new StackPane();
        Rectangle rect = new Rectangle(TAILLE_VIGNETTE_W, TAILLE_VIGNETTE_H);
        rect.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rect);
        return pane;
    }

    private Image chargerImageDosChapitre(int chapitre) {
        String key = "dos_carte_chapitre_" + chapitre;
        if (cacheImages.containsKey(key)) return cacheImages.get(key);
        Image img = null;
        java.io.InputStream in = getClass().getResourceAsStream("/objets/" + key + ".png");
        if (in != null) {
            try {
                img = new Image(in);
                if (img.isError()) img = null;
            } catch (Exception e) {
                img = null;
            }
        }
        cacheImages.put(key, img);
        return img;
    }

    private static final double TAILLE_VIGNETTE_W = 100;
    private static final double TAILLE_VIGNETTE_H = 145;

    private StackPane construireVignetteCarte(Carte carte, boolean cliquable) {
        StackPane pane = new StackPane();

        // Cartes en portrait apres rotation : ratio ~0.68
        double w = TAILLE_VIGNETTE_W;
        double h = TAILLE_VIGNETTE_H;

        Image img = chargerImageCarte(carte);
        if (img != null) {
            // Affichage de l'image scannee
            ImageView iv = new ImageView(img);
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            if (!cliquable) {
                iv.setOpacity(0.5);
            }
            pane.getChildren().add(iv);
        } else {
            // Fallback : rectangle colore + texte (carte sans image)
            Rectangle rect = new Rectangle(w, h);
            Color fond = couleurDe(carte.getCouleur());
            if (!cliquable) {
                fond = fond.deriveColor(0, 0.3, 1, 0.6);
            }
            rect.setFill(fond);
            rect.setStroke(Color.BLACK);

            StringBuilder label = new StringBuilder(carte.getNom());
            label.append("\nCoût: ").append(carte.getCoutOr());
            if (carte.getSymbolesRequis() != null && !carte.getSymbolesRequis().isEmpty()) {
                label.append(" (").append(String.join(", ", carte.getSymbolesRequis())).append(")");
            }
            if (carte.getSymboleAlliance() != null && !carte.getSymboleAlliance().isEmpty()) {
                label.append("\n⚑ ").append(carte.getSymboleAlliance());
            }
            if (carte.getSymbolesCompetences() != null && !carte.getSymbolesCompetences().isEmpty()) {
                label.append("\n◆ ").append(String.join(", ", carte.getSymbolesCompetences()));
            }
            if (carte.getAvanceAnneau() > 0) {
                label.append("\n↯ Anneau +").append(carte.getAvanceAnneau());
            }
            Text text = new Text(label.toString());
            text.setTextAlignment(TextAlignment.CENTER);
            text.setFont(Font.font("System", cliquable ? 11 : 9));

            pane.getChildren().addAll(rect, text);
        }

        // Affichage du cout en surimpression sur l'image (toujours visible)
        if (img != null) {
            Text coutText = new Text(String.valueOf(carte.getCoutOr()));
            coutText.setFont(Font.font("System", FontWeight.BOLD, 16));
            coutText.setFill(Color.WHITE);
            coutText.setStroke(Color.BLACK);
            coutText.setStrokeWidth(0.8);
            StackPane.setAlignment(coutText, Pos.TOP_LEFT);
            StackPane.setMargin(coutText, new Insets(4));
            pane.getChildren().add(coutText);
        }

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

    /** Cache des images deja chargees (evite de relire le PNG a chaque rafraichissement). */
    private final Map<String, Image> cacheImages = new HashMap<>();

    /**
     * Charge l'image d'une carte depuis /cartes/{cheminImage}.png.
     * Retourne null si la carte n'a pas de cheminImage ou si le fichier est absent
     * (auquel cas le code appelant retombera sur le rectangle colore).
     */
    private Image chargerImageCarte(Carte carte) {
        String chemin = carte.getCheminImage();
        if (chemin == null || chemin.isEmpty()) return null;
        if (cacheImages.containsKey(chemin)) return cacheImages.get(chemin);

        Image img = null;
        java.io.InputStream in = getClass().getResourceAsStream("/cartes/" + chemin + ".png");
        if (in != null) {
            try {
                img = new Image(in);
                if (img.isError()) img = null;
            } catch (Exception e) {
                img = null;
            }
        }
        cacheImages.put(chemin, img); // null aussi mis en cache pour eviter re-essais
        return img;
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
            if (carte.getSymbolesCompetences() != null && !carte.getSymbolesCompetences().isEmpty()) eff.append("◆");
            if (carte.getAvanceAnneau() > 0) eff.append("↯");
            if ("Forteresse".equals(carte.getSymboleAlliance())) eff.append("⚑");

            Text t = new Text(eff.toString());
            t.setFont(Font.font("System", FontWeight.BOLD, 10));
            vignette.getChildren().addAll(rect, t);

            // Clic = zoom plein ecran sur la carte
            vignette.setStyle("-fx-cursor: hand;");
            vignette.setOnMouseClicked(e -> afficherCarteZoomee(carte));

            box.getChildren().add(vignette);
        }
    }

    /** Overlay plein ecran montrant le scan de la carte cliquee. Clic ferme l'overlay. */
    private void afficherCarteZoomee(Carte carte) {
        if (rootStack == null) return;
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

        Image img = chargerImageCarte(carte);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setPreserveRatio(true);
            // Taille adaptee a la fenetre : 70% de la hauteur disponible
            iv.fitHeightProperty().bind(rootStack.heightProperty().multiply(0.7));
            overlay.getChildren().add(iv);
        } else {
            // Fallback : afficher le nom et les attributs
            VBox info = new VBox(8);
            info.setAlignment(Pos.CENTER);
            Text titre = new Text(carte.getNom());
            titre.setFill(Color.WHITE);
            titre.setFont(Font.font("Serif", FontWeight.BOLD, 28));
            info.getChildren().add(titre);
            overlay.getChildren().add(info);
        }

        // Clic n'importe ou sur l'overlay = fermer
        overlay.setOnMouseClicked(e -> rootStack.getChildren().remove(overlay));
        rootStack.getChildren().add(overlay);
    }

    private void mettreAJourPiste() {
        if (pisteBox == null) return;
        pisteBox.getChildren().clear();

        Text titre = new Text("Piste de l'Anneau ");
        titre.setFont(Font.font("Serif", FontWeight.BOLD, 16));
        titre.setFill(Color.GOLD);
        pisteBox.getChildren().add(titre);

        int posCom = gameManager.getPisteAnneau().getPositionCommunaute();
        int posNaz = gameManager.getPisteAnneau().getPositionNazguls();

        for (int i = 0; i < TAILLE_PISTE; i++) {
            StackPane case_ = new StackPane();
            Rectangle rect = new Rectangle(40, 40);
            if (i == TAILLE_PISTE - 1) {
                rect.setFill(Color.GOLD);
                rect.setStroke(Color.DARKGOLDENROD);
            } else {
                rect.setFill(Color.WHEAT);
                rect.setStroke(Color.SADDLEBROWN);
            }
            rect.setStrokeWidth(1.5);
            rect.setArcWidth(6);
            rect.setArcHeight(6);
            case_.getChildren().add(rect);

            Text num = new Text(String.valueOf(i));
            num.setFont(Font.font("System", FontWeight.BOLD, 10));
            num.setFill(Color.SADDLEBROWN);
            StackPane.setAlignment(num, Pos.TOP_LEFT);
            StackPane.setMargin(num, new Insets(2, 0, 0, 4));
            case_.getChildren().add(num);

            HBox pions = new HBox(2);
            pions.setAlignment(Pos.CENTER);
            if (i == posCom) {
                Circle pionCom = new Circle(8, Color.DODGERBLUE);
                pionCom.setStroke(Color.WHITE);
                pionCom.setStrokeWidth(2);
                pions.getChildren().add(pionCom);
            }
            if (i == posNaz) {
                Circle pionNaz = new Circle(8, Color.BLACK);
                pionNaz.setStroke(Color.DARKRED);
                pionNaz.setStrokeWidth(2);
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
            VBox carte = new VBox(3);
            carte.setAlignment(Pos.CENTER);
            carte.setPadding(new Insets(8));
            carte.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #f5e9b8, #d4ba6a);"
                    + " -fx-border-color: #8b6f1c; -fx-border-width: 2;"
                    + " -fx-background-radius: 6; -fx-border-radius: 6;");

            Text nom = new Text(hl.getNom());
            nom.setFont(Font.font("Serif", FontWeight.BOLD, 14));
            nom.setFill(Color.web("#3a2a0a"));
            Text cout = new Text(hl.getCoutOr() + " or • " + hl.getCoutForteresses() + " ⚑");
            cout.setFont(Font.font("System", FontWeight.BOLD, 12));
            cout.setFill(Color.web("#5a3a0a"));
            Text effet = new Text(hl.getEffet());
            effet.setFont(Font.font("System", 11));
            effet.setFill(Color.web("#3a2a0a"));

            // Hover effect sur les Hauts-Lieux aussi
            carte.setOnMouseEntered(e -> carte.setEffect(new DropShadow(12, Color.GOLD)));
            carte.setOnMouseExited(e -> carte.setEffect(null));

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
            vide.setFont(Font.font("Serif", FontWeight.NORMAL, 12));
            vide.setFill(Color.LIGHTGRAY);
            hautsLieuxBox.getChildren().add(vide);
        }
    }

    private void mettreAJourEcranFin() {
        if (rootStack == null) return;

        if (gameManager.estPartieTerminee()) {
            // Arrete la musique a la fin de partie (les boutons permettent de la relancer si besoin)
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }

            if (ecranFin == null) {
                ecranFin = new VBox(18);
                ecranFin.setAlignment(Pos.CENTER);
                ecranFin.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

                Text titre = new Text("FIN DE LA PARTIE");
                titre.setFill(Color.WHITE);
                titre.setFont(Font.font("Serif", FontWeight.BOLD, 42));

                Text vainqueur = new Text();
                vainqueur.setFill(Color.GOLD);
                vainqueur.setFont(Font.font("Serif", FontWeight.BOLD, 24));
                vainqueur.setTextAlignment(TextAlignment.CENTER);
                vainqueur.setId("vainqueurText");

                javafx.scene.control.Button rejouer = new javafx.scene.control.Button("Rejouer");
                rejouer.setStyle(
                        "-fx-background-color: #2d6a4f; -fx-text-fill: white;"
                        + " -fx-font-size: 16px; -fx-font-weight: bold;"
                        + " -fx-padding: 10 30; -fx-background-radius: 6;");
                rejouer.setOnAction(e -> {
                    gameManager.reset();
                    if (mediaPlayer != null) mediaPlayer.play(); // relance la musique
                });

                javafx.scene.control.Button quitter = new javafx.scene.control.Button("Quitter");
                quitter.setStyle(
                        "-fx-background-color: #6a2d2d; -fx-text-fill: white;"
                        + " -fx-font-size: 16px; -fx-font-weight: bold;"
                        + " -fx-padding: 10 30; -fx-background-radius: 6;");
                quitter.setOnAction(e -> javafx.application.Platform.exit());

                HBox boutons = new HBox(20, rejouer, quitter);
                boutons.setAlignment(Pos.CENTER);

                ecranFin.getChildren().addAll(titre, vainqueur, boutons);
                rootStack.getChildren().add(ecranFin);
            }
            // Mettre à jour le texte du vainqueur
            for (javafx.scene.Node n : ecranFin.getChildren()) {
                if ("vainqueurText".equals(n.getId()) && n instanceof Text) {
                    String v = gameManager.getVainqueurFinal();
                    ((Text) n).setText(v != null ? v : "Partie terminée");
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
