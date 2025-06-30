package com.mycompany.mavenproject3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PromotionForm extends JFrame {
    private JTable promoTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> productField;
    private JTextField discountField;
    private JButton saveButton, editButton, deleteButton;
    private List<Product> products;
    private List<Promotion> promotions;

    public PromotionForm(List<Product> products, List<Promotion> promotions) {
        this.products = products;
        this.promotions = promotions;

        setTitle("Pengaturan Diskon & Promosi");
        setSize(500, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Produk:"));
        productField = new JComboBox<>();
        for (Product p : products) {
            productField.addItem(p.getName());
        }
        formPanel.add(productField);

        formPanel.add(new JLabel("Diskon (%):"));
        discountField = new JTextField(5);
        formPanel.add(discountField);

        saveButton = new JButton("Simpan");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Hapus");
        formPanel.add(saveButton);
        formPanel.add(editButton);
        formPanel.add(deleteButton);

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Produk", "Diskon (%)"}, 0);
        promoTable = new JTable(tableModel);
        loadPromoData();

        add(new JScrollPane(promoTable), BorderLayout.CENTER);

        saveButton.addActionListener(e -> {
            String product = (String) productField.getSelectedItem();
            String discountText = discountField.getText();
            if (discountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Diskon harus diisi!");
                return;
            }
            try {
                double discount = Double.parseDouble(discountText);
                if (discount < 0 || discount > 100) {
                    JOptionPane.showMessageDialog(this, "Diskon harus 0-100!");
                    return;
                }
                int selectedRow = promoTable.getSelectedRow();
                if (selectedRow != -1) {
                    promotions.get(selectedRow).setDiscountPercent(discount);
                    tableModel.setValueAt(discount, selectedRow, 1);
                    JOptionPane.showMessageDialog(this, "Diskon diperbarui.");
                } else {
                    promotions.add(new Promotion(product, discount));
                    tableModel.addRow(new Object[]{product, discount});
                    JOptionPane.showMessageDialog(this, "Diskon ditambahkan.");
                }
                discountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Diskon harus berupa angka!");
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = promoTable.getSelectedRow();
            if (selectedRow != -1) {
                productField.setSelectedItem(tableModel.getValueAt(selectedRow, 0));
                discountField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = promoTable.getSelectedRow();
            if (selectedRow != -1) {
                promotions.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                discountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
            }
        });
    }

    private void loadPromoData() {
        tableModel.setRowCount(0);
        for (Promotion promo : promotions) {
            tableModel.addRow(new Object[]{promo.getProductName(), promo.getDiscountPercent()});
        }
    }
}