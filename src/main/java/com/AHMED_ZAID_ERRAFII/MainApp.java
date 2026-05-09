package com.AHMED_ZAID_ERRAFII;

import com.AHMED_ZAID_ERRAFII.entities.PlanetModel;
import com.AHMED_ZAID_ERRAFII.entities.WeaponSystem;
import com.AHMED_ZAID_ERRAFII.logic.Simulation;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class MainApp extends Application {
    private Simulation sim = new Simulation();
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private double lastX;
    private final double ZOOM_OUT_Z = -1200;

    private AudioClip launchSound, explosionSound, polandSound, moroccoSound;
    private MediaPlayer backgroundMusic;

    @Override
    public void start(Stage stage) {
        Runnable startGameLogic = () -> {
            initSounds(); // Initialize sounds
            setupGameScene(stage); // Move your existing 3D/HUD setup into this method
        };


        // Show the Menu first
        StartMenu menu = new StartMenu(stage, startGameLogic);
        menu.show();

    }

    private void setupGameScene(Stage stage) {

        initSounds();

        PlanetModel planet = new PlanetModel(rotateY);
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-1500);

        Group worldRoot = new Group(planet.group, light, new AmbientLight(Color.rgb(50, 50, 50)));

        SubScene sub3D = new SubScene(worldRoot, 1200, 800, true, SceneAntialiasing.BALANCED);
        sub3D.setFill(Color.BLACK);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1); camera.setFarClip(10000.0);
        camera.setTranslateZ(ZOOM_OUT_Z);
        sub3D.setCamera(camera);

        // --- HUD ELEMENTS ---
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("logo.png")));
        logo.setFitWidth(200);
        logo.setPreserveRatio(true);

// 2. Population Labels (Top-Left)
        Label totalPop = new Label("TOTAL POPULATION: " + sim.totalPopulation);
        totalPop.setStyle("-fx-text-fill: green; -fx-font-size: 30px; -fx-font-weight: bold;");
        Label currentPop = new Label("CURRENT POPULATION: " + sim.currentPopulation);
        currentPop.setStyle("-fx-text-fill: red; -fx-font-size: 30px; -fx-font-weight: bold;");

        VBox topLeftLabels = new VBox(5, totalPop, currentPop);
        topLeftLabels.setPadding(new Insets(20));
        topLeftLabels.setMouseTransparent(true);

// 3. Clean Button (Bottom-Left)
        Button cleanBtn = new Button("CLEAN EARTH");
        cleanBtn.setStyle("-fx-font-size: 20px; -fx-base: #3a5a81; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-border-color: black; -fx-border-width: 3px;");
        cleanBtn.setOnAction(e -> {
            planet.resetTexture();
            sim.currentPopulation = sim.totalPopulation;
            currentPop.setText("CURRENT POPULATION: " + sim.currentPopulation);
        });

// Wrap the button in a HBox or VBox to add padding at the bottom
        VBox bottomLeftContainer = new VBox(cleanBtn);
        bottomLeftContainer.setPadding(new Insets(20));
        bottomLeftContainer.setPickOnBounds(false);
        bottomLeftContainer.setAlignment(Pos.BOTTOM_LEFT);

// 4. ORCHESTRATE LAYOUT IN STACKPANE
        StackPane root = new StackPane();
        root.getChildren().addAll(sub3D, topLeftLabels, logo, bottomLeftContainer);

// Align the elements
        StackPane.setAlignment(topLeftLabels, Pos.TOP_LEFT);
        StackPane.setAlignment(logo, Pos.TOP_RIGHT);
        StackPane.setAlignment(bottomLeftContainer, Pos.BOTTOM_LEFT);

// Set logo padding
        StackPane.setMargin(logo, new Insets(20));

        stage.setScene(new Scene(root, 1200, 800));

        sub3D.setOnMouseClicked(e -> {
            var res = e.getPickResult();
            if (res.getIntersectedNode() == planet.getGlobe()) {
                // 1. PLAY LAUNCH IMMEDIATELY
                if (launchSound != null) launchSound.play();

                Point3D worldHit = res.getIntersectedPoint();
                Point2D uv = res.getIntersectedTexCoord();

                WeaponSystem.fireMissile(worldHit, worldRoot, planet.group, ZOOM_OUT_Z, () -> {
                    // 2. IMPACT LOGIC (AFTER 1 SECOND)
                    planet.applyTextureDamage(uv);
                    sim.registerHit();
                    currentPop.setText("CURRENT POPULATION: " + sim.currentPopulation);

                    // 3. ALWAYS PLAY EXPLOSION
                    if (explosionSound != null) explosionSound.play();

                    // 4. CHECK FOR SPECIAL SOUNDS (LAYERED)
                    String country = planet.getCountryAt(uv).toUpperCase().trim();
                    if (country.equals("POLAND") && polandSound != null) polandSound.play();
                    if (country.equals("MOROCCO") && moroccoSound != null) moroccoSound.play();
                });
            }
        });

        sub3D.setOnMousePressed(e -> lastX = e.getSceneX());
        sub3D.setOnMouseDragged(e -> {
            rotateY.setAngle(rotateY.getAngle() + (e.getSceneX() - lastX) * 0.5);
            lastX = e.getSceneX();
        });


        stage.setTitle("Planetary Destruction GAME");
        stage.show();
    }

    private void initSounds() {
        try {
            launchSound = loadEffect("launch.wav");
            explosionSound = loadEffect("explosion.wav");
            polandSound = loadEffect("poland.wav");
            moroccoSound = loadEffect("morocco.wav");

            URL musicURL = getClass().getResource("/com/AHMED_ZAID_ERRAFII/space.wav");
            if (musicURL != null) {
                backgroundMusic = new MediaPlayer(new Media(musicURL.toExternalForm()));
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.setVolume(0.4);
                backgroundMusic.play();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private AudioClip loadEffect(String name) {
        URL url = getClass().getResource("/com/AHMED_ZAID_ERRAFII/" + name);
        return (url == null) ? null : new AudioClip(url.toExternalForm());
    }

    public static void main(String[] args) { launch(args); }
}