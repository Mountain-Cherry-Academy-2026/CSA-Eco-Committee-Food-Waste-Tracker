package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
}