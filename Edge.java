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

    public int other (int vertex) {
        if (this.u == vertex) return v;
        if (this.v == vertex) return u;
        return -1;
    }

}
