import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.List;

public class UpdatePatient extends JFrame {
    private JTextField searchField;
    private JTextField patientIdField;
    private JTextField nameField;
    private JComboBox<String> genderCombo;
    private JTextField diseaseField;
    private JComboBox<String> roomCombo;
    private JTextField depositField;
    private String currentRoomNo; // To track current room for validation

    public UpdatePatient() {
        setTitle("Update Patient Details");
        setSize(550, 500);
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
        headerPanel.setPreferredSize(new Dimension(550, 50));
        JLabel headerLabel = new JLabel("Update Patient Details", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Patient"));

        JLabel searchLabel = new JLabel("Patient Name:");
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(new SearchPatientListener());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Patient ID (read-only)
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        patientIdField = new JTextField(15);
        patientIdField.setEditable(false);
        patientIdField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(patientIdField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderCombo, gbc);

        // Disease
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Disease:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        diseaseField = new JTextField(15);
        formPanel.add(diseaseField, gbc);

        // Room No (Dropdown with available rooms + current room)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Room No:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        roomCombo = new JComboBox<>();
        
        // Add refresh button for rooms
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.add(roomCombo, BorderLayout.CENTER);
        
        JButton refreshRoomButton = new JButton("↻");
        refreshRoomButton.setPreferredSize(new Dimension(25, 25));
        refreshRoomButton.setBackground(new Color(52, 152, 219));
        refreshRoomButton.setForeground(Color.WHITE);
        refreshRoomButton.setFocusPainted(false);
        refreshRoomButton.setToolTipText("Refresh available rooms");
        refreshRoomButton.addActionListener(e -> loadAvailableRoomsForUpdate());
        
        roomPanel.add(refreshRoomButton, BorderLayout.EAST);
        formPanel.add(roomPanel, gbc);

        // Deposit
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Deposit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        depositField = new JTextField(15);
        formPanel.add(depositField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(46, 204, 113));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.addActionListener(new UpdatePatientListener());

        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(230, 126, 34));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearFields());

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);

        // Main panel combining search and form
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAvailableRoomsForUpdate() {
        roomCombo.removeAllItems();
        
        // Add current room first (if exists)
        if (currentRoomNo != null && !currentRoomNo.isEmpty()) {
            roomCombo.addItem(currentRoomNo + " (Current)");
        }
        
        // Add available rooms
        List<String> availableRooms = DBManager.getAvailableRooms();
        for (String room : availableRooms) {
            if (!room.equals(currentRoomNo)) { // Don't duplicate current room
                roomCombo.addItem(room);
            }
        }
        
        if (currentRoomNo == null && availableRooms.isEmpty()) {
            roomCombo.addItem("No rooms available");
            roomCombo.setEnabled(false);
        } else {
            roomCombo.setEnabled(true);
        }
    }

    private class SearchPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchName = searchField.getText().trim();

            if (searchName.isEmpty()) {
                JOptionPane.showMessageDialog(UpdatePatient.this,
                    "Please enter patient name to search", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ResultSet rs = DBManager.searchPatientByName(searchName);
                if (rs != null && rs.next()) {
                    patientIdField.setText(rs.getString("patient_id"));
                    nameField.setText(rs.getString("name"));
                    genderCombo.setSelectedItem(rs.getString("gender"));
                    diseaseField.setText(rs.getString("disease"));
                    
                    currentRoomNo = rs.getString("room_no");
                    loadAvailableRoomsForUpdate();
                    
                    depositField.setText(String.valueOf(rs.getDouble("deposit")));
                } else {
                    JOptionPane.showMessageDialog(UpdatePatient.this,
                        "Patient not found", "Error", JOptionPane.ERROR_MESSAGE);
                    clearFormFields();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(UpdatePatient.this,
                    "Error searching patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdatePatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String patientId = patientIdField.getText().trim();
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String disease = diseaseField.getText().trim();
            String selectedRoom = (String) roomCombo.getSelectedItem();
            String depositStr = depositField.getText().trim();

            if (patientId.isEmpty()) {
                JOptionPane.showMessageDialog(UpdatePatient.this,
                    "Please search for a patient first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (name.isEmpty() || disease.isEmpty() || depositStr.isEmpty()) {
                JOptionPane.showMessageDialog(UpdatePatient.this,
                    "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedRoom == null || selectedRoom.equals("No rooms available")) {
                JOptionPane.showMessageDialog(UpdatePatient.this,
                    "Please select a valid room", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract room number (remove "(Current)" suffix if present)
            String roomNo = selectedRoom.replace(" (Current)", "");

            try {
                double deposit = Double.parseDouble(depositStr);

                int choice = JOptionPane.showConfirmDialog(UpdatePatient.this,
                    "Are you sure you want to update this patient's details?",
                    "Confirm Update", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    if (DBManager.updatePatient(patientId, name, gender, disease, roomNo, deposit)) {
                        String message = "Patient details updated successfully!";
                        if (!roomNo.equals(currentRoomNo)) {
                            message += "\nRoom changed from " + currentRoomNo + " to " + roomNo;
                        }
                        JOptionPane.showMessageDialog(UpdatePatient.this,
                            message, "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    } else {
                        String errorMessage = "Failed to update patient details.";
                        if (!roomNo.equals(currentRoomNo)) {
                            if (!DBManager.roomExists(roomNo)) {
                                errorMessage = "Room " + roomNo + " does not exist.";
                            } else if (!DBManager.isRoomAvailable(roomNo)) {
                                errorMessage = "Room " + roomNo + " is already occupied.";
                            }
                        }
                        JOptionPane.showMessageDialog(UpdatePatient.this,
                            errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                        loadAvailableRoomsForUpdate(); // Refresh room list
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(UpdatePatient.this,
                    "Please enter a valid deposit amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        searchField.setText("");
        clearFormFields();
        currentRoomNo = null;
    }

    private void clearFormFields() {
        patientIdField.setText("");
        nameField.setText("");
        genderCombo.setSelectedIndex(0);
        diseaseField.setText("");
        roomCombo.removeAllItems();
        depositField.setText("");
        currentRoomNo = null;
    }
}