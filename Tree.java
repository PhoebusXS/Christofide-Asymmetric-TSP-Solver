public class Tree {

    private int[][] table;
    private int[] degCnt;

    public Tree (int n) {
        this.table = new int[n][n];
    }

    public void addEdge (int u, int v) {
        this.table[u][v] = 1;
    }

    public void addEdge (Edge e) {
        this.addEdge(e.src(), e.des());
    }

    public int degCnt (int u) {
        return this.degCnt[u];
    }

}
