package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.util.ArrayList;
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
import javax.swing.table.DefaultTableModel;

public class CustomerForm extends JFrame {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JComboBox<String> genderField;
    private JTextField umurField;
    private JButton saveButton, editButton, deleteButton;

    private List<Customer> customers = new ArrayList<>();
    private int nextId = 1;

    public CustomerForm() {
        setTitle("WK. Cuan | Data Customer");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Form
        JPanel formPanel = new JPanel();

        formPanel.add(new JLabel("Nama:"));
        nameField = new JTextField(10);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Jenis Kelamin:"));
        genderField = new JComboBox<>(new String[]{"Laki-Laki", "Perempuan"});
        formPanel.add(genderField);

        formPanel.add(new JLabel("Umur:"));
        umurField = new JTextField(5);
        formPanel.add(umurField);

        saveButton = new JButton("Simpan");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Hapus");

        formPanel.add(saveButton);
        formPanel.add(editButton);
        formPanel.add(deleteButton);

        add(formPanel, BorderLayout.NORTH);

    
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Jenis Kelamin", "Umur"}, 0);
        customerTable = new JTable(tableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String genderText = (String) genderField.getSelectedItem();
            String umurText = umurField.getText();

            if (name.isEmpty() || umurText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama dan umur harus diisi!");
                return;
            }

            try {
                int umur = Integer.parseInt(umurText);
                boolean gender = genderText.equals("Male");

                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.setValueAt(name, selectedRow, 1);
                    tableModel.setValueAt(genderText, selectedRow, 2);
                    tableModel.setValueAt(umur, selectedRow, 3);

                    Customer customer = customers.get(selectedRow);
                    customer.setName(name);
             
                    customers.set(selectedRow, new Customer(customer.getId(), name, gender, umur));

                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                } else {
                    Customer customer = new Customer(nextId++, name, gender, umur);
                    customers.add(customer);
                    tableModel.addRow(new Object[]{
                            customer.getId(), customer.getName(), customer.getGenderString(), customer.getumur()
                    });
                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                }

                nameField.setText("");
                genderField.setSelectedIndex(0);
                umurField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Umur harus berupa angka!");
            }
        });

     
        editButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
                genderField.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
                umurField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data customer yang ingin diedit.");
            }
        });

       
        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                customers.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                nameField.setText("");
                genderField.setSelectedIndex(0);
                umurField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data customer yang ingin dihapus.");
            }
        });
    }
}
