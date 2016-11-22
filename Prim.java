import java.util.*;

public class Prim {

    private static double inf = Double.POSITIVE_INFINITY;

    private double[][] g;
    private int size;
    private int[] q; // q[v] = whether v is included in tree
    public Tree mst;

    public Prim (double[][] g, int src) {
        this.g = g.clone();
        this.size = g.length;
        this.q = new int[this.size];
        this.mst = new Tree(size);
        primMST(src);
    }

    private void primMST (int src) {
        int u = src;
        int v;
        while (u != -1) {
            v = findMinEdge(u);
            if (v == -1) System.exit(666);
            mst.addEdge(u, v);
            this.q[u] = 1;
            this.q[v] = 1;
            u = getFree();
        }

    }

    private int findMinEdge (int u) {
        int v = -1;
        double min = inf;
        for (int i = 0; i < size; i++) {
            if (this.g[u][i] < min) {
                min = this.g[u][i];
                v = i;
            }
        }
        return v;
    }

    private int getFree () {
        for (int i = 0; i < size; i++) {
            if (this.q[i] == 0) return i;
        }
        return -1;
    }

    public int[] oddDegreeV () {
        int[] vList = new int[size]; 
        for (int i = 0; i < size; i++) {
            if (mst.degCnt(i) % 2 == 1) {
                vList[i] = 1;
            }
        }
        return vList;
    }

}
