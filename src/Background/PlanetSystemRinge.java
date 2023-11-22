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
public class PlanetSystemRinge {
    
    //Position des PlanetenSystems
    float scaleFactor = 29.0f; //Groeße
    float x = -180.0f;
    float y = 100.0f;
    float z = -480.0f;
    
    private Quaternion rotation;
    private final float rotationSpeed = 1.0f;
    
    private AssetManager assetManager;
    private Node rootNode;

    
    public PlanetSystemRinge(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    Spatial pSystemRinge;
    
    public Spatial getPlanet(){
        return this.pSystemRinge;
    }
    
    public void ladePlaneten(){
    //Quelle: Eigener Ansatz und Code teilweise mithilfe von ChatGPT, 21.11.2023
    //und Material Aufteilung von https://www.youtube.com/watch?v=WkFLNmPRYio&t=364s, 21.11.2023
        
    //hier Spatial und kein Node, weil das Objekt nur aus einem Objekt und nicht mehreren kleinen besteht
    pSystemRinge = assetManager.loadModel("Planets/PlanetSystemRinge.j3o");
    //Materialien
    // Erstellen der Materialien und Texturen für jedes Geometry
    Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex0 = assetManager.loadTexture("Texture/Texture_PlanetSystemRinge.png");
    mat0.setTexture("ColorMap", tex0);
    pSystemRinge.setMaterial(mat0);

    pSystemRinge.scale(scaleFactor); 
    pSystemRinge.setLocalTranslation(x, y, z);
    
    // Neigung des Objekts nach vorne
    float tiltAngle = FastMath.DEG_TO_RAD * 25.0f; // Zum Beispiel 30 Grad
    Quaternion tilt = new Quaternion().fromAngleAxis(tiltAngle, new Vector3f(1, 0, 0));
    pSystemRinge.setLocalRotation(tilt);

    rootNode.attachChild(pSystemRinge);
    
    }
    
    public void drehePlanet(float tpf, Spatial dobjModel){ 
        //Quelle: Code mithilfe von ChatGPT, 19.11.2023

        if (dobjModel != null && rotation != null) {
        // Berechne die Rotation für diesen Frame
        Quaternion frameRotation = new Quaternion().fromAngleAxis(rotationSpeed * tpf, Vector3f.UNIT_Y);

        // Füge die Rotation dieses Frames zur gesamten Rotation hinzu
        rotation = rotation.mult(frameRotation);

        // Wende die Gesamtrotation auf das Objekt an
        dobjModel.setLocalRotation(rotation);
    }
    }
    
}
