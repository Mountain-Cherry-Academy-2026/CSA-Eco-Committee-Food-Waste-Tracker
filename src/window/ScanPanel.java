package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import dao.WasteRecordDAO;
import model.WasteRecord;

public class ScanPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Window window;
	
	private JLabel instructionLabel;
	private JLabel resultLabel;
	private JTextField barcodeHiddenField;
	private Timer transitionTimer;
	private WasteRecordDAO dao;
	
	private double measuredWeight = 0.0;
	
	public ScanPanel (final Window window) {
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

        barcodeHiddenField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String barcodeValue = barcodeHiddenField.getText().trim();
                if (barcodeValue.length() > 0) {
                    barcodeHiddenField.setEnabled(false);
                    
                    dao = new WasteRecordDAO();
                    
                    String studentName = dao.getStudentNameByBarcode(barcodeValue);
                    
                    if (studentName == null || studentName.length() == 0) {
                        studentName = "Unknown User";
                    }
                    
                    WasteRecord record = new WasteRecord(measuredWeight, new Date());
                    dao.insertRecordWithStudent(record, barcodeValue);
                    
                    instructionLabel.setText("Success!");
                    instructionLabel.setForeground(Color.GREEN);
                    
                    resultLabel.setText("ID: " + barcodeValue + " / Name: " + studentName + " / Saved: " + measuredWeight + "g");
                    
                    transitionTimer = new Timer(2000, new ActionListener() {
                        public void actionPerformed(ActionEvent ex) {
                            resetPanel();
                            ScanPanel.this.window.changeScreen("MEASURE_PANEL");
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
                ScanPanel.this.window.changeScreen("MAIN_MENU");
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
	    this.measuredWeight = weight;
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
	    measuredWeight = 0.0;
	}
}