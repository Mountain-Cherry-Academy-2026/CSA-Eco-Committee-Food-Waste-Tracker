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
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
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
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));
        title.setFont(defaultFont60);
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel lblPort = new JLabel("Port Name:", JLabel.RIGHT);
        lblPort.setFont(labelFont);
        tfPortName = new JTextField(configManager.getAPortName());
        tfPortName.setFont(labelFont);

        JLabel lblTare = new JLabel("Tare Value:", JLabel.RIGHT);
        lblTare.setFont(labelFont);
        tfTareValue = new JTextField(String.valueOf(configManager.getTareValue()));
        tfTareValue.setFont(labelFont);

        JLabel lblScale = new JLabel("Scale Factor:", JLabel.RIGHT);
        lblScale.setFont(labelFont);
        tfScaleFactor = new JTextField(String.valueOf(configManager.getScaleFactor()));
        tfScaleFactor.setFont(labelFont);

        JLabel lblRaw = new JLabel("Current Raw Value:", JLabel.RIGHT);
        lblRaw.setFont(labelFont);
        lblCurrentRawValue = new JLabel("0", JLabel.LEFT);
        lblCurrentRawValue.setFont(labelFont);
        lblCurrentRawValue.setForeground(Color.RED);

        centerPanel.add(lblPort);
        centerPanel.add(tfPortName);
        centerPanel.add(lblTare);
        centerPanel.add(tfTareValue);
        centerPanel.add(lblScale);
        centerPanel.add(tfScaleFactor);
        centerPanel.add(lblRaw);
        centerPanel.add(lblCurrentRawValue);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton btnSave = new JButton("Save");
        JButton btnReturn = new JButton("Return");

        btnSave.setFont(defaultFont30);
        btnReturn.setFont(defaultFont30);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnReturn);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String port = tfPortName.getText().trim();
                    String tareStr = tfTareValue.getText().trim();
                    String scaleStr = tfScaleFactor.getText().trim();
                    
                    if(port.length() == 0 || tareStr.length() == 0 || scaleStr.length() == 0) {
                        JOptionPane.showMessageDialog(CalibratePanel.this.window, "All fields must be filled.");
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
        
        this.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                if (arduinoController != null) {
                    arduinoController.stopMeasurement();
                }
                String activePort = configManager.getAPortName();
                arduinoController = new controller.MeasureController(CalibratePanel.this, activePort, 0, 1.0);
                arduinoThread = new Thread(arduinoController);
                arduinoThread.start();
            }
            public void ancestorRemoved(AncestorEvent event) {
                if (arduinoController != null) {
                    arduinoController.stopMeasurement();
                }
            }
            public void ancestorMoved(AncestorEvent event) {}
        });
	}
	
	public void updateRawLabel(double rawValue) {
	    long cleanRaw = (long) rawValue;
	    this.lblCurrentRawValue.setText(String.valueOf(cleanRaw));
	}
}