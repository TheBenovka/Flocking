/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Background;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
/**
 *
 * @author louis
 */
public class SkyBox {
    private AssetManager assetManager;
    private Node rootNode;

    public SkyBox(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    public void ladeSkybox(){
   //Code Idee von https://wiki.jmonkeyengine.org/docs/3.4/core/util/sky.html 20.11.2023
  //Quelle: Texture von https://tools.wwwtyro.net/space-3d/index.html#animationSpeed=1&fov=80&nebulae=true&pointStars=true&resolution=1024&seed=1ncya657pfs0&stars=true&sun=true, 19.11.2023
        Texture west = assetManager.loadTexture("Textures/left.png"); //links
        Texture east = assetManager.loadTexture("Textures/right.png"); //rechts
        Texture north = assetManager.loadTexture("Textures/front.png"); //front
        Texture south = assetManager.loadTexture("Textures/back.png"); //back
        
        Texture up = assetManager.loadTexture("Textures/top.png");
        Texture down = assetManager.loadTexture("Textures/bottom.png");
        
        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
    
    }
    
}
