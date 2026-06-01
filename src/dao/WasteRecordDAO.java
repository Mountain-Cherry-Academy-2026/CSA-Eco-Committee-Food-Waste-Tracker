package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.WasteRecord;
import model.StudentInfo;
import util.ConfigManager;
import util.DatabaseManager;

public class WasteRecordDAO {

    private ConfigManager config = DatabaseManager.getConfig();

    public WasteRecordDAO() {
    }

    public boolean insertRecord(WasteRecord record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSuccess = false;
        
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO ").append(config.getHistoryTable());
        sb.append(" (record_time, weight_g, tray_type) VALUES (?, ?, ?);");

        try {
            conn = DatabaseManager.getHistoryConnection();
            conn.setAutoCommit(false); 
            
            pstmt = conn.prepareStatement(sb.toString());
            
            pstmt.setTimestamp(1, new java.sql.Timestamp(record.getRecordTime().getTime()));
            pstmt.setDouble(2, record.getWeightG());
            pstmt.setInt(3, record.getTrayType());
            
            int row = pstmt.executeUpdate();
            if (row > 0) {
                conn.commit();
                isSuccess = true;
                System.out.println("insertRecord committed! (Order: Time -> Weight -> Tray)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return isSuccess;
    }

    public StudentInfo getStudentInfoByBarcode(String barcode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String studentName = null;
        int grade = 0;

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ENGNAME, KORNAME, SURNAME, GRADE FROM INFO WHERE ID = ?;");

        try {
            conn = DatabaseManager.getStudentConnection();
            pstmt = conn.prepareStatement(sb.toString());
            
            int studentId = Integer.parseInt(barcode.trim());
            pstmt.setInt(1, studentId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String engName = rs.getString("ENGNAME");
                String korName = rs.getString("KORNAME");
                String surName = rs.getString("SURNAME");
                grade = rs.getInt("GRADE");
                
                StringBuffer namePart = new StringBuffer();
                
                if (engName != null && !engName.trim().equals("") && !engName.trim().equalsIgnoreCase("NULL")) {
                    namePart.append(engName.trim());
                }
                
                if (korName != null && !korName.trim().equals("") && !korName.trim().equalsIgnoreCase("NULL")) {
                    if (namePart.length() > 0) {
                        namePart.append(" ");
                    }
                    namePart.append(korName.trim());
                }
                
                if (surName != null && !surName.trim().equals("") && !surName.trim().equalsIgnoreCase("NULL")) {
                    if (namePart.length() > 0) {
                        namePart.append(" ");
                    }
                    namePart.append(surName.trim());
                }
                
                studentName = namePart.toString();
                
                if (studentName.trim().equals("")) {
                    studentName = "Unknown Name";
                }
            } else {
                studentName = "Non-registered";
            }
        } catch (Exception e) {
            e.printStackTrace();
            studentName = "Error: " + e.getMessage();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        
        return new StudentInfo(studentName, grade);
    }

    public boolean insertRecordWithStudent(WasteRecord record, String studentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSuccess = false;
        
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO ").append(config.getHistoryTable());
        sb.append(" (record_time, weight_g, tray_type, student_id, grade) VALUES (?, ?, ?, ?, ?);");

        try {
            conn = DatabaseManager.getHistoryConnection();
            conn.setAutoCommit(false); 
            
            pstmt = conn.prepareStatement(sb.toString());
            
            pstmt.setTimestamp(1, new java.sql.Timestamp(record.getRecordTime().getTime()));
            pstmt.setDouble(2, record.getWeightG());
            pstmt.setInt(3, record.getTrayType());
            
            int sId = Integer.parseInt(studentId.trim());
            pstmt.setInt(4, sId);
            pstmt.setInt(5, record.getGrade());
            
            int row = pstmt.executeUpdate();
            if (row > 0) {
                conn.commit();
                isSuccess = true;
                System.out.println("insertRecordWithStudent committed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return isSuccess;
    }
}