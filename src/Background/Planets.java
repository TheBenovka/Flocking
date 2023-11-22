/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Background;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

/**
 *
 * @author louis
 */
public class Planets{
    
    float scaleFactor = 25.0f;
    float x = -600.0f;
    float y = 400.0f;
    float z = -500.0f;
    
    private AssetManager assetManager;
    private Node rootNode;

    public Planets(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    public void ladePlaneten(){
    //Quelle: Eigener Ansatz und Code teilweise mithilfe von ChatGPT, 21.11.2023
    //und Material Aufteilung von https://www.youtube.com/watch?v=WkFLNmPRYio&t=364s, 21.11.2023
    
    // Laden des test.j3o-Objekts
    Node explodingPlanet = (Node) assetManager.loadModel("Planets/explodingPlanet.j3o");

    //Materialien
    // Erstellen der Materialien und Texturen für jedes Geometry
    Material mat0 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex0 = assetManager.loadTexture("Texture/explodingPlanet_Black.png");
    mat0.setTexture("ColorMap", tex0);
    explodingPlanet.getChild("explodingPlanet-geom-0").setMaterial(mat0);

    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex1 = assetManager.loadTexture("Texture/explodingPlanet_Black.png");
    mat1.setTexture("ColorMap", tex1);
    explodingPlanet.getChild("explodingPlanet-geom-1").setMaterial(mat1);

    Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex2 = assetManager.loadTexture("Texture/explodingPlanet_Lava.jpg");
    mat2.setTexture("ColorMap", tex2);
    explodingPlanet.getChild("explodingPlanet-geom-2").setMaterial(mat2);

    explodingPlanet.scale(scaleFactor); 
    explodingPlanet.setLocalTranslation(x, y, z);
    
    
    rootNode.attachChild(explodingPlanet);
    
    }
 
}
