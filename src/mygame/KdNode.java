/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import java.util.List;

/**
 *
 * @author Benjamin
 */
public class KdNode {
    public final Boid boid;
    KdNode parent = null;
    KdNode leftChild = null;
    KdNode rightChild = null;
    final int numDims = 3;		
    // This is the data. Each node has K different properties
    // public final KdPoint point;
    
    public KdNode(Boid props) {
	this.boid = props;
    }    
    
    public int depth(KdNode node) {
        if (node == null) {
            return -1; // null node has depth -1
        } else {
            return 1 + Math.max(depth(node.leftChild), depth(node.rightChild));
        }
    }
    
    int add(KdNode n, KdNode parent) {
	return this.add(n, 0, parent);
    }
	
    int add(KdNode n, int k, KdNode parent) {
	if (n.boid.position.get(k%numDims) < this.boid.position.get(k%numDims)) {
            if (leftChild == null) {
            	leftChild = n;
                if (rightChild == null) return 1;
            } else {
		return leftChild.add(n, k+1, this);
            }
	} else {
            if (rightChild == null) {
		rightChild = n;
                if (leftChild == null) return 1;
            } else {
		return rightChild.add(n, k+1, this);
            }
	}
        return 0;
    }
	
    @Override
    public String toString() {
	return "(point: " + this.boid.position.toString() + ")";
    }
}