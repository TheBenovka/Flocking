/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.instancing.InstancedNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * This class controls and manages all boids within a flock (swarm)
 * @author philipp lensing
 */
public class Flock {
    static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final Material boidMaterial;
    private final Mesh boidMesh;
    private final Node scene;
    private final InstancedNode instancedNode;
    private KdTree boidTree;
    private Boid[] boidsV3;
    private Vector3f centroid;

    /**
     * MADE BY BENI!
     * TODO: Dimensions must be tested...
     * STUPID IDEA FOR LATER: Getting min and max dist. from boids to center
     * and calculate the radius
     */
    private final float radius = 0.25f;

    /**
     * MADE BY BENI!
     * Parameter for the angle.
     * THETA STANDARDS: 
     * 30° for sqrt(3)
     * 45° for sqrt(2)
     * 60° for 1 
     * 90° for 0
     */
    private final float theta = 120; 
    
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
    private final float angle = (float) Math.cos(theta/2);
    
    /**
     * MADE BY BENI!
     * This method calculates and sets the centroid of the flock.
     * Each Boid vector will be added to a vector 
     * then divided by the count of Boid.
     */
    private void calcNextCentroid() {
        Vector3f vecSum = new Vector3f();
        //for (int i = 0; i < boidsV3.length;++i) {}
        for (Boid boid : boidsV3) {
            vecSum = vecSum.add(boid.position);
        }
        centroid = vecSum.divide(boidsV3.length);
    }

    /**
     * MADE BY BENI!
     * This method calculates the direction vector to the
     * centroid from the boid.
     */
    private void setBoidCohesion(Boid boid) {
        boid.cohesion = centroid.subtract(boid.position);
    }
    
    /**
     * MADE BY BENI!
     * This method sets the @boid.d(irection)FromNeighbnour the driection vector from
     * the neighbour to the boid position. To calculate it the nearest neighbour is needed therefore
     * we search for it. At the moment O(n²), but with the k-d tree or the OcTree it can be reduced to O(n log(n)).
     */
    public void setBoidDirectionFromNearestNeighbour(Boid boid) {
        Boid n = boidTree.NN(boid).boid; 
        boid.dFromNeighbour = (n == null) ? Vector3f.ZERO : boid.position.subtract(n.position);
    }
    
    /**
     * MADE BY BENI!
     * This method sets the separation object variable for a boid.
     * It's calculated from the direction from the nearest neighbour to the Boid and multiplicated with a weighting.
     */
    public void setBoidSeperation(Boid boid) {
        setBoidDirectionFromNearestNeighbour(boid);
        if (boid.dFromNeighbour.lengthSquared() == 0) {
            boid.seperation = Vector3f.ZERO;
        } else {
            boid.seperation = boid.dFromNeighbour.divide(boid.dFromNeighbour.lengthSquared());
        }
    }
    
    /**
     * MADE BY BENI!
     * @param startBoid
     * @param targetBoid
     * @return true if the both boid vectors are spanning a smaller angle than the defined, else return false. 
     */
    private boolean isBoidInAngle(Boid startBoid, Boid targetBoid) {
        Vector3f direction = targetBoid.position.subtract(startBoid.position);
        float actAngle = direction.angleBetween(targetBoid.position);   
        return actAngle >= angle;
    }
    
    /**
     * MADE BY BENI!
     * This method creates an ArrayList with Boids which are in the defined field of view of the Boid.
     * @param startBoid 
     * @return the reference of the created ArrayList FOV of the boid.
     */
    public ArrayList getBoidsInFieldOfView(Boid startBoid) {
        ArrayList<Boid> boidsInFieldOfView = boidTree.getKNN(startBoid, radius);
        for (Boid targetBoid : boidsInFieldOfView) {
            if (!isBoidInAngle(startBoid, targetBoid)) {
                boidsInFieldOfView.remove(targetBoid);
            }
        }
        return boidsInFieldOfView;
    }   
    
