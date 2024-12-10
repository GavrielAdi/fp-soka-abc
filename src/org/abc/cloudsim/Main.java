package org.abc.cloudsim;

import java.util.*;
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
            List<Scenario> scenarios = new ArrayList<>();

            int[] vmCounts = {5, 10, 15, 20, 25};       // Jumlah VM
            int[] cloudletCounts = {50, 100, 150, 200, 250}; // Jumlah Cloudlets

            for (int vmCount : vmCounts) {
                for (int cloudletCount : cloudletCounts) {
                    scenarios.add(new Scenario(vmCount, cloudletCount, 2, 4, 1000, 750, 1250));
                }
            }

            for (int i = 0; i < scenarios.size(); i++) {
                Scenario scenario = scenarios.get(i);
                System.out.println("\n" + "=".repeat(50));
                System.out.println("=== Running Scenario " + (i + 1) + " ===");
                System.out.println("Parameters: " + scenario.toString());
                System.out.println("=".repeat(50) + "\n");

                runScenarioMultipleTimes(scenario, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runScenarioMultipleTimes(Scenario scenario, int times) {
        List<Double> makespanResults = new ArrayList<>();
        List<Double> imbalanceResults = new ArrayList<>();
        List<List<Cloudlet>> cloudletAssignments = new ArrayList<>();  // List to store Cloudlet assignments

        // Run multiple iterations of the simulation
        for (int i = 0; i < times; i++) {
            Object[] results = runScenario(scenario, i + 1);  // Get results and cloudlet assignments
            makespanResults.add((Double) results[0]);
            imbalanceResults.add((Double) results[1]);

            // Collect Cloudlet assignments for this iteration
            List<Cloudlet> cloudletsForIteration = (List<Cloudlet>) results[2];
            cloudletAssignments.add(cloudletsForIteration);
        }

        // Write results to Excel
        String filename = String.format("simulation-result/Simulation Results Scenario (VMs=%d, Cloudlets=%d).xlsx", scenario.numVms, scenario.numCloudlets);
        ExcelWriter.writeResultsToExcel(makespanResults, imbalanceResults, filename, cloudletAssignments);

    }


    public static Object[] runScenario(Scenario scenario, int iteration) {
        try {
            CloudSim.init(1, Calendar.getInstance(), false);

            Datacenter datacenter = createDatacenter(scenario.numHosts, scenario.peCountPerHost);
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Create VMs
            List<Vm> vmList = new ArrayList<>();
            for (int i = 0; i < scenario.numVms; i++) {
                vmList.add(new Vm(i, broker.getId(), scenario.vmMips, 1, 2048, 10000, 1000, "Xen", new CloudletSchedulerTimeShared()));
            }
            broker.submitVmList(vmList);

            // Create Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<>();
            for (int i = 0; i < scenario.numCloudlets; i++) {
                cloudletList.add(new Cloudlet(
                        i,
                        getRandomInRange(scenario.cloudletLengthMin, scenario.cloudletLengthMax),
                        1,
                        300,
                        300,
                        new UtilizationModelFull(),
                        new UtilizationModelFull(),
                        new UtilizationModelFull()
                ));
            }
            broker.submitCloudletList(cloudletList);

            // Simulate and assign tasks
            TaskSchedulerABC taskScheduler = new TaskSchedulerABC(vmList, cloudletList);
            taskScheduler.optimize();

            double makespan = taskScheduler.getFitnessFunction().calculateMakespan(taskScheduler.getBestAllocation());
            double imbalance = taskScheduler.getFitnessFunction().calculateDegreeOfImbalance(taskScheduler.getBestAllocation());

            // Collect the cloudlet assignments
            List<Cloudlet> cloudletAssignments = new ArrayList<>();
            for (Cloudlet cloudlet : cloudletList) {
                int assignedVmId = cloudlet.getVmId();
                System.out.println("Cloudlet ID: " + cloudlet.getCloudletId() + " assigned to VM ID: " + assignedVmId);
                cloudletAssignments.add(cloudlet);  // Store the cloudlet
            }

            // Return makespan, imbalance, and cloudlet assignments
            return new Object[]{makespan, imbalance, cloudletAssignments};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{Double.MAX_VALUE, Double.MAX_VALUE, new ArrayList<Cloudlet>()};
        }
    }



    public static List<Cloudlet> getCloudletAssignments(Scenario scenario, int iteration) {
        // This should return the actual list of Cloudlets assigned during each iteration
        List<Cloudlet> cloudletList = new ArrayList<>();
    
        
        return cloudletList;
    }

    private static Datacenter createDatacenter(int numHosts, int peCountPerHost) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            for (int j = 0; j < peCountPerHost; j++) {
                peList.add(new Pe(j, new PeProvisionerSimple(1000)));
            }
            hostList.add(new Host(i, new RamProvisionerSimple(16384), new BwProvisionerSimple(100000), 1000000, peList, new VmSchedulerTimeShared(peList)));
        }

        try {
            return new Datacenter(
                    "Datacenter",
                    new DatacenterCharacteristics("x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0),
                    new VmAllocationPolicySimple(hostList),
                    new LinkedList<>(),
                    0
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getRandomInRange(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
