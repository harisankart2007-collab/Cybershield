package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.User;
import util.Session;

public class AuthFrame extends JFrame {

    // colours from our presentation
    Color navy = new Color(17, 24, 39);
    Color teal = new Color(0, 217, 192);
    Color lightBg = new Color(244, 246, 250);

    // login tab components
    JTextField loginEmail = new JTextField();
    JPasswordField loginPassword = new JPasswordField();
    JButton loginButton = new JButton("Log In");

    // register tab components
    JTextField regName = new JTextField();
    JTextField regEmail = new JTextField();
    JPasswordField regPassword = new JPasswordField();
    JPasswordField regConfirm = new JPasswordField();
    JButton registerButton = new JButton("Create Account");

    public AuthFrame() {
        setTitle("CyberShield");
        setSize(440, 580);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);   // opens in the middle of the screen
        setLayout(new BorderLayout());

        // top banner
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(navy);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel title = new JLabel("CYBERSHIELD", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Cyber Fraud Identifier Verification System", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(teal);

        header.add(title);
        header.add(subtitle);
        add(header, BorderLayout.NORTH);

        // two tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Login", makeLoginTab());
        tabs.addTab("Register", makeRegisterTab());
        add(tabs, BorderLayout.CENTER);

        // button clicks
        loginButton.addActionListener(e -> doLogin());
        registerButton.addActionListener(e -> doRegister());
    }

    // builds the login tab
    private JPanel makeLoginTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        addRow(form, "Email", loginEmail, 0);
        addRow(form, "Password", loginPassword, 2);
        addButton(form, loginButton, 4);

        return wrapInCentre(form);
    }

    // builds the register tab
    private JPanel makeRegisterTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        addRow(form, "Full Name", regName, 0);
        addRow(form, "Email", regEmail, 2);
        addRow(form, "Password", regPassword, 4);
        addRow(form, "Confirm Password", regConfirm, 6);
        addButton(form, registerButton, 8);

        return wrapInCentre(form);
    }

    // puts a label and a text box in the form (label on row, box on row + 1)
    private void addRow(JPanel form, String labelText, JTextField field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = row;
        gbc.insets = new Insets(12, 0, 4, 0);
        form.add(label, gbc);

        field.setPreferredSize(new Dimension(300, 36));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(field, gbc);
    }

    // adds the teal button at the bottom of the form
    private void addButton(JPanel form, JButton button, int row) {
        button.setPreferredSize(new Dimension(300, 40));
        button.setBackground(teal);
        button.setForeground(navy);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(25, 0, 0, 0);
        form.add(button, gbc);
    }

    // keeps the form in the middle so the boxes don't stretch
    private JPanel wrapInCentre(JPanel form) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(lightBg);
        wrapper.add(form);
        return wrapper;
    }

    // login button
    private void doLogin() {
        String email = loginEmail.getText().trim();
        String password = new String(loginPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // dummy check for now, will use UserDAO.login() after the database is ready
        if (email.equals("test@test.com") && password.equals("1234")) {
            User u = new User();
            u.setName("Test");
            u.setEmail(email);
            u.setRole("USER");
            Session.setUser(u);

            JOptionPane.showMessageDialog(this, "Login successful");
            // TODO: open HomeFrame and close this window
        } else {
            JOptionPane.showMessageDialog(this, "Wrong email or password.");
        }
    }

    // register button
    private void doRegister() {
        String name = regName.getText().trim();
        String email = regEmail.getText().trim();
        String password = new String(regPassword.getPassword());
        String confirm = new String(regConfirm.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email.");
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        // TODO: save to database using UserDAO.register()
        JOptionPane.showMessageDialog(this, "Registered (dummy).");
    }

    public static void main(String[] args) {
        AuthFrame frame = new AuthFrame();
        frame.setVisible(true);
    }
}