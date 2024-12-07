package org.abc.cloudsim;

import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;

public class FitnessFunction {
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    public FitnessFunction(List<Vm> vmList, List<Cloudlet> cloudletList) {
        this.vmList = vmList;
        this.cloudletList = cloudletList;
    }

    // Hitung Makespan
    public double calculateMakespan(int[] allocation) {
        double[] vmFinishTimes = new double[vmList.size()];
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);
            Vm vm = vmList.get(allocation[i]);
            double executionTime = cloudlet.getCloudletLength() / vm.getMips();
            vmFinishTimes[allocation[i]] += executionTime;
        }
        return getMax(vmFinishTimes);
    }

    // Hitung Degree of Imbalance
    public double calculateDegreeOfImbalance(int[] allocation) {
        double[] vmFinishTimes = new double[vmList.size()];
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);
            Vm vm = vmList.get(allocation[i]);
            double executionTime = cloudlet.getCloudletLength() / vm.getMips();
            vmFinishTimes[allocation[i]] += executionTime;
        }
        double avgFinishTime = getAverage(vmFinishTimes);
        double imbalance = 0.0;
        for (double finishTime : vmFinishTimes) {
            imbalance += Math.abs(finishTime - avgFinishTime);
        }
        return imbalance / vmList.size();
    }

    // Hitung Fitness: Kombinasi Makespan dan Degree of Imbalance
    public double calculateFitness(int[] allocation) {
        double makespan = calculateMakespan(allocation);
        double imbalance = calculateDegreeOfImbalance(allocation);
        return makespan + imbalance; // Kombinasi kedua metrik
    }

    // Fungsi utilitas untuk mendapatkan nilai maksimum
    private double getMax(double[] array) {
        double max = array[0];
        for (double val : array) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    // Fungsi utilitas untuk mendapatkan rata-rata
    private double getAverage(double[] array) {
        double sum = 0.0;
        for (double val : array) {
            sum += val;
        }
        return sum / array.length;
    }

    public int getCloudletCount() {
        return cloudletList.size();
    }

    public int getVmCount() {
        return vmList.size();
    }
}
