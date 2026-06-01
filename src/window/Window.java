package window;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Window extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private CardLayout cardLayout;
	private JPanel mainContainer;
	private Map panels;
	
	public void mainFrame() {
		setTitle("Food Waste Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(true);
		
		cardLayout = new CardLayout();
		mainContainer = new JPanel(cardLayout);
		panels = new HashMap();
		
		MainMenuPanel mainMenuPanel = new MainMenuPanel(this);
		MeasurePanel measurePanel = new MeasurePanel(this);
		ScanPanel scanPanel = new ScanPanel(this);
		ViewPanel viewPanel = new ViewPanel(this);
		CalibratePanel calibratePanel = new CalibratePanel(this);
		
		panels.put("MAIN_MENU", mainMenuPanel);
		panels.put("MEASURE_PANEL", measurePanel);
		panels.put("SCAN_PANEL", scanPanel);
		panels.put("VIEW_PANEL", viewPanel);
		panels.put("CALIBRATE_PANEL", calibratePanel);
		
		mainContainer.add("MAIN_MENU", mainMenuPanel);
		mainContainer.add("MEASURE_PANEL", measurePanel);
		mainContainer.add("SCAN_PANEL", scanPanel);
		mainContainer.add("VIEW_PANEL", viewPanel);
		mainContainer.add("CALIBRATE_PANEL", calibratePanel);
		
		getContentPane().add(mainContainer);
		cardLayout.show(mainContainer, "MAIN_MENU");
		
		setVisible(true);
	}
	
    public void changeScreen(String screenName) {
        if (cardLayout != null && mainContainer != null) {
            cardLayout.show(mainContainer, screenName);
        }
    }
    
    public JPanel getPanel(String name) {
        if (panels == null || name == null) {
            return null;
        }
        return (JPanel) panels.get(name);
    }
}