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
    
    private Vector3f centroid;
    
    
    /**
     * MADE BY BENI!
     * TODO: Dimensions must be tested...
     * STUPID IDEA FOR LATER: Getting min and max dist. from boids to center
     * and calculate the radius
     */
    private final static float radius = 10;
    
    /**
     * MADE BY BENI!
     * Parameter for the angle.
     * THETA STANDARDS: 
     * 30° for sqrt(3)
     * 45° for sqrt(2)
     * 60° for 1 
     * 90° for 0
     */
    private final static float theta = 90; 
    
    /**
     * MADE BY BENI!
     * Angle of the view field for the boids.
     * Cos: 
     * cos(0°) = 1
     * cos(30°) = sqrt(3)/2
     * cos(45°) = sqrt(2)/2
     * cos(60°) = 1/2
     * cos(90°) = 0
     */
    private final static float angle = (float) Math.cos(theta/2);
    
    /**
     * MADE BY BENI!
     * This method calculates and sets the centroid of the flock.
     * Each Boid vector will be added to a vector 
     * then divided by the count of Boid.
     */
    private void calcNextCentroid() {
        Vector3f vecSum = new Vector3f();
        for (Boid boid : boids) {
            vecSum.add(boid.position);
        }
        centroid = vecSum.divide(boids.size()); 
    }

    /**
     * MADE BY BENI!
     * This method calculates the direction vector to the
     * centroid of each boid
     * # Maybe later testing if there is a performance dif.
     */
    private void setBoidCohesion(Boid boid) {
        boid.cohesion.set(centroid.subtract(boid.position));
    }
    
    /**
     * MADE BY BENI!
     * This method sets the @boid.dToNeighbnour 
     */
    public void setBoidDirectionFromNearestNeighbour(Boid boid) {
        Vector3f shortestDistance = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        for (Boid nearest : boids) {
            if (nearest != boid) {
                if (shortestDistance.length() > (boid.position.subtract(nearest.position)).length()) {
                    shortestDistance = boid.position.subtract(nearest.position);
                }
            }
        boid.dFromNeighbour = shortestDistance;
        }
    }
    
    /**
     * MADE BY BENI!
     * This method...
     */
    public void setBoidSeperation(Boid boid) {
        setBoidDirectionFromNearestNeighbour(boid);
        boid.seperation = boid.dFromNeighbour.divide(boid.dFromNeighbour.lengthSquared());
    }
    
    /**
     * MADE BY BENI!
     * @param startBoid
     * @param targetBoid
     * @return 
     */
    private boolean isBoidInRadius(Boid startBoid, Boid targetBoid) {
        return targetBoid.position.subtract(startBoid.position).length() <= radius;
    }
    
    /**
     * MADE BY BENI!
     * @param startBoid
     * @param targetBoid
     * @return 
     */
    private boolean isBoidInAngle(Boid startBoid, Boid targetBoid) {
        Vector3f direction = targetBoid.position.subtract(startBoid.position);
        float actAngle = direction.angleBetween(targetBoid.position);   
        return actAngle <= angle;
    }
    
    /**
     * MADE BY BENI!
     * @param startBoid
     * @return 
     */
    public ArrayList getBoidsInFieldOfView(Boid startBoid) {
        ArrayList<Boid> boidsInFieldOfView = new ArrayList<>();
        for (Boid targetBoid : boids) {
            if (targetBoid != startBoid) {
                if (isBoidInRadius(startBoid, targetBoid) 
                        && isBoidInAngle(startBoid, targetBoid)) {
                    boidsInFieldOfView.add(targetBoid);
                }
            }
        }
        return boidsInFieldOfView;
    }
    


    /**
     * MADE BY BENI!
     * This method sets the alignement for a boid
     * @param boid 
     */
    private void setBoidAlignement(Boid boid) {
        ArrayList<Boid> boidsInFieldOfView = getBoidsInFieldOfView(boid);
        float weighting = 0f;
        Vector3f alignment = new Vector3f();
        for (Boid boidInField : boidsInFieldOfView) {
            // left part withouht 1/..
            Vector3f direction = boidInField.position.subtract(boid.position);
            weighting += 1 / direction.length();
            // right part
            alignment.add(boidInField.position.mult(1 / direction.length())); 
        }
        boid.alignement = alignment.mult(1/weighting);
    }
    
    /**
     * MADE BY BENI! 
     * This method sets all kind of forces for each Boid Object in Flock
     */
    private void calcForceForEachBoid() {
        calcNextCentroid();
        boids.forEach(boid-> {
            setBoidCohesion(boid);  
            setBoidSeperation(boid);
            setBoidAlignement(boid);
        });
    }
    
        /**
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
    public void update(float dtime) {
        calcForceForEachBoid();
        for( Boid boid : boids ) {
            // netAccelaration should be a linear combination of
            // separation,
            // alignment, 
            // cohes<ion, and
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
    private List<Boid> createBoids(int boidCount) {
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
    private Geometry createInstance() {
        Geometry geometry = new Geometry("boid", boidMesh);
        geometry.setMaterial(boidMaterial);
        instancedNode.attachChild(geometry);
        return geometry;
    }      
}
