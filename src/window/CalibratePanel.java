package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.ConfigManager;

public class CalibratePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Window window;
    
    private JTextField tfPortName;
    private JTextField tfTareValue;
    private JTextField tfScaleFactor;
    private JLabel lblCurrentRawValue;
    
    private ConfigManager configManager;
    private controller.MeasureController arduinoController;
    private Thread arduinoThread;
	
	public CalibratePanel(Window window) {
		this.window = window;
		this.configManager = new ConfigManager();
		
        setLayout(new BorderLayout());
        
        Font defaultFont60 = new Font("Arial", Font.BOLD, 60);
        Font defaultFont30 = new Font("Arial", Font.BOLD, 30);
        Font labelFont = new Font("Arial", Font.PLAIN, 24);
        
        JLabel title = new JLabel("Calibrate", JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));
        title.setFont(defaultFont60);
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));
        
        JLabel lblPort = new JLabel("Serial Port Name:", JLabel.RIGHT);
        lblPort.setFont(labelFont);
        tfPortName = new JTextField(configManager.getAPortName());
        tfPortName.setFont(labelFont);
        
        JLabel lblTare = new JLabel("Tare Value (Offset):", JLabel.RIGHT);
        lblTare.setFont(labelFont);
        tfTareValue = new JTextField(String.valueOf(configManager.getTareValue()));
        tfTareValue.setFont(labelFont);
        
        JLabel lblScale = new JLabel("Scale Factor (Calibration):", JLabel.RIGHT);
        lblScale.setFont(labelFont);
        tfScaleFactor = new JTextField(String.valueOf(configManager.getScaleFactor()));
        tfScaleFactor.setFont(labelFont);
        
        JLabel lblRawTitle = new JLabel("Real-time Raw Data:", JLabel.RIGHT);
        lblRawTitle.setFont(labelFont);
        lblCurrentRawValue = new JLabel("Connecting...", JLabel.LEFT);
        lblCurrentRawValue.setFont(labelFont);
        lblCurrentRawValue.setForeground(Color.RED);
        
        formPanel.add(lblPort);
        formPanel.add(tfPortName);
        formPanel.add(lblTare);
        formPanel.add(tfTareValue);
        formPanel.add(lblScale);
        formPanel.add(tfScaleFactor);
        formPanel.add(lblRawTitle);
        formPanel.add(lblCurrentRawValue);
        
        add(formPanel, BorderLayout.CENTER);

        JPanel menuPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        JButton btnSave = new JButton("Save Settings");
        JButton btnReturn = new JButton("Return");
        
        btnSave.setFont(defaultFont30);
        btnReturn.setFont(defaultFont30);
        
        menuPanel.add(btnSave);
        menuPanel.add(btnReturn);
        
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        bottomContainer.add(menuPanel);
        add(bottomContainer, BorderLayout.SOUTH);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String port = tfPortName.getText().trim();
                    String tareStr = tfTareValue.getText().trim();
                    String scaleStr = tfScaleFactor.getText().trim();
                    
                    if (port.length() == 0 || tareStr.length() == 0 || scaleStr.length() == 0) {
                        JOptionPane.showMessageDialog(CalibratePanel.this.window, "Please fill in all fields.");
                        return;
                    }
                    
                    long tare = Long.parseLong(tareStr);
                    double scale = Double.parseDouble(scaleStr);
                    
                    configManager.setConfig(port, tare, scale);
                    
                    JOptionPane.showMessageDialog(CalibratePanel.this.window, "Settings saved successfully!\nIf the port changed, please reopen this screen.");
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CalibratePanel.this.window, "Scale Factor and Tare must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });	

        btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (arduinoController != null) {
                    arduinoController.stopMeasurement();
                }
                CalibratePanel.this.window.changeScreen("MAIN_MENU");
            }
        });	
        
        String activePort = configManager.getAPortName();
        this.arduinoController = new controller.MeasureController(this, activePort, 0, 1.0);
        this.arduinoThread = new Thread(this.arduinoController);
        this.arduinoThread.start();
	}
	
	public void updateRawLabel(double rawValue) {
	    long cleanRaw = (long) rawValue;
	    this.lblCurrentRawValue.setText(String.valueOf(cleanRaw));
	}
}