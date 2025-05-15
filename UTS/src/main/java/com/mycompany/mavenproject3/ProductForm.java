
package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
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

public class ProductForm extends JFrame {
    private JTable drinkTable;
    private DefaultTableModel tableModel;
    private JTextField codeField, nameField, priceField, stockField;
    private JComboBox<String> categoryField;
    private JButton addButton, editButton, deleteButton;

    private List<Product> products;
    private Mavenproject3 parent;

    public ProductForm(List<Product> products, Mavenproject3 parent) {
        this.products = products;
        this.parent = parent;

        setTitle("WK. Cuan | Stok Barang");
        setSize(700, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Kode Barang:"));
        codeField = new JTextField();
        formPanel.add(codeField);

        formPanel.add(new JLabel("Nama Barang:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Kategori:"));
        categoryField = new JComboBox<>(new String[]{"Coffee", "Dairy", "Juice", "Soda", "Tea"});
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField();
        formPanel.add(stockField);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Tambah");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Hapus");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Kode", "Nama", "Kategori", "Harga", "Stok"}, 0);
        drinkTable = new JTable(tableModel);
        loadProductData();
        add(new JScrollPane(drinkTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> tambahProduk());
        editButton.addActionListener(e -> editProduk());
        deleteButton.addActionListener(e -> hapusProduk());
        drinkTable.getSelectionModel().addListSelectionListener(e -> isiFormDariTabel());
    }

    private void loadProductData() {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getCode(), p.getName(), p.getCategory(), p.getPrice(), p.getStock()});
        }
    }

    private void tambahProduk() {
        try {
            Product p = new Product(
                products.size() + 1,
                codeField.getText(),
                nameField.getText(),
                (String) categoryField.getSelectedItem(),
                Double.parseDouble(priceField.getText()),
                Integer.parseInt(stockField.getText())
            );
            products.add(p);
            loadProductData();
            clearForm();
            parent.refreshBanner(); // Auto refresh banner!
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka.");
        }
    }

    private void editProduk() {
        int selectedRow = drinkTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product p = products.get(selectedRow);
            p.setCode(codeField.getText());
            p.setName(nameField.getText());
            p.setCategory((String) categoryField.getSelectedItem());
            try {
                p.setPrice(Double.parseDouble(priceField.getText()));
                p.setStock(Integer.parseInt(stockField.getText()));
                loadProductData();
                clearForm();
                parent.refreshBanner(); // Refresh banner
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan stok harus berupa angka.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin diedit.");
        }
    }

    private void hapusProduk() {
        int selectedRow = drinkTable.getSelectedRow();
        if (selectedRow >= 0) {
            products.remove(selectedRow);
            loadProductData();
            clearForm();
            parent.refreshBanner(); // Refresh banner
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dihapus.");
        }
    }

    private void isiFormDariTabel() {
        int row = drinkTable.getSelectedRow();
        if (row >= 0) {
            Product p = products.get(row);
            codeField.setText(p.getCode());
            nameField.setText(p.getName());
            categoryField.setSelectedItem(p.getCategory());
            priceField.setText(String.valueOf(p.getPrice()));
            stockField.setText(String.valueOf(p.getStock()));
        }
    }

    private void clearForm() {
        codeField.setText("");
        nameField.setText("");
        categoryField.setSelectedIndex(0);
        priceField.setText("");
        stockField.setText("");
        drinkTable.clearSelection();
    }
}