    /**
     * MADE BY BENI!
     * This method sets the alignement for a boid.
     * But to set the alignement first an ArrayList will be created with the boids in the
     * field of view from the actual boid.
     * The next Task is to calculate the direction, which will be used in both sums.
     * After it the weighting and the 'alignemnet' (without the last weighting) will be calculated with the velocity of the boids
     * in the FOV list.
     * At last a second weighting will be multiplicated.
     * @param boid 
     */
    private void setBoidAlignementV2(Boid boid) {
        ArrayList<Boid> boidsInFieldOfView = getBoidsInFieldOfView(boid);
        if (boidsInFieldOfView.isEmpty()) {
            boid.alignement = Vector3f.ZERO;
            return;
        }
        float weighting = 0f;
        Vector3f alignment = new Vector3f();
        for (Boid boidInField : boidsInFieldOfView) {
            // weighting vector part withouht 1/..
            Vector3f direction = boidInField.position.subtract(boid.position);
            weighting += 1 / direction.lengthSquared();
            // right part            
            alignment = alignment.add(boidInField.velocity.normalize().mult(1 / direction.lengthSquared()));    
        }
        boid.alignement = alignment.mult(1/weighting);
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
        this.createBoids(boidCount);
        instancedNode.instance();
    }
    
    /**
     * MADE BY BENI!
     * @param c - cohesion force of the boid
     * @param s - separation force of the boid
     * @param a - alignement force of the boid
     * @param boid
     * @return 
     */
    private Vector3f getForce(Vector3f c, Vector3f s, Vector3f a, Boid boid) {
        a = a.mult(1f);
        s = s.mult(0.2f);
        c = c.mult(1f);
        // collision force
        //Vector3f d = new Vector3f();
        //if (boid.position.length() > 5){
            //d = boid.position.mult(FastMath.pow((boid.position.length()-1f),11f)*(-0.000001f));
        //}
        Vector3f fullForce = (a.add(c).add(s));
       // if (boid.position.length() > 10){
            //d = boid.position.mult(FastMath.pow((boid.position.length()-1f),11f)*(-0.000001f));
            //fullForce = boid.position.negate();
        //}
        return fullForce.length() > 5 ? fullForce.normalize().mult(5f) : fullForce;
    }
    
    /**
     * The update method should be called once per frame
     * @param dtime determines the elapsed time in seconds (floating-point) between two consecutive frames
     */
    public void update(float dtime) {
        // Maybe building the tree new
        //if (boidTree.depth) {
        //}
 long t, e = 0;
        t = System.currentTimeMillis();
        calcNextCentroid();

        List<Callable<Void>> tasks = new ArrayList<>();
        
  /*
        for (Boid boid : boidsV3) {
            
         
                boidTree.delete(boid);
                //if (reBuildCount % 2 == 0) {
                setBoidCohesion(boid);
                setBoidSeperation(boid);
                setBoidAlignementV2(boid); 
                //}
                Vector3f netAccelarationForBoid = getForce(boid.cohesion, boid.seperation, boid.alignement, boid);
                boid.update(netAccelarationForBoid.mult(0.5f), dtime);
                boidTree.insert(boid);
               // return null;
               
         
         }
        reBuildCount++;
   */
        for (Boid boid : boidsV3) {
            tasks.add(() -> {
                boidTree.delete(boid);
                setBoidCohesion(boid);
                setBoidSeperation(boid);
                setBoidAlignementV2(boid);

                Vector3f netAccelarationForBoid = getForce(boid.cohesion, boid.seperation, boid.alignement, boid);

                boid.update(netAccelarationForBoid.mult(0.5f), dtime);
                boidTree.insert(boid);
                return null;
            });
        }

        try {
            // Execute all tasks using the executor service
            List<Future<Void>> futures = executor.invokeAll(tasks);

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //boidTree.bulkInsert(boidsV3);
        e = System.currentTimeMillis() - t;
        System.err.println(e);
    }

    private void createBoids(int boidCount) {       
        boidsV3 = new Boid[boidCount];
        for(int i=0; i<boidCount; ++i) {
            Boid newBoid = new Boid(createInstance());
            boidsV3[i] = newBoid;
        } 
        boidTree = new KdTree(boidsV3);
    }
    
    public void destroy(){
    executor.shutdownNow();
    
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
