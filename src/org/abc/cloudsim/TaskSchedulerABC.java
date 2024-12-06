package org.abc.cloudsim;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class TaskSchedulerABC {
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private FitnessFunction fitnessFunction;
    private ABC abc;
    private int[] bestAllocation;

    public TaskSchedulerABC(List<Vm> vmList, List<Cloudlet> cloudletList) {
        this.vmList = vmList;
        this.cloudletList = cloudletList;
        this.fitnessFunction = new FitnessFunction(vmList, cloudletList);
        this.abc = new ABC(fitnessFunction);
    }

    public void optimize() {
        // Jalankan ABC untuk mendapatkan alokasi terbaik
        bestAllocation = abc.optimize();

        // Tetapkan Cloudlets ke VMs berdasarkan solusi terbaik
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);
            Vm vm = vmList.get(bestAllocation[i]);
            cloudlet.setVmId(vm.getId());
        }

        // Hitung metrik
        double makespan = fitnessFunction.calculateMakespan(bestAllocation);
        double degreeOfImbalance = fitnessFunction.calculateDegreeOfImbalance(bestAllocation);
    }

    // Metode untuk mendapatkan FitnessFunction
    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    // Metode untuk mendapatkan alokasi terbaik
    public int[] getBestAllocation() {
        return bestAllocation;
    }
}
