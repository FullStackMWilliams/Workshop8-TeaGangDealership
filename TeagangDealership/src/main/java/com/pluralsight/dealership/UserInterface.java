package com.pluralsight.dealership;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class UserInterface {
    private final Scanner in = new Scanner(System.in);
    private Dealership dealership;
    private DealershipFileManager fileManager;

    public void display() {
        init();
        boolean running = true;
        while (running) {
            printHeader();
            printMenu();
            System.out.print("👉 Choose option: ");
            String choice = in.nextLine().trim();

            switch (choice) {
                case "1" -> processPriceRange();
                case "2" -> processMakeModel();
                case "3" -> processYearRange();
                case "4" -> processColor();
                case "5" -> processMileageRange();
                case "6" -> processType();
                case "7" -> processAllVehicles();
                case "8" -> processAddVehicle();
                case "9" -> processRemoveVehicle();
                case "0" -> {
                    System.out.println("\n👋 Goodbye!");
                    running = false;
                }
                default -> System.out.println("⚠️  Invalid option. Try again.");
            }
            if (running) pause();
        }
    }

    // ============ init & rendering ============
    private void init() {
        fileManager = new DealershipFileManager(); // defaults to "inventory.csv"
        dealership = fileManager.getDealership();

        // After loading the dealership
        List<String> badRecords = fileManager.getSkippedRecords();

        if (!badRecords.isEmpty()) {
            System.out.println("\n⚠️ Some records were skipped due to invalid data:");
            for (String bad : badRecords) {
                System.out.println("   " + bad);
            }

            System.out.println("\nWould you like to fix these now? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.startsWith("y")) {
                fixSkippedRecords(fileManager, dealership, badRecords);
            } else {
                System.out.println("⏭️ Skipped fixing. You can fix them later in the inventory file.");
            }
        }

    }

    private void printHeader() {
        System.out.println("\n=========================================================");
        System.out.printf("   🚗 %s  —  %s  —  %s%n", dealership.getName(), dealership.getAddress(), dealership.getPhone());
        System.out.println("=========================================================");
    }

    private void printMenu() {
        System.out.println("""
                1 - Find vehicles within a price range
                2 - Find vehicles by make / model
                3 - Find vehicles by year range
                4 - Find vehicles by color
                5 - Find vehicles by mileage range
                6 - Find vehicles by type (car, truck, SUV, van)
                7 - List ALL vehicles
                8 - Add a vehicle
                9 - Remove a vehicle
                0 - Quit
                """);
    }

    private void fixSkippedRecords(DealershipFileManager fileManager, Dealership dealership, List<String> badRecords) {
        Scanner scanner = new Scanner(System.in);

        for (String bad : badRecords) {
            System.out.println("\n❌ Invalid record detected: " + bad);
            System.out.println("Please re-enter the full line in this format:");
            System.out.println("VIN|Year|Make|Model|Type|Color|Odometer|Price");
            System.out.print("> ");
            String fixed = scanner.nextLine().trim();

            // Validate new entry
            String[] p = fixed.split("\\|");
            if (p.length == 8) {
                try {
                    int vin = Integer.parseInt(p[0].trim());
                    int year = Integer.parseInt(p[1].trim());
                    String make = p[2].trim();
                    String model = p[3].trim();
                    String type = p[4].trim();
                    String color = p[5].trim();
                    long odometer = Long.parseLong(p[6].trim());
                    double price = Double.parseDouble(p[7].trim());

                    Vehicle v = new Vehicle(vin, year, make, model, type, color, odometer, price);
                    dealership.addVehicle(v);
                    System.out.println("✅ Record fixed and added successfully!");

                } catch (Exception e) {
                    System.err.println("⚠️ Invalid input — skipping this record for now.");
                }
            } else {
                System.err.println("⚠️ Incorrect format. Skipping this record.");
            }
        }

        // Save the updated dealership
        fileManager.saveDealership(dealership);
    }

    private void displayVehicles(List<Vehicle> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("⚠️  No matching vehicles found.");
            return;
        }
        System.out.println("VIN     YEAR  MAKE       MODEL        TYPE   COLOR          MILEAGE         PRICE");
        System.out.println("------  ----  ---------- ------------ ------ ---------- ------------  ------------");
        for (Vehicle v : list) {
            System.out.println(v);
        }
    }

    private void pause() {
        System.out.print("\nPress ENTER to continue...");
        in.nextLine();
    }

    // ============ menu handlers ============
    private void processAllVehicles() {
        displayVehicles(dealership.getAllVehicles());
    }

    private void processPriceRange() {
        double min = readDouble("Min price: ");
        double max = readDouble("Max price: ");
        displayVehicles(dealership.getVehiclesByPrice(min, max));
    }

    private void processMakeModel() {
        System.out.print("Make (blank = any): ");
        String make = in.nextLine();
        System.out.print("Model (blank = any): ");
        String model = in.nextLine();
        displayVehicles(dealership.getVehiclesByMakeModel(make, model));
    }

    private void processYearRange() {
        int min = readInt("Min year: ");
        int max = readInt("Max year: ");
        displayVehicles(dealership.getVehiclesByYear(min, max));
    }

    private void processColor() {
        System.out.print("Color: ");
        String color = in.nextLine();
        displayVehicles(dealership.getVehiclesByColor(color));
    }

    private void processMileageRange() {
        long min = readLong("Min mileage: ");
        long max = readLong("Max mileage: ");
        displayVehicles(dealership.getVehiclesByMileage(min, max));
    }

    private void processType() {
        System.out.print("Type (car, truck, suv, van, ...): ");
        String type = in.nextLine();
        displayVehicles(dealership.getVehiclesByType(type));
    }

    private void processAddVehicle() {
        System.out.println("\n➕ Add Vehicle");

        int vin = readInt("VIN (int): ");
        int year = readInt("Year: ");
        System.out.print("Make: ");
        String make = in.nextLine().trim();
        System.out.print("Model: ");
        String model = in.nextLine().trim();
        System.out.print("Type (car/truck/suv/van): ");
        String type = in.nextLine().trim().toLowerCase(Locale.ROOT);
        System.out.print("Color: ");
        String color = in.nextLine().trim();
        long mileage = readLong("Odometer (miles): ");
        double price = readDouble("Price: ");

        Vehicle v = new Vehicle(vin, year, make, model, type, color, mileage, price);

        // ✅ Check for existing VIN before adding
        boolean added = dealership.addVehicle(v);

        if (!added) {
            System.out.println("\n⚠️ A vehicle with VIN " + vin + " already exists in inventory.");
            System.out.print("Would you like to replace it? (yes/no): ");
            String response = in.nextLine().trim().toLowerCase();

            if (response.startsWith("y")) {
                dealership.removeVehicleByVin(vin);
                dealership.addVehicle(v);
                System.out.println("✅ Existing vehicle replaced successfully!");
            } else {
                System.out.println("⏭️ Skipped adding duplicate VIN: " + vin);
            }
        } else {
            System.out.println("✅ Vehicle added successfully!");
        }

        // ✅ Always save after add/replace
        fileManager.saveDealership(dealership);
        System.out.println("💾 Inventory saved.");
    }


    private void processRemoveVehicle() {
        System.out.println("\n🗑  Remove Vehicle");
        int vin = readInt("Enter VIN to remove: ");
        boolean removed = dealership.removeVehicleByVin(vin);
        if (removed) {
            fileManager.saveDealership(dealership);
            System.out.println("✅ Vehicle removed and inventory saved.");
        } else {
            System.out.println("⚠️  No vehicle with that VIN was found.");
        }
    }

    // ============ input helpers ============
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int i = Integer.parseInt(in.nextLine().trim());
                return i;
            } catch (Exception e) { System.out.println("  Please enter a whole number."); }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                long l = Long.parseLong(in.nextLine().trim());
                return l;
            } catch (Exception e) { System.out.println("  Please enter a whole number."); }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double d = Double.parseDouble(in.nextLine().trim());
                return d;
            } catch (Exception e) { System.out.println("  Please enter a number (e.g., 12345.67)."); }
        }
    }
}