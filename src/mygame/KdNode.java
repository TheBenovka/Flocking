/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

/**
 *
 * @author Benjamin
 */
public class KdNode {
    public Boid boid;
    KdNode leftChild = null;
    KdNode rightChild = null;
    public final int numDims = 3;		
    // This is the data. Each node has K different properties
    // public final KdPoint point;
    
    public KdNode(Boid props) {
	this.boid = props;
    }    
	
    @Override
    public String toString() {
	return "(point: " + this.boid.position.toString() + ")";
    }
}