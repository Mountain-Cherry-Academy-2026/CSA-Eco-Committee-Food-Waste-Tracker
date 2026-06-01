package window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class MeasurePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private Window window;
    private JLabel weightLabel;
    private controller.MeasureController arduinoController;
    private Thread arduinoThread;
    
    private String portName;
    private long tareValue = 258520;
    private double scaleFactor = 213.0;
    
    private double lastWeight = 0.0;
    private int stabilityCount = 0;
    private boolean isSettled = false;
    private double currentFinalWeight = 0.0;
    
    private final double THRESHOLD_OBJECT_ON = 5.0;
    private final double THRESHOLD_STABILITY = 0.5;
    private final int REQUIRED_STABILITY_COUNT = 10;
    
	public MeasurePanel (final Window window) {
		this.window = window;
        setLayout(new BorderLayout());
        
        Font defaultFont60 = new Font("Arial", Font.BOLD, 60);
        Font defaultFont45 = new Font("Arial", Font.BOLD, 45);
        Font defaultFont30 = new Font("Arial", Font.BOLD, 30);
        
        JLabel title = new JLabel("Measure", JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 30, 0));
        title.setFont(defaultFont60);
        add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        JButton btnSkip = new JButton("Skip");
        JButton btnReturn = new JButton("Return");
        
        btnSkip.setFont(defaultFont30);
        btnReturn.setFont(defaultFont30);
        
        menuPanel.add(btnSkip);
        menuPanel.add(btnReturn);
        
        JLabel instruction = new JLabel("Please place your plate on the scale.", JLabel.CENTER);
        instruction.setBorder(BorderFactory.createEmptyBorder(50, 0, 10, 0));
        instruction.setFont(defaultFont45);
        
        JLabel weight = new JLabel("Weight:", JLabel.CENTER);
        weight.setFont(defaultFont45);
        
        weightLabel = new JLabel("0.00 g", JLabel.CENTER);
        weightLabel.setFont(defaultFont60);
        weightLabel.setForeground(Color.BLUE);
        weightLabel.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createLineBorder(Color.BLACK, 2),
        		BorderFactory.createEmptyBorder(10, 30, 10, 30)        		
        ));
        JPanel weightLabelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        weightLabelWrapper.add(weightLabel);
        
        JPanel centerContainer = new JPanel(new GridLayout(3, 1));
        centerContainer.add(instruction);
        centerContainer.add(weight);
        centerContainer.add(weightLabelWrapper);
        add(centerContainer, BorderLayout.CENTER);
        
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        bottomContainer.add(menuPanel);
        add(bottomContainer, BorderLayout.SOUTH);

        btnSkip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isSettled = true;
                if (arduinoController != null) {
                    arduinoController.stopMeasurement();
                }
                
                ScanPanel scanPanel = (ScanPanel) MeasurePanel.this.window.getPanel("SCAN_PANEL");
                if (scanPanel != null) {
                    scanPanel.setMeasuredWeight(currentFinalWeight);
                }
                
                MeasurePanel.this.window.changeScreen("SCAN_PANEL");
            }
        });

        btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (arduinoController != null) {
            		arduinoController.stopMeasurement();
        		}
            	MeasurePanel.this.window.changeScreen("MAIN_MENU");
            }
        });
        
        this.portName = "COM3";
        this.tareValue = 258520;
        this.scaleFactor = 213.0;
        
        this.arduinoController = new controller.MeasureController(this, this.portName, this.tareValue, this.scaleFactor);
        this.arduinoThread = new Thread(this.arduinoController);
        this.arduinoThread.start();
	}
	
	public void updateWeightLabel(double finalWeight) {
	     DecimalFormat format = new DecimalFormat("#,##0.00");
	     weightLabel.setText(format.format(finalWeight) + " g");
	     
	     if (isSettled) {
	         return;
	     }
	     
	     currentFinalWeight = finalWeight;
	     
	     if (finalWeight > THRESHOLD_OBJECT_ON) {
	         double diff = Math.abs(finalWeight - lastWeight);
	         if (diff <= THRESHOLD_STABILITY) {
	             stabilityCount++;
	         } else {
	             stabilityCount = 0;
	         }
	         
	         if (stabilityCount >= REQUIRED_STABILITY_COUNT) {
	             isSettled = true;
	             weightLabel.setForeground(Color.GREEN);
	             
	             if (arduinoController != null) {
	                 arduinoController.stopMeasurement();
	             }
	             
	             Timer timer = new Timer(1500, new ActionListener() {
	                 public void actionPerformed(ActionEvent e) {
	                     ScanPanel scanPanel = (ScanPanel) MeasurePanel.this.window.getPanel("SCAN_PANEL");
	                     if (scanPanel != null) {
	                         scanPanel.setMeasuredWeight(currentFinalWeight);
	                     }
	                     MeasurePanel.this.window.changeScreen("SCAN_PANEL");
	                 }
	             });
	             timer.setRepeats(false);
	             timer.start();
	         }
	     } else {
	         stabilityCount = 0;
	     }
	     
	     lastWeight = finalWeight;
	}
}