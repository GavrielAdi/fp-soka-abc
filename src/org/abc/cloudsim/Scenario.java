package org.abc.cloudsim;

public class Scenario {
    int numVms;
    int numCloudlets;
    int numHosts;
    int peCountPerHost;
    double vmMips;
    int cloudletLengthMin;
    int cloudletLengthMax;

    public Scenario(int numVms, int numCloudlets, int numHosts, int peCountPerHost, double vmMips, int cloudletLengthMin, int cloudletLengthMax) {
        this.numVms = numVms;
        this.numCloudlets = numCloudlets;
        this.numHosts = numHosts;
        this.peCountPerHost = peCountPerHost;
        this.vmMips = vmMips;
        this.cloudletLengthMin = cloudletLengthMin;
        this.cloudletLengthMax = cloudletLengthMax;
    }

    @Override
    public String toString() {
        return String.format("VMs: %d, Cloudlets: %d, Hosts: %d, PEs per Host: %d, VM MIPS: %.2f, Cloudlet Length: [%d, %d]",
                numVms, numCloudlets, numHosts, peCountPerHost, vmMips, cloudletLengthMin, cloudletLengthMax);
    }
}
