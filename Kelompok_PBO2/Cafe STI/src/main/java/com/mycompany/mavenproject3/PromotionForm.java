package com.mycompany.mavenproject3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class PromotionForm extends JFrame {
    private JTable promoTable;
    private DefaultTableModel tableModel;
    private JTextField productNameField;
    private JTextField discountField;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;

    public PromotionForm(Mavenproject3 mainApp) {
        setTitle("WK. Cuan | Data Promosi");
        setSize(500, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Produk:"));
        productNameField = new JTextField(10);
        formPanel.add(productNameField);

        formPanel.add(new JLabel("Diskon (%):"));
        discountField = new JTextField(5);
        formPanel.add(discountField);

        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

        editButton = new JButton("Edit");
        formPanel.add(editButton);

        deleteButton = new JButton("Hapus");
        formPanel.add(deleteButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Produk", "Diskon (%)"}, 0);
        promoTable = new JTable(tableModel);
        loadPromotionData();

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(promoTable), BorderLayout.CENTER);

        // Tombol Simpan (Insert/Update)
        saveButton.addActionListener(e -> {
            String product = productNameField.getText();
            String discountText = discountField.getText();

            if (product.isEmpty() || discountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double discount = Double.parseDouble(discountText);

                int selectedRow = promoTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Update
                    int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    updatePromotion(id, product, discount);
                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                } else {
                    // Insert
                    insertPromotion(product, discount);
                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                }
                clearForm();
                loadPromotionData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Diskon harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Edit (isi form dari tabel)
        editButton.addActionListener(e -> {
            int selectedRow = promoTable.getSelectedRow();
            if (selectedRow != -1) {
                productNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                discountField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Tombol Hapus
        deleteButton.addActionListener(e -> {
            int selectedRow = promoTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deletePromotion(id);
                    loadPromotionData();
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Ambil data promosi dari database dan tampilkan di tabel
    private void loadPromotionData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM promotion")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("product_name"));
                row.add(rs.getDouble("discount_percent"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    // Insert promotion ke database
    private void insertPromotion(String product, double discount) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO promotion (product_name, discount_percent) VALUES (?, ?)")) {
            stmt.setString(1, product);
            stmt.setDouble(2, discount);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah promosi: " + e.getMessage());
        }
    }

    // Update promotion di database
    private void updatePromotion(int id, String product, double discount) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE promotion SET product_name=?, discount_percent=? WHERE id=?")) {
            stmt.setString(1, product);
            stmt.setDouble(2, discount);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal update promosi: " + e.getMessage());
        }
    }

    // Hapus promotion dari database
    private void deletePromotion(int id) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM promotion WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus promosi: " + e.getMessage());
        }
    }

    // Bersihkan form
    private void clearForm() {
        productNameField.setText("");
        discountField.setText("");
        promoTable.clearSelection();
    }
}