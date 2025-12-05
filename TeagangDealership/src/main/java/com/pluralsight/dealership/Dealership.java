package com.pluralsight.dealership;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Dealership {

    private String name;
    private String address;
    private String phone;
    private final List<Vehicle> inventory;

    public Dealership(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.inventory = new ArrayList<>();
    }

    // --- Basic Getters ---
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    // --- Add Vehicle (with Duplicate VIN Check) ---
    public boolean addVehicle(Vehicle vehicle) {
        for (Vehicle existing : inventory) {
            if (existing.getVin() == vehicle.getVin()) {
                System.out.println("⚠️ A vehicle with VIN " + vehicle.getVin() + " already exists in inventory.");
                return false; // Duplicate detected
            }
        }
        inventory.add(vehicle);
        return true; // Added successfully
    }

    // --- Remove Vehicle by VIN ---
    public boolean removeVehicleByVin(int vin) {
        return inventory.removeIf(v -> v.getVin() == vin);
    }

    // --- Get All Vehicles ---
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(inventory);
    }

    // --- Filtering / Query Methods ---
    public List<Vehicle> getVehiclesByPrice(double min, double max) {
        return inventory.stream()
                .filter(v -> v.getPrice() >= min && v.getPrice() <= max)
                .collect(Collectors.toList());
    }

    public List<Vehicle> getVehiclesByMakeModel(String make, String model) {
        String m1 = make == null ? "" : make.toLowerCase(Locale.ROOT).trim();
        String m2 = model == null ? "" : model.toLowerCase(Locale.ROOT).trim();
        return inventory.stream()
                .filter(v ->
                        (m1.isEmpty() || v.getMake().toLowerCase(Locale.ROOT).contains(m1)) &&
                                (m2.isEmpty() || v.getModel().toLowerCase(Locale.ROOT).contains(m2)))
                .collect(Collectors.toList());
    }

    public List<Vehicle> getVehiclesByYear(int minYear, int maxYear) {
        return inventory.stream()
                .filter(v -> v.getYear() >= minYear && v.getYear() <= maxYear)
                .collect(Collectors.toList());
    }

    public List<Vehicle> getVehiclesByColor(String color) {
        String c = color == null ? "" : color.toLowerCase(Locale.ROOT).trim();
        return inventory.stream()
                .filter(v -> v.getColor().toLowerCase(Locale.ROOT).contains(c))
                .collect(Collectors.toList());
    }

    public List<Vehicle> getVehiclesByMileage(long min, long max) {
        return inventory.stream()
                .filter(v -> v.getOdometer() >= min && v.getOdometer() <= max)
                .collect(Collectors.toList());
    }

    public List<Vehicle> getVehiclesByType(String type) {
        String t = type == null ? "" : type.toLowerCase(Locale.ROOT).trim();
        return inventory.stream()
                .filter(v -> v.getType().toLowerCase(Locale.ROOT).contains(t))
                .collect(Collectors.toList());
    }
}
