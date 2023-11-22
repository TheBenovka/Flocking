/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Background;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 *
 * @author louis
 */
public class PlanetEarth {
    
    
    //Position des PlanetenSystems
    float scaleFactor = 20.0f; //Groeße
    float x = 80.0f;
    float y = 40.0f;
    float z = -240.0f;
    
    private Quaternion rotation;
    private final float rotationSpeed = 1.0f; //legt Rotations Geschwindigkeit der Planeten fest

    
    private AssetManager assetManager;
    private Node rootNode;

    public PlanetEarth(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    Spatial earth;
    
    public void ladePlaneten(){
    //Quelle: Eigener Ansatz und Code teilweise mithilfe von ChatGPT, 21.11.2023
    //und Material Aufteilung von https://www.youtube.com/watch?v=WkFLNmPRYio&t=364s, 21.11.2023
    earth = assetManager.loadModel("Planets/earth.j3o");
    //hier Spatial und kein Node, weil das Objekt nur aus einem Objekt und nicht mehreren kleinen besteht

    //Materialien
    // Erstellen der Materialien und Texturen für jedes Geometry
    Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex0 = assetManager.loadTexture("Texture/earthNight.png");
    mat0.setTexture("ColorMap", tex0);
    earth.setMaterial(mat0);

    earth.scale(scaleFactor); 
    earth.setLocalTranslation(x, y, z);
    
    // Neigung des Objekts nach vorne
    //float tiltAngle = FastMath.DEG_TO_RAD * 40.0f; // Zum Beispiel 30 Grad
    //Quaternion tilt = new Quaternion().fromAngleAxis(tiltAngle, new Vector3f(1, 0, 0));
    //earth.setLocalRotation(tilt);

    float rotationSpeed = 0.5f; // Beispielgeschwindigkeit der Rotation (in Grad pro Sekunde)
    rotation = new Quaternion().fromAngleAxis(rotationSpeed * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y); 
    
    
    rootNode.attachChild(earth);
    
    }
    
    public void drehePlanet(float tpf){ 
        //Quelle: Code mithilfe von ChatGPT, 19.11.2023

        if (earth != null && rotation != null) {
        // Berechne die Rotation für diesen Frame
        Quaternion frameRotation = new Quaternion().fromAngleAxis(rotationSpeed * tpf, Vector3f.UNIT_Y);

        // Füge die Rotation dieses Frames zur gesamten Rotation hinzu
        rotation = rotation.mult(frameRotation);

        // Wende die Gesamtrotation auf das Objekt an
        earth.setLocalRotation(rotation);
    }
    }
    
}
