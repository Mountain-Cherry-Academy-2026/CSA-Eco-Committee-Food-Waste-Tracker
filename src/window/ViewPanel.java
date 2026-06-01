package window;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.*;

import util.ConfigManager;
import util.DatabaseManager;

public class ViewPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private Window window;
    private JTable table;
    private DefaultTableModel tableModel;
    private ConfigManager config;

    public ViewPanel(final Window window) {
        this.window = window;
        config = DatabaseManager.getConfig();
        
        setLayout(new BorderLayout());

        Font defaultFont60 = new Font("Arial", Font.BOLD, 60);
        Font defaultFont30 = new Font("Arial", Font.BOLD, 30);
        Font tableFont = new Font("Arial", Font.PLAIN, 20);
        Font headerFont = new Font("Arial", Font.BOLD, 22);

        JLabel title = new JLabel("View", JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));
        title.setFont(defaultFont60);
        add(title, BorderLayout.NORTH);

        Vector columnNames = new Vector();
        columnNames.add("ID");
        columnNames.add("Time");
        columnNames.add("Weight (g)");
        columnNames.add("Tray Type");
        columnNames.add("Student ID");
        columnNames.add("Grade");

        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(tableFont);
        table.setRowHeight(35);
        table.getTableHeader().setFont(headerFont);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        add(scrollPane, BorderLayout.CENTER);

        JPanel menuPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnReturn = new JButton("Return");

        btnRefresh.setFont(defaultFont30);
        btnReturn.setFont(defaultFont30);

        menuPanel.add(btnRefresh);
        menuPanel.add(btnReturn);
        
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 60));
        bottomContainer.add(menuPanel);
        add(bottomContainer, BorderLayout.SOUTH);

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ViewPanel.this.window.changeScreen("MAIN_MENU");
            }
        });

        this.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                loadData();
            }
            public void ancestorRemoved(AncestorEvent event) {}
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT history_id, record_time, weight_g, tray_type, student_id, grade ");
        sb.append("FROM ").append(config.getHistoryTable()).append(" ");
        sb.append("ORDER BY history_id DESC;");

        try {
            conn = DatabaseManager.getHistoryConnection();
            pstmt = conn.prepareStatement(sb.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector row = new Vector();
                row.add(new Integer(rs.getInt("history_id")));
                row.add(rs.getTimestamp("record_time"));
                row.add(new Double(rs.getDouble("weight_g")));
                row.add(new Integer(rs.getInt("tray_type")));
                
                int studentId = rs.getInt("student_id");
                if (rs.wasNull() || studentId == 0) {
                    row.add("-");
                } else {
                    row.add(new Integer(studentId));
                }

                int grade = rs.getInt("grade");
                if (rs.wasNull() || grade == 0) {
                    row.add("-");
                } else {
                    row.add(new Integer(grade));
                }

                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
    }
}