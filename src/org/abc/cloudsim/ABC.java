package org.abc.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ABC {
    private final int populationSize = 50;  // Ukuran populasi
    private final int maxIterations = 100;  // Jumlah iterasi
    private final double alpha = 0.5;
    private final double beta = 0.3;
    private final double delta = 0.2;
    
    private FitnessFunction fitnessFunction;

    public ABC(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public int[] optimize() {
        int[] bestSolution = new int[fitnessFunction.getCloudletCount()];
        double bestMakespan = Double.MAX_VALUE;
        double bestDegreeOfImbalance = Double.MAX_VALUE;

        List<int[]> population = initializePopulation();

        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < populationSize; i++) {
                int[] currentSolution = population.get(i);

                // Hitung Makespan dan Degree of Imbalance
                double makespan = fitnessFunction.calculateMakespan(currentSolution);
                double degreeOfImbalance = fitnessFunction.calculateDegreeOfImbalance(currentSolution);

                // Jika solusi lebih baik berdasarkan Makespan dan Degree of Imbalance
                if (makespan < bestMakespan && degreeOfImbalance < bestDegreeOfImbalance) {
                    bestMakespan = makespan;
                    bestDegreeOfImbalance = degreeOfImbalance;
                    bestSolution = currentSolution;
                }
            }

            // Update population (imitation of bee behavior: onlooker bees and scout bees)
            population = updatePopulation(population);
        }
        return bestSolution;
    }

    private List<int[]> initializePopulation() {
        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateSolution());
        }
        return population;
    }

    private List<int[]> updatePopulation(List<int[]> population) {
        List<int[]> newPopulation = new ArrayList<>();
        for (int[] solution : population) {
            // Simulate the onlooker and scout bee behavior
            int[] newSolution = generateSolution(); // This could also involve making small changes to the current solution
            newPopulation.add(newSolution);
        }
        return newPopulation;
    }

    private int[] generateSolution() {
        int[] solution = new int[fitnessFunction.getCloudletCount()];
        for (int i = 0; i < solution.length; i++) {
            solution[i] = new Random().nextInt(fitnessFunction.getVmCount());
        }
        return solution;
    }
}
