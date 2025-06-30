/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.table.DefaultTableModel;

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
    private List<Product> products;

    private String getAllProductNames() {
        StringBuilder sb = new StringBuilder("Menu yang tersedia: ");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            sb.append(tableModel.getValueAt(i, 1)); 
            if (i < tableModel.getRowCount() - 1) {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
    
    public ProductForm(Mavenproject3 mainApp) {
        this.products = mainApp.getProductList(); 

        setTitle("WK. Cuan | Stok Barang");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel form pemesanan
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

        tableModel = new DefaultTableModel(new String[]{"Kode", "Nama", "Kategori", "Harga Jual", "Stok"}, 0);
        drinkTable = new JTable(tableModel);
        loadProductData(products);

        // Menampilkan panel CRUD
        add(formPanel, BorderLayout.NORTH);
        
        // Menampilkan tabel data
        JScrollPane scrollPane = new JScrollPane(drinkTable);
        add(scrollPane, BorderLayout.CENTER);

        
        saveButton.addActionListener(e -> {
            int selectedRow = drinkTable.getSelectedRow();
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

                if (selectedRow != -1) {
                    tableModel.setValueAt(code, selectedRow, 0);
                    tableModel.setValueAt(name, selectedRow, 1);
                    tableModel.setValueAt(category, selectedRow, 2);
                    tableModel.setValueAt(price, selectedRow, 3);
                    tableModel.setValueAt(stock, selectedRow, 4);

                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                } else {
                    tableModel.addRow(new Object[]{code, name, category, price, stock});
                    products.add(new Product(0, code, name, category, price, stock));

                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                }

                codeField.setText("");
                nameField.setText("");
                categoryField.setSelectedIndex(0);
                priceField.setText("");
                stockField.setText("");

                mainApp.setBannerText(getAllProductNames());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = drinkTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    String selectedCode = drinkTable.getValueAt(selectedRow, 0).toString();
                    String selectedName = drinkTable.getValueAt(selectedRow, 1).toString();
                    String selectedCategory = drinkTable.getValueAt(selectedRow, 2).toString();
                    String selectedPrice = drinkTable.getValueAt(selectedRow, 3).toString();
                    String selectedStock = drinkTable.getValueAt(selectedRow, 4).toString();

                    codeField.setText(selectedCode);
                    nameField.setText(selectedName);
                    categoryField.setSelectedItem(selectedCategory);
                    priceField.setText(selectedPrice);
                    stockField.setText(selectedStock);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = drinkTable.getSelectedRow();
            if (selectedRow != -1) {
                products.remove(selectedRow);
                tableModel.removeRow(selectedRow);

                codeField.setText("");
                nameField.setText("");
                categoryField.setSelectedIndex(0);
                priceField.setText("");
                stockField.setText("");

                mainApp.setBannerText(getAllProductNames());
            } else {
                JOptionPane.showMessageDialog(formPanel, "tidak ada yang dipilih");
            }
        });
    }

    private void loadProductData(List<Product> productList) {
        for (Product product : productList) {
            tableModel.addRow(new Object[]{
                product.getCode(), product.getName(), product.getCategory(), product.getPrice(), product.getStock()
            });
        }
    }
}
