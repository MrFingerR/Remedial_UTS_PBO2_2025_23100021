package com.mycompany.mavenproject3;

public class Promotion {
    private String productName;
    private double discountPercent; // contoh: 10 berarti diskon 10%

    public Promotion(String productName, double discountPercent) {
        this.productName = productName;
        this.discountPercent = discountPercent;
    }

    public String getProductName() { return productName; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
}
