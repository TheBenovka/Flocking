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
import com.jme3.texture.Texture;

/**
 *
 * @author louis
 */
public class SpaceStation {
      
    //Position des PlanetenSystems
    float scaleFactor = 8.0f; //Groeße
    float x = 10.0f;
    float y = -20.0f;
    float z = -190.0f;
    
    private Quaternion rotation;
    private final float rotationSpeed = 0.2f; //legt Rotations Geschwindigkeit der Planeten fest
    
    private AssetManager assetManager;
    private Node rootNode;

    public SpaceStation(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    Node spaceStation;
    
    public void ladePlaneten(){
        //Quelle: Eigener Ansatz und Code teilweise mithilfe von ChatGPT, 21.11.2023
    //und Material Aufteilung von https://www.youtube.com/watch?v=WkFLNmPRYio&t=364s, 21.11.2023
    
    // Laden des test.j3o-Objekts
     spaceStation = (Node) assetManager.loadModel("Spacestation/spacestation.j3o");

    //Materialien
    // Erstellen der Materialien und Texturen für jedes Geometry
   
    /*
    Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex0 = assetManager.loadTexture("Texture/spacestation_ panel.png");
    mat0.setTexture("ColorMap", tex0);
    spaceStation.getChild("spacestation-geom-0").setMaterial(mat0);

    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex1 = assetManager.loadTexture("Texture/spacestation_panel.png");
    mat1.setTexture("ColorMap", tex1);
    spaceStation.getChild("spacestation-geom-1").setMaterial(mat1);

    Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex2 = assetManager.loadTexture("Texture/spacestation_panel.png");  
    mat2.setTexture("ColorMap", tex2);
    spaceStation.getChild("spacestation-geom-2").setMaterial(mat2);
    */ 
    
    Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex0 = assetManager.loadTexture("Texture/spacestation_panelhell.png");
    mat0.setTexture("ColorMap", tex0);
    spaceStation.setMaterial(mat0);
    
    spaceStation.scale(scaleFactor); 
    spaceStation.setLocalTranslation(x, y, z);

    float rotationSpeed = 0.3f; // Beispielgeschwindigkeit der Rotation (in Grad pro Sekunde)
    rotation = new Quaternion().fromAngleAxis(rotationSpeed * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y); 
    
    
    rootNode.attachChild(spaceStation);
    
    }
    
    public void drehePlanet(float tpf){ 
        //Quelle: Code mithilfe von ChatGPT, 19.11.2023

        if (spaceStation != null && rotation != null) {
        // Berechne die Rotation für diesen Frame
        Quaternion frameRotation = new Quaternion().fromAngleAxis(rotationSpeed * tpf, Vector3f.UNIT_Y);

        // Füge die Rotation dieses Frames zur gesamten Rotation hinzu
        rotation = rotation.mult(frameRotation);

        // Wende die Gesamtrotation auf das Objekt an
        spaceStation.setLocalRotation(rotation);
    }
    }
    
}
