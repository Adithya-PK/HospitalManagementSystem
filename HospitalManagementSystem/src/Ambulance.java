import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class Ambulance extends JFrame {
    private JComboBox<String> typeCombo;
    private JTextField companyField;
    private JTextField locationField;
    private JTextField patientNameField;
    private JComboBox<String> genderCombo;
    private JTable ambulanceTable;
    private DefaultTableModel tableModel;
    
    // Edit mode components
    private JTextField editIdField;
    private JComboBox<String> editStatusCombo;
    private JTextField editLocationField;
    private JButton updateButton;
    private JButton selectButton;
    private int selectedRowId = -1;
    
    public Ambulance() {
        setTitle("Ambulance Management");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeComponents();
        loadAmbulanceData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(231, 76, 60));
        headerPanel.setPreferredSize(new Dimension(1100, 50));
        JLabel headerLabel = new JLabel("Ambulance Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Left panel containing both request and update forms
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(400, 0));
        
        // Form panel for new requests
        JPanel formPanel = createRequestForm();
        
        // Update panel for editing existing ambulances
        JPanel updatePanel = createUpdateForm();
        
        leftPanel.add(formPanel, BorderLayout.NORTH);
        leftPanel.add(updatePanel, BorderLayout.SOUTH);
        
        // Table panel (right side)
        JPanel tablePanel = createTablePanel();
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Bottom button panel
        JPanel bottomButtonPanel = new JPanel(new FlowLayout());
        bottomButtonPanel.setBackground(Color.WHITE);
        
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        bottomButtonPanel.add(backButton);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomButtonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createRequestForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Request New Ambulance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Ambulance Type
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Ambulance Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        typeCombo = new JComboBox<>(new String[]{"Basic Life Support", "Advanced Life Support", "Emergency", "Non-Emergency"});
        formPanel.add(typeCombo, gbc);
        
        // Company
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Company:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        companyField = new JTextField(15);
        formPanel.add(companyField, gbc);
        
        // Location
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        locationField = new JTextField(15);
        formPanel.add(locationField, gbc);
        
        // Patient Name
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        patientNameField = new JTextField(15);
        formPanel.add(patientNameField, gbc);
        
        // Gender
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderCombo, gbc);
        
        // Buttons for form
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(15, 5, 5, 5);
        JPanel formButtonPanel = new JPanel(new FlowLayout());
        formButtonPanel.setBackground(Color.WHITE);
        
        JButton requestButton = new JButton("Request");
        requestButton.setBackground(new Color(231, 76, 60));
        requestButton.setForeground(Color.WHITE);
        requestButton.setFocusPainted(false);
        requestButton.addActionListener(new RequestAmbulanceListener());
        
        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(46, 204, 113));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearRequestFields());
        
        formButtonPanel.add(requestButton);
        formButtonPanel.add(clearButton);
        formPanel.add(formButtonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createUpdateForm() {
        JPanel updatePanel = new JPanel(new GridBagLayout());
        updatePanel.setBackground(new Color(240, 248, 255));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update Ambulance Status"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Ambulance ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        updatePanel.add(new JLabel("Ambulance ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        editIdField = new JTextField(10);
        editIdField.setEditable(false);
        editIdField.setBackground(new Color(220, 220, 220));
        updatePanel.add(editIdField, gbc);
        
        // Current Location
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        updatePanel.add(new JLabel("Current Location:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        editLocationField = new JTextField(15);
        updatePanel.add(editLocationField, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        updatePanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        editStatusCombo = new JComboBox<>(new String[]{
            "Requested", "Dispatched", "On Route", "On Scene", 
            "Transporting", "At Hospital", "Available", "Busy"
        });
        updatePanel.add(editStatusCombo, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(15, 5, 5, 5);
        JPanel updateButtonPanel = new JPanel(new FlowLayout());
        updateButtonPanel.setBackground(new Color(240, 248, 255));
        
        selectButton = new JButton("Select from Table");
        selectButton.setBackground(new Color(52, 152, 219));
        selectButton.setForeground(Color.WHITE);
        selectButton.setFocusPainted(false);
        selectButton.addActionListener(new SelectAmbulanceListener());
        
        updateButton = new JButton("Update Status");
        updateButton.setBackground(new Color(230, 126, 34));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.addActionListener(new UpdateAmbulanceListener());
        updateButton.setEnabled(false);
        
        JButton clearUpdateButton = new JButton("Clear");
        clearUpdateButton.setBackground(new Color(149, 165, 166));
        clearUpdateButton.setForeground(Color.WHITE);
        clearUpdateButton.setFocusPainted(false);
        clearUpdateButton.addActionListener(e -> clearUpdateFields());
        
        updateButtonPanel.add(selectButton);
        updateButtonPanel.add(updateButton);
        updateButtonPanel.add(clearUpdateButton);
        updatePanel.add(updateButtonPanel, gbc);
        
        return updatePanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Ambulance Status"));
        
        String[] columns = {"ID", "Type", "Company", "Location", "Patient", "Gender", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable directly
            }
        };
        ambulanceTable = new JTable(tableModel);
        ambulanceTable.setRowHeight(25);
        ambulanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ambulanceTable.getTableHeader().setBackground(new Color(231, 76, 60));
        ambulanceTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(ambulanceTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button for table
        JPanel tableButtonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadAmbulanceData());
        tableButtonPanel.add(refreshButton);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    private class RequestAmbulanceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String type = (String) typeCombo.getSelectedItem();
            String company = companyField.getText().trim();
            String location = locationField.getText().trim();
            String patientName = patientNameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            
            if (company.isEmpty() || location.isEmpty() || patientName.isEmpty()) {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (DBManager.addAmbulanceRequest(type, company, location, patientName, gender)) {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Ambulance request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearRequestFields();
                loadAmbulanceData();
            } else {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Failed to submit ambulance request", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class SelectAmbulanceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = ambulanceTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Please select an ambulance from the table", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get data from selected row
            selectedRowId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String currentLocation = (String) tableModel.getValueAt(selectedRow, 3);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);
            
            // Populate update fields
            editIdField.setText(String.valueOf(selectedRowId));
            editLocationField.setText(currentLocation);
            editStatusCombo.setSelectedItem(currentStatus);
            
            updateButton.setEnabled(true);
            
            JOptionPane.showMessageDialog(Ambulance.this, 
                "Ambulance ID " + selectedRowId + " selected for editing", "Selection Confirmed", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private class UpdateAmbulanceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedRowId == -1) {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Please select an ambulance first", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String newLocation = editLocationField.getText().trim();
            String newStatus = (String) editStatusCombo.getSelectedItem();
            
            if (newLocation.isEmpty()) {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Please enter a location", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (DBManager.updateAmbulanceStatus(selectedRowId, newLocation, newStatus)) {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Ambulance status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearUpdateFields();
                loadAmbulanceData();
            } else {
                JOptionPane.showMessageDialog(Ambulance.this, 
                    "Failed to update ambulance status", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadAmbulanceData() {
        tableModel.setRowCount(0);
        
        try {
            ResultSet rs = DBManager.getAllAmbulances();
            while (rs != null && rs.next()) {
                Object[] row = {
                    rs.getInt("amb_id"),
                    rs.getString("type"),
                    rs.getString("company"),
                    rs.getString("location"),
                    rs.getString("patient_name"),
                    rs.getString("gender"),
                    rs.getString("availability")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading ambulance data: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearRequestFields() {
        typeCombo.setSelectedIndex(0);
        companyField.setText("");
        locationField.setText("");
        patientNameField.setText("");
        genderCombo.setSelectedIndex(0);
    }
    
    private void clearUpdateFields() {
        editIdField.setText("");
        editLocationField.setText("");
        editStatusCombo.setSelectedIndex(0);
        selectedRowId = -1;
        updateButton.setEnabled(false);
    }
}