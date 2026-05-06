package com.AHMED_ZAID_ERRAFII;

import com.AHMED_ZAID_ERRAFII.entities.PlanetModel;
import com.AHMED_ZAID_ERRAFII.entities.WeaponSystem;
import com.AHMED_ZAID_ERRAFII.logic.Simulation;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.net.URL;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MainApp extends Application {
    private Simulation sim = new Simulation();
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private double lastX;
    private final double ZOOM_OUT_Z = -1200;

    // Sound Effects
    private AudioClip launchSound;
    private AudioClip explosionSound;
    private AudioClip polandSound;
    private AudioClip moroccoSound;
    private MediaPlayer backgroundMusic;

    @Override
    public void start(Stage stage) {
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

        Label totalPop = new Label("TOTAL POPULATION: " + sim.totalPopulation);
        totalPop.setStyle("-fx-text-fill: green; -fx-font-size: 28px; -fx-font-weight: bold;");

        Label currentPop = new Label("CURRENT POPULATION: " + sim.currentPopulation);
        currentPop.setStyle("-fx-text-fill: red; -fx-font-size: 28px; -fx-font-weight: bold;");

        VBox hud = new VBox(5, totalPop, currentPop);
        hud.setPadding(new Insets(20));
        hud.setMouseTransparent(true);

        sub3D.setOnMouseClicked(e -> {
            var res = e.getPickResult();
            if (res.getIntersectedNode() == planet.getGlobe()) {
                Point3D worldHit = res.getIntersectedPoint();
                Point2D uv = res.getIntersectedTexCoord();

                // Play launch sound immediately
                if (launchSound != null) launchSound.play();

                WeaponSystem.fireMissile(worldHit, worldRoot, planet.group, ZOOM_OUT_Z, () -> {
                    planet.applyTextureDamage(uv);
                    sim.registerHit();
                    currentPop.setText("CURRENT POPULATION: " + sim.currentPopulation);

                    // Determine sound based on location
                    String country = planet.getCountryAt(uv);
                    switch (country) {
                        case "POLAND" -> polandSound.play();
                        case "MOROCCO" -> moroccoSound.play();
                        default -> explosionSound.play();
                    }
                });
            }
        });

        sub3D.setOnMousePressed(e -> lastX = e.getSceneX());
        sub3D.setOnMouseDragged(e -> {
            rotateY.setAngle(rotateY.getAngle() + (e.getSceneX() - lastX) * 0.5);
            lastX = e.getSceneX();
        });

        stage.setScene(new Scene(new StackPane(sub3D, hud), 1200, 800));
        stage.setTitle("Planetary Destruction GAME - Final Layout");
        stage.show();
    }

    private void initSounds() {
        try {
            // Load sound effects
            launchSound = new AudioClip(getClass().getResource("launch.wav").toExternalForm());
            explosionSound = new AudioClip(getClass().getResource("explosion.wav").toExternalForm());
            polandSound = new AudioClip(getClass().getResource("poland.wav").toExternalForm());
            moroccoSound = new AudioClip(getClass().getResource("morocco.wav").toExternalForm());

            // Load background ambiance
            Media media = new Media(getClass().getResource("space_ambiance.mp3").toExternalForm());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loop
            backgroundMusic.setVolume(0.4);
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Audio files missing in resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) { launch(args); }
}