package org.abc.cloudsim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class Main {
    public static void main(String[] args) {
        try {
            // Definisikan beberapa skenario
            List<Scenario> scenarios = List.of(
                new Scenario(5, 100, 2, 4, 1000, 750, 1250),  // Skenario 1
                new Scenario(10, 200, 3, 4, 1200, 800, 1500) // Skenario 2
            );

            // Jalankan setiap skenario
            for (int i = 0; i < scenarios.size(); i++) {
                Scenario scenario = scenarios.get(i);
                System.out.println("\n=== Running Scenario " + (i + 1) + " ===");
                runScenarioMultipleTimes(scenario, 10); // Jalankan 10 kali per skenario
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk menjalankan satu skenario beberapa kali dengan output tabel
    public static void runScenarioMultipleTimes(Scenario scenario, int times) {
        List<Double> makespanResults = new ArrayList<>();
        List<Double> imbalanceResults = new ArrayList<>();

        System.out.println("Scenario: " + scenario.toString());
        printTableHeader();

        for (int i = 0; i < times; i++) {
            double[] results = runScenario(scenario);
            makespanResults.add(results[0]);
            imbalanceResults.add(results[1]);

            // Cetak hasil untuk iterasi ini
            printTableRow(i + 1, results[0], results[1]);
        }

        // Hitung rata-rata hasil
        double avgMakespan = makespanResults.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgImbalance = imbalanceResults.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Cetak rata-rata hasil
        printTableFooter(avgMakespan, avgImbalance);
    }

    // Fungsi untuk menjalankan satu skenario
    public static double[] runScenario(Scenario scenario) {
        try {
            int numUsers = 1; // Hanya satu pengguna
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // Buat datacenter
            Datacenter datacenter = createDatacenter(scenario.numHosts, scenario.peCountPerHost);

            // Buat broker
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Buat Virtual Machines (VM)
            List<Vm> vmList = new ArrayList<>();
            for (int i = 0; i < scenario.numVms; i++) {
                Vm vm = new Vm(i, broker.getId(), scenario.vmMips, 1, 2048, 10000, 1000, "Xen", new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }
            broker.submitVmList(vmList);

            // Buat Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<>();
            for (int i = 0; i < scenario.numCloudlets; i++) {
                Cloudlet cloudlet = new Cloudlet(
                    i,
                    getRandomInRange(scenario.cloudletLengthMin, scenario.cloudletLengthMax),
                    1,
                    getRandomInRange(75, 125),
                    getRandomInRange(25, 75),
                    new UtilizationModelFull(),
                    new UtilizationModelFull(),
                    new UtilizationModelFull());
                cloudlet.setUserId(broker.getId());
                cloudletList.add(cloudlet);
            }
            broker.submitCloudletList(cloudletList);

            // Optimasi dengan ABC
            TaskSchedulerABC taskScheduler = new TaskSchedulerABC(vmList, cloudletList);
            taskScheduler.optimize();

            // Hitung hasil
            double makespan = taskScheduler.getFitnessFunction().calculateMakespan(taskScheduler.getBestAllocation());
            double imbalance = taskScheduler.getFitnessFunction().calculateDegreeOfImbalance(taskScheduler.getBestAllocation());

            // Kembalikan hasil tanpa mencetak ke console
            return new double[]{makespan, imbalance};
        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
        }
    }

    // Helper untuk menghasilkan nilai acak dalam rentang
    public static int getRandomInRange(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
    
    // Helper untuk membuat Datacenter
    private static Datacenter createDatacenter(int numHosts, int peCountPerHost) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            for (int j = 0; j < peCountPerHost; j++) {
                peList.add(new Pe(j, new PeProvisionerSimple(1000)));
            }
            Host host = new Host(i, new RamProvisionerSimple(16384), new BwProvisionerSimple(100000), 1000000, peList, new VmSchedulerTimeShared(peList));
            hostList.add(host);
        }

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, timeZone, costPerSec, costPerMem, costPerStorage, costPerBw);

        try {
            return new Datacenter("Datacenter", characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper untuk mencetak header tabel
    public static void printTableHeader() {
        System.out.printf("%-10s | %-15s | %-25s%n", "Iteration", "Makespan", "Degree of Imbalance");
        System.out.println("-------------------------------------------------------------");
    }

    // Helper untuk mencetak baris tabel
    public static void printTableRow(int iteration, double makespan, double imbalance) {
        System.out.printf("%-10d | %-15.6f | %-25.6f%n", iteration, makespan, imbalance);
    }

    // Helper untuk mencetak footer hasil rata-rata
    public static void printTableFooter(double avgMakespan, double avgImbalance) {
        System.out.println("-------------------------------------------------------------");
        System.out.printf("%-10s | %-15.6f | %-25.6f%n", "Average", avgMakespan, avgImbalance);
        System.out.println();
    }
}
