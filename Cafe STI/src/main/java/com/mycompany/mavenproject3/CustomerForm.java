package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CustomerForm extends JFrame {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JComboBox<String> genderField;
    private JTextField umurField;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;

    public CustomerForm(Mavenproject3 mainApp) {
        setTitle("WK. Cuan | Data Customer");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Nama:"));
        nameField = new JTextField(10);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Gender:"));
        genderField = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        formPanel.add(genderField);

        formPanel.add(new JLabel("Umur:"));
        umurField = new JTextField(5);
        formPanel.add(umurField);

        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

        editButton = new JButton("Edit");
        formPanel.add(editButton);

        deleteButton = new JButton("Hapus");
        formPanel.add(deleteButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Gender", "Umur"}, 0);
        customerTable = new JTable(tableModel);
        loadCustomerData();

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Tombol Simpan (Insert/Update)
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String gender = (String) genderField.getSelectedItem();
            String umurText = umurField.getText();

            if (name.isEmpty() || umurText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int umur = Integer.parseInt(umurText);
                boolean genderBool = gender.equals("Laki-laki");

                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Update
                    int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    updateCustomer(id, name, genderBool, umur);
                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                } else {
                    // Insert
                    insertCustomer(name, genderBool, umur);
                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                }
                clearForm();
                loadCustomerData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Umur harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Edit (isi form dari tabel)
        editButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                genderField.setSelectedItem(
                    ((String)tableModel.getValueAt(selectedRow, 2)).equals("Laki-laki") ? "Laki-laki" : "Perempuan"
                );
                umurField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Tombol Hapus
        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteCustomer(id);
                    loadCustomerData();
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Ambil data customer dari database dan tampilkan di tabel
    private void loadCustomerData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customer")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getBoolean("gender") ? "Laki-laki" : "Perempuan");
                row.add(rs.getInt("umur"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    // Insert customer ke database
    private void insertCustomer(String name, boolean gender, int umur) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO customer (name, gender, umur) VALUES (?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setBoolean(2, gender);
            stmt.setInt(3, umur);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah customer: " + e.getMessage());
        }
    }

    // Update customer di database
    private void updateCustomer(int id, String name, boolean gender, int umur) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE customer SET name=?, gender=?, umur=? WHERE id=?")) {
            stmt.setString(1, name);
            stmt.setBoolean(2, gender);
            stmt.setInt(3, umur);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal update customer: " + e.getMessage());
        }
    }

    // Hapus customer dari database
    private void deleteCustomer(int id) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM customer WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus customer: " + e.getMessage());
        }
    }

    // Bersihkan form
    private void clearForm() {
        nameField.setText("");
        genderField.setSelectedIndex(0);
        umurField.setText("");
        customerTable.clearSelection();
    }
}