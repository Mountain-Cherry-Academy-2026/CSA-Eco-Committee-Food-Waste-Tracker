package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.WasteRecord;

public class WasteRecordDAO {
    private String url = "jdbc:mysql://localhost:3306/food_waste_db";
    private String user = "root";
    private String password = "your_password";

    public WasteRecordDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean insertRecord(WasteRecord record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSuccess = false;
        
        String sql = "INSERT INTO waste_records (weight_g, record_time) VALUES (?, ?)";

        try {
            conn = DriverManager.getConnection(url, user, password);
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setDouble(1, record.getWeightG());
            pstmt.setTimestamp(2, new java.sql.Timestamp(record.getRecordTime().getTime()));
            
            int row = pstmt.executeUpdate();
            if (row > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (Exception e) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
        return isSuccess;
    }

    public String getStudentNameByBarcode(String barcode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String studentName = null;

        String sql = "SELECT name FROM students WHERE student_id = ?";

        try {
            conn = DriverManager.getConnection(url, user, password);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, barcode);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                studentName = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (Exception e) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
        return studentName;
    }

    public boolean insertRecordWithStudent(WasteRecord record, String studentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSuccess = false;
        
        String sql = "INSERT INTO waste_records (weight_g, record_time, student_id) VALUES (?, ?, ?)";

        try {
            conn = DriverManager.getConnection(url, user, password);
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setDouble(1, record.getWeightG());
            pstmt.setTimestamp(2, new java.sql.Timestamp(record.getRecordTime().getTime()));
            pstmt.setString(3, studentId);
            
            int row = pstmt.executeUpdate();
            if (row > 0) {
                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (Exception e) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
        return isSuccess;
    }
}