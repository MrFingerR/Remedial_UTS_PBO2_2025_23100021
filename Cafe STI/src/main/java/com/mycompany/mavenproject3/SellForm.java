package com.mycompany.mavenproject3;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        this.products = mainApp.getProductList();

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

        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = productField.getSelectedIndex();
                Product selectedProduct = products.get(selectedIndex);
                try {
                    int qty = Integer.parseInt(qtyField.getText());

                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(SellForm.this, "Qty harus lebih dari 0.");
                        return;
                    }

                    if (qty > selectedProduct.getStock()) {
                        JOptionPane.showMessageDialog(SellForm.this, "Stok tidak mencukupi!");
                        return;
                    }

                    // Cek diskon/promosi
                    double hargaJual = selectedProduct.getPrice();
                    double diskon = 0;
                    for (Promotion promo : mainApp.promotions) {
                        if (promo.getProductName().equals(selectedProduct.getName())) {
                            diskon = promo.getDiscountPercent();
                            break;
                        }
                    }
                    double hargaSetelahDiskon = hargaJual;
                    if (diskon > 0) {
                        hargaSetelahDiskon = hargaJual - (hargaJual * diskon / 100.0);
                    }

                    // Tambahkan input nama customer
                    String customerName = JOptionPane.showInputDialog(SellForm.this, "Masukkan nama customer:");
                    if (customerName == null || customerName.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(SellForm.this, "Nama customer harus diisi!");
                        return;
                    }

                    selectedProduct.setStock(selectedProduct.getStock() - qty);
                    mainApp.sales.add(new SaleRecord(selectedProduct.getName(), qty, hargaSetelahDiskon, customerName));

                    String infoDiskon = (diskon > 0)
                        ? "\nDiskon: " + diskon + "%\nHarga setelah diskon: " + hargaSetelahDiskon
                        : "";
                    JOptionPane.showMessageDialog(SellForm.this, "Transaksi berhasil!\nSisa stok: " + selectedProduct.getStock() + infoDiskon);

                    updateFields();
                    qtyField.setText("");

                    mainApp.refreshBanner();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SellForm.this, "Qty harus berupa angka.");
                }
            }
        });
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