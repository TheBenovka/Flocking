/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.instancing.InstancedNode;
import java.util.ArrayList;
import java.util.List;
/**
 * This class controls and manages all boids within a flock (swarm)
 * @author philipp lensing
 */
public class Flock {
    
    private Material boidMaterial;
    private Mesh boidMesh;
    private Node scene;
    private InstancedNode instancedNode;
    private List<Boid> boids;
    
    /**
     * This Method calculates the cohesion of each Boid
     */
    

    
    /**
     * 
     * @param scene a reference to the root node of the scene graph (e. g. rootNode from SimpleApplication).
     * @param boidCount number of boids to create.
     * @param boidMesh reference mesh (geometric model) which should be used for a single boid.
     * @param boidMaterial the material controls the visual appearance (e. g. color or reflective behavior) of the surface of the boid model.
     */
    public Flock( Node scene, int boidCount, Mesh boidMesh, Material boidMaterial ) {
        
        this.boidMesh = boidMesh;
        this.boidMaterial = boidMaterial;
        this.scene = scene;
        
        this.boidMaterial.setBoolean("UseInstancing", true);
        this.instancedNode = new InstancedNode("instanced_node");
        this.scene.attachChild(instancedNode);
        
        boids = createBoids(boidCount);
        
        instancedNode.instance();
    }

    /**
     * The update method should be called once per frame
     * @param dtime determines the elapsed time in seconds (floating-point) between two consecutive frames
     */
    public void update(float dtime)
    {
        for( Boid boid : boids )
        {
            // netAccelaration should be a linear combination of
            // separation,
            // alignment, 
            // cohesion, and
            // further forces..
            Vector3f netAccelarationForBoid = boid.position.negate(); // accelaration=boid.position.negate()) means that there is a permanent acceleration towards the origin of the coordinate system (0,0,0) which decreases if the distance of the boid to origin decreases.


            boid.update(netAccelarationForBoid, dtime); 
        }
    }
    
    /**
     * Creates a list of Boid objects and adds corresponding instanced models (based on boidMesh) to the scene graph
     * @param boidCount The number of boids to create
     * @return A list of Boid objects. For each object a corresponding instanced geometry is added to the scene graph (Boid.geometry)
     */ 
    private List<Boid> createBoids(int boidCount)
    {
        List<Boid> boidList = new ArrayList<Boid>();
        
        for(int i=0; i<boidCount; ++i)
        {
            Boid newBoid = new Boid(createInstance());
            boidList.add(newBoid);
        }
        
        return boidList;
        
    }
    
    /**
     * Creates an instanced copy of boidMesh using boidMaterial with individual geometric transform
     * @return The instanced geometry attached to the scene graph
     */
    private Geometry createInstance()
    {
        Geometry geometry = new Geometry("boid", boidMesh);
        geometry.setMaterial(boidMaterial);
        instancedNode.attachChild(geometry);
        return geometry;
    }    

    
}
