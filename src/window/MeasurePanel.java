package window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class MeasurePanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Window window;
    private JLabel weightLabel;
    private controller.MeasureController arduinoController;
    private Thread arduinoThread;
    
    private String portName;
    private long tareValue = 125000;
    private double scaleFactor = 213.0;
    
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
        JButton btnReturn = new JButton("Return");
        btnReturn.setFont(defaultFont30);
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

        btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (arduinoController != null) {
            		arduinoController.stopMeasurement();
        		}
            	MeasurePanel.this.window.changeScreen("MAIN_MENU");
            }
        });
        
        util.ConfigManager config = new util.ConfigManager();
        portName = config.getAPortName();
        tareValue = config.getTareValue();
        scaleFactor = config.getScaleFactor();
        
        this.arduinoController = new controller.MeasureController(this, portName, tareValue, scaleFactor);
        arduinoThread = new Thread(this.arduinoController);
        arduinoThread.start();
	}
	
	public void updateWeightLabel(double finalWeight) {
	     DecimalFormat format = new DecimalFormat("#,##0.00");
	     weightLabel.setText(format.format(finalWeight) + " g");
	}
}