package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {

    private static ConfigManager config = new ConfigManager();

    static {
        try {
            Class.forName("SQLite.JDBCDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver Missing!");
            e.printStackTrace();
        }
    }

    public static ConfigManager getConfig() {
        return config;
    }

    public static Connection getStudentConnection() throws Exception {
        return DriverManager.getConnection(config.getStudentDbUrl());
    }

    public static Connection getHistoryConnection() throws Exception {
        return DriverManager.getConnection(config.getHistoryDbUrl());
    }

    public static void initDatabase() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = getHistoryConnection();
            stmt = conn.createStatement();

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(config.getHistoryTable()).append(" (");
            sb.append("history_id INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append("record_time DATETIME NOT NULL,");
            sb.append("weight_g REAL NOT NULL,");
            sb.append("tray_type INTEGER DEFAULT 0,");
            sb.append("student_id INTEGER,");
            sb.append("grade INTEGER");
            sb.append(");");
            
            stmt.execute(sb.toString());
            
            System.out.println("DB Table Prepared");
            
        } catch (Exception e) {
            System.out.println("DB Initialization Failed");
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}