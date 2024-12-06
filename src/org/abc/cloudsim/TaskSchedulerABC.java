package org.abc.cloudsim;

import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;

public class TaskSchedulerABC {
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private FitnessFunction fitnessFunction;
    private ABC abc;

    public TaskSchedulerABC(List<Vm> vmList, List<Cloudlet> cloudletList) {
        this.vmList = vmList;
        this.cloudletList = cloudletList;
        this.fitnessFunction = new FitnessFunction(vmList, cloudletList);
        this.abc = new ABC(fitnessFunction);
    }

    public void optimize() {
        // Jalankan ABC untuk mendapatkan alokasi terbaik
        int[] bestAllocation = abc.optimize();

        // Tetapkan Cloudlets ke VMs berdasarkan solusi terbaik
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);
            Vm vm = vmList.get(bestAllocation[i]);
            cloudlet.setVmId(vm.getId());
        }

        // Hitung metrik
        double makespan = fitnessFunction.calculateMakespan(bestAllocation);
        double degreeOfImbalance = fitnessFunction.calculateDegreeOfImbalance(bestAllocation);

        // Cetak hasil
        System.out.println("\n==== Hasil Optimasi ====");
        System.out.println("Makespan: " + makespan);
        System.out.println("Degree of Imbalance: " + degreeOfImbalance);
        System.out.println("========================");
    }
}
