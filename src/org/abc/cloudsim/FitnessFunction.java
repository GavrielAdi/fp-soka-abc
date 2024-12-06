package org.abc.cloudsim;

import java.util.Arrays;
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
        return Arrays.stream(vmFinishTimes).max().orElse(0.0);
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
        double avgFinishTime = Arrays.stream(vmFinishTimes).average().orElse(0.0);
        double imbalance = Arrays.stream(vmFinishTimes)
                                .map(time -> Math.abs(time - avgFinishTime))
                                .sum();
        return imbalance / vmList.size();
    }

    public int getCloudletCount() {
        return cloudletList.size();
    }

    public int getVmCount() {
        return vmList.size();
    }

    public double calculateFitness(int[] allocation) {
        double makespan = calculateMakespan(allocation);
        double degreeOfImbalance = calculateDegreeOfImbalance(allocation);
        
        // Fungsi fitness: Meminimalkan makespan dan degree of imbalance
        return makespan + degreeOfImbalance;
    }
}
