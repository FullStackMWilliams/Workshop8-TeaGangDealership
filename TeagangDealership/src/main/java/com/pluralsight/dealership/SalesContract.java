package com.pluralsight.dealership;


public class SalesContract extends Contract {
    private static final double SALES_TAX_RATE = 0.05;
    private static final double RECORDING_FEE = 100.00;

    private double processingFee;
    private boolean financeOption;

    public SalesContract(String date, String customerName, String customerEmail,
                         Vehicle vehicleSold, boolean financeOption) {
        super(date, customerName, customerEmail, vehicleSold);
        this.financeOption = financeOption;
        this.processingFee = vehicleSold.getPrice() < 10000 ? 295.00 : 495.00;
    }

    @Override
    public double getTotalPrice() {
        double salesTax = getVehicleSold().getPrice() * SALES_TAX_RATE;
        return getVehicleSold().getPrice() + salesTax + RECORDING_FEE + processingFee;
    }

    @Override
    public double getMonthlyPayment() {
        if (!financeOption) return 0.0;

        double principal = getTotalPrice();
        int months = (getVehicleSold().getPrice() >= 10000) ? 48 : 24;
        double rate = (getVehicleSold().getPrice() >= 10000) ? 0.0425 : 0.0525;
        double monthlyRate = rate / 12.0;

        return principal * (monthlyRate / (1 - Math.pow(1 + monthlyRate, -months)));
    }

    public boolean isFinanceOption() { return financeOption; }
}
