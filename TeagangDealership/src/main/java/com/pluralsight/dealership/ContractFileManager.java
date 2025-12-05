package com.pluralsight.dealership;

import java.io.FileWriter;
import java.io.IOException;

public class ContractFileManager {
    private static final String FILE_NAME = "contracts.csv";

    public void saveContract(Contract contract) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            StringBuilder sb = new StringBuilder();

            if (contract instanceof SalesContract sale) {
                sb.append("SALE|")
                        .append(sale.getDate()).append("|")
                        .append(sale.getCustomerName()).append("|")
                        .append(sale.getCustomerEmail()).append("|")
                        .append(sale.getVehicleSold().toDataString()).append("|")
                        .append(String.format("%.2f", sale.getTotalPrice())).append("|")
                        .append(sale.isFinanceOption() ? "YES" : "NO").append("|")
                        .append(String.format("%.2f", sale.getMonthlyPayment()))
                        .append("\n");

            } else if (contract instanceof LeaseContract lease) {
                sb.append("LEASE|")
                        .append(lease.getDate()).append("|")
                        .append(lease.getCustomerName()).append("|")
                        .append(lease.getCustomerEmail()).append("|")
                        .append(lease.getVehicleSold().toDataString()).append("|")
                        .append(String.format("%.2f", lease.getExpectedEndingValue())).append("|")
                        .append(String.format("%.2f", lease.getLeaseFee())).append("|")
                        .append(String.format("%.2f", lease.getTotalPrice())).append("|")
                        .append(String.format("%.2f", lease.getMonthlyPayment()))
                        .append("\n");
            }

            writer.write(sb.toString());
            System.out.println("✅ Contract saved successfully!");
        } catch (IOException e) {
            System.err.println("❌ Error saving contract: " + e.getMessage());
        }
    }
}
