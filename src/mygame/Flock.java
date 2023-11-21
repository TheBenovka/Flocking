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
    private Boid[] boidsV3;
    private Vector3f maxVector = new Vector3f();
    private Vector3f centroid;
    private float[] boidsV2;
    private int q = 0;
    
    /**
     * MADE BY BENI!
     * TODO: Dimensions must be tested...
     * STUPID IDEA FOR LATER: Getting min and max dist. from boids to center
     * and calculate the radius
     */
    private final float radius = 1f;

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
     * A second version could be used for optimizing.
     */
    private float[] calcNextCentroidV2() {
        float[] vecSum = new float[3];
        for (int i = 0; i < boidsV2.length/3; ++i) {
            vecSum[0] += boidsV2[i*3]; //0-3-6-9 //x
            vecSum[1] += boidsV2[i*3+1]; //1-4-7-10 //y
            vecSum[2] += boidsV2[i*3+2]; //2-5-8-11 //y
        }
            vecSum[0] /= (boidsV2.length/3);
            vecSum[1] /= (boidsV2.length/3);
            vecSum[2] /= (boidsV2.length/3);
        return vecSum;
    }
    
    /**
     * MADE BY BENI!
     * This method calculates and sets the centroid of the flock.
     * Each Boid vector will be added to a vector 
     * then divided by the count of Boid.
     */
    private void calcNextCentroid() {
        Vector3f vecSum = new Vector3f();
        for (Boid boid : boids) {
            vecSum = vecSum.add(boid.position);
        }
        centroid = vecSum.divide(boids.size());
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
     * This method sets the separation object variable for a boid.
     * It's calculated from the direction from the nearest neighbour to the Boid and multiplicated with a weighting.
     */
    public void setBoidSeperation(Boid boid) {
        setBoidDirectionFromNearestNeighbour(boid);
        boid.seperation = boid.dFromNeighbour.divide(boid.dFromNeighbour.lengthSquared());
    }
    
    /**
     * MADE BY BENI!
     * @param startBoid 
     * @param targetBoid 
     * @return true if the distance from the boid to another is in the defined radius, if not return false.
     */
    private boolean isBoidInRadius(Boid startBoid, Boid targetBoid) {
        return targetBoid.position.subtract(startBoid.position).length() <= radius;
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
        
        //boidsV2 = new float[boidCount*3];
        
        this.boidMaterial.setBoolean("UseInstancing", true);
        this.instancedNode = new InstancedNode("instanced_node");
        this.scene.attachChild(instancedNode);
        // Garbage
        Boid a = new Boid(createInstance());
        boids = createBoids(boidCount);
        KdTree kt = new KdTree(boidsV3);
        ArrayList<Boid> lb = new ArrayList<>();
        for (Boid b : boids) {
            boolean t = true;
            kt.findNeighbors(kt.root, a, 1f, lb, 0);
            for (Boid c : lb) {
                if (!isBoidInAngle(a, c)) {
                    lb.remove(c);
                }
            }
            ArrayList<Boid> lb2 = getBoidsInFieldOfView(a);
            if (lb2.size() != lb.size()) {
                t = false;
            } else {
                for (Boid c : lb) {
                    if (!lb2.contains(c)) {
                        t = false;
                    }
                }
            }
            System.err.println(t);
        }

        // end
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
        Vector3f d = new Vector3f();
        if (boid.position.length() > 5){
            //d = boid.position.mult(FastMath.pow((boid.position.length()-1f),11f)*(-0.000001f));
        }
        Vector3f fullForce = (a.add(c).add(s).add(d)).mult(10f/10f);
        if (boid.position.length() > 10){
            //d = boid.position.mult(FastMath.pow((boid.position.length()-1f),11f)*(-0.000001f));
            fullForce = boid.position.negate();
        }
        return fullForce.length() > 3 ? fullForce.normalize().mult(3f) : fullForce;
    }
    
    /**
     * The update method should be called once per frame
     * @param dtime determines the elapsed time in seconds (floating-point) between two consecutive frames
     */
    public void update(float dtime) {
        calcNextCentroid();
        for( Boid boid : boids ) {
            
            // netAccelaration should be a linear combination of
            // separation,
            // alignment, 
            // cohes<ion, and
            // further forces..
            
            setBoidCohesion(boid);  
            setBoidSeperation(boid);
            setBoidAlignementV2(boid);

            // Vector3f netAccelarationForBoid = boid.position.negate(); 
            // accelaration=boid.position.negate()) means that there is a permanent acceleration towards the origin of the coordinate system (0,0,0) 
            // which decreases if the distance of the boid to origin decreases.

            Vector3f netAccelarationForBoid = getForce(boid.cohesion,
                    boid.seperation, boid.alignement, boid);
 
            boid.update(netAccelarationForBoid.mult(0.5f), dtime); 
            if(boid.position.length() > maxVector.length()) {
                maxVector = boid.position;
            }
            if(q % 200000 == 0) {
                System.err.println(maxVector);
                q = 0;
            }
            q++;
        }
    }
    
    
    
    /**
     * Creates a list of Boid objects and adds corresponding instanced models (based on boidMesh) to the scene graph
     * @param boidCount The number of boids to create
     * @return A list of Boid objects. For each object a corresponding instanced geometry is added to the scene graph (Boid.geometry)
     */ 
    private List<Boid> createBoids(int boidCount) {
        List<Boid> boidList = new ArrayList<Boid>();
        boidsV3 = new Boid[boidCount];
        for(int i=0; i<boidCount; ++i) {
            Boid newBoid = new Boid(createInstance());
            boidList.add(newBoid);
            boidsV3[i] = boidList.get(i);
        }
        /*
        Boid a = new Boid(createInstance());
        Boid boidTest = null;
        Vector3f shortestDistance = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        for (Boid nearest : boids) {
            if (nearest != a) {
                if (shortestDistance.length() > (a.position.distance(nearest.position))) {
                    shortestDistance = a.position.subtract(nearest.position);
                    boidTest = nearest; 
                }
            }
        boidList.get(0).dFromNeighbour = shortestDistance;
        }
        */
        //kt.nearestNeighbor(a);
        //System.err.println("\n\n " + kt.nearestNeighbor(a) +
        //        " \n" + boidTest.position.toString() + "\n" + a.position +"\n");
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
