public class Tree {

    private int[][] table;
    private int[] degCnt;

    public Tree (int n) {
        this.table = new int[n][n];
    }

    public void addEdge (int u, int v) {
        this.table[u][v] = 1;
    }

    public int degCnt (int u) {
        return this.degCnt[u];
    }

}
