package com.mycompany.mavenproject3;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SellForm extends JFrame {
    private JComboBox<String> productField;
    private JTextField stockField;
    private JTextField priceField;
    private JTextField qtyField;
    private JButton processButton;
    private List<Product> products;
    private Mavenproject3 mainApp;

    public SellForm(Mavenproject3 mainApp) {
        this.mainApp = mainApp;
        this.products = loadProductsFromDB();

        setTitle("WK. Cuan | Jual Barang");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel sellPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dropdown produk
        gbc.gridx = 0; gbc.gridy = 0;
        sellPanel.add(new JLabel("Barang:"), gbc);

        productField = new JComboBox<>();
        for (Product p : products) {
            productField.addItem(p.getName());
        }
        gbc.gridx = 1;
        sellPanel.add(productField, gbc);

        // Stok
        gbc.gridx = 0; gbc.gridy = 1;
        sellPanel.add(new JLabel("Stok:"), gbc);

        stockField = new JTextField(8);
        stockField.setEditable(false);
        gbc.gridx = 1;
        sellPanel.add(stockField, gbc);

        // Harga
        gbc.gridx = 0; gbc.gridy = 2;
        sellPanel.add(new JLabel("Harga Satuan:"), gbc);

        priceField = new JTextField(8);
        priceField.setEditable(false);
        gbc.gridx = 1;
        sellPanel.add(priceField, gbc);

        // Qty
        gbc.gridx = 0; gbc.gridy = 3;
        sellPanel.add(new JLabel("Qty:"), gbc);

        qtyField = new JTextField(8);
        gbc.gridx = 1;
        sellPanel.add(qtyField, gbc);

        // Tombol proses
        processButton = new JButton("Proses");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        sellPanel.add(processButton, gbc);

        add(sellPanel);

        // Update field saat produk dipilih
        productField.addActionListener(e -> updateFields());
        updateFields();

        processButton.addActionListener(e -> processTransaction());
    }

    // Ambil produk dari database
    private List<Product> loadProductsFromDB() {
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product")) {
            while (rs.next()) {
                list.add(new Product(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load produk: " + e.getMessage());
        }
        return list;
    }

    // Ambil diskon dari database
    private double getDiskonProduk(String namaProduk) {
        double diskon = 0;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT discount_percent FROM promotion WHERE product_name=?")) {
            stmt.setString(1, namaProduk);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    diskon = rs.getDouble("discount_percent");
                }
            }
        } catch (Exception e) {
            // Bisa tampilkan pesan jika mau
        }
        return diskon;
    }

    // Update stok produk di database
    private void updateStokProduk(int id, int stokBaru) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE product SET stock=? WHERE id=?")) {
            stmt.setInt(1, stokBaru);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal update stok: " + e.getMessage());
        }
    }

    // Insert sale record ke database
    private void insertSaleRecord(String productName, int qty, double price, String customerName) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO sale_record (product_name, qty, price, customer_name, date_time) VALUES (?, ?, ?, ?, NOW())")) {
            stmt.setString(1, productName);
            stmt.setInt(2, qty);
            stmt.setDouble(3, price);
            stmt.setString(4, customerName);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan penjualan: " + e.getMessage());
        }
    }

    // Proses transaksi penjualan
    private void processTransaction() {
        int selectedIndex = productField.getSelectedIndex();
        if (selectedIndex < 0) return;
        Product selectedProduct = products.get(selectedIndex);
        try {
            int qty = Integer.parseInt(qtyField.getText());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Qty harus lebih dari 0.");
                return;
            }
            if (qty > selectedProduct.getStock()) {
                JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!");
                return;
            }
            double hargaJual = selectedProduct.getPrice();
            double diskon = getDiskonProduk(selectedProduct.getName());
            double hargaSetelahDiskon = (diskon > 0)
                ? hargaJual - (hargaJual * diskon / 100.0)
                : hargaJual;

            String customerName = JOptionPane.showInputDialog(this, "Masukkan nama customer:");
            if (customerName == null || customerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama customer harus diisi!");
                return;
            }

            int stokBaru = selectedProduct.getStock() - qty;
            updateStokProduk(selectedProduct.getId(), stokBaru);
            insertSaleRecord(selectedProduct.getName(), qty, hargaSetelahDiskon, customerName);

            JOptionPane.showMessageDialog(this, "Transaksi berhasil!\nSisa stok: " + stokBaru +
                (diskon > 0 ? "\nDiskon: " + diskon + "%\nHarga setelah diskon: " + hargaSetelahDiskon : ""));

            // Refresh produk dari database
            this.products = loadProductsFromDB();
            updateFields();
            qtyField.setText("");
            mainApp.refreshBanner();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Qty harus berupa angka.");
        }
    }

    private void updateFields() {
        int selectedIndex = productField.getSelectedIndex();
        if (selectedIndex >= 0) {
            Product selectedProduct = products.get(selectedIndex);
            stockField.setText(String.valueOf(selectedProduct.getStock()));
            priceField.setText(String.valueOf(selectedProduct.getPrice()));
        } else {
            stockField.setText("");
            priceField.setText("");
        }
    }
}