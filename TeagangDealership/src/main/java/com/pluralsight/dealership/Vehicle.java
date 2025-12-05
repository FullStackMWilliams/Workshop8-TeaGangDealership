package com.pluralsight.dealership;

import java.util.Objects;


public class Vehicle {

    private int vin;
    private int year;
    private String make;
    private String model;
    private String vehicle_type;
    private String color;
    private long odometer;
    private double price;


    public Vehicle(int vin, int year, String make, String model, String type, String color, long odometer, double price) {
        this.vin = vin;
        this.year = year;
        this.make = make;
        this.model = model;
        this.vehicle_type = type;
        this.color = color;
        this.odometer = odometer;
        this.price = price;
    }

    public int getVin() {
        return vin;
    }

    public void setVin(int vin) {
        this.vin = vin;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return vehicle_type;
    }

    public void setType(String type) {
        this.vehicle_type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getOdometer() {
        return odometer;
    }

    public void setOdometer(long odometer) {
        this.odometer = odometer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String toPipe(){
        return vin + "|" + year + "|" + make + "|" + model + "|" + vehicle_type + "|" + color + "|" + odometer + "|" + String.format("%.2f", price);
    }
    public String toString() {
        return String.format("%-6d %-4d %-10s %-12s %-6s %-10s %9d $%,10.2f", vin, year, make, model, vehicle_type, color, odometer, price);
    }

    public static Vehicle fromPipe(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] parts = line.split("\\|");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid vehicle data format: " + line);
        }

        int vin = Integer.parseInt(parts[0].trim());
        int year = Integer.parseInt(parts[1].trim());
        String make = parts[2].trim();
        String model = parts[3].trim();
        String type = parts[4].trim();
        String color = parts[5].trim();
        int odometer = Integer.parseInt(parts[6].trim());
        double price = Double.parseDouble(parts[7].trim());

        return new Vehicle(vin, year, make, model, type, color, odometer, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vehicle vehicle = (Vehicle) o;
        return vin == vehicle.vin &&
                year == vehicle.year &&
                odometer == vehicle.odometer &&
                Double.compare(vehicle.price, price) == 0 &&
                Objects.equals(make, vehicle.make) &&
                Objects.equals(model, vehicle.model) &&
                Objects.equals(vehicle_type, vehicle.vehicle_type) &&
                Objects.equals(color, vehicle.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, year, make, model, vehicle_type, color, odometer, price);
    }

    public String toDataString() {
        return getVin() + "|" + getYear() + "|" + getMake() + "|" + getModel() + "|" +
                 "|" + getColor() + "|" + getOdometer() + "|" + getPrice();
    }

}