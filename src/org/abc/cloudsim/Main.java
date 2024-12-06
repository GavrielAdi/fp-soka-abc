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

/**
 * Main class untuk menjalankan simulasi CloudSim dengan optimasi menggunakan algoritma 
 * Artificial Bee Colony (ABC) untuk penjadwalan tugas.
 */
public class Main {
    public static void main(String[] args) {
        try {
            int numUsers = 1; // Hanya satu pengguna
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // Membuat datacenter
            Datacenter datacenter = createDatacenter("Datacenter");

            // Membuat Datacenter Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Membuat Virtual Machines (VM)
            List<Vm> vmList = new ArrayList<>();
            int vmCount = 5; // Jumlah VM
            for (int i = 0; i < vmCount; i++) {
                Vm vm = new Vm(i, broker.getId(), 1000, 1, 2048, 10000, 1000, "Xen", new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }
            broker.submitVmList(vmList);

            // Membuat Cloudlets (tugas-tugas yang harus dijalankan)
            List<Cloudlet> cloudletList = new ArrayList<>();
            int cloudletCount = 100; // Jumlah tugas
            for (int i = 0; i < cloudletCount; i++) {
                Cloudlet cloudlet = new Cloudlet(
                    i, 
                    getRandomInRange(750, 1250), 
                    1, 
                    getRandomInRange(75, 125), 
                    getRandomInRange(25, 75), 
                    new UtilizationModelFull(), 
                    new UtilizationModelFull(), 
                    new UtilizationModelFull()
                );
                cloudlet.setUserId(broker.getId());
                cloudletList.add(cloudlet);
            }
            broker.submitCloudletList(cloudletList);

            // Gunakan Artificial Bee Colony (ABC) untuk optimasi penjadwalan
            TaskSchedulerABC TaskSchedulerABC = new TaskSchedulerABC(vmList, cloudletList);
            TaskSchedulerABC.optimize();

            // Menjalankan simulasi CloudSim
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Menampilkan hasil akhir
            List<Cloudlet> finishedTasks = broker.getCloudletReceivedList();
            printCloudletList(finishedTasks);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk mencetak daftar cloudlet setelah eksekusi
    public static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        System.out.println("\n========== OUTPUT ==========");
        System.out.println("Cloudlet ID\tStatus\tData center ID\tVM ID\tTime\tStart Time\tFinish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);

            System.out.print(cloudlet.getCloudletId() + "\t\t");

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                System.out.print("SUCCESS\t");
                System.out.print(cloudlet.getResourceId() + "\t\t");
                System.out.print(cloudlet.getVmId() + "\t");
                System.out.print(dft.format(cloudlet.getActualCPUTime()) + "\t");
                System.out.print(dft.format(cloudlet.getExecStartTime()) + "\t\t");
                System.out.print(dft.format(cloudlet.getFinishTime()) + "\t");
            } else {
                System.out.print("FAILED\t");
            }
            System.out.println();
        }
    }

    // Fungsi untuk menghasilkan nilai acak dalam rentang
    public static int getRandomInRange(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    // Fungsi untuk membuat Datacenter
    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int hostCount = 2;
        for (int i = 0; i < hostCount; i++) {
            List<Pe> peList = new ArrayList<>();
            int peCount = 4;
            for (int j = 0; j < peCount; j++) {
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
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
