import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Patient extends JFrame {
    private JTextField patientIdField;
    private JTextField nameField;
    private JComboBox<String> genderCombo;
    private JTextField diseaseField;
    private JComboBox<String> roomCombo;
    private JTextField depositField;

    public Patient() {
        setTitle("Add New Patient");
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
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setPreferredSize(new Dimension(500, 50));
        JLabel headerLabel = new JLabel("Add New Patient", SwingConstants.CENTER);
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
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Patient ID (Phone):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        patientIdField = new JTextField(15);
        formPanel.add(patientIdField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderCombo, gbc);

        // Disease
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Disease:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        diseaseField = new JTextField(15);
        formPanel.add(diseaseField, gbc);

        // Room No (Dropdown with available rooms)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Room No:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        roomCombo = new JComboBox<>();
        loadAvailableRooms();
        
        // Add refresh button for rooms
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.add(roomCombo, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("↻");
        refreshButton.setPreferredSize(new Dimension(25, 25));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setToolTipText("Refresh available rooms");
        refreshButton.addActionListener(e -> loadAvailableRooms());
        
        roomPanel.add(refreshButton, BorderLayout.EAST);
        formPanel.add(roomPanel, gbc);

        // Deposit
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Deposit:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        depositField = new JTextField(15);
        formPanel.add(depositField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("Add Patient");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(new AddPatientListener());
        
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAvailableRooms() {
        roomCombo.removeAllItems();
        roomCombo.addItem("Select Room...");
        
        List<String> availableRooms = DBManager.getAvailableRooms();
        for (String room : availableRooms) {
            roomCombo.addItem(room);
        }
        
        if (availableRooms.isEmpty()) {
            roomCombo.addItem("No rooms available");
            roomCombo.setEnabled(false);
        } else {
            roomCombo.setEnabled(true);
        }
    }

    private class AddPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String patientId = patientIdField.getText().trim();
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String disease = diseaseField.getText().trim();
            String roomNo = (String) roomCombo.getSelectedItem();
            String depositStr = depositField.getText().trim();

            // Validation
            if (patientId.isEmpty() || name.isEmpty() || disease.isEmpty() || depositStr.isEmpty()) {
                JOptionPane.showMessageDialog(Patient.this, 
                    "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (roomNo == null || roomNo.equals("Select Room...") || roomNo.equals("No rooms available")) {
                JOptionPane.showMessageDialog(Patient.this, 
                    "Please select a valid room", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double deposit = Double.parseDouble(depositStr);
                
                if (DBManager.addPatient(patientId, name, gender, disease, roomNo, deposit)) {
                    JOptionPane.showMessageDialog(Patient.this, 
                        "Patient added successfully!\nRoom " + roomNo + " has been assigned.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadAvailableRooms(); // Refresh room list after successful addition
                } else {
                    // Get specific error message
                    String errorMessage = DBManager.getPatientAddErrorMessage(patientId, roomNo);
                    JOptionPane.showMessageDialog(Patient.this, 
                        errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    loadAvailableRooms(); // Refresh room list in case room status changed
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(Patient.this, 
                    "Please enter a valid deposit amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        patientIdField.setText("");
        nameField.setText("");
        genderCombo.setSelectedIndex(0);
        diseaseField.setText("");
        roomCombo.setSelectedIndex(0);
        depositField.setText("");
    }
}







