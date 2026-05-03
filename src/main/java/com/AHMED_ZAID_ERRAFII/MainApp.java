package com.AHMED_ZAID_ERRAFII;

import com.AHMED_ZAID_ERRAFII.entities.SpaceEnvironment;
import com.AHMED_ZAID_ERRAFII.logic.Simulation;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Simulation sim = new Simulation();
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS); // Only Horizontal Rotation
    private double lastX;

    @Override
    public void start(Stage stage) {
        // We pass only the Y rotation now
        SpaceEnvironment env = new SpaceEnvironment(rotateY);

        SubScene sub3D = new SubScene(env.root, 1200, 800, true, SceneAntialiasing.BALANCED);
        sub3D.setFill(Color.BLACK);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(5000);
        camera.setTranslateZ(-800); // Fixed Distance
        sub3D.setCamera(camera);

        Label ui = new Label("POPULATION: " + sim.population);
        ui.setTextFill(Color.RED);
        ui.setStyle("-fx-font-size: 35px; -fx-font-weight: bold;");

        StackPane layout = new StackPane(sub3D, ui);
        StackPane.setAlignment(ui, javafx.geometry.Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 1200, 800);

        // Logic: Hit detection
        sub3D.setOnMouseClicked(e -> {
            Node hit = e.getPickResult().getIntersectedNode();
            if (hit == env.planet) {
                env.handleImpact(e.getPickResult().getIntersectedPoint());
                sim.handleDamage();
                ui.setText("POPULATION: " + sim.population);
            }
        });

        // Simplified Controls: Only Left/Right dragging
        sub3D.setOnMousePressed(e -> lastX = e.getSceneX());
        sub3D.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - lastX;
            rotateY.setAngle(rotateY.getAngle() + deltaX * 0.3);
            lastX = e.getSceneX();
        });

        stage.setTitle("Planetary Destruction - Stable Edition");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}