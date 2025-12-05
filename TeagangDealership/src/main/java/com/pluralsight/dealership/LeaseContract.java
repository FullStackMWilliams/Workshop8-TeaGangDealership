package com.pluralsight.dealership;


public class LeaseContract extends Contract {
    private static final double LEASE_FEE_RATE = 0.07;
    private static final double INTEREST_RATE = 0.04;
    private static final int LEASE_MONTHS = 36;

    public LeaseContract(String date, String customerName, String customerEmail, Vehicle vehicleSold) {
        super(date, customerName, customerEmail, vehicleSold);
    }

    public double getExpectedEndingValue() {
        return getVehicleSold().getPrice() * 0.5;
    }

    public double getLeaseFee() {
        return getVehicleSold().getPrice() * LEASE_FEE_RATE;
    }

    @Override
    public double getTotalPrice() {
        return getLeaseFee() + getExpectedEndingValue();
    }

    @Override
    public double getMonthlyPayment() {
        double principal = getTotalPrice();
        double monthlyRate = INTEREST_RATE / 12.0;
        return principal * (monthlyRate / (1 - Math.pow(1 + monthlyRate, -LEASE_MONTHS)));
    }
}
