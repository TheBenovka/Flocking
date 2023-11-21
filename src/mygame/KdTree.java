
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
    
    private int m3Partition(Boid[] boids, int p, int r, int dim) {
	// Source: Lecture "Folien_4_Suchen_Sortiern" p.14
	m3(boids, p, r, dim);
        float pivot = boids[r].position.get(dim);
	int i = p-1;
	for (int j = p; j < r; ++j) {
            if (boids[j].position.get(dim) <= pivot) {
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

    private void m3(Boid[] boids, int p, int r, int dim) {
	int m = (r+p)/2;
	if (boids[m].position.get(dim) < boids[p].position.get(dim)) {
		swap(boids,m,p);
	}
	if (boids[r].position.get(dim) < boids[p].position.get(dim)) {
		swap(boids,r,p);
	}
	if (boids[m].position.get(dim) < boids[r].position.get(dim)) {
		swap(boids,r,m);
	}
    }

    private void m3QuickSort(Boid[] boids, int p, int r, int dim) {
	// here comes the algorithm from the lecture
	if (p < r) {
            int s = m3Partition(boids,p,r,dim);
            m3QuickSort(boids, p, s-1, dim);  
            m3QuickSort(boids, s+1, r, dim);
	}
    }
    
    public void bulkInsert(Boid[] data) {
        root = bulkInsert(data, 0, data.length - 1, 0);
    }

    private KdNode bulkInsert(Boid[] data, int start, int end, int depth) {
        if (start > end) {
            return null;
        }
        m3QuickSort(data, start, end, depth%numDims);
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

   
    //Extra parameter for bounding box
    private KdNode nearestNeighbor(KdNode node, Boid target, int depth) {
        if (node == null) {
            return null;
        }
        KdNode nextBranch, otherBranch; 
        if (target.position.get(depth % numDims) < node.boid.position.get(depth % numDims)) {
            nextBranch = node.leftChild;
            otherBranch = node.rightChild;
        } else {
            nextBranch = node.rightChild;
            otherBranch = node.leftChild;
        }
        // hier ist der fehler er darf nicht aufs ende gehen
        // Muss vorher auf bounding box prüfen! 
        KdNode temp = nearestNeighbor(nextBranch, target, (depth + 1) % numDims);
        KdNode best = closest(temp, node, target);
        float distSquared = target.position.distanceSquared(best.boid.position);
        float dist = target.position.get(depth % numDims) - node.boid.position.get(depth % numDims);

        if (distSquared >= dist * dist) {
            temp = nearestNeighbor(otherBranch, target, (depth + 1) % numDims);
            best = closest(temp, best, target);
        }
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
 /*   
    public List<Boid> kNearestNeighbors(Boid target, float radius) {
    List<Boid> neighbors = new ArrayList<>();
    findNeighbors(root, target, radius * radius, neighbors, 0);
    return neighbors;
    }
*/
    
public void reinsertBoid(Boid boid) {
    root = delete(root, boid, 0);
    // Position  updaten
    root = insert(root, boid, 0, null);
}

private KdNode delete(KdNode node, Boid boid, int depth) {
    if (node == null) {
        return null;
    }

    //int axis = node.depth % numDims;

    if (node.boid.equals(boid)) {
        if (node.rightChild != null) {
            KdNode minNode = findMin(node.rightChild, depth);
            node.boid = minNode.boid;
            node.rightChild = delete(node.rightChild, minNode.boid,(depth+1)%numDims);
        } else if (node.leftChild != null) {
            KdNode minNode = findMin(node.leftChild, (depth+1)%numDims);
            node.boid = minNode.boid;
            node.rightChild = delete(node.leftChild, minNode.boid, (depth +1 )%numDims);
            node.leftChild = null;
        } else {
            return null;
        }
    } else if (boid.position.get(depth) < node.boid.position.get(depth)) {
        node.leftChild = delete(node.leftChild, boid, (depth+1)%numDims);
    } else {
        node.rightChild = delete(node.rightChild, boid, (depth+1)%numDims);
    }

    return node;
}

private KdNode insert(KdNode node, Boid boid, int depth, KdNode parent) {
    if (node == null) {
        return new KdNode(boid);
    }

    int axis = depth % numDims;

    if (boid.position.get(axis) < node.boid.position.get(axis)) {
        node.leftChild = insert(node.leftChild, boid, depth + 1, node);
    } else {
        node.rightChild = insert(node.rightChild, boid, depth + 1, node);
    }

    return node;
}

private KdNode findMin(KdNode node, int axis) {
    while (node != null && node.leftChild != null) {
        node = node.leftChild;
    }
    return node;
}    
    
public void findNeighbors(KdNode node, Boid target, float radiusSquared, List<Boid> neighbors, int depth) {
    if (node == null) {
        return;
    }

    float distSquared = node.boid.position.distanceSquared(target.position);

    if (distSquared <= radiusSquared && node.boid != target) {
        neighbors.add(node.boid);
    }

    int axis = depth % numDims;

    if (target.position.get(axis) < node.boid.position.get(axis)) {
        findNeighbors(node.leftChild, target, radiusSquared, neighbors, depth + 1);
    } else {
        findNeighbors(node.rightChild, target, radiusSquared, neighbors, depth + 1);
    }

    //Bounding Box prüfen 
    if (Math.abs(target.position.get(axis) - node.boid.position.get(axis)) * Math.abs(target.position.get(axis) - node.boid.position.get(axis)) <= radiusSquared) {
        if (target.position.get(axis) < node.boid.position.get(axis)) {
            findNeighbors(node.rightChild, target, radiusSquared, neighbors, depth + 1);
        } else {
            findNeighbors(node.leftChild, target, radiusSquared, neighbors, depth + 1);
        }
    }
}
}
// != self, old distance > smaller