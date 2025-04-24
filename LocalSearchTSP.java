import java.io.*;
import java.util.*;

public class LocalSearchTSP {

    public static void main(String[] args) throws IOException {
        String[] inputFiles = {"tsp1.txt", "tsp2.txt", "tsp3.txt", "tsp4.txt","tsp5.txt"};
        String[] outputFiles = {"out_tsp1.txt", "out_tsp2.txt", "out_tsp3.txt", "out_tsp4.txt","out_tsp5.txt"};

        for (int i = 0; i < inputFiles.length; i++) {
            long startTime = System.currentTimeMillis();
            
            int[][] distance = readDistanceMatrix(inputFiles[i]);
            int[] bestTour = findBestTour(distance);
            int totalCost = calculateTourCost(bestTour, distance);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            saveTour(outputFiles[i], bestTour, totalCost, executionTime);
        }
    }

    static int[][] readDistanceMatrix(String fileName) throws IOException {
        Scanner sc = new Scanner(new File(fileName));
        int n = sc.nextInt();
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                dist[i][j] = sc.nextInt();
        sc.close();
        return dist;
    }

    static int[] findBestTour(int[][] distance) {
        int n = distance.length;
        int[] globalBestTour = null;
        int globalBestCost = Integer.MAX_VALUE;

        // Run local search 10 times with different starting solutions
        for (int iteration = 0; iteration < 100; iteration++) {
            // Generate initial solution using nearest neighbor
            int startCity = iteration % n;
            int[] currentTour = nearestNeighbor(distance, startCity);
            int currentCost = calculateTourCost(currentTour, distance);
            
            // Apply 2-opt local search improvement
            boolean improved;
            do {
                improved = false;
                for (int i = 0; i < n - 1; i++) {
                    for (int j = i + 1; j < n; j++) {
                        int[] newTour = twoOptSwap(currentTour, i, j);
                        int newCost = calculateTourCost(newTour, distance);
                        
                        if (newCost < currentCost) {
                            currentTour = newTour;
                            currentCost = newCost;
                            improved = true;
                        }
                    }
                }
            } while (improved);

            // Update global best if current solution is better
            if (currentCost < globalBestCost) {
                globalBestCost = currentCost;
                globalBestTour = currentTour.clone();
            }
        }

        return globalBestTour;
    }

    static int[] nearestNeighbor(int[][] distance, int startCity) {
        int n = distance.length;
        int[] tour = new int[n];
        boolean[] visited = new boolean[n];
        
        // Start from the given city
        tour[0] = startCity;
        visited[startCity] = true;
        
        // Build the tour by always choosing the nearest unvisited city
        for (int i = 1; i < n; i++) {
            int lastCity = tour[i - 1];
            int nearestCity = -1;
            int minDistance = Integer.MAX_VALUE;
            
            // Find the nearest unvisited city
            for (int j = 0; j < n; j++) {
                if (!visited[j] && distance[lastCity][j] < minDistance) {
                    minDistance = distance[lastCity][j];
                    nearestCity = j;
                }
            }
            
            tour[i] = nearestCity;
            visited[nearestCity] = true;
        }
        
        return tour;
    }

    static int calculateTourCost(int[] tour, int[][] dist) {
        int cost = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            cost += dist[tour[i]][tour[i + 1]];
        }
        cost += dist[tour[tour.length - 1]][tour[0]]; // return to start
        return cost;
    }

    static void saveTour(String fileName, int[] tour, int cost, long executionTime) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(fileName));
        out.println("Total cost: " + cost);
        double minutes = executionTime / (1000.0 * 60); // Convert ms to minutes
        out.printf("Execution time: %.2f minutes (%.0f ms)%n", minutes, (double)executionTime);
        out.print("Tour: ");
        for (int city : tour) {
            out.print(city + " ");
        }
        out.println();
        out.close();
    }

    // New method: Perform 2-opt swap between positions i and j
    static int[] twoOptSwap(int[] tour, int i, int j) {
        int[] newTour = tour.clone();
        // Reverse the segment between i and j
        while (i < j) {
            int temp = newTour[i];
            newTour[i] = newTour[j];
            newTour[j] = temp;
            i++;
            j--;
        }
        return newTour;
    }
} 