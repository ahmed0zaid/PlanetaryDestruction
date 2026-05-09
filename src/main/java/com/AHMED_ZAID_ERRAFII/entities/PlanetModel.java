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
    private Image baseImage;
    private Image detectionMap;

    public PlanetModel(Rotate rotateY) {
        // 1. Store original texture reference
        baseImage = new Image("https://raw.githubusercontent.com/mrdoob/three.js/master/examples/textures/planets/earth_atmos_2048.jpg", true);

        // 2. Load the detection map for country logic
        try {
            detectionMap = new Image(getClass().getResourceAsStream("/com/AHMED_ZAID_ERRAFII/detection_map.png"));
        } catch (Exception e) {
            System.err.println("Detection map not found: " + e.getMessage());
        }

        // 3. Initialize Globe
        globe = new Sphere(200);
        PhongMaterial mat = new PhongMaterial();
        globe.setMaterial(mat);

        baseImage.progressProperty().addListener((obs, old, progress) -> {
            if (progress.doubleValue() == 1.0) {
                // Initialize the writable skin from the clean base image
                planetSkin = new WritableImage(baseImage.getPixelReader(), (int)baseImage.getWidth(), (int)baseImage.getHeight());
                mat.setDiffuseMap(planetSkin);
            }
        });

        group.getChildren().add(globe);
        group.getTransforms().add(rotateY);
        addStars(group, 1500); //
    }

    /**
     * Restores the planet's texture to its original clean state.
     */
    public void resetTexture() {
        if (planetSkin != null && baseImage != null) {
            planetSkin.getPixelWriter().setPixels(0, 0,
                    (int)baseImage.getWidth(), (int)baseImage.getHeight(),
                    baseImage.getPixelReader(), 0, 0);
        }
    }

    public String getCountryAt(Point2D uv) {
        if (detectionMap == null || uv == null) return "OTHER";
        PixelReader reader = detectionMap.getPixelReader();
        int x = (int) (uv.getX() * (detectionMap.getWidth() - 1));
        int y = (int) (uv.getY() * (detectionMap.getHeight() - 1));

        Color color = reader.getColor(x, y);
        if (color.getRed() > 0.8 && color.getGreen() < 0.2) return "POLAND";
        if (color.getGreen() > 0.8 && color.getRed() < 0.2) return "MOROCCO";
        return "OTHER";
    }

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
                    int px = (int)((centerX + x + width) % width);
                    int py = (int)((centerY + y + height) % height);
                    double dist = Math.sqrt(x * x + y * y) / radius;
                    if (Math.random() > dist * 0.8) {
                        writer.setColor(px, py, Color.rgb(255, 120, 0)); // Lava core
                    } else {
                        writer.setColor(px, py, Color.rgb(50, 10, 0));  // Charred ring
                    }
                }
            }
        }
    }

    private void addStars(Group root, int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Sphere star = new Sphere(rand.nextDouble() * 1.5 + 0.5);
            star.setMaterial(new PhongMaterial(Color.WHITE));
            double radius = 1200 + rand.nextDouble() * 1000;
            double theta = 2 * Math.PI * rand.nextDouble();
            double phi = Math.acos(2 * rand.nextDouble() - 1);
            star.setTranslateX(radius * Math.sin(phi) * Math.cos(theta));
            star.setTranslateY(radius * Math.sin(phi) * Math.sin(theta));
            star.setTranslateZ(radius * Math.cos(phi));
            root.getChildren().add(star);
        }
    }

    public Sphere getGlobe() { return globe; }
}