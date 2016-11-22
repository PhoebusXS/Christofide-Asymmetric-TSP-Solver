import java.util.*;

public class ApproxATSP {

    private static final double inf = Double.POSITIVE_INFINITY;

    private static final double[][] publicCost = new double[][] {
        { inf, 0.83, 1.18, 4.03, 0.88, 1.96 },
        { 0.83, inf, 1.26, 4.03, 0.98, 1.89 },
        { 1.18, 1.26, inf, 2.00, 0.98, 1.99 },
        { 1.18, 1.26, 0.00, inf, 0.98, 1.99 },
        { 0.88, 0.98, 0.98, 3.98, inf, 1.91 },
        { 1.88, 1.96, 2.11, 4.99, 1.91, inf }
    };

    private static final double[][] publicTime = new double[][] {
        { inf, 17, 26, 35, 19, 84 },
        { 17, inf, 31, 38, 24, 85 },
        { 24, 29, inf, 10, 18, 85 },
        { 33, 38, 10, inf, 27, 92 },
        { 18, 23, 19, 28, inf, 83 },
        { 86, 87, 86, 96, 84, inf }
    };

    private static final double[][] taxiCost = new double[][] {
        { inf, 3.22, 6.96, 8.5, 4.98, 18.4 },
        { 4.32, inf, 7.84, 9.38, 4.76, 18.18 },
        { 8.3, 7.96, inf, 4.54, 6.42, 22.58 },
        { 8.74, 8.4, 3.22, inf, 6.64, 22.8 },
        { 5.32, 4.76, 4.98, 6.52, inf, 18.4 },
        { 22.48, 19.4, 21.48, 23.68, 21.6, inf }
    };

    private static final double[][] taxiTime = new double[][] {
        { inf, 3, 14, 19, 8, 30 },
        { 6, inf, 13, 18, 8, 29 },
        { 12, 14, inf, 9, 11, 31 },
        { 13, 14, 4, inf, 12, 32 },
        { 7, 8, 9, 14, inf, 30 },
        { 32, 29, 32, 36, 30, inf }
    };

    private static final double[][] footTime = new double[][] {
        { inf, 14, 69, 76, 28, 269 },
        { 14, inf, 81, 88, 39, 264 },
        { 69, 81, inf, 12, 47, 270 },
        { 76, 88, 12, inf, 55, 285 },
        { 28, 39, 47, 55, inf, 264 },
        { 269, 264, 270, 285, 264, inf }
    };

    // values used in this case
    private static int numberOfPlaces = 6;
    private static double budget = 20;

    public static void main (String[] args) {
        /*
        // an example of how to use this class
        int[] toVisit = {2,3,5};
        int[][] plan = approxATSPTour(publicTime, publicCost, taxiTime, taxiCost, toVisit);
        System.out.println(Arrays.deepToString(plan));
        */
    }

