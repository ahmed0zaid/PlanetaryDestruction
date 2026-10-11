# Planetary Destruction

Interactive 3D simulation game built with Java and JavaFX for the Computer Graphics course at Politechnika Świętokrzyska.

The application renders a 3D Earth model inside a procedural starfield. Players can freely rotate the planet, target coordinates, launch missiles with animated trails, and see dynamic texture damage mapped directly to the globe's surface.

---

## Features

- **JavaFX 3D Scene**: Custom rendering using `SubScene`, `PerspectiveCamera`, and ambient/point lighting without external game engines.
- **Procedural Starfield**: Background stars generated across random spherical coordinates.
- **Dynamic Cratering**: Intersected 3D pick points are translated to 2D UV texture coordinates, modifying diffuse map pixels in memory via `PixelWriter` to render molten impact zones.
- **Regional Detection & Audio**: Samples an internal political detection map to identify impacted countries (e.g., Poland, Morocco) and trigger layered, region-specific voice lines alongside the explosion sound effect.
- **Volumetric Smoke Trails**: Projectile trajectory logic with parallel fade and scale transitions on particle meshes.
- **State Tracking**: Real-time population tracking that decreases with every strike, with a reset option to clear damage and restore the original texture.

---

## Project Structure

src/main/
├── java/com/AHMED_ZAID_ERRAFII/
│   ├── MainApp.java            # App entry point, 3D setup, HUD, click handlers
│   ├── StartMenu.java          # Main menu, procedural star backdrop, UI layout
│   ├── entities/
│   │   ├── PlanetModel.java    # 3D Earth sphere, UV damage logic, hit detection
│   │   └── WeaponSystem.java   # Missile mesh, trajectory math, smoke emitter
│   └── logic/
│       └── Simulation.java     # Population counters and hit processing
└── resources/com/AHMED_ZAID_ERRAFII/
├── detection_map.png       # Color-coded political map for hit checks
├── logo.png                # Game logo
├── uni_logo.png            # Politechnika Świętokrzyska logo
└── *.wav                   # Sound effects and soundtrack


---

## Controls

- **Left Mouse Click + Drag**: Rotate the Earth on its Y-axis.
- **Left Mouse Click (on Earth)**: Target coordinates and launch an orbital missile.
- **CLEAN EARTH**: Resets the planet's texture and population count.

---

## Requirements & Running

- JDK 17 or higher
- Maven

Run from the command line:

```bash
mvn clean compile javafx:run
Or open the root folder in IntelliJ IDEA and run MainApp.java.


Scroll to the bottom of the page, keep **"Commit directly to the `master` branch"*
