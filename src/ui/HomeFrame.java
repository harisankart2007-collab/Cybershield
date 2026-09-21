package ui;

import model.User;
import util.Session;

import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {

    Color navy = new Color(17, 24, 39);
    Color teal = new Color(0, 217, 192);
    Color lightBg = new Color(244, 246, 250);

    JButton searchButton = new JButton("Search Identifier");
    JButton reportButton = new JButton("Report Fraud");
    JButton adminButton = new JButton("Admin Panel");
    JButton logoutButton = new JButton("Logout");

    public HomeFrame() {
        setTitle("CyberShield - Home");
        setSize(440, 580);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        User user = Session.getUser();

        // top banner with welcome message
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(navy);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel title = new JLabel("CYBERSHIELD", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel welcome = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcome.setForeground(teal);

        header.add(title);
        header.add(welcome);
        add(header, BorderLayout.NORTH);

        // buttons in the middle
        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 15));
        buttons.setOpaque(false);
        styleButton(searchButton);
        styleButton(reportButton);
        styleButton(adminButton);
        styleButton(logoutButton);

        buttons.add(searchButton);
        buttons.add(reportButton);

        // admin button only for admins
        if (user.getRole().equals("ADMIN")) {
            buttons.add(adminButton);
        }
        buttons.add(logoutButton);

        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(lightBg);
        centre.add(buttons);
        add(centre, BorderLayout.CENTER);

        // button clicks
        searchButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Search screen coming soon"));
        reportButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Report screen coming soon"));
        adminButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Admin screen coming soon"));

        logoutButton.addActionListener(e -> {
            Session.clear();
            dispose();
            new AuthFrame().setVisible(true);
        });
    }

    private void styleButton(JButton b) {
        b.setPreferredSize(new Dimension(300, 45));
        b.setBackground(teal);
        b.setForeground(navy);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
    }
}