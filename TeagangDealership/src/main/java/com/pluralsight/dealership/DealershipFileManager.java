package com.pluralsight.dealership;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DealershipFileManager {

    // --- Summary statistics ---
    private int vehiclesLoaded = 0;
    private int vehiclesSkipped = 0;
    private int duplicateCount = 0;

    public void resetStats() {
        vehiclesLoaded = 0;
        vehiclesSkipped = 0;
        duplicateCount = 0;
    }

    public int getVehiclesLoaded()     { return vehiclesLoaded; }
    public int getVehiclesSkipped()    { return vehiclesSkipped; }
    public int getDuplicateCount()     { return duplicateCount; }

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


    private final java.util.List<String> skippedRecords = new java.util.ArrayList<>();

    public java.util.List<String> getSkippedRecords() {
        return skippedRecords;
    }

    private final Path dataPath;

    public DealershipFileManager() {
        this("inventory.csv");
    }

    public DealershipFileManager(String filename) {
        this.dataPath = Path.of(filename);
    }

    public Dealership getDealership() {

        resetStats(); // ✅ Reset counters each time we load

        // If file missing → create default dealership
        if (!Files.exists(dataPath)) {
            return new Dealership("Your Dealership", "123 Main st", "888-888-8888");
        }

        try (BufferedReader br = Files.newBufferedReader(dataPath, StandardCharsets.UTF_8)) {
            String header = br.readLine();

            // Handle missing or blank header
            if (header == null || header.isBlank()) {
                return new Dealership("Your Dealership", "Your address", "000-000-0000");
            }

            // Parse header
            String[] d = header.split("\\|");
            String name = d.length > 0 ? d[0].trim() : "Your Dealership";
            String address = d.length > 1 ? d[1].trim() : "123 Main st";
            String phone = d.length > 2 ? d[2].trim() : "000-000-0000";

            Dealership dealership = new Dealership(name, address, phone);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|");

                // ✅ Validate vehicle data before parsing
                if (!isValidVehicleData(p)) {
                    System.err.println("⚠️ Invalid vehicle data skipped: " + line);
                    skippedRecords.add(line);
                    vehiclesSkipped++; // ⬅️ Count skipped lines
                    continue;
                }

                try {
                    int vin = Integer.parseInt(p[0].trim());
                    int year = Integer.parseInt(p[1].trim());
                    String make = p[2].trim();
                    String model = p[3].trim();
                    String type = p[4].trim();
                    String color = p[5].trim();
                    long odometer = Long.parseLong(p[6].trim());
                    double price = Double.parseDouble(p[7].trim());

                    // ✅ Check for duplicate VIN before adding
                    if (dealership.getAllVehicles().stream().anyMatch(v -> v.getVin() == vin)) {
                        System.err.println("⚠️ Duplicate VIN " + vin + " found. Skipping duplicate record.");
                        duplicateCount++; // ⬅️ Count duplicates
                        continue;
                    }

                    dealership.addVehicle(new Vehicle(vin, year, make, model, type, color, odometer, price));
                    vehiclesLoaded++; // ⬅️ Count successfully loaded vehicles

                } catch (Exception ex) {
                    System.err.println("❌ Skipping corrupt record: " + line);
                    vehiclesSkipped++; // ⬅️ Count parsing errors as skipped
                }
            }

            return dealership;

        } catch (IOException e) {
            System.err.println("⚠️ Failed to read dealership file: " + e.getMessage());
            return new Dealership("Your Dealership", "123 Main st", "000-000-0000");
        }
    }

    public void saveDealership(Dealership d) {
        try (BufferedWriter bw = Files.newBufferedWriter(dataPath, StandardCharsets.UTF_8)) {
            bw.write(d.getName() + "|" + d.getAddress() + "|" + d.getPhone());
            bw.newLine();

            for (Vehicle vehicle : d.getAllVehicles()) {
                bw.write(vehicle.toPipe());
                bw.newLine();
            }

            System.out.println("✅ Dealership data saved successfully.");

        } catch (IOException e) {
            System.err.println("❌ Failed to save dealership file: " + e.getMessage());
        }
    }

    // --- Helper Parsing Methods ---
    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static long parseLong(String s, long def) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static double parseDouble(String s, double def) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    // --- Data Validation ---
    private boolean isValidVehicleData(String[] p) {
        try {
            if (p.length < 8) return false;

            Integer.parseInt(p[0].trim()); // vin
            Integer.parseInt(p[1].trim()); // year
            Long.parseLong(p[6].trim());   // odometer
            Double.parseDouble(p[7].trim()); // price

            // Validate required strings are not blank
            return !p[2].isBlank() && !p[3].isBlank() && !p[4].isBlank() && !p[5].isBlank();
        } catch (Exception e) {
            return false;
        }
    }
}
