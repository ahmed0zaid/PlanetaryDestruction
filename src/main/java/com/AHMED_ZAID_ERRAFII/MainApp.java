package com.AHMED_ZAID_ERRAFII;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.util.Random;

public class MainApp extends Application {

    private double lastMouseX, lastMouseY;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private double cameraZ = -800;

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();

        //pl
        Sphere planet = new Sphere(200);
        PhongMaterial planetMaterial = new PhongMaterial();
        planetMaterial.setDiffuseMap(new Image("https://raw.githubusercontent.com/mrdoob/three.js/master/examples/textures/planets/earth_atmos_2048.jpg"));
        planet.setMaterial(planetMaterial);
        planet.getTransforms().addAll(rotateX, rotateY);
        root.getChildren().add(planet);

        addStars(root, 2000);

        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-1000);
        light.setTranslateX(-500);
        root.getChildren().add(light);
        root.getChildren().add(new AmbientLight(Color.rgb(30, 30, 30)));

        Scene scene = new Scene(root, 1200, 800, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(cameraZ);
        scene.setCamera(camera);

        handleMouseEvents(scene, planet, camera);

        primaryStage.setTitle("Planetary-Destruction GAME by AHMED-ZAID ERRAFII");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addStars(Group root, int count) {
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            Sphere star = new Sphere(rand.nextDouble() * 2.0 + 1.0);

            PhongMaterial starMaterial = new PhongMaterial();
            starMaterial.setDiffuseColor(Color.WHITE);
            starMaterial.setSpecularColor(Color.WHITE);
            star.setMaterial(starMaterial);

            double radius = 1500 + rand.nextDouble() * 2500;
            double theta = 2 * Math.PI * rand.nextDouble();
            double phi = Math.acos(2 * rand.nextDouble() - 1);

            star.setTranslateX(radius * Math.sin(phi) * Math.cos(theta));
            star.setTranslateY(radius * Math.sin(phi) * Math.sin(theta));
            star.setTranslateZ(radius * Math.cos(phi));

            root.getChildren().add(star);
        }
    }

    private void handleMouseEvents(Scene scene, Sphere planet, PerspectiveCamera camera) {
        scene.setOnMousePressed(e -> {
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

        scene.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - lastMouseX;
            double deltaY = e.getSceneY() - lastMouseY;

            rotateY.setAngle(rotateY.getAngle() + deltaX * 0.2);
            rotateX.setAngle(rotateX.getAngle() - deltaY * 0.2);

            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

        scene.addEventHandler(ScrollEvent.SCROLL, e -> {
            cameraZ += e.getDeltaY() * 2;
            camera.setTranslateZ(cameraZ);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}