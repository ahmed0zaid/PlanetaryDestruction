package com.AHMED_ZAID_ERRAFII.entities;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import java.util.Random;

public class PlanetModel {
    public Group group = new Group();
    private Sphere globe;
    private WritableImage planetSkin;
    private Image detectionMap;

    public PlanetModel(Rotate rotateY) {
        // 1. Load high-quality Earth texture from the web[cite: 3]
        Image base = new Image("https://raw.githubusercontent.com/mrdoob/three.js/master/examples/textures/planets/earth_atmos_2048.jpg", true);

        // 2. Load the hidden detection map from local resources for sound logic
        // This image should have Poland in Red and Morocco in Green
        try {
            detectionMap = new Image(getClass().getResourceAsStream("/com/AHMED_ZAID_ERRAFII/detection_map.png"));
        } catch (Exception e) {
            System.err.println("Detection map not found: " + e.getMessage());
        }

        // 3. Initialize the Globe[cite: 3]
        globe = new Sphere(200);
        PhongMaterial mat = new PhongMaterial();
        globe.setMaterial(mat);

        // 4. Set up a listener to apply the texture once it finishes downloading[cite: 3]
        base.progressProperty().addListener((obs, old, progress) -> {
            if (progress.doubleValue() == 1.0) {
                // Create a WritableImage so we can draw craters on it later[cite: 3]
                planetSkin = new WritableImage(base.getPixelReader(), (int)base.getWidth(), (int)base.getHeight());
                mat.setDiffuseMap(planetSkin);
            }
        });

        group.getChildren().add(globe);
        group.getTransforms().add(rotateY);

        // 5. Add the classic starfield background[cite: 3]
        addStars(group, 1500);
    }

    /**
     * Identifies the country based on the color of the detection map at specific UV coordinates.
     */
    public String getCountryAt(Point2D uv) {
        if (detectionMap == null || uv == null) return "OTHER";

        PixelReader reader = detectionMap.getPixelReader();
        int x = (int) (uv.getX() * (detectionMap.getWidth() - 1));
        int y = (int) (uv.getY() * (detectionMap.getHeight() - 1));

        Color color = reader.getColor(x, y);

        // Logic check: Red = Poland, Green = Morocco
        if (color.getRed() > 0.8 && color.getGreen() < 0.2) return "POLAND";
        if (color.getGreen() > 0.8 && color.getRed() < 0.2) return "MOROCCO";

        return "OTHER";
    }

    /**
     * Generates a 3D field of stars surrounding the planet[cite: 3].
     */
    private void addStars(Group root, int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Sphere star = new Sphere(rand.nextDouble() * 1.5 + 0.5);
            PhongMaterial starMat = new PhongMaterial(Color.WHITE);
            star.setMaterial(starMat);

            // Random spherical coordinates[cite: 3]
            double radius = 1200 + rand.nextDouble() * 1000;
            double theta = 2 * Math.PI * rand.nextDouble();
            double phi = Math.acos(2 * rand.nextDouble() - 1);

            star.setTranslateX(radius * Math.sin(phi) * Math.cos(theta));
            star.setTranslateY(radius * Math.sin(phi) * Math.sin(theta));
            star.setTranslateZ(radius * Math.cos(phi));
            root.getChildren().add(star);
        }
    }

    public Sphere getGlobe() {
        return globe;
    }

    /**
     * Paints a permanent "lava crater" onto the Earth's texture skin[cite: 3].
     * Accuracy is maintained during rotation because we modify the texture itself[cite: 3].
     */
    public void applyTextureDamage(Point2D uv) {
        if (planetSkin == null || uv == null) return;

        PixelWriter writer = planetSkin.getPixelWriter();
        double width = planetSkin.getWidth();
        double height = planetSkin.getHeight();
        int centerX = (int) (uv.getX() * width);
        int centerY = (int) (uv.getY() * height);
        int radius = 35;

        for (int y = -radius; y < radius; y++) {
            for (int x = -radius; x < radius; x++) {
                if (x * x + y * y <= radius * radius) {
                    // Modulo ensures the paint wraps correctly around the texture edges[cite: 3]
                    int px = (int)((centerX + x + width) % width);
                    int py = (int)((centerY + y + height) % height);

                    double dist = Math.sqrt(x * x + y * y) / radius;

                    // Create a "Cracked Lava" noise effect[cite: 3]
                    if (Math.random() > dist * 0.8) {
                        writer.setColor(px, py, Color.rgb(255, 120, 0)); // Bright Lava Core
                    } else {
                        writer.setColor(px, py, Color.rgb(50, 10, 0));  // Dark Charred Ring
                    }
                }
            }
        }
    }
}