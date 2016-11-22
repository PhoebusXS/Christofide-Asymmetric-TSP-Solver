import java.util.*;

public class Tree {

    public int[][] table;
    private int[] degCnt;

    public Tree (int n) {
        this.table = new int[n][n];
        this.degCnt = new int[n];
    }

    public void addEdge (int u, int v) {
        this.table[u][v] = 1;
        this.degCnt[u]++;
        this.degCnt[v]++;
    }

    public void addEdge (Edge e) {
        this.addEdge(e.src(), e.des());
    }

    public int degCnt (int u) {
        return this.degCnt[u];
    }

}