    private static int[][] approxATSPTour (
            double[][] publicTime,
            double[][] publicCost,
            double[][] taxiTime,
            double[][] taxiCost,
            int[] toVisit
        ) {

        // Using Christofides Algorithm
        double[][] time = delUnvisitedNodes(publicTime, toVisit); // Keep only the nodes to visit, as triangle inequallity mostly holds

        double[][] newTime = toSymmetric(time); // Convert asymmetric graph to symmetric, by adding ghost vertices
        Prim mst = new Prim(time, 0); // MST by Prim
        double[][] subTime = subGraph(time, mst.oddDegreeV()); // Obtain subGraph
        ArrayList<Edge> matches = minGreedyMatch(subTime); // An approx. to minimum perfect match
        for (Edge e : matches) subTime[e.src()][e.des()] = newTime[e.src()][e.des()]; // Add matches to subGraph
        ArrayList<Integer> route = eulerTour(newTime); // Getting eulerian cycle
        route = deleteDuplicate(route); // Merge ghost vertices and delete revisited vertices
        int[] transportation = planTransportation(route, budget, publicTime, publicCost, taxiTime, taxiCost); // Based on budget left, take taxi


        // Printing reults to console
        System.out.print("Visit order: ");
        for (int i = 0; i < route.size(); i++) System.out.print(route.get(i));
        System.out.println();
        System.out.print("Taking: taxi(1), bus(0): ");
        System.out.println(Arrays.toString(transportation));

        // Arranging return value
        int[][] routeAndTrans = new int[2][transportation.length];
        for (int i = 0; i < transportation.length; i++) {
            routeAndTrans[0][i] = route.get(i + 1);
            routeAndTrans[1][i] = transportation[i];
        }
        return routeAndTrans;

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
        double[][] ans = new double[g.length][g.length];
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g.length; j++ ) {
                if (vToKeep[i] == 0 | vToKeep[j] == 0) {
                    ans[i][j] = inf;
                } else {
                    ans[i][j] = g[i][j];
                }
            }
        }
        return ans;
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
            while (hasNext(g[v]) != -1) {
                stack.push(v);
                int tmp = hasNext(g[v]);
                g[v][tmp] = inf;
                v = tmp;
            }
            cycle.push(v);
        }
        return new ArrayList<Integer>(cycle);
    }

    private static int hasNext(double[] adj) {
        for (int i = 0; i < adj.length; i++) {
            if (adj[i] != inf) {
                return i;
            }
        }
        return -1;
    }

    private static ArrayList<Integer> deleteDuplicate (ArrayList<Integer> input) {
        for (int i = 0; i < input.size(); i++) {
            int n = input.get(i);
            if (n >= numberOfPlaces) input.set(i, n - numberOfPlaces);
        } // reverting ghost vertices to real vertices
        ArrayList<Integer> output = new ArrayList<>();
        for (int i : input) {
            boolean in = false;
            for (int j : output) {
                if (i == j) {
                    in = true;
                }
            }
            if (!in) {
                output.add(i);
            }
        }
        output.add(0); // Travel back to origin
        return output;
    }

    private static int[] planTransportation (
            ArrayList<Integer> route,
            double budget,
            double[][] publicTime,
            double[][] publicCost,
            double[][] taxiTime,
            double[][] taxiCost
        ) {
        int[] busOrTaxi = new int[route.size() - 1]; // 0 -- bus, 1 -- taxi;
        double ogTotCost = 0d;
        double ogTotTime = 0d;
        for (int i = 0; i < route.size() - 1; i++) {
            ogTotCost += publicCost[route.get(i)][route.get(i+1)];
            ogTotTime += publicTime[route.get(i)][route.get(i+1)];
        }
        budget -= ogTotCost;
        ArrayList<Double> timeSaving = new ArrayList<Double> ();
        ArrayList<Double> moneySpent = new ArrayList<Double> ();
        ArrayList<Double> savingRatio = new ArrayList<Double> ();
        ArrayList<Integer> transportation = new ArrayList<Integer> ();
        double maxRatio = -inf;
        double timeDiff;
        double moneyDiff;
        double ratio;
        for (int k = 0; k < route.size() - 1; k++) {
            int i = route.get(k);
            for (int l = 1; l < route.size(); l++) {
                int j = route.get(l);
                timeDiff = publicTime[i][j] - taxiTime[i][j];
                moneyDiff = taxiCost[i][j] - publicCost[i][j];
                ratio = timeDiff / moneyDiff;
                if (ratio > maxRatio && moneyDiff <= budget) {
                    timeSaving.add(timeDiff);
                    moneySpent.add(moneyDiff);
                    savingRatio.add(ratio);
                    transportation.add(i);
                }
            }
        }
        int startSize = savingRatio.size();
        double totTime = ogTotTime;
        for (int i = startSize - 1; i >= 0 ; i--) {
            moneyDiff = moneySpent.get(i);
            if (moneyDiff < budget) { // dicided to take taxi instead
                budget -= moneyDiff;
                totTime += timeSaving.get(i);
                busOrTaxi[transportation.get(i)] = 1;
            }
        }
        // Printing costs to console
        System.out.print("Money spent: ");
        System.out.println(20 - budget);
        System.out.print("Time spent: ");
        System.out.println(totTime);
        return busOrTaxi;
    }

    private static double[][] delUnvisitedNodes (double[][] g, int[] toVisit) {
        int[] withOrigin = new int[g.length];
        for (int i = 0; i < toVisit.length; i++) {
            withOrigin[toVisit[i]] = 1;
        }
        withOrigin[0] = 1;
        return subGraph(g, withOrigin);
    }

}
