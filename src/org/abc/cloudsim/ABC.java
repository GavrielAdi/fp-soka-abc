package org.abc.cloudsim;

import java.util.*;

/**
 * Implementasi algoritma Artificial Bee Colony (ABC) dengan parameter sesuai paper.
 */
public class ABC {
    // Parameter dari paper
    private final int numberOfBees = 100;            // Jumlah total lebah
    private final int numberOfActive = 75;           // Jumlah lebah aktif
    private final int numberOfScout = 15;            // Jumlah lebah scout
    private final int numberOfInactive = 10;         // Jumlah lebah inaktif
    private final int maxNumberOfVisits = 70;        // Jumlah maksimal kunjungan
    private final double probMistake = 0.01;        // Probabilitas kesalahan
    private final double probPersuasion = 0.90;     // Probabilitas persuasi
    private final int tmax = 100;                    // Iterasi maksimal

    private FitnessFunction fitnessFunction;         // Fungsi Fitness
    private int[] bestSolution;                      // Solusi terbaik
    private double bestFitness = Double.MAX_VALUE;   // Fitness terbaik
    private int[] visitCounts;                       // Jumlah kunjungan per solusi

    public ABC(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
        this.visitCounts = new int[fitnessFunction.getCloudletCount()]; // Inisialisasi array kunjungan
    }

    // Optimasi dengan algoritma ABC
    public int[] optimize() {
        // Inisialisasi solusi awal
        int[] currentSolution = generateInitialSolution();
        double currentFitness = fitnessFunction.calculateFitness(currentSolution);

        for (int iter = 0; iter < tmax; iter++) {
            List<int[]> activeBees = generateBees(numberOfActive);  // Lebah aktif
            List<int[]> scoutBees = generateBees(numberOfScout);    // Lebah scout

            // Proses lebah aktif
            for (int i = 0; i < numberOfActive; i++) {
                activeBees.get(i)[0] = exploreActiveBee(activeBees.get(i)[0], probPersuasion);
            }

            // Proses lebah scout
            for (int i = 0; i < numberOfScout; i++) {
                scoutBees.get(i)[0] = exploreScoutBee(scoutBees.get(i)[0], probMistake);
            }

            // Evaluasi fitness dan update solusi terbaik
            for (int i = 0; i < activeBees.size(); i++) {
                double fitness = fitnessFunction.calculateFitness(activeBees.get(i));
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestSolution = activeBees.get(i);
                }
            }

            // Update jumlah kunjungan
            updateVisits();
        }
        return bestSolution;
    }

    // Fungsi untuk menggenerasi solusi awal secara acak
    private int[] generateInitialSolution() {
        int[] solution = new int[fitnessFunction.getCloudletCount()];
        Random random = new Random();
        for (int i = 0; i < solution.length; i++) {
            solution[i] = random.nextInt(fitnessFunction.getVmCount());
        }
        return solution;
    }

    // Fungsi untuk mengeksplorasi solusi dengan lebah aktif
    private int exploreActiveBee(int bee, double prob) {
        Random random = new Random();
        if (random.nextDouble() < prob) {
            // Lakukan pencarian eksplorasi berdasarkan probabilitas
            bee = random.nextInt(fitnessFunction.getVmCount());
        }
        return bee;
    }

    // Fungsi untuk mengeksplorasi solusi dengan lebah scout
    private int exploreScoutBee(int bee, double prob) {
        Random random = new Random();
        if (random.nextDouble() < prob) {
            // Lakukan pencarian eksplorasi lebih jauh
            bee = random.nextInt(fitnessFunction.getVmCount());
        }
        return bee;
    }

    // Fungsi untuk memperbarui jumlah kunjungan
    private void updateVisits() {
        for (int i = 0; i < visitCounts.length; i++) {
            // Jika jumlah kunjungan melebihi maxNumberOfVisits, reset solusi
            if (visitCounts[i] > maxNumberOfVisits) {
                resetSolutionAtIndex(i);  // Reset solusi tertentu
                visitCounts[i] = 0;       // Reset jumlah kunjungan
            } else {
                visitCounts[i]++;         // Tambah jumlah kunjungan
            }
        }
    }

    // Fungsi untuk mereset solusi pada indeks tertentu
    private void resetSolutionAtIndex(int index) {
        Random random = new Random();
        // Reset solusi dengan VM baru secara acak
        bestSolution[index] = random.nextInt(fitnessFunction.getVmCount());
    }

    // Fungsi untuk menggenerasi solusi acak berdasarkan jumlah lebah
    private List<int[]> generateBees(int numberOfBees) {
        List<int[]> bees = new ArrayList<>();
        for (int i = 0; i < numberOfBees; i++) {
            int[] bee = new int[fitnessFunction.getCloudletCount()];
            Random random = new Random();
            for (int j = 0; j < bee.length; j++) {
                bee[j] = random.nextInt(fitnessFunction.getVmCount());
            }
            bees.add(bee);
        }
        return bees;
    }

    // Getter untuk solusi terbaik
    public int[] getBestSolution() {
        return bestSolution;
    }

    // Getter untuk fitness terbaik
    public double getBestFitness() {
        return bestFitness;
    }
}
