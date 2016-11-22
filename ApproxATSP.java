import java.util.*;

public class ApproxATSP {

    private final double inf = Double.POSITIVE_INFINITY;
    private int[][] routeAndTrans;
    private int numberOfPlaces;
    private double budget;

    public ApproxATSP (
            double[][] publicTime,
            double[][] publicCost,
            double[][] taxiTime,
            double[][] taxiCost,
            int[] toVisit,
            double budget
        ) {

        this.numberOfPlaces = publicTime.length;
        this.budget = budget;

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

        /*
        // Printing reults to console
        System.out.print("Visit order: ");
        for (int i = 0; i < route.size(); i++) System.out.print(route.get(i));
        System.out.println();
        System.out.print("Taking: taxi(1), bus(0): ");
        System.out.println(Arrays.toString(transportation));
        */

        // Arranging outputs into an array
        routeAndTrans = new int[2][transportation.length];
        for (int i = 0; i < transportation.length; i++) {
            this.routeAndTrans[0][i] = route.get(i + 1);
            this.routeAndTrans[1][i] = transportation[i];
        }

    }

    public int[][] getPlan () {
        return this.routeAndTrans;
    }

    private double[][] toSymmetric (double[][] g) {
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

    private double[][] subGraph (double[][] g, int[] vToKeep) {
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

    private ArrayList<Edge> minGreedyMatch(double[][] g) {
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

    private double getEdgeWeight(double[][] g, Edge e) {
        return g[e.src()][e.des()];
    }


    private ArrayList<Integer> eulerTour (double[][] g){
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

    private int hasNext(double[] adj) {
        for (int i = 0; i < adj.length; i++) {
            if (adj[i] != inf) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Integer> deleteDuplicate (ArrayList<Integer> input) {
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

    private int[] planTransportation (
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
        /*
        // Printing costs to console
        System.out.print("Money spent: ");
        System.out.println(20 - budget);
        System.out.print("Time spent: ");
        System.out.println(totTime);
        */
        return busOrTaxi;
    }

    private double[][] delUnvisitedNodes (double[][] g, int[] toVisit) {
        int[] withOrigin = new int[g.length];
        for (int i = 0; i < toVisit.length; i++) {
            withOrigin[toVisit[i]] = 1;
        }
        withOrigin[0] = 1;
        return subGraph(g, withOrigin);
    }

}
