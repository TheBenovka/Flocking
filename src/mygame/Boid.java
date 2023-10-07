/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * Boid represents one individual boid in the flock.
 * It's motion is integrated within the update method, which should be called once per frame.
 * @author philipp lensing
 */
public class Boid {
    public static float spawnVolumeSize = 10.0f;
    public Vector3f position;
    public Vector3f velocity;
    private Geometry geometry;
    
    /**
     * MADE BY BENI!
     * cohesion -> force to the centroid
     * seperation -> force to not collide with other boids
     * alignment -> force to fly to the same direction as the boids in the field of view
     * a -> the combined force (a only for testing)
     */
    public Vector3f dFromNeighbour;
    public Vector3f seperation;
    public Vector3f cohesion;
    public Vector3f alignement;
    private Vector3f a;
    
    /**
     * The constructor instantiates a Boid a random position p within -spawnVolumeSize/2 <= p <= spawnVolumeSize/2.
     * The initial velocity is set to random 3D-vector with a magnitude of one.
     * @param geom corresponds to a geometry object within the scene graph and has to exist.
     */
    public Boid(Geometry geom) {
        this.geometry = geom;
        seperation = new Vector3f();
        cohesion = new Vector3f();
        velocity = new Vector3f();
        position = new Vector3f();
        position.x = (FastMath.nextRandomFloat() -0.5f) * spawnVolumeSize;
        position.y = (FastMath.nextRandomFloat() -0.5f) * spawnVolumeSize;
        position.z = (FastMath.nextRandomFloat() -0.5f) * spawnVolumeSize;
        velocity.x = (FastMath.nextRandomFloat() -0.5f);
        velocity.y = (FastMath.nextRandomFloat() -0.5f);
        velocity.z = (FastMath.nextRandomFloat() -0.5f);
        velocity.normalizeLocal();
    }
        
    private void setA() {
        a = alignement.mult(2f).add(cohesion.mult(2f).add(seperation.mult(1f)));
    }
    
    /**
     * update calculates the new position of the Boid based on its current position and velocity influenced by accelaration. update should be called once per frame
     * @param accelaration The net accelaration of all forces that influence the boid
     * @param dtime The elapsed time in seconds between two consecutive frames
     */
    public void update(Vector3f accelaration, float dtime) {
        setA();
        //integrate velocity: v = v + a*dt; keep in mind: [m/s^2 * s = m/s]
        //integrate position: p = p + v*dt; keep in mind: [m/s * s = m]
        velocity = velocity.add(accelaration.mult(dtime));
        position = position.add(velocity.mult(dtime));        
        //update scene instance
        geometry.setLocalTranslation(position);
        geometry.lookAt(position.add(velocity), Vector3f.UNIT_Y);
    }
}
