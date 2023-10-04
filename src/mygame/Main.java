package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 */

public class Main extends SimpleApplication {

    private Flock flock;
    private final int boidCount = 300;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        Mesh mesh = new Box(0.01f, 0.01f, 0.03f); // the geometric model of one boid. Here a cube of size=(x:0.01,y:0.01,z:0.03)
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // the surface material for the geometric boid model.
        mat.setColor("Color", ColorRGBA.White);
        
        // instantiation of the flock
        flock = new Flock(rootNode, boidCount, mesh, mat );
        
        // camera rotation is controlled via mouse movement while the position is controlled via wasd-keys
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(30);
    }

    @Override
    public void simpleUpdate(float tpf) {
        flock.update(tpf); // called once per frame
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // add here custom rendering stuff if needed
    }
}
