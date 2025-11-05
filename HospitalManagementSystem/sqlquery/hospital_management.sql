CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
    room_no VARCHAR(10) PRIMARY KEY,
    availability ENUM('Available','Occupied') DEFAULT 'Available',
    price DECIMAL(10,2),
    bed_type VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS patients (
    patient_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    gender ENUM('Male','Female') NOT NULL,
    disease VARCHAR(200) NOT NULL,
    room_no VARCHAR(10),
    in_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    out_time TIMESTAMP NULL,
    deposit DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (room_no) REFERENCES rooms(room_no)
);

CREATE TABLE IF NOT EXISTS departments (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL,
    phone_no VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS ambulance (
    amb_id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    gender ENUM('Male','Female','Other') NOT NULL,
    availability ENUM('Available','Requested','On Scene','Busy') DEFAULT 'Requested'
);

INSERT INTO users (username, password)
VALUES ('admin', 'admin123') AS new_user
ON DUPLICATE KEY UPDATE password = new_user.password;

INSERT INTO rooms (room_no, availability, price, bed_type) VALUES
('101', 'Available', 1500.00, 'General Ward'),
('102', 'Occupied', 1500.00, 'General Ward'),
('103', 'Available', 1500.00, 'General Ward'),
('201', 'Available', 2500.00, 'Semi Private'),
('202', 'Available', 2500.00, 'Semi Private'),
('203', 'Available', 2500.00, 'Semi Private'),
('301', 'Available', 4000.00, 'Private Room'),
('302', 'Available', 4000.00, 'Private Room'),
('303', 'Available', 4000.00, 'Private Room'),
('ICU1', 'Available', 8000.00, 'ICU'),
('ICU2', 'Available', 8000.00, 'ICU'),
('ICU3', 'Available', 8000.00, 'ICU')
AS new_room
ON DUPLICATE KEY UPDATE 
    availability = new_room.availability,
    price = new_room.price,
    bed_type = new_room.bed_type;

INSERT INTO departments (dept_name, phone_no) VALUES
('Emergency Department', '9876543210'),
('Cardiology', '9876543211'),
('Neurology', '9876543212'),
('Orthopedics', '9876543213'),
('Pediatrics', '9876543214'),
('Surgery', '9876543216'),
('Laboratory', '9876543218'),
('Pharmacy', '9876543219'),
('ICU', '9876543221');

INSERT INTO patients (patient_id, name, gender, disease, room_no, deposit) VALUES
('9876543210', 'Rajesh Kumar', 'Male', 'Fracture', '102', 5000.00),
('9876543211', 'Adithya Sharma', 'Female', 'Diabetes', '202', 7500.00),
('9876543212', 'Amit Patel', 'Male', 'Heart Attack', 'ICU2', 15000.00),
('9876543213', 'Sunita Devi', 'Female', 'Appendicitis', '303', 10000.00)
AS new_patient
ON DUPLICATE KEY UPDATE 
    name = new_patient.name,
    gender = new_patient.gender,
    disease = new_patient.disease,
    room_no = new_patient.room_no,
    deposit = new_patient.deposit;

INSERT INTO ambulance (type, company, location, patient_name, gender, availability) VALUES
('Emergency', 'City Ambulance Service', 'Anna Nagar', 'Thilak Kumar', 'Male', 'On Scene'),
('Basic Life Support', 'Metro Medical Services', 'Ramapuram', 'Dev Patel', 'Male', 'Requested'),
('Advanced Life Support', 'Quick Response Medical', 'Perambur', 'Reinhard Osborn', 'Male', 'Busy')
AS new_amb
ON DUPLICATE KEY UPDATE 
    type = new_amb.type,
    company = new_amb.company,
    location = new_amb.location,
    patient_name = new_amb.patient_name,
    gender = new_amb.gender,
    availability = new_amb.availability;

DELIMITER //
CREATE TRIGGER trg_after_patient_insert
AFTER INSERT ON patients
FOR EACH ROW
BEGIN
    UPDATE rooms
    SET availability = 'Occupied'
    WHERE room_no = NEW.room_no;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_after_patient_delete
AFTER DELETE ON patients
FOR EACH ROW
BEGIN
    UPDATE rooms
    SET availability = 'Available'
    WHERE room_no = OLD.room_no;
END;
//
DELIMITER ;

CREATE INDEX idx_patient_name ON patients(name);
CREATE INDEX idx_room_availability ON rooms(availability);

CREATE OR REPLACE VIEW active_patients AS
SELECT patient_id, name, gender, disease, room_no, in_time, deposit
FROM patients 
WHERE out_time IS NULL;

CREATE OR REPLACE VIEW available_rooms AS
SELECT room_no, price, bed_type
FROM rooms 
WHERE availability = 'Available';

CREATE OR REPLACE VIEW department_contacts AS
SELECT dept_name, phone_no
FROM departments
ORDER BY dept_name;
