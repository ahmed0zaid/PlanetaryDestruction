package com.AHMED_ZAID_ERRAFII;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import java.util.Random;

public class StartMenu {
    private final Stage stage;
    private final Runnable onStartGame;

    public StartMenu(Stage stage, Runnable onStartGame) {
        this.stage = stage;
        this.onStartGame = onStartGame;
    }

    public void show() {
        // 1. BACKGROUND STARS (Adjusted for visibility)
        Group starGroup = new Group();
        addStars(starGroup, 1500); // Increased count

        SubScene starBackdrop = new SubScene(starGroup, 1200, 800, true, SceneAntialiasing.BALANCED);
        starBackdrop.setFill(Color.BLACK);

        // Move camera closer to the stars
        PerspectiveCamera menuCamera = new PerspectiveCamera(true);
        menuCamera.setNearClip(0.1);
        menuCamera.setFarClip(2000.0);
        menuCamera.setTranslateZ(-500);
        starBackdrop.setCamera(menuCamera);

        // 2. GAME LOGO (Now Smaller: 300)
        ImageView gameLogo = new ImageView(new Image(getClass().getResourceAsStream("logo.png")));
        gameLogo.setFitWidth(350);
        gameLogo.setPreserveRatio(true);

        // 3. UNIVERSITY LOGO (Now Bigger: 250)
        ImageView uniLogo = new ImageView(new Image(getClass().getResourceAsStream("uni_logo.png")));
        uniLogo.setFitWidth(250);
        uniLogo.setPreserveRatio(true);

        // 4. BUTTONS (Smaller Padding and Font)
        Button startBtn = new Button("START MISSION");
        styleButton(startBtn, "#3a5a81");
        startBtn.setOnAction(e -> onStartGame.run());

        Button exitBtn = new Button("EXIT GAME");
        styleButton(exitBtn, "#c0392b");
        exitBtn.setOnAction(e -> stage.close());

        // LAYOUT
        VBox menuBox = new VBox(20, gameLogo, startBtn, exitBtn);
        menuBox.setAlignment(Pos.CENTER);

        // The backdrop must be the first element in StackPane
        StackPane root = new StackPane(starBackdrop, menuBox, uniLogo);

        // Position University Logo Top-Left
        StackPane.setAlignment(uniLogo, Pos.TOP_LEFT);
        StackPane.setMargin(uniLogo, new Insets(30));

        Scene menuScene = new Scene(root, 1200, 800);
        stage.setScene(menuScene);
        stage.show();
    }

    private void addStars(Group root, int count) {
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            // Randomly vary star size
            Sphere s = new Sphere(r.nextDouble() * 1.5 + 0.5);
            s.setMaterial(new PhongMaterial(Color.WHITE));

            // Adjusted distance so they are visible within the camera clip
            double dist = 400 + r.nextDouble() * 600;
            double t = 2 * Math.PI * r.nextDouble();
            double p = Math.acos(2 * r.nextDouble() - 1);

            s.setTranslateX(dist * Math.sin(p) * Math.cos(t));
            s.setTranslateY(dist * Math.sin(p) * Math.sin(t));
            s.setTranslateZ(dist * Math.cos(p));
            root.getChildren().add(s);
        }
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-font-size: 20px; " + // Smaller font (was 24)
                "-fx-base: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 30 8 30; " + // Smaller padding (was 10 40)
                "-fx-background-radius: 12; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 12;");
    }
}