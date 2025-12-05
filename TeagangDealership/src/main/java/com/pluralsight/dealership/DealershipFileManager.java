package com.pluralsight.dealership;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DealershipFileManager {

    // --- Statistics (kept for UI compatibility) ---
    private int vehiclesLoaded = 0;
    private int vehiclesSkipped = 0;
    private int duplicateCount = 0;

    public void resetStats() {
        vehiclesLoaded = 0;
        vehiclesSkipped = 0;
        duplicateCount = 0;
    }

    public int getVehiclesLoaded()  { return vehiclesLoaded; }
    public int getVehiclesSkipped() { return vehiclesSkipped; }
    public int getDuplicateCount()  { return duplicateCount; }

    public void printSummaryReport() {
        System.out.println("\n📊 Dealership Data Summary");
        System.out.println("--------------------------");
        System.out.println("✅ Vehicles loaded:   " + vehiclesLoaded);
        System.out.println("⚠️ Skipped bad lines: " + vehiclesSkipped);
        System.out.println("🚫 Duplicates ignored: " + duplicateCount);
        int total = vehiclesLoaded + vehiclesSkipped + duplicateCount;
        System.out.println("--------------------------");
        System.out.println("📁 Total records processed: " + total + "\n");
    }

    // Kept for compatibility; DB does not skip records
    private final List<String> skippedRecords = new ArrayList<>();
    public List<String> getSkippedRecords() {
        return skippedRecords;
    }

    // Constructors kept for compatibility; filename is unused
    public DealershipFileManager() { this("inventory.csv"); }
    public DealershipFileManager(String filename) { }

    /**
     * Loads the dealership and its vehicles from Supabase.
     */
    public Dealership getDealership() {
        resetStats();
        skippedRecords.clear();

        try (Connection conn = DatabaseUtil.getConnection()) {

            // 1. Load dealership (always use the first one)
            String dealershipSql = """
                SELECT dealership_id, name, address, phone
                FROM dealerships
                ORDER BY dealership_id
                LIMIT 1;
            """;

            Dealership dealership;
            int dealershipId;

            try (PreparedStatement ps = conn.prepareStatement(dealershipSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    dealershipId = rs.getInt("dealership_id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    String phone = rs.getString("phone");

                    dealership = new Dealership(name, address, phone);
                } else {
                    // No dealership found → return default
                    return new Dealership("Your Dealership", "123 Main st", "888-888-8888");
                }
            }

            // 2. Load all vehicles with that dealership_id
            String vehicleSql = """
                SELECT vin, make, model, type, year, price, color, odometer, sold
                FROM vehicles
                WHERE dealership_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(vehicleSql)) {
                ps.setInt(1, dealershipId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String vinStr  = rs.getString("vin");
                        int vin        = Integer.parseInt(vinStr);
                        String make    = rs.getString("make");
                        String model   = rs.getString("model");
                        int year       = rs.getInt("year");
                        double price   = rs.getDouble("price");
                        String color   = rs.getString("color");
                        int odometer   = rs.getInt("odometer");
                        boolean sold   = rs.getBoolean("sold");
                        String type = rs.getString("type");

                        String type1 = sold ? "Sold" : "Available";

                        Vehicle v = new Vehicle(
                         vin, year, make, model, type, color, odometer, price
                        );

                        dealership.addVehicle(v);
                        vehiclesLoaded++;
                    }
                }
            }

            return dealership;

        } catch (SQLException e) {
            System.err.println("⚠️ Error loading dealership from Supabase: " + e.getMessage());
            return new Dealership("Your Dealership", "123 Main st", "000-000-0000");
        }
    }

    /**
     * Saves the dealership + all vehicles to Supabase.
     */
    public void saveDealership(Dealership d) {
        try (Connection conn = DatabaseUtil.getConnection()) {

            conn.setAutoCommit(false);

            // 1. Ensure dealership row exists (always dealership_id=1)
            int dealershipId = upsertDealership(conn, d);

            // 2. Remove old vehicles
            String deleteSql = "DELETE FROM vehicles WHERE dealership_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, dealershipId);
                ps.executeUpdate();
            }

            // 3. Insert current vehicles
            String insertSql = """
                INSERT INTO vehicles
                (vin, make, model, year, price, color, sold, dealership_id, odometer)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Vehicle v : d.getAllVehicles()) {

                    ps.setString(1, String.valueOf(v.getVin()));
                    ps.setString(2, v.getMake());
                    ps.setString(3, v.getModel());
                    ps.setInt(4, v.getYear());
                    ps.setDouble(5, v.getPrice());
                    ps.setString(6, v.getColor());

                    boolean isSold = v.getType().equalsIgnoreCase("Sold");
                    ps.setBoolean(7, isSold);

                    ps.setInt(8, dealershipId);
                    ps.setInt(9, (int) v.getOdometer());

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            conn.commit();
            System.out.println("✅ Dealership + vehicles saved to Supabase.");

        } catch (SQLException e) {
            System.err.println("❌ Error saving dealership to Supabase: " + e.getMessage());
        }
    }

    private int upsertDealership(Connection conn, Dealership d) throws SQLException {
        String upsertSql = """
            INSERT INTO dealerships (dealership_id, name, address, phone)
            VALUES (1, ?, ?, ?)
            ON CONFLICT (dealership_id)
            DO UPDATE SET
              name = EXCLUDED.name,
              address = EXCLUDED.address,
              phone = EXCLUDED.phone
        """;

        try (PreparedStatement ps = conn.prepareStatement(upsertSql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getAddress());
            ps.setString(3, d.getPhone());
            ps.executeUpdate();
        }

        return 1;
    }
}