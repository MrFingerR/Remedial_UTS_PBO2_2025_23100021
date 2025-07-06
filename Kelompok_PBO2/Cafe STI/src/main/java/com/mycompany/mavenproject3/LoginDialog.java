package com.mycompany.mavenproject3;

import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginDialog extends JDialog {
    private boolean succeeded;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        add(new JLabel("Username:"));
        usernameField = new JTextField(12);
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField(12);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        add(loginButton);
        add(cancelButton);

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            // Username dan password bisa diganti sesuai kebutuhan
            if ("admin".equals(user) && "1234".equals(pass)) {
                succeeded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login", JOptionPane.ERROR_MESSAGE);
                usernameField.setText("");
                passwordField.setText("");
                succeeded = false;
            }
        });

        cancelButton.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}