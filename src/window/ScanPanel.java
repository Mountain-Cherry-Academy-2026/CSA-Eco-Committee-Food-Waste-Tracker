package window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import dao.WasteRecordDAO;
import model.WasteRecord;
import model.StudentInfo;

public class ScanPanel extends JPanel {
    private Window window;
    
    private JLabel instructionLabel;
    private JLabel resultLabel;
    private JTextField barcodeHiddenField;
    private Timer transitionTimer;
    private WasteRecordDAO dao;
    
    private double currentFinalWeight = 0.0;
    private int currentTrayType = 0;
    private int currentGrade = 0;
    
    public ScanPanel(Window window) {
        this.window = window;
        setLayout(new BorderLayout());
        
        Font defaultFont60 = new Font("Arial", Font.BOLD, 60);
        Font defaultFont45 = new Font("Arial", Font.BOLD, 45);
        Font defaultFont30 = new Font("Arial", Font.BOLD, 30);
        
        JLabel title = new JLabel("Scan ID", JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(80, 0, 20, 0));
        title.setFont(defaultFont60);
        add(title, BorderLayout.NORTH);

        instructionLabel = new JLabel("Please scan the barcode on your student ID.", JLabel.CENTER);
        instructionLabel.setFont(defaultFont45);
        instructionLabel.setForeground(Color.BLACK);
        
        resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(defaultFont30);
        resultLabel.setForeground(Color.DARK_GRAY);
        
        barcodeHiddenField = new JTextField(0);
        barcodeHiddenField.setPreferredSize(new Dimension(0, 0));
        barcodeHiddenField.setBorder(null);
        
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.add(instructionLabel);
        centerPanel.add(resultLabel);
        centerPanel.add(barcodeHiddenField);
        add(centerPanel, BorderLayout.CENTER);

        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        JButton btnReturn = new JButton("Return");
        btnReturn.setFont(defaultFont30);
        menuPanel.add(btnReturn);
        
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        bottomContainer.add(menuPanel);
        add(bottomContainer, BorderLayout.SOUTH);

        final ScanPanel self = this;

        barcodeHiddenField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String barcodeValue = barcodeHiddenField.getText().trim();
                if (barcodeValue.length() > 0) {
                    barcodeHiddenField.setEnabled(false);
                    
                    dao = new WasteRecordDAO();
                    
                    StudentInfo info = dao.getStudentInfoByBarcode(barcodeValue);
                    String studentName = info.getName();
                    self.currentGrade = info.getGrade();
                    
                    if (studentName == null || studentName.length() == 0) {
                        studentName = "Unknown User";
                    }
                    
                    WasteRecord record = new WasteRecord();
                    record.setRecordTime(new java.util.Date());
                    record.setWeightG(self.currentFinalWeight);      
                    record.setTrayType(self.currentTrayType);        
                    record.setGrade(self.currentGrade);   
                    
                    if (studentName.equals("Non-registered") || studentName.startsWith("Error")) {
                        dao.insertRecord(record);
                    } else {
                        dao.insertRecordWithStudent(record, barcodeValue);
                    }
                    
                    instructionLabel.setText("Success!");
                    instructionLabel.setForeground(Color.GREEN);
                    
                    resultLabel.setText("ID: " + barcodeValue + " / Name: " + studentName + " / Grade: " + self.currentGrade + " / Tray: " + self.currentTrayType + " / Saved: " + self.currentFinalWeight + "g");
                    
                    transitionTimer = new Timer(2000, new ActionListener() {
                        public void actionPerformed(ActionEvent ex) {
                            resetPanel();
                            self.currentFinalWeight = 0.0;
                            self.currentTrayType = 0;
                            self.currentGrade = 0;
                            self.window.changeScreen("MEASURE_PANEL");
                        }
                    });
                    transitionTimer.setRepeats(false);
                    transitionTimer.start();
                }
            }
        });

        btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPanel();
                self.currentFinalWeight = 0.0;
                self.currentTrayType = 0;
                self.currentGrade = 0;
                self.window.changeScreen("MAIN_MENU");
            }
        });	
        
        this.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        barcodeHiddenField.requestFocusInWindow();
                    }
                });
            }
            public void ancestorRemoved(AncestorEvent event) {}
            public void ancestorMoved(AncestorEvent event) {}
        });
    }
    
    public void setMeasuredWeight(double weight) {
        this.currentFinalWeight = weight;
    }

    public void setTrayType(int trayType) {
        this.currentTrayType = trayType;
    }

    public void setCurrentGrade(int grade) {
        this.currentGrade = grade;
    }
    
    private void resetPanel() {
        if (transitionTimer != null && transitionTimer.isRunning()) {
            transitionTimer.stop();
        }
        barcodeHiddenField.setText("");
        barcodeHiddenField.setEnabled(true);
        instructionLabel.setText("Please scan the barcode on your student ID.");
        instructionLabel.setForeground(Color.BLACK);
        resultLabel.setText("");
    }
}