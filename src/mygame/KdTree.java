
package mygame;

import com.jme3.math.Vector3f;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/**
 * KD Tree implementation for the Boids.
 * @author Benjamin
 * Should handle/manage the nodes (traversing, reinsert, etc)
 */
public class KdTree{
    // manage the nodes
    KdNode root = null;
    final int numDims = 3;       
    // Nodes should only knopw the children the tree should know the the parent,
    // to traverse. 
    // how will i make a queue to work out, should i make a array of references?
    // because i can not make it for each node in the tree. (dynamic changes pos)
    // With Quicksort getting all medians at each step
    // 0 - length-1 [2,3,5,7,8] -> [2,3,nil,7,8] 0-length-1/2 ; length/2 

    private void build(Boid[] boids) {
        // bulk insert 
        bulkInsert(boids);
    }
    
    public KdTree(Boid[] boids) {
        build(boids);
        System.err.println( this.root.depth(this.root)+"\n\n");
    }
    
    // Source ChatGPT with this-
    // Source https://github.com/locationtech/jts/tree/master/modules/lab/src
    // And https://github.com/elki-project/elki
    private class DimensionComparator implements java.util.Comparator<Boid> {
        private final int dimension;

        public DimensionComparator(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public int compare(Boid boid1, Boid boid2) {
            return Float.compare(boid1.position.get(dimension), boid2.position.get(dimension));
        }
    }

    //saiudhbf
    
    private int m3Partition(Boid[] boids, int p, int r, int depth) {
	// Source: Lecture "Folien_4_Suchen_Sortiern" p.14
	m3(boids, p, r, depth);
        float pivot = boids[r].position.get(depth);
	int i = p-1;
	for (int j = p; j < r; ++j) {
            if (boids[j].position.get(depth) <= pivot) {
		++i;			
                swap(boids,i,j);
            }
	}
	swap(boids,i+1,r);
	return i+1;
    }

    private void swap(Boid[] boids, int a, int b) {
	Boid temp = boids[a];
	boids[a] = boids[b];
	boids[b] = temp;
    }

    private void m3(Boid[] boids, int p, int r, int depth) {
	int m = (r+p)/2;
	if (boids[m].position.get(depth) < boids[p].position.get(depth)) {
		swap(boids,m,p);
	}
	if (boids[r].position.get(depth) < boids[p].position.get(depth)) {
		swap(boids,r,p);
	}
	if (boids[m].position.get(depth) < boids[r].position.get(depth)) {
		swap(boids,r,m);
	}
    }

    private void m3QuickSort(Boid[] boids, int p, int r, int depth) {
	// here comes the algorithm from the lecture
	if (p < r) {
            int s = m3Partition(boids,p,r,depth);
            m3QuickSort(boids, p, s-1, depth);  
            m3QuickSort(boids, s+1, r, depth);
	}
    }
    
    public void bulkInsert(Boid[] data) {
        // Sort by the first dimension initially
        root = bulkInsert(data, 0, data.length - 1, 0);
    }

    private KdNode bulkInsert(Boid[] data, int start, int end, int depth) {
        if (start > end) {
            return null;
        }
        m3(data, start, end, depth%numDims);
        int mid = (start + end) / 2;
        KdNode newNode = new KdNode(data[mid]);
        // getting the perfect childpair
        // delegate the work to the leafs
        newNode.leftChild = bulkInsert(data, start, mid - 1, (depth + 1) % numDims);
        newNode.rightChild = bulkInsert(data, mid + 1, end, (depth + 1) % numDims);

        return newNode;
    }
    
    public KdNode nearestNeighbor(Boid target) {
        return nearestNeighbor(root, target, 0);
    }

   
    private KdNode nearestNeighbor(KdNode root, Boid target, int depth) {
        if (root == null) {
            return null;
        }
        KdNode nextBranch, otherBranch; 
        if (target.position.get(depth % numDims) < root.boid.position.get(depth % numDims)) {
            nextBranch = root.leftChild;
            otherBranch = root.rightChild;
        } else {
            nextBranch = root.rightChild;
            otherBranch = root.leftChild;
        }
        KdNode temp = nearestNeighbor(nextBranch, target, (depth + 1) % numDims);
        KdNode best = closest(temp, root, target);
        float radiusSquared = target.position.distanceSquared(best.boid.position);
        float dist = target.position.get(depth % numDims) - root.boid.position.get(depth % numDims);

        // if (radiusSquared >= dist * dist) {
            temp = nearestNeighbor(otherBranch, target, (depth + 1) % numDims);
            best = closest(temp, best, target);
        //}
    return best;
    }

    private KdNode closest(KdNode n0, KdNode n1, Boid target) {   
        if (n0 == null) return n1;
        if (n1 == null) return n0;

        float d1 = n0.boid.position.distanceSquared(target.position);
        float d2 = n1.boid.position.distanceSquared(target.position);

        if (d1 < d2) {
            return n0;
        } else {
            return n1;
        }
    }  

    public String printTree(KdNode node, int i) {
        if (node == null) return "";
        return node.toString() + i + "\n"+ printTree(node.leftChild, i+1) + printTree(node.rightChild, i+1);
    }
    
    public String toString() {
        return printTree(root,0);
    }
}
// != self, old distance > smaller