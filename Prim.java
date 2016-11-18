public class Prim {

    private static double inf = Double.POSITIVE_INFINITY;

    private double[][] g;
    private int size;
    private int[] q; // q[v] = whether v is included in tree
    private Tree mst;

    public Prim (double[][] g) {
        this.g = g;
        this.size = g.length;
        this.q = new int[this.size];
        this.mst = new Tree(size);
    }

    private void primMST (int src) {
        int u = src;
        int v;
        while (u != -1) {
            v = findMinEdge(u);
            if (v == -1) System.exit(666);
            mst.addEdge(u, v);
            q[u] = 1;
            q[v] = 1;
            u = getFree();
        }
    }

    private int findMinEdge (int u) {
        int v = -1;
        double min = inf;
        for (int i = 0; i < size; i++) {
            if (g[u][i] < min) {
                min = g[u][i];
                v = i;
            }
        }
        return v;
    }

    private int getFree () {
        for (int i = 0; i < size; i++) {
            if (q[i] == 0) return i;
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
