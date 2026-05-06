package com.AHMED_ZAID_ERRAFII.entities;

import javafx.animation.*;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.util.Random;

public class WeaponSystem {

    // Generate a soft "Puff" texture once to reuse for all smoke particles
    private static final Image SMOKE_TEXTURE = createSmokeTexture();

    public static void fireMissile(Point3D target, Group worldRoot, Group planetGroup, double camZ, Runnable onHit) {
        Random rand = new Random();

        // 1. IMPROVED MISSILE MODEL
        Group missile = new Group();
        Cylinder body = new Cylinder(1.5, 30);
        body.setMaterial(new PhongMaterial(Color.web("#1a1a1a"))); // Dark metal

        Sphere nose = new Sphere(2.2);
        PhongMaterial noseMat = new PhongMaterial(Color.BLACK);
        nose.setMaterial(noseMat);
        nose.setTranslateY(-15);
        nose.setScaleY(3.0);

        // Glowing Thruster
        Sphere flame = new Sphere(2.6);
        PhongMaterial flameMat = new PhongMaterial(Color.WHITE);
        flameMat.setSpecularColor(Color.ORANGE);
        flame.setMaterial(flameMat);
        flame.setTranslateY(16);

        missile.getChildren().addAll(body, nose, flame);

        // 2. POSITIONING
        Point3D startPos = new Point3D(0, 0, camZ);
        missile.setTranslateX(startPos.getX());
        missile.setTranslateY(startPos.getY());
        missile.setTranslateZ(startPos.getZ());

        // Rotate missile to point at the target
        Point3D direction = target.subtract(startPos).normalize();
        Point3D axis = new Point3D(0, 1, 0).crossProduct(direction);
        double angle = Math.acos(new Point3D(0, 1, 0).dotProduct(direction));
        missile.getTransforms().add(new Rotate(Math.toDegrees(angle), axis));

        worldRoot.getChildren().add(missile);

        // 3. REALISTIC SMOKE TRAIL[cite: 1]
        Timeline smokeEmitter = new Timeline(new KeyFrame(Duration.millis(12), e -> {
            // Spawn 3 particles per tick for a "thick" plume[cite: 1]
            for (int i = 0; i < 3; i++) {
                Sphere puff = new Sphere(rand.nextDouble() * 5 + 3);

                // Use the soft texture instead of a solid color[cite: 1]
                PhongMaterial puffMat = new PhongMaterial();
                puffMat.setDiffuseMap(SMOKE_TEXTURE);
                puff.setMaterial(puffMat);

                // Jitter to make the trail look volumetric[cite: 1]
                puff.setTranslateX(missile.getTranslateX() + (rand.nextDouble() - 0.5) * 6);
                puff.setTranslateY(missile.getTranslateY() + (rand.nextDouble() - 0.5) * 6);
                puff.setTranslateZ(missile.getTranslateZ() + (rand.nextDouble() - 0.5) * 6);

                worldRoot.getChildren().add(puff);

                // Dissipation logic[cite: 1]
                FadeTransition ft = new FadeTransition(Duration.millis(1200), puff);
                ft.setToValue(0);

                ScaleTransition st = new ScaleTransition(Duration.millis(1200), puff);
                st.setToX(4); st.setToY(4); st.setToZ(4); // Grow the smoke[cite: 1]

                ParallelTransition pt = new ParallelTransition(ft, st);
                pt.setOnFinished(ev -> worldRoot.getChildren().remove(puff));
                pt.play();
            }
        }));
        smokeEmitter.setCycleCount(Timeline.INDEFINITE);
        smokeEmitter.play();

        // 4. ANIMATION[cite: 1]
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), missile);
        tt.setToX(target.getX());
        tt.setToY(target.getY());
        tt.setToZ(target.getZ());

        tt.setOnFinished(e -> {
            smokeEmitter.stop();
            worldRoot.getChildren().remove(missile);
            onHit.run();
        });
        tt.play();
    }

    // Creates a soft, radial gradient texture so the smoke isn't a hard circle[cite: 1]
    private static Image createSmokeTexture() {
        int size = 64;
        WritableImage img = new WritableImage(size, size);
        var writer = img.getPixelWriter();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dx = x - size / 2.0;
                double dy = y - size / 2.0;
                double dist = Math.sqrt(dx * dx + dy * dy) / (size / 2.0);
                double alpha = Math.max(0, 1.0 - dist); // Soft edges[cite: 1]
                writer.setColor(x, y, Color.rgb(220, 220, 220, alpha * 0.3));
            }
        }
        return img;
    }
}