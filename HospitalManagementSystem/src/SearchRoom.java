import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class SearchRoom extends JFrame {
    private JTextField roomNoField;
    private JTextField availabilityField;
    private JTextField priceField;
    private JTextField bedTypeField;
    
    public SearchRoom() {
        setTitle("Search Room");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(450, 50));
        JLabel headerLabel = new JLabel("Search Room", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Room No search
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomNoField = new JTextField(15);
        formPanel.add(roomNoField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(new SearchRoomListener());
        formPanel.add(searchButton, gbc);
        
        // Room details (read-only)
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Availability:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        availabilityField = new JTextField(15);
        availabilityField.setEditable(false);
        availabilityField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(availabilityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        priceField = new JTextField(15);
        priceField.setEditable(false);
        priceField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Bed Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        bedTypeField = new JTextField(15);
        bedTypeField.setEditable(false);
        bedTypeField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(bedTypeField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(46, 204, 113));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearFields());
        
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private class SearchRoomListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String roomNo = roomNoField.getText().trim();
            
            if (roomNo.isEmpty()) {
                JOptionPane.showMessageDialog(SearchRoom.this, 
                    "Please enter Room Number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                ResultSet rs = DBManager.searchRoom(roomNo);
                if (rs != null && rs.next()) {
                    availabilityField.setText(rs.getString("availability"));
                    priceField.setText(String.valueOf(rs.getDouble("price")));
                    bedTypeField.setText(rs.getString("bed_type"));
                } else {
                    JOptionPane.showMessageDialog(SearchRoom.this, 
                        "Room not found", "Error", JOptionPane.ERROR_MESSAGE);
                    clearResultFields();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(SearchRoom.this, 
                    "Error searching room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearFields() {
        roomNoField.setText("");
        clearResultFields();
    }
    
    private void clearResultFields() {
        availabilityField.setText("");
        priceField.setText("");
        bedTypeField.setText("");
    }
}