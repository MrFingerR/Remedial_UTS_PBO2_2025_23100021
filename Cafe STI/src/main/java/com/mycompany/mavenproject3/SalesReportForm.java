package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class SalesReportForm extends JFrame {
    private JTable saleTable;
    private DefaultTableModel tableModel;
    private JTextField productNameField;
    private JTextField qtyField;
    private JTextField priceField;
    private JTextField customerNameField;
    private JButton saveButton;
    private JButton deleteButton;

    public SalesReportForm(Mavenproject3 mainApp) {
        setTitle("WK. Cuan | Data Penjualan");
        setSize(700, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Produk:"));
        productNameField = new JTextField(10);
        formPanel.add(productNameField);

        formPanel.add(new JLabel("Qty:"));
        qtyField = new JTextField(5);
        formPanel.add(qtyField);

        formPanel.add(new JLabel("Harga:"));
        priceField = new JTextField(7);
        formPanel.add(priceField);

        formPanel.add(new JLabel("Customer:"));
        customerNameField = new JTextField(10);
        formPanel.add(customerNameField);

        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

        deleteButton = new JButton("Hapus");
        formPanel.add(deleteButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Produk", "Qty", "Harga", "Customer", "Tanggal"}, 0);
        saleTable = new JTable(tableModel);
        loadSaleData();

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(saleTable), BorderLayout.CENTER);

        // Tombol Simpan (Insert)
        saveButton.addActionListener(e -> {
            String product = productNameField.getText();
            String qtyText = qtyField.getText();
            String priceText = priceField.getText();
            String customer = customerNameField.getText();

            if (product.isEmpty() || qtyText.isEmpty() || priceText.isEmpty() || customer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int qty = Integer.parseInt(qtyText);
                double price = Double.parseDouble(priceText);
                String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                insertSaleRecord(product, qty, price, customer, dateTime);
                JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                clearForm();
                loadSaleData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Qty dan harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Hapus
        deleteButton.addActionListener(e -> {
            int selectedRow = saleTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteSaleRecord(id);
                    loadSaleData();
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Ambil data penjualan dari database dan tampilkan di tabel
    private void loadSaleData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM sale_record")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("product_name"));
                row.add(rs.getInt("qty"));
                row.add(rs.getDouble("price"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getString("date_time"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    // Insert sale record ke database
    private void insertSaleRecord(String product, int qty, double price, String customer, String dateTime) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO sale_record (product_name, qty, price, customer_name, date_time) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, product);
            stmt.setInt(2, qty);
            stmt.setDouble(3, price);
            stmt.setString(4, customer);
            stmt.setString(5, dateTime);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah penjualan: " + e.getMessage());
        }
    }

    // Hapus sale record dari database
    private void deleteSaleRecord(int id) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM sale_record WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus penjualan: " + e.getMessage());
        }
    }

    // Bersihkan form
    private void clearForm() {
        productNameField.setText("");
        qtyField.setText("");
        priceField.setText("");
        customerNameField.setText("");
        saleTable.clearSelection();
    }
}