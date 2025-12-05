package com.pluralsight.dealership;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContractFileManager {

    public void saveContract(Contract contract) {
        if (contract instanceof SalesContract sale) {
            saveSalesContract(sale);
        } else if (contract instanceof LeaseContract lease) {
            saveLeaseContract(lease);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported contract type: " + contract.getClass().getName()
            );
        }
    }

    private void saveSalesContract(SalesContract sale) {
        String sql = """
            INSERT INTO sales_contracts (
                customer_name,
                customer_email,
                vehicle_vin,
                financial_option
            )
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sale.getCustomerName());
            ps.setString(2, sale.getCustomerEmail());

            // Adjust if your Vehicle class exposes VIN differently
            String vin = String.valueOf(sale.getVehicleSold().getVin());
            ps.setString(3, vin);

            ps.setBoolean(4, sale.isFinanceOption());

            ps.executeUpdate();
            System.out.println("✅ Sales contract saved to Supabase!");

        } catch (SQLException e) {
            System.err.println("❌ Error saving sales contract: " + e.getMessage());
        }
    }

    private void saveLeaseContract(LeaseContract lease) {
        String sql = """
            INSERT INTO lease_contracts (
                customer_name,
                customer_email,
                vehicle_vin
            )
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lease.getCustomerName());
            ps.setString(2, lease.getCustomerEmail());

            // Adjust if your Vehicle class exposes VIN differently
            String vin = String.valueOf(lease.getVehicleSold().getVin());
            ps.setString(3, vin);

            ps.executeUpdate();
            System.out.println("✅ Lease contract saved to Supabase!");

        } catch (SQLException e) {
            System.err.println("❌ Error saving lease contract: " + e.getMessage());
        }
    }
}
