package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

public class SalesReportForm extends JFrame {
    private JTable saleTable;
    private DefaultTableModel tableModel;
    private JTextField dateField;
    private JButton pickDateButton;

    public SalesReportForm(Mavenproject3 mainApp) {
        setTitle("WK. Cuan | Laporan Penjualan");
        setSize(700, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel atas hanya dateField dan tombol
        JPanel topPanel = new JPanel();

        dateField = new JTextField(10);
        dateField.setEditable(false);
        pickDateButton = new JButton("Pilih Tanggal");

        topPanel.add(new JLabel("Tanggal:"));
        topPanel.add(dateField);
        topPanel.add(pickDateButton);

        // Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Produk", "Qty", "Harga", "Customer", "Tanggal"}, 0);
        saleTable = new JTable(tableModel);
        loadAllSaleData();  // Load semua data di awal

        // Event tombol pilih tanggal
        pickDateButton.addActionListener(e -> {
            JSpinner spinner = new JSpinner(new SpinnerDateModel());
            spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));

            int result = JOptionPane.showConfirmDialog(
                    this,
                    spinner,
                    "Pilih Tanggal",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                Date selectedDate = (Date) spinner.getValue();
                String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
                dateField.setText(dateStr);
                loadSaleDataByDate(dateStr);
            }
        });

        // Tambahkan ke frame
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(saleTable), BorderLayout.CENTER);
    }

    // Load semua data
    private void loadAllSaleData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sale_record");
             ResultSet rs = stmt.executeQuery()) {
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
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    // Load data berdasarkan tanggal
    private void loadSaleDataByDate(String date) {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM sale_record WHERE DATE(date_time) = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();
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
            JOptionPane.showMessageDialog(this, "Gagal memfilter data: " + e.getMessage());
        }
    }
}
