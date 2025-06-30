package com.mycompany.mavenproject3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class SalesReportForm extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private List<SaleRecord> sales;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private JLabel subtotalLabel;

    public SalesReportForm(List<SaleRecord> sales) {
        this.sales = sales;
        setTitle("Laporan Penjualan");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {"Tanggal & Waktu", "Customer", "Produk", "Qty", "Harga Satuan", "Total"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Dari:"));
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd"));
        filterPanel.add(fromDateSpinner);

        filterPanel.add(new JLabel("Sampai:"));
        toDateSpinner = new JSpinner(new SpinnerDateModel());
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd"));
        filterPanel.add(toDateSpinner);

        JButton filterButton = new JButton("Tampilkan");
        filterPanel.add(filterButton);

        filterButton.addActionListener(e -> loadFilteredData());

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        subtotalLabel = new JLabel("Subtotal: Rp 0");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        subtotalPanel.add(subtotalLabel);
        add(subtotalPanel, BorderLayout.SOUTH);

        loadFilteredData();
    }

    private void loadFilteredData() {
        model.setRowCount(0);
        double subtotal = 0;
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();

        for (SaleRecord s : sales) {
            LocalDate saleDate = s.getDateTimeObj().toLocalDate();
            boolean include = true;
            if (fromDate != null && saleDate.isBefore(fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) include = false;
            if (toDate != null && saleDate.isAfter(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) include = false;

            if (include) {
                String hargaSatuan = rupiahFormat.format(s.getPrice()).replace(",00", "");
                String total = rupiahFormat.format(s.getTotal()).replace(",00", "");
                model.addRow(new Object[]{
                    s.getDateTime(),
                    s.getCustomerName() == null ? "-" : s.getCustomerName(),
                    s.getProductName(),
                    s.getQty(),
                    hargaSatuan,
                    total
                });
                subtotal += s.getTotal();
            }
        }
        subtotalLabel.setText("Subtotal: " + rupiahFormat.format(subtotal).replace(",00", ""));
    }
}