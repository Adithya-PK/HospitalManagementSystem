import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    public Login() {
        setTitle("Hospital Management System - Login");
        
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
             
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 55)); 
        JLabel logoLabel = new JLabel("CITY HOSPITAL ", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); 
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel);
        
        JPanel containerPanel = new JPanel(new GridBagLayout());
        containerPanel.setBackground(Color.WHITE);
        
        JPanel instructionPanel = new JPanel(new FlowLayout());
        instructionPanel.setBackground(Color.WHITE);

        
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        loginPanel.setPreferredSize(new Dimension(500, 350));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(250, 35));
        loginPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 35));
        loginPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 15, 15);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 45));
        loginButton.setFont(new Font("Segou UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new LoginActionListener());
        loginPanel.add(loginButton, gbc);
        
        JPanel mainLoginContainer = new JPanel();
        mainLoginContainer.setLayout(new BoxLayout(mainLoginContainer, BoxLayout.Y_AXIS));
        mainLoginContainer.setBackground(Color.WHITE);
        mainLoginContainer.add(Box.createVerticalStrut(20));
        mainLoginContainer.add(instructionPanel);
        mainLoginContainer.add(Box.createVerticalStrut(20));
        mainLoginContainer.add(loginPanel);
        
        GridBagConstraints containerGbc = new GridBagConstraints();
        containerGbc.anchor = GridBagConstraints.CENTER;
        containerPanel.add(mainLoginContainer, containerGbc);
        
        add(headerPanel, BorderLayout.NORTH);
        add(containerPanel, BorderLayout.CENTER);
    }
    
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if ("admin".equals(username) && "admin123".equals(password)) {
                JOptionPane.showMessageDialog(Login.this, "Login Successful!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new Dashboard().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(Login.this, "Invalid credentials",
                        "Error", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}