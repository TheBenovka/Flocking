package mygame;

//Louis: Imports fuer Background
import Background.PlanetEarth;
import Background.randomGroßePlanets;
import Background.PlanetSystemRinge;
import Background.Planets;
import Background.SkyBox;
import Background.randomPlanets;
import Background.SpaceStation;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import java.util.Random;

//Imports fuer den Fullscreen
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.DisplayMode;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 */

/**
 * TASKS!
 * TODO: Optimizaition: -> Search with OcTree, k-d Tree, Hashing 
 *                      -> Multithreading
 *                      -> Make the code with less Objects
 * TODO: Aesthetic  -> Boid Models or Materials
 *                  -> Scene Background
 *                  -> Special Effects
 * TODO: User Interface -> CPU Stats
 *                      -> Time since start
 *                      -> Boids count
 *                      -> GPU (maybe)
 * @author beni
 */

public class Main extends SimpleApplication {
    
    private Flock flock;

    public static final int boidCount = 700;

    
    /**
    * @author Louis
    * Quellen sind jeweils unten und in den Klassen angegeben
    */
    private Spatial objModel;
    private Spatial objModel2; //legt die Anzahl der random gespawnen Planeten fest
    private Quaternion rotation;
    private final float rotationSpeed = 1.0f; //legt Rotations Geschwindigkeit der Planeten fest
    private static final boolean fullscreen = true; //legt Vollbildmodus fest
    
    private Planets p;
    private PlanetSystemRinge pSystemRinge;
    private SkyBox skyBox;
    private randomPlanets randomPlanets;
    private randomGroßePlanets randomGroßePlanets;
    private PlanetEarth pEarth;
    private SpaceStation sStation;

    
    public static void main(String[] args) {
        Main app = new Main();
        
        if(fullscreen){
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            DisplayMode displayMode = device.getDisplayMode();

            int screenWidth = displayMode.getWidth();
            int screenHeight = displayMode.getHeight();

            AppSettings settings = new AppSettings(true);
            settings.put("Width", screenWidth);
            settings.put("Height", screenHeight);
            settings.setFullscreen(true);

            app.setSettings(settings);
            app.setShowSettings(false);
        }       
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        rootNode.addLight(sun);
        
        Spatial raumschiff = assetManager.loadModel("Spaceship/raumschiff.j3o");
        Material raumschiffMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // the surface material for the geometric boid model.
        raumschiffMaterial.setColor("Color", ColorRGBA.Magenta);
        float scale = 0.05f;
        raumschiff.scale(scale);
        Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex0 = assetManager.loadTexture("Textures/metal.jpg");
        mat0.setTexture("ColorMap", tex0);

        // instantiation of the flock
        flock = new Flock(rootNode, boidCount, raumschiff, mat0 ); //mesh 
        // camera rotation is controlled via mouse movement while the position is controlled via wasd-keys
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(30);
        
        //Louis
        p = new Planets(assetManager, rootNode);
        pSystemRinge = new PlanetSystemRinge(assetManager, rootNode);
        skyBox = new SkyBox(assetManager, rootNode);
        randomPlanets = new randomPlanets(assetManager, rootNode);
        randomGroßePlanets = new randomGroßePlanets(assetManager, rootNode);
        pEarth = new PlanetEarth(assetManager, rootNode);
        sStation = new SpaceStation(assetManager, rootNode);
        
        p.ladePlaneten(); // Lade die Planeten
        pSystemRinge.ladePlaneten();
        skyBox.ladeSkybox();
        randomPlanets.spawnRandomPlanets();
        randomGroßePlanets.spawnRandomPlanets();
        pEarth.ladePlaneten();
        sStation.ladePlaneten();

    }
    @Override
    public void simpleUpdate(float tpf) {
        flock.update(tpf); // called once per frame
        //pEarth.drehePlanet(tpf);
        sStation.drehePlanet(tpf);
        
    }
    
    @Override
    public void stop() {
        super.stop();
        Flock.executor.shutdownNow();
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        // add here custom rendering stuff if needed
    }
}
