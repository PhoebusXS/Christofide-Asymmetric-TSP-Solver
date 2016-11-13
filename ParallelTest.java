import java.util.Arrays;

public class ParallelTest {

    private static double inf = Double.POSITIVE_INFINITY;

    public static double[][] publicCost = new double[][]{
        { inf, 0.83, 1.18, 4.03, 0.88, 1.96 },
        { 0.83, inf, 1.26, 4.03, 0.98, 1.89 },
        { 1.18, 1.26, inf, 2.00, 0.98, 1.99 },
        { 1.18, 1.26, 0.00, inf, 0.98, 1.99 },
        { 0.88, 0.98, 0.98, 3.98, inf, 1.91 },
        { 1.88, 1.96, 2.11, 4.99, 1.91, inf }
    };

    public static double[][] publicTime = new double[][]{
        { inf, 17, 26, 35, 19, 84 },
        { 17, inf, 31, 38, 24, 85 },
        { 24, 29, inf, 10, 18, 85 },
        { 33, 38, 10, inf, 27, 92 },
        { 18, 23, 19, 28, inf, 83 },
        { 86, 87, 86, 96, 84, inf }
    };

    public static double[][] taxiCost = new double[][]{
        { inf, 3.22, 6.96, 8.5, 4.98, 18.4 },
        { 4.32, inf, 7.84, 9.38, 4.76, 18.18 },
        { 8.3, 7.96, inf, 4.54, 6.42, 22.58 },
        { 8.74, 8.4, 3.22, inf, 6.64, 22.8 },
        { 5.32, 4.76, 4.98, 6.52, inf, 18.4 },
        { 22.48, 19.4, 21.48, 23.68, 21.6, inf }
    };

    public static double[][] taxiTime = new double[][]{
        { inf, 3, 14, 19, 8, 30 },
        { 6, inf, 13, 18, 8, 29 },
        { 12, 14, inf, 9, 11, 31 },
        { 13, 14, 4, inf, 12, 32 },
        { 7, 8, 9, 14, inf, 30 },
        { 32, 29, 32, 36, 30, inf }
    };

    public static double[][] footTime = new double[][]{
        { inf, 14, 69, 76, 28, 269 },
        { 14, inf, 81, 88, 39, 264 },
        { 69, 81, inf, 12, 47, 270 },
        { 76, 88, 12, inf, 55, 285 },
        { 28, 39, 47, 55, inf, 264 },
        { 269, 264, 270, 285, 264, inf }
    };

    public static void main (String[] args) {
        for (int i = 0; i < 6; i++) {
            System.out.println("Cost:");
            System.out.println(Arrays.toString(getIndices(publicCost[i])));
            System.out.println(Arrays.toString(getIndices(taxiCost[i])));
            System.out.println("Time:");
            System.out.println(Arrays.toString(getIndices(publicTime[i])));
            System.out.println(Arrays.toString(getIndices(taxiTime[i])));
            System.out.println(Arrays.toString(getIndices(footTime[i])));
            System.out.println("=========================");
        }
    }

    public static int[] getIndices(double[] originalArray)
    {
        int len = originalArray.length;

        double[] sortedCopy = originalArray.clone();
        int[] indices = new int[len];

        // Sort the copy
        Arrays.sort(sortedCopy);

        // Go through the original array: for the same index, fill the position where the
        // corresponding number is in the sorted array in the indices array
        for (int index = 0; index < len; index++)
            indices[index] = Arrays.binarySearch(sortedCopy, originalArray[index]);

        return indices;
    }
}

// result:
// time of different transports is mostly parallel

/*
Cost:
[5, 0, 2, 4, 1, 3]
[5, 0, 2, 3, 1, 4]
Time:
[5, 0, 2, 3, 1, 4]
[5, 0, 2, 3, 1, 4]
[5, 0, 2, 3, 1, 4]
=========================
Cost:
[0, 5, 2, 4, 1, 3]
[0, 5, 2, 3, 1, 4]
Time:
[0, 5, 2, 3, 1, 4]
[0, 5, 2, 3, 1, 4]
[0, 5, 2, 3, 1, 4]
=========================
Cost:
[1, 2, 5, 4, 0, 3]
[3, 2, 5, 0, 1, 4]
Time:
[2, 3, 5, 0, 1, 4]
[2, 3, 5, 0, 1, 4]
[2, 3, 5, 0, 1, 4]
=========================
Cost:
[2, 3, 0, 5, 1, 4]
[3, 2, 0, 5, 1, 4]
Time:
[2, 3, 0, 5, 1, 4]
[2, 3, 0, 5, 1, 4]
[2, 3, 0, 5, 1, 4]
=========================
Cost:
[0, 2, 2, 4, 5, 3]
[2, 0, 1, 3, 5, 4]
Time:
[0, 2, 1, 3, 5, 4]
[0, 1, 2, 3, 5, 4]
[0, 1, 2, 3, 5, 4]
=========================
Cost:
[0, 2, 3, 4, 1, 5]
[3, 0, 1, 4, 2, 5]
Time:
[2, 3, 2, 4, 0, 5]
[2, 0, 2, 4, 1, 5]
[2, 0, 3, 4, 0, 5]
=========================
*/
