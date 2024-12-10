package org.abc.cloudsim;

import org.cloudbus.cloudsim.Cloudlet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelWriter {
    // This method writes results to Excel
    public static void writeResultsToExcel(List<Double> makespanResults, List<Double> imbalanceResults, String filename, List<List<Cloudlet>> cloudletAssignments) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Scenario");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Iteration");
            header.createCell(1).setCellValue("Makespan");
            header.createCell(2).setCellValue("Degree of Imbalance");
            header.createCell(3).setCellValue("Cloudlet Assignments (ID, Length, Assigned VM)");

            double totalMakespan = 0;
            double totalImbalance = 0;

            // Loop through the results and write them into the sheet
            for (int i = 0; i < makespanResults.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(makespanResults.get(i));
                row.createCell(2).setCellValue(imbalanceResults.get(i));

                List<Cloudlet> cloudletsForIteration = cloudletAssignments.get(i);
                StringBuilder assignmentDetails = new StringBuilder();
                for (Cloudlet cloudlet : cloudletsForIteration) {
                    // Format the cloudlet details as (ID, Length, Assigned VM)
                    assignmentDetails.append(String.format("(%d, %d, %d), ", cloudlet.getCloudletId(), cloudlet.getCloudletLength(), cloudlet.getVmId()));
                }
                
                // Remove the last comma and space if present
                if (assignmentDetails.length() > 0) {
                    assignmentDetails.setLength(assignmentDetails.length() - 2); // Remove last comma and space
                }

                row.createCell(3).setCellValue(assignmentDetails.toString());

                // Add to total makespan and imbalance for average calculation
                totalMakespan += makespanResults.get(i);
                totalImbalance += imbalanceResults.get(i);
            }

            // Calculate and add the averages row
            Row avgRow = sheet.createRow(makespanResults.size() + 1);
            avgRow.createCell(0).setCellValue("Average");
            avgRow.createCell(1).setCellValue(totalMakespan / makespanResults.size());
            avgRow.createCell(2).setCellValue(totalImbalance / makespanResults.size());
            avgRow.createCell(3).setCellValue(""); // Cloudlet Assignments is not needed for the average row

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}