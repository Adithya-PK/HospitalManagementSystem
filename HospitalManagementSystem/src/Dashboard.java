import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {
    
    public Dashboard() {
        setTitle("Hospital Management System - Dashboard");
        
        // FULL SCREEN CHANGES
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Remove setResizable(false) to allow resizing
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Header panel - now responsive
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 55));
        JLabel titleLabel = new JLabel("HOSPITAL MANAGEMENT DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Larger font
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Button panel (center) - 3x3 grid with new arrangement
        JPanel buttonContainer = new JPanel(new GridBagLayout());
        buttonContainer.setBackground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 20, 20)); // Increased gaps
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Increased padding
        buttonPanel.setBackground(Color.WHITE);
        
        // Set preferred size based on screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int buttonPanelWidth = (int)(screenSize.width * 0.6); // 60% of screen width
        int buttonPanelHeight = (int)(screenSize.height * 0.6); // 60% of screen height
        buttonPanel.setPreferredSize(new Dimension(buttonPanelWidth, buttonPanelHeight));
        
        // Create buttons with reorganized layout
        // Row 1: Patient Management
        JButton addPatientBtn = createButton("Add Patient", new Color(46, 204, 113));
        JButton viewPatientsBtn = createButton("View Patients", new Color(52, 152, 219));
        JButton updatePatientBtn = createButton("Update Patient", new Color(231, 76, 60));
        
        // Row 2: Room and Department Management
        JButton roomBtn = createButton("Rooms", new Color(155, 89, 182));
        JButton searchRoomBtn = createButton("Room Search", new Color(26, 188, 156));
        JButton departmentBtn = createButton("Department", new Color(230, 126, 34));
        
        // Row 3: Operations and System
        JButton dischargeBtn = createButton("Discharge", new Color(241, 196, 15));
        JButton ambulanceBtn = createButton("Ambulance", new Color(192, 57, 43));
        JButton logoutBtn = createButton("Logout", new Color(149, 165, 166));
        
        // Add action listeners
        addPatientBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Patient().setVisible(true);
            }
        });
        
        viewPatientsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPatientInfo();
            }
        });
        
        updatePatientBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UpdatePatient().setVisible(true);
            }
        });
        
        roomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Room().setVisible(true);
            }
        });
        
        searchRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SearchRoom().setVisible(true);
            }
        });
        
        departmentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Department().setVisible(true);
            }
        });
        
        dischargeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PatientDischarge().setVisible(true);
            }
        });
        
        ambulanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Ambulance().setVisible(true);
            }
        });
        
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        // Add buttons to panel in new 3x3 arrangement
        // Row 1: Patient Management
        buttonPanel.add(addPatientBtn);
        buttonPanel.add(viewPatientsBtn);
        buttonPanel.add(updatePatientBtn);
        
        // Row 2: Room and Department Management
        buttonPanel.add(roomBtn);
        buttonPanel.add(searchRoomBtn);
        buttonPanel.add(departmentBtn);
        
        // Row 3: Operations and System
        buttonPanel.add(dischargeBtn);
        buttonPanel.add(ambulanceBtn);
        buttonPanel.add(logoutBtn);
        
        // Center the button panel
        buttonContainer.add(buttonPanel);
        
        // Logo/Info panel (right side) - properly sized and positioned
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setPreferredSize(new Dimension(540, 0)); // Increased width to prevent cutoff
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, -20, 20, 30)); // Balanced padding
        
        // Add hospital logo/image placeholder with centered content and updated info
        JLabel logoLabel = new JLabel();
        logoLabel.setText("<html><div style='text-align: center; width: 270px; padding: 10px;'>" +
                         "<h1 style='color: #2980b9; font-size: 22px; margin: 0 0 8px 0; text-align: center;'>CITY HOSPITAL</h1>" +
                         "<p style='color: #7f8c8d; font-size: 16px; margin: 0 0 15px 0; text-align: center;'>Comprehensive Healthcare Solutions</p>" +
                         "<div style='font-size: 70px; color: #e74c3c; text-align: center; margin: 15px 0; line-height: 1;'>⚕</div>" +
                         "<div style='color: #34495e; font-size: 13px; text-align: center; line-height: 1.5; margin-top: 15px;'>" +
                         "<strong>Patient Care Services:</strong><br>" +
                         "• Patient Registration & Management<br>" +
                         "• Room Assignment & Tracking<br>" +
                         "• Department Coordination<br>" +
                         "• Discharge Processing<br>" +
                         "• Emergency Ambulance Services<br><br>" +
                         "<strong>Available 24/7</strong>" +
                         "</div>" +
                         "</div></html>");
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        
        mainPanel.add(buttonContainer, BorderLayout.CENTER);
        mainPanel.add(logoPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        // Make buttons larger and responsive
        button.setPreferredSize(new Dimension(200, 120)); // Increased size
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger font
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void showPatientInfo() {
        try {
            java.sql.ResultSet rs = DBManager.getAllPatients();
            
            String[] columns = {"Patient ID", "Name", "Gender", "Disease", "Room No", "In Time", "Deposit"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0);
            
            while (rs != null && rs.next()) {
                Object[] row = {
                    rs.getString("patient_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("disease"),
                    rs.getString("room_no"),
                    rs.getString("in_time"),
                    rs.getDouble("deposit")
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setRowHeight(30); // Increased row height
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.getTableHeader().setBackground(new Color(52, 152, 219));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            JScrollPane scrollPane = new JScrollPane(table);
            
            // Make dialog larger and responsive
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int dialogWidth = (int)(screenSize.width * 0.8);
            int dialogHeight = (int)(screenSize.height * 0.7);
            scrollPane.setPreferredSize(new Dimension(dialogWidth, dialogHeight));
            
            JDialog dialog = new JDialog(this, "Patient Information", true);
            dialog.add(scrollPane);
            dialog.setSize(dialogWidth + 50, dialogHeight + 100);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading patient data: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                                                  "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new Login().setVisible(true);
        }
    }
}