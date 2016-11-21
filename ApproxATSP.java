import java.util.*;

public class ApproxATSP {

    private static double inf = Double.POSITIVE_INFINITY;

    private static double[][] publicCost = new double[][] {
        { inf, 0.83, 1.18, 4.03, 0.88, 1.96 },
        { 0.83, inf, 1.26, 4.03, 0.98, 1.89 },
        { 1.18, 1.26, inf, 2.00, 0.98, 1.99 },
        { 1.18, 1.26, 0.00, inf, 0.98, 1.99 },
        { 0.88, 0.98, 0.98, 3.98, inf, 1.91 },
        { 1.88, 1.96, 2.11, 4.99, 1.91, inf }
    };

    private static double[][] publicTime = new double[][] {
        { inf, 17, 26, 35, 19, 84 },
        { 17, inf, 31, 38, 24, 85 },
        { 24, 29, inf, 10, 18, 85 },
        { 33, 38, 10, inf, 27, 92 },
        { 18, 23, 19, 28, inf, 83 },
        { 86, 87, 86, 96, 84, inf }
    };

    private static double[][] taxiCost = new double[][] {
        { inf, 3.22, 6.96, 8.5, 4.98, 18.4 },
        { 4.32, inf, 7.84, 9.38, 4.76, 18.18 },
        { 8.3, 7.96, inf, 4.54, 6.42, 22.58 },
        { 8.74, 8.4, 3.22, inf, 6.64, 22.8 },
        { 5.32, 4.76, 4.98, 6.52, inf, 18.4 },
        { 22.48, 19.4, 21.48, 23.68, 21.6, inf }
    };

    private static double[][] taxiTime = new double[][] {
        { inf, 3, 14, 19, 8, 30 },
        { 6, inf, 13, 18, 8, 29 },
        { 12, 14, inf, 9, 11, 31 },
        { 13, 14, 4, inf, 12, 32 },
        { 7, 8, 9, 14, inf, 30 },
        { 32, 29, 32, 36, 30, inf }
    };

    private static double[][] footTime = new double[][] {
        { inf, 14, 69, 76, 28, 269 },
        { 14, inf, 81, 88, 39, 264 },
        { 69, 81, inf, 12, 47, 270 },
        { 76, 88, 12, inf, 55, 285 },
        { 28, 39, 47, 55, inf, 264 },
        { 269, 264, 270, 285, 264, inf }
    };

    // values in this case
    private static int numberOfPlaces = 6;
    private static double budget = 20;

    public static void main (String[] args) {
        int[] toVisit = {0,1,2,3,4,5};
        approxATSPTour(publicTime, publicCost, toVisit);
    }

    private static void approxATSPTour (double[][] time, double[][] cost, int[] toVisit) {
        // Using Christofides Algorithm
        
        double[][] newTime = toSymmetric(time); // Convert asymmetric graph to symmetric, by adding ghost vertices
        Prim mst = new Prim(time); // MST by Prim
        double[][] subTime = subGraph(time, mst.oddDegreeV()); // Obtain subgraph
        ArrayList<Edge> matches = minGreedyMatch(subTime); // An approx. to minimum perfect match
        for (Edge e : matches) subTime[e.src()][e.des()] = newTime[e.src()][e.des()]; // Add matches to subGraph
        ArrayList<Integer> route = eulerTour(newTime); // Getting eulerian cycle
        route = deleteDuplicate(route); // Merge ghost vertices
        route = deleteRivisited(route); // TODO: Delete revisited vertices
        planTransportation(route); // TODO: Based on budget left, take taxi
    }

    private static double[][] toSymmetric (double[][] g) {
        int size = g.length * 2;
        double[][] newG = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((i<size/2 && j<size/2) || (i>=size/2 && j>=size/2)) {
                    newG[i][j] = inf;
                } else if (i-j==size/2 || j-i==size/2) {
                    newG[i][j] = -inf;
                } else if (i>=size/2) {
                    newG[i][j] = g[i-size/2][j];
                } else {
                    newG[i][j] = g[size/2-1-i][size-1-j];
                }
            }
        }
        return newG;
    }

    private static double[][] subGraph (double[][] g, int[] vToKeep) {
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g.length; j++ ) {
                if (vToKeep[i] == 0 | vToKeep[j] == 0) g[i][j] = inf;
            }
        }
        return g;
    }

    private static ArrayList<Edge> minGreedyMatch(double[][] g) {
        ArrayList<Edge> matches = new ArrayList<Edge>();

        // get edges in graph
        ArrayList<Edge> allEdges = new ArrayList<Edge>();
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g.length; j++) {
                if (g[i][j] != inf) {
                    allEdges.add(new Edge(i, j));
                }
            }
        }

        // sort edges (ascending)
        Collections.sort(allEdges, (e1, e2) -> Double.compare(getEdgeWeight(g, e1), getEdgeWeight(g, e2)));

        Set<Integer> matchedVertices = new HashSet<Integer>();
        
        for (Edge e : allEdges) {
            int src = e.src();
            int des = e.des();
            if (src != des) {
                if (!matchedVertices.contains(src) && !matchedVertices.contains(des)) {
                    matches.add(e);
                    matchedVertices.add(src);
                    matchedVertices.add(des);
                }
            }
        }

        return matches;
    }

    private static double getEdgeWeight(double[][] g, Edge e) {
        return g[e.src()][e.des()];
    }


    private static ArrayList<Integer> eulerTour (double[][] g){
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(0); // start at 0
        Stack<Integer> cycle = new Stack<Integer>();
        while (!stack.isEmpty()) {
            int v = stack.pop();
            boolean noNext = true;
            while (hasNext(g[v]) != -1) {
                stack.push(v);
                v = hasNext(g[v]);
            }
            cycle.push(v);
        }
        return new ArrayList<Integer>(cycle);
    }

    private static ArrayList<Integer> deleteDuplicate (ArrayList<Integer> input) {
        for (int i = 0; i < input.size(); i++) {
            int n = input.get(i);
            if (n >= numberOfPlaces) {
                input.set(i, n - numberOfPlaces);
            }
        }
        int i = 0;
        while (i < input.size()) {
            if (input.get(i).equals(input.get(i+1))) input.remove(i);
            i++;
        }
        return input;
    }

    private static int hasNext(double[] adj) {
        for (int i = 0; i < adj.length; i++) {
            if (adj[i] != inf) {
                return i;
            }
        }
        return -1;
    }

}