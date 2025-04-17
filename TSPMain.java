import java.io.*;
import java.util.*;

public class TSPMain {
    public static void main(String[] args) throws IOException {
        int[][] distance = Utils.readDistanceMatrix("tsp4.txt");

        long startTime = System.currentTimeMillis();
        int[] solution = TSPSolver.localSearch(distance);
        int totalCost = TSPSolver.calculateCost(solution, distance);
        long endTime = System.currentTimeMillis();

        long elapsedTime = endTime - startTime;
        Utils.writeSolution("out_tsp4.txt", solution, totalCost, elapsedTime);
    }
}

class TSPSolver {
    public static int[] localSearch(int[][] dist) {
        int n = dist.length;
        int[] path = new int[n];
        for (int i = 0; i < n; i++) path[i] = i;
        shuffleArray(path);

        boolean improved;
        do {
            improved = false;
            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    swap(path, i, j);
                    int newCost = calculateCost(path, dist);
                    swap(path, i, j);

                    if (newCost < calculateCost(path, dist)) {
                        swap(path, i, j);
                        improved = true;
                    }
                }
            }
        } while (improved);

        return path;
    }

    public static int calculateCost(int[] path, int[][] dist) {
        int cost = 0, n = path.length;
        for (int i = 0; i < n - 1; i++) cost += dist[path[i]][path[i + 1]];
        cost += dist[path[n - 1]][path[0]];
        return cost;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static void shuffleArray(int[] arr) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            swap(arr, i, index);
        }
    }
}

class Utils {
    public static int[][] readDistanceMatrix(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        int n = Integer.parseInt(br.readLine().trim());
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            String[] tokens = br.readLine().trim().split("\\s+");
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Integer.parseInt(tokens[j]);
            }
        }
        br.close();
        return matrix;
    }

    public static void writeSolution(String filename, int[] path, int cost, long elapsedTime) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write("Total cost: " + cost + "\n");
        bw.write("Execution time (ms): " + elapsedTime + "\n");
        for (int city : path) bw.write(city + " ");
        bw.write("\n");
        bw.close();
    }
}
