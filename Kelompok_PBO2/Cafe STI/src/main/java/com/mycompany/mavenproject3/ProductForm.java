package com.mycompany.mavenproject3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ProductForm extends JFrame {
    private JTable drinkTable;
    private DefaultTableModel tableModel;
    private JTextField codeField;
    private JTextField nameField;
    private JComboBox<String> categoryField;
    private JTextField priceField;
    private JTextField stockField;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;

    public ProductForm(Mavenproject3 mainApp) {
        setTitle("WK. Cuan | Stok Barang");
        setSize(600, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel form
        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Kode Barang"));
        codeField = new JTextField(5);
        formPanel.add(codeField);

        formPanel.add(new JLabel("Nama Barang:"));
        nameField = new JTextField(8);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Kategori:"));
        categoryField = new JComboBox<>(new String[]{"Coffee", "Dairy", "Juice", "Soda", "Tea"});
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField(7);
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField(5);
        formPanel.add(stockField);

        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

        editButton = new JButton("Edit");
        formPanel.add(editButton);

        deleteButton = new JButton("Hapus");
        formPanel.add(deleteButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Kode", "Nama", "Kategori", "Harga Jual", "Stok"}, 0);
        drinkTable = new JTable(tableModel);
        loadProductData();

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(drinkTable), BorderLayout.CENTER);

        // Tombol Simpan (Insert/Update)
        saveButton.addActionListener(e -> {
            String code = codeField.getText();
            String name = nameField.getText();
            String category = (String) categoryField.getSelectedItem();
            String priceText = priceField.getText();
            String stockText = stockField.getText();

            if (code.isEmpty() || name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                int stock = Integer.parseInt(stockText);

                int selectedRow = drinkTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Update
                    int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    updateProduct(id, code, name, category, price, stock);
                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                } else {
                    // Insert
                    insertProduct(code, name, category, price, stock);
                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                }
                clearForm();
                loadProductData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Edit (isi form dari tabel)
        editButton.addActionListener(e -> {
            int selectedRow = drinkTable.getSelectedRow();
            if (selectedRow != -1) {
                codeField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                nameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                categoryField.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
                priceField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                stockField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Tombol Hapus
        deleteButton.addActionListener(e -> {
            int selectedRow = drinkTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteProduct(id);
                    loadProductData();
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Ambil data produk dari database dan tampilkan di tabel
    private void loadProductData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("code"));
                row.add(rs.getString("name"));
                row.add(rs.getString("category"));
                row.add(rs.getDouble("price"));
                row.add(rs.getInt("stock"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    // Insert produk ke database
    private void insertProduct(String code, String name, String category, double price, int stock) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO product (code, name, category, price, stock) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setString(3, category);
            stmt.setDouble(4, price);
            stmt.setInt(5, stock);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah produk: " + e.getMessage());
        }
    }

    // Update produk di database
    private void updateProduct(int id, String code, String name, String category, double price, int stock) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE product SET code=?, name=?, category=?, price=?, stock=? WHERE id=?")) {
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setString(3, category);
            stmt.setDouble(4, price);
            stmt.setInt(5, stock);
            stmt.setInt(6, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal update produk: " + e.getMessage());
        }
    }

    // Hapus produk dari database
    private void deleteProduct(int id) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM product WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus produk: " + e.getMessage());
        }
    }

    // Bersihkan form
    private void clearForm() {
        codeField.setText("");
        nameField.setText("");
        categoryField.setSelectedIndex(0);
        priceField.setText("");
        stockField.setText("");
        drinkTable.clearSelection();
    }
}