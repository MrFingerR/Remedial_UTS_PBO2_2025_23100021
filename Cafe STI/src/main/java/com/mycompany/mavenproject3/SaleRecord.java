package com.mycompany.mavenproject3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaleRecord {
    private String productName;
    private int qty;
    private double price;
    private String customerName; // Tambahan field customer
    private LocalDateTime dateTime;

    // Constructor baru dengan customer
    public SaleRecord(String productName, int qty, double price, String customerName) {
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.customerName = customerName;
        this.dateTime = LocalDateTime.now();
    }

    // Constructor lama (jika masih dipakai di tempat lain)
    public SaleRecord(String productName, int qty, double price) {
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.customerName = "-";
        this.dateTime = LocalDateTime.now();
    }

    // Jika ingin set waktu secara manual, bisa tambah constructor berikut:
    public SaleRecord(String productName, int qty, double price, String customerName, LocalDateTime dateTime) {
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.customerName = customerName;
        this.dateTime = dateTime;
    }

    public String getProductName() {
        return productName;
    }

    public int getQty() {
        return qty;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return price * qty;
    }

    public String getCustomerName() {
        return customerName;
    }

    // Getter untuk tanggal & waktu dalam format string
    public String getDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    // Getter untuk mengambil objek LocalDateTime
    public LocalDateTime getDateTimeObj() {
        return dateTime;
    }
}