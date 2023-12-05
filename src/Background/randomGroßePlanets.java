/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Background;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Random;

/**
 *
 * @author louis
 */
public class randomGroßePlanets {
    
    private AssetManager assetManager;
    private Node rootNode;
    
    private Random random = new Random();
    private Spatial objModel;
    private Spatial objModel2;
    private final int anzahlRandomPlaneten = 20;

    public randomGroßePlanets(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
       //Quelle: Code von ChatGPT, 19.11.2023
    public void spawnRandomPlanets(){
        assetManager.registerLocator("assets", FileLocator.class);

    float minDistance = 150.0f; // Der minimale Abstand zwischen den Objekten

    for (int i = 0; i < anzahlRandomPlaneten; i++) {
        boolean validPosition = false;
        
        Spatial randomObjModel;
        float randomNumber = random.nextFloat();
        
        if (random.nextFloat() < 0.5f) {
            randomObjModel = assetManager.loadModel("Planets/Mars.j3o");
        } else {
            randomObjModel = assetManager.loadModel("Planets/Moon.j3o");
        }
        
        //Fuer 4 Planeten
        /*
        if (randomNumber < 0.25f) {
            randomObjModel = assetManager.loadModel("Planets/Mars.j3o");
        } else if (randomNumber < 0.5f) {
            randomObjModel = assetManager.loadModel("Planets/Earth.j3o");
        } else if (randomNumber < 0.75f) {
            randomObjModel = assetManager.loadModel("Planets/Venus.j3o");
        } else {
            randomObjModel = assetManager.loadModel("Planets/Mercury.j3o");
        }
        */        
        
        // Versuche eine gültige Position zu finden, die weit genug von bereits platzierten Objekten entfernt ist
        while (!validPosition) {
            
            float x, y, z;

            // Generiere x, y, z, die außerhalb des Bereichs von -200 bis 200 liegen
            do {
                x = random.nextFloat() * 1300 - 500; // Bereich: -500 bis 700
                y = random.nextFloat() * 1800 - 500; // Bereich: -500 bis 1300
                z = random.nextFloat() * 1400 - 700; // Bereich: -700 bis 700
            } while (Math.abs(x) <= 200 || Math.abs(y) <= 200 || Math.abs(z) <= 200);


            Vector3f newPosition = new Vector3f(x, y, z);

            // Überprüfe den Abstand zu bereits platzierten Objekten
            validPosition = true; // Annahme, dass die Position gültig ist, es sei denn...
            for (Spatial obj : rootNode.getChildren()) {
                Vector3f objPosition = obj.getWorldTranslation();
                if (objPosition.distance(newPosition) < minDistance) {
                    validPosition = false; // ...falls der Abstand zu nah ist, ist die Position ungültig
                    break;
                }
            }
            if (validPosition) {
                randomObjModel.setLocalTranslation(newPosition);

                // Random scale
                float scaleFactor = random.nextFloat() * 12; //Range 0 to 3 
                
                randomObjModel.scale(scaleFactor);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture("Texture/texturePlanetDark.png")); // Change the path to your texture
                randomObjModel.setMaterial(mat);

                rootNode.attachChild(randomObjModel);
            }
        }
    }
    }
    
}
