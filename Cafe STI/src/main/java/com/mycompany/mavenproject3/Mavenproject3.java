package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

public class Mavenproject3 extends JFrame implements Runnable {
    private String text;
    private int x;
    private int width;
    private BannerPanel bannerPanel;
    private JButton addProductButton;
    private JButton sellProductButton;

    public Mavenproject3() {
        setTitle("WK. STI Chill");
        setSize(900, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 220));

        // Panel judul tanpa shadow
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Shadow dihilangkan
            }
        };
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("☕ Selamat Datang di WK. STI Chill ☕");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        this.text = getBannerTextFromProducts();
        this.x = -getFontMetrics(new Font("Arial", Font.BOLD, 18)).stringWidth(text);

        // Panel teks berjalan tanpa shadow
        bannerPanel = new BannerPanel();
        bannerPanel.setBackground(new Color(255, 255, 240));
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(bannerPanel, BorderLayout.CENTER);

        // Panel bawah untuk tombol-tombol
        JPanel bottomPanel = new JPanel(new GridLayout(1, 5, 30, 10));
        bottomPanel.setBackground(new Color(230, 230, 250));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(25, 60, 25, 60));

        addProductButton = createRoundMenuButton("Kelola Produk", new Color(144, 238, 144), new Color(34, 139, 34));
        bottomPanel.add(addProductButton);

        sellProductButton = createRoundMenuButton("Jual Produk", new Color(255, 182, 193), new Color(220, 20, 60));
        bottomPanel.add(sellProductButton);

        JButton customerButton = createRoundMenuButton("Customer", new Color(255, 255, 153), new Color(255, 215, 0));
        customerButton.addActionListener(e -> new CustomerForm(this).setVisible(true));
        bottomPanel.add(customerButton);

        JButton reportButton = createRoundMenuButton("Laporan Penjualan", new Color(173, 216, 230), new Color(70, 130, 180));
        reportButton.addActionListener(e -> new SalesReportForm(this).setVisible(true));
        bottomPanel.add(reportButton);

        JButton promoButton = createRoundMenuButton("Diskon & Promosi", new Color(255, 228, 181), new Color(255, 140, 0));
        promoButton.addActionListener(e -> new PromotionForm(this).setVisible(true));
        bottomPanel.add(promoButton);

        add(bottomPanel, BorderLayout.SOUTH);

        addProductButton.addActionListener(e -> new ProductForm(this).setVisible(true));
        sellProductButton.addActionListener(e -> new SellForm(this).setVisible(true));

        setVisible(true);

        Thread thread = new Thread(this);
        thread.start();
    }

    // Custom rounded button dengan efek hover dan shadow tipis
    private JButton createRoundMenuButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed() || getModel().isRollover()) {
                    g2.setColor(hover);
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                // Shadow tipis (bisa dihapus jika ingin benar-benar polos)
                // g2.setColor(new Color(0,0,0,30));
                // g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 30, 30);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setForeground(Color.DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setUI(new BasicButtonUI());
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.repaint();
            }
        });
        return btn;
    }

    class BannerPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Shadow dihilangkan
            // g2.setColor(new Color(0,0,0,40));
            // g2.fillRoundRect(10, getHeight()/2-14, getWidth()-20, 28, 30, 30);
            // Text
            g2.setColor(new Color(70, 130, 180));
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.drawString(text, x, getHeight() / 2 + 8);
        }
    }

    public void setBannerText(String newText) {
        this.text = newText;
        this.x = -getFontMetrics(new Font("Arial", Font.BOLD, 20)).stringWidth(text);
    }

    public String getBannerTextFromProducts() {
    StringBuilder sb = new StringBuilder("Menu yang tersedia: ");
    try (Connection conn = DBUtil.getConnection();
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery("SELECT name FROM product")) {
        boolean first = true;
        while (rs.next()) {
            if (!first) sb.append(" | ");
            sb.append(rs.getString("name"));
            first = false;
        }
    } catch (Exception e) {
        sb.append("Gagal load produk");
    }
    return sb.toString();
}

    public void refreshBanner() {
        setBannerText(getBannerTextFromProducts());
    }

    @Override
    public void run() {
        width = getWidth();
        while (true) {
            x += 4;
            if (x > width) {
                x = -getFontMetrics(new Font("Arial", Font.BOLD, 20)).stringWidth(text);
            }
            bannerPanel.repaint();
            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        LoginDialog login = new LoginDialog(null);
        login.setVisible(true);

        if (login.isSucceeded()) {
            new Mavenproject3();
        } else {
            System.exit(0);
        }
    }
}