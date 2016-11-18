public class Edge {

    private int u, v;

    public Edge (int u, int v) {
        this.u = u;
        this.v = v;
    }

    public int src () {
        return this.u;
    }

    public int des () {
        return this.v;
    }

}
