package mygame;

import java.util.ArrayList;
import java.util.List;

/**
 * KD Tree implementation for the Boids.
 *
 * @author Benjamin Should handle/manage the nodes (traversing, reinsert, etc)
 */
public class KdTree {

    KdNode root = null;
    final int numDims = 3;
    // how will i make a queue to work out, should i make a array of references?
    // because i can not make it for each node in the tree. (dynamic changes pos)
    // With Quicksort getting all medians at each step
    // 0 - length-1 [2,3,5,7,8] -> [2,3,nil,7,8] 0-length-1/2 ; length/2 

    public KdTree(Boid[] boids) {
        bulkInsert(boids);
    }

    // Source: Lecture Algorithmen und Datenstrukturen "Folien_4_Suchen_Sortiern" p.14
    private int m3Partition(Boid[] boids, int p, int r, int dim) {
        m3(boids, p, r, dim);
        float pivot = boids[r].position.get(dim);
        int i = p - 1;
        for (int j = p; j < r; ++j) {
            if (boids[j].position.get(dim) <= pivot) {
                ++i;
                swap(boids, i, j);
            }
        }
        swap(boids, i + 1, r);
        return i + 1;
    }

    private void swap(Boid[] boids, int a, int b) {
        Boid temp = boids[a];
        boids[a] = boids[b];
        boids[b] = temp;
    }

    private void m3(Boid[] boids, int p, int r, int dim) {
        int m = (r + p) / 2;
        if (boids[m].position.get(dim) < boids[p].position.get(dim)) {
            swap(boids, m, p);
        }
        if (boids[r].position.get(dim) < boids[p].position.get(dim)) {
            swap(boids, r, p);
        }
        if (boids[m].position.get(dim) < boids[r].position.get(dim)) {
            swap(boids, r, m);
        }
    }

    private void m3QuickSort(Boid[] boids, int p, int r, int dim) {
        // here comes the algorithm from the lecture
        if (p < r) {
            int s = m3Partition(boids, p, r, dim);
            m3QuickSort(boids, p, s - 1, dim);
            m3QuickSort(boids, s + 1, r, dim);
        }
    }

    public final void bulkInsert(Boid[] boids) {
        root = bulkInsert(boids, 0, boids.length - 1, 0);
    }

    private KdNode bulkInsert(Boid[] boids, int start, int end, int depth) {
        if (start > end) {
            return null;
        }
        m3QuickSort(boids, start, end, depth % numDims);
        int mid = (start + end) / 2;
        KdNode newNode = new KdNode(boids[mid]);
        // getting the perfect childpair
        // delegate the work to the leafs
        newNode.leftChild = bulkInsert(boids, start, mid - 1, (depth + 1) % numDims);
        newNode.rightChild = bulkInsert(boids, mid + 1, end, (depth + 1) % numDims);

        return newNode;
    }

    public KdNode NN(Boid boid) {
        return NN(root, boid, 0);
    }

    private KdNode NN(KdNode node, Boid target, int depth) {
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
        
        // Muss vorher auf bounding box prüfen! 
        KdNode temp = NN(nextBranch, target, (depth + 1) % numDims);
        KdNode best = closest(temp, node, target);
        float distSquared = target.position.distanceSquared(best.boid.position);
        float dist = target.position.get(depth % numDims) - node.boid.position.get(depth % numDims);

        if (distSquared >= dist * dist) {
            temp = KdTree.this.NN(otherBranch, target, (depth + 1) % numDims);
            best = closest(temp, best, target);
        }
        return best;
    }

    private KdNode closest(KdNode n0, KdNode n1, Boid target) {
        if (n0 == null) {
            return n1;
        }
        if (n1 == null) {
            return n0;
        }

        float d1 = n0.boid.position.distanceSquared(target.position);
        float d2 = n1.boid.position.distanceSquared(target.position);

        if (d1 < d2) {
            return n0;
        } else {
            return n1;
        }
    }

    public String printTree(KdNode node, int i) {
        if (node == null) {
            return "";
        }
        return node.toString() + i + "\n" + printTree(node.leftChild, i + 1) + printTree(node.rightChild, i + 1);
    }

    public String toString() {
        return printTree(root, 0);
    }

    // Saeed
    public void insert(Boid boid) {
        root = insert(root, boid, 0, null);
    }

    public void delete(Boid boid) {
        root = delete(root, boid, 0);
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
                node.rightChild = delete(node.rightChild, minNode.boid, (depth + 1) % numDims);
            } else if (node.leftChild != null) {
                KdNode minNode = findMin(node.leftChild, (depth + 1) % numDims);
                node.boid = minNode.boid;
                node.rightChild = delete(node.leftChild, minNode.boid, (depth + 1) % numDims);
                node.leftChild = null;
            } else {
                return null;
            }
        } else if (boid.position.get(depth % numDims) < node.boid.position.get(depth % numDims)) {
            node.leftChild = delete(node.leftChild, boid, (depth + 1) % numDims);
        } else {
            node.rightChild = delete(node.rightChild, boid, (depth + 1) % numDims);
        }
        return node;
    }

    public KdNode insert(KdNode node, Boid boid, int depth, KdNode parent) {
        if (node == null) {
            return new KdNode(boid);
        }
        int axis = depth % numDims;

        if (boid.position.get(axis) < node.boid.position.get(axis)) {
            node.leftChild = insert(node.leftChild, boid, (depth + 1) % numDims, node);
        } else {
            node.rightChild = insert(node.rightChild, boid, (depth + 1) % numDims, node);
        }
        return node;
    }

    private KdNode findMin(KdNode node, int axis) {
        while (node != null && node.leftChild != null) {
            node = node.leftChild;
        }
        return node;
    }

    public ArrayList<Boid> getKNN(Boid target, float diameter) {
        ArrayList<Boid> bl = new ArrayList<>();
        KNN(root, target, diameter * diameter, bl, 0);
        return bl;
    }

    private void KNN(KdNode node, Boid target, float radiusSquared, List<Boid> neighbors, int depth) {
        if (node == null) {
            return;
        }

        float distSquared = node.boid.position.distanceSquared(target.position);

        if (distSquared <= radiusSquared && node.boid != target) {
            neighbors.add(node.boid);
        }

        int axis = depth % numDims;

        if (target.position.get(axis) < node.boid.position.get(axis)) {
            KNN(node.leftChild, target, radiusSquared, neighbors, depth + 1);
        } else {
            KNN(node.rightChild, target, radiusSquared, neighbors, depth + 1);
        }

        //Bounding Box prüfen 
        if (Math.abs(target.position.get(axis) - node.boid.position.get(axis)) * Math.abs(target.position.get(axis) - node.boid.position.get(axis)) <= radiusSquared) {
            if (target.position.get(axis) < node.boid.position.get(axis)) {
                KNN(node.rightChild, target, radiusSquared, neighbors, depth + 1);
            } else {
                KNN(node.leftChild, target, radiusSquared, neighbors, depth + 1);
            }
        }
    }

    public int depth(KdNode node) {
        if (node == null) {
            return -1; // null node has depth -1
        } else {
            return 1 + Math.max(depth(node.leftChild), depth(node.rightChild));
        }
    }
}
// != self, old distance > smaller
