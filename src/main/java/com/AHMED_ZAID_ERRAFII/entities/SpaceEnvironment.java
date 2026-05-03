package com.AHMED_ZAID_ERRAFII.entities;

import javafx.animation.*;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.util.Random;

public class SpaceEnvironment {
    public Group root = new Group();
    public Group planetGroup = new Group();
    public Sphere planet = new Sphere(200);

    public SpaceEnvironment(Rotate rotateY) {
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(new Image("https://raw.githubusercontent.com/mrdoob/three.js/master/examples/textures/planets/earth_atmos_2048.jpg"));
        planet.setMaterial(mat);

        planetGroup.getChildren().add(planet);
        planetGroup.getTransforms().add(rotateY); // Simplified Transform

        root.getChildren().add(planetGroup);
        setupLighting();
    }

    public void handleImpact(Point3D worldPoint) {
        Point3D localPoint = planetGroup.sceneToLocal(worldPoint);
        Random rand = new Random();

        // Visual "Burn" Mark (Flat circle)
        Sphere mark = new Sphere(6);
        mark.setMaterial(new PhongMaterial(Color.web("#330000")));
        mark.setTranslateX(localPoint.getX());
        mark.setTranslateY(localPoint.getY());
        mark.setTranslateZ(localPoint.getZ());
        planetGroup.getChildren().add(mark);

        // Explosion bits
        for (int i = 0; i < 10; i++) {
            Sphere bit = new Sphere(rand.nextDouble() * 3);
            bit.setMaterial(new PhongMaterial(Color.ORANGE));
            bit.setTranslateX(localPoint.getX());
            bit.setTranslateY(localPoint.getY());
            bit.setTranslateZ(localPoint.getZ());
            planetGroup.getChildren().add(bit);

            Timeline t = new Timeline(new KeyFrame(Duration.millis(400),
                    new KeyValue(bit.translateXProperty(), localPoint.getX() * 1.2 + (rand.nextDouble()-0.5)*50),
                    new KeyValue(bit.translateYProperty(), localPoint.getY() * 1.2 + (rand.nextDouble()-0.5)*50),
                    new KeyValue(bit.scaleXProperty(), 0)
            ));
            t.setOnFinished(e -> planetGroup.getChildren().remove(bit));
            t.play();
        }
    }

    private void setupLighting() {
        root.getChildren().add(new PointLight(Color.WHITE));
        root.getChildren().add(new AmbientLight(Color.rgb(50, 50, 50)));
    }
}