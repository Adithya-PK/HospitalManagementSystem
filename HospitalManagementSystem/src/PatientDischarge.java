import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class PatientDischarge extends JFrame {
    private JTextField patientIdField;
    private JTextField nameField;
    private JTextField roomNoField;
    private JTextField inTimeField;
    private JLabel outTimeLabel;
    
    public PatientDischarge() {
        setTitle("Patient Discharge");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(231, 76, 60));
        headerPanel.setPreferredSize(new Dimension(500, 50));
        JLabel headerLabel = new JLabel("Patient Discharge", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Patient ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        patientIdField = new JTextField(15);
        formPanel.add(patientIdField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(new SearchPatientListener());
        formPanel.add(searchButton, gbc);
        
        // Patient Name (read-only)
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(15);
        nameField.setEditable(false);
        nameField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(nameField, gbc);
        
        // Room No (read-only)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Room No:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomNoField = new JTextField(15);
        roomNoField.setEditable(false);
        roomNoField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(roomNoField, gbc);
        
        // In Time (read-only)
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("In Time:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inTimeField = new JTextField(15);
        inTimeField.setEditable(false);
        inTimeField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(inTimeField, gbc);
        
        // Out Time (label)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Out Time:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        outTimeLabel = new JLabel("Will be set to current time");
        outTimeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        outTimeLabel.setForeground(Color.GRAY);
        formPanel.add(outTimeLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton dischargeButton = new JButton("Discharge Patient");
        dischargeButton.setBackground(new Color(231, 76, 60));
        dischargeButton.setForeground(Color.WHITE);
        dischargeButton.setFocusPainted(false);
        dischargeButton.addActionListener(new DischargePatientListener());
        
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(dischargeButton);
        buttonPanel.add(backButton);
        
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private class SearchPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String patientId = patientIdField.getText().trim();
            
            if (patientId.isEmpty()) {
                JOptionPane.showMessageDialog(PatientDischarge.this, 
                    "Please enter Patient ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                ResultSet rs = DBManager.getPatientById(patientId);
                if (rs != null && rs.next()) {
                    nameField.setText(rs.getString("name"));
                    roomNoField.setText(rs.getString("room_no"));
                    inTimeField.setText(rs.getString("in_time"));
                } else {
                    JOptionPane.showMessageDialog(PatientDischarge.this, 
                        "Patient not found or already discharged", "Error", JOptionPane.ERROR_MESSAGE);
                    clearFields();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PatientDischarge.this, 
                    "Error searching patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class DischargePatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String patientId = patientIdField.getText().trim();
            
            if (patientId.isEmpty() || nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(PatientDischarge.this, 
                    "Please search for a patient first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int choice = JOptionPane.showConfirmDialog(PatientDischarge.this, 
                "Are you sure you want to discharge this patient?", 
                "Confirm Discharge", JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                if (DBManager.dischargePatient(patientId)) {
                    JOptionPane.showMessageDialog(PatientDischarge.this, 
                        "Patient discharged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(PatientDischarge.this, 
                        "Failed to discharge patient", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void clearFields() {
        patientIdField.setText("");
        nameField.setText("");
        roomNoField.setText("");
        inTimeField.setText("");
    }
}