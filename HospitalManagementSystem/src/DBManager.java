import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "arkquez050406";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validateLogin(String username, String password) {
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Enhanced addPatient method with room validation and status update
    public static boolean addPatient(String patientId, String name, String gender, String disease, String roomNo, double deposit) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // First, check if room exists and is available
            String checkRoomQuery = "SELECT availability FROM rooms WHERE room_no = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkRoomQuery);
            checkStmt.setString(1, roomNo);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                // Room doesn't exist
                conn.rollback();
                return false;
            }
            
            String availability = rs.getString("availability");
            if (!"Available".equals(availability)) {
                // Room is occupied
                conn.rollback();
                return false;
            }
            
            // Add patient
            String insertPatientQuery = "INSERT INTO patients (patient_id, name, gender, disease, room_no, in_time, deposit) VALUES (?, ?, ?, ?, ?, NOW(), ?)";
            PreparedStatement patientStmt = conn.prepareStatement(insertPatientQuery);
            patientStmt.setString(1, patientId);
            patientStmt.setString(2, name);
            patientStmt.setString(3, gender);
            patientStmt.setString(4, disease);
            patientStmt.setString(5, roomNo);
            patientStmt.setDouble(6, deposit);
            
            int patientResult = patientStmt.executeUpdate();
            
            if (patientResult > 0) {
                // Update room availability to Occupied
                String updateRoomQuery = "UPDATE rooms SET availability = 'Occupied' WHERE room_no = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateRoomQuery);
                updateStmt.setString(1, roomNo);
                updateStmt.executeUpdate();
                
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get available rooms for dropdown
    public static List<String> getAvailableRooms() {
        List<String> availableRooms = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT room_no FROM rooms WHERE availability = 'Available' ORDER BY room_no";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                availableRooms.add(rs.getString("room_no"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableRooms;
    }

    // Method to check if room exists
    public static boolean roomExists(String roomNo) {
        try (Connection conn = getConnection()) {
            String query = "SELECT room_no FROM rooms WHERE room_no = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, roomNo);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to check if room is available
    public static boolean isRoomAvailable(String roomNo) {
        try (Connection conn = getConnection()) {
            String query = "SELECT availability FROM rooms WHERE room_no = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, roomNo);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return "Available".equals(rs.getString("availability"));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet getAllPatients() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM patients WHERE out_time IS NULL";
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Room management methods
    public static ResultSet getAllRooms() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM rooms ORDER BY room_no";
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet searchRoom(String roomNo) {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM rooms WHERE room_no = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, roomNo);
            return pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Department methods
    public static ResultSet getAllDepartments() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM departments ORDER BY dept_name";
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Patient discharge methods
    public static ResultSet getPatientById(String patientId) {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM patients WHERE patient_id = ? AND out_time IS NULL";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, patientId);
            return pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Enhanced discharge method with proper room availability update
    public static boolean dischargePatient(String patientId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Get patient's room number before discharging
            String getRoomQuery = "SELECT room_no FROM patients WHERE patient_id = ? AND out_time IS NULL";
            PreparedStatement getRoomStmt = conn.prepareStatement(getRoomQuery);
            getRoomStmt.setString(1, patientId);
            ResultSet rs = getRoomStmt.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false; // Patient not found or already discharged
            }
            
            String roomNo = rs.getString("room_no");
            
            // Update patient out_time
            String updatePatientQuery = "UPDATE patients SET out_time = NOW() WHERE patient_id = ? AND out_time IS NULL";
            PreparedStatement updatePatientStmt = conn.prepareStatement(updatePatientQuery);
            updatePatientStmt.setString(1, patientId);
            
            int patientResult = updatePatientStmt.executeUpdate();
            
            if (patientResult > 0) {
                // Make room available
                String updateRoomQuery = "UPDATE rooms SET availability = 'Available' WHERE room_no = ?";
                PreparedStatement updateRoomStmt = conn.prepareStatement(updateRoomQuery);
                updateRoomStmt.setString(1, roomNo);
                updateRoomStmt.executeUpdate();
                
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Update patient methods
    public static ResultSet searchPatientByName(String name) {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM patients WHERE name LIKE ? AND out_time IS NULL";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, "%" + name + "%");
            return pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Enhanced updatePatient method with room change validation
    public static boolean updatePatient(String patientId, String name, String gender, String disease, String newRoomNo, double deposit) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Get current room number
            String getCurrentRoomQuery = "SELECT room_no FROM patients WHERE patient_id = ? AND out_time IS NULL";
            PreparedStatement getCurrentRoomStmt = conn.prepareStatement(getCurrentRoomQuery);
            getCurrentRoomStmt.setString(1, patientId);
            ResultSet rs = getCurrentRoomStmt.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false; // Patient not found
            }
            
            String currentRoomNo = rs.getString("room_no");
            
            // If room is changing, validate new room
            if (!currentRoomNo.equals(newRoomNo)) {
                // Check if new room exists and is available
                if (!roomExists(newRoomNo)) {
                    conn.rollback();
                    return false; // New room doesn't exist
                }
                
                if (!isRoomAvailable(newRoomNo)) {
                    conn.rollback();
                    return false; // New room is not available
                }
                
                // Make current room available
                String makeCurrentRoomAvailableQuery = "UPDATE rooms SET availability = 'Available' WHERE room_no = ?";
                PreparedStatement makeAvailableStmt = conn.prepareStatement(makeCurrentRoomAvailableQuery);
                makeAvailableStmt.setString(1, currentRoomNo);
                makeAvailableStmt.executeUpdate();
                
                // Make new room occupied
                String makeNewRoomOccupiedQuery = "UPDATE rooms SET availability = 'Occupied' WHERE room_no = ?";
                PreparedStatement makeOccupiedStmt = conn.prepareStatement(makeNewRoomOccupiedQuery);
                makeOccupiedStmt.setString(1, newRoomNo);
                makeOccupiedStmt.executeUpdate();
            }
            
            // Update patient details
            String updatePatientQuery = "UPDATE patients SET name = ?, gender = ?, disease = ?, room_no = ?, deposit = ? WHERE patient_id = ?";
            PreparedStatement updatePatientStmt = conn.prepareStatement(updatePatientQuery);
            updatePatientStmt.setString(1, name);
            updatePatientStmt.setString(2, gender);
            updatePatientStmt.setString(3, disease);
            updatePatientStmt.setString(4, newRoomNo);
            updatePatientStmt.setDouble(5, deposit);
            updatePatientStmt.setString(6, patientId);
            
            int result = updatePatientStmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get specific error messages for better user feedback
    public static String getPatientAddErrorMessage(String patientId, String roomNo) {
        if (!roomExists(roomNo)) {
            return "Room " + roomNo + " does not exist.";
        }
        if (!isRoomAvailable(roomNo)) {
            return "Room " + roomNo + " is already occupied.";
        }
        
        // Check if patient ID already exists
        try (Connection conn = getConnection()) {
            String query = "SELECT patient_id FROM patients WHERE patient_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, patientId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return "Patient ID " + patientId + " already exists.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "Unknown error occurred.";
    }

    // ===== AMBULANCE MANAGEMENT METHODS =====
    
    // Get all ambulances
    public static ResultSet getAllAmbulances() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM ambulance ORDER BY amb_id";
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add new ambulance request
    public static boolean addAmbulanceRequest(String type, String company, String location, String patientName, String gender) {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO ambulance (type, company, location, patient_name, gender, availability) VALUES (?, ?, ?, ?, ?, 'Requested')";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, type);
            pst.setString(2, company);
            pst.setString(3, location);
            pst.setString(4, patientName);
            pst.setString(5, gender);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update ambulance status and location
    public static boolean updateAmbulanceStatus(int ambulanceId, String location, String status) {
        String sql = "UPDATE ambulance SET location = ?, availability = ? WHERE amb_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, location);
            pstmt.setString(2, status);
            pstmt.setInt(3, ambulanceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating ambulance status: " + e.getMessage());
            return false;
        }
    }

    // Get ambulance by ID
    public static ResultSet getAmbulanceById(int ambulanceId) {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM ambulance WHERE amb_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, ambulanceId);
            return pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Search ambulances by status
    public static ResultSet getAmbulancesByStatus(String status) {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM ambulance WHERE availability = ? ORDER BY amb_id";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, status);
            return pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Search ambulances by patient name
    public static ResultSet searchAmbulanceByPatient(String patientName) {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM ambulance WHERE patient_name LIKE ? ORDER BY amb_id";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, "%" + patientName + "%");
            return pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get available ambulances (status = 'Available')
    public static ResultSet getAvailableAmbulances() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM ambulance WHERE availability = 'Available' ORDER BY amb_id";
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update only ambulance status (without location change)
    public static boolean updateAmbulanceStatusOnly(int ambulanceId, String status) {
        String sql = "UPDATE ambulance SET availability = ? WHERE amb_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, ambulanceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating ambulance status: " + e.getMessage());
            return false;
        }
    }

    // Delete ambulance record (for completed/cancelled requests)
    public static boolean deleteAmbulanceRecord(int ambulanceId) {
        String sql = "DELETE FROM ambulance WHERE amb_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ambulanceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting ambulance record: " + e.getMessage());
            return false;
        }
    }

    // Get ambulance statistics
    public static int getAmbulanceCountByStatus(String status) {
        try (Connection conn = getConnection()) {
            String query = "SELECT COUNT(*) as count FROM ambulance WHERE availability = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Check if ambulance exists
    public static boolean ambulanceExists(int ambulanceId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT amb_id FROM ambulance WHERE amb_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, ambulanceId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}