package window;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CardLayout cardLayout;
	private JPanel mainContainer;
	
	public void mainFrame() {
		setTitle("Food Waste Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(true);
		
		cardLayout = new CardLayout();
		mainContainer = new JPanel(cardLayout);
		
		MainMenuPanel mainMenuPanel = new MainMenuPanel(this);
		MeasurePanel measurePanel = new MeasurePanel(this);
		ViewPanel viewPanel = new ViewPanel(this);
		CalibratePanel calibratePanel = new CalibratePanel(this);
		
		mainContainer.add("MAIN_MENU", mainMenuPanel);
		mainContainer.add("MEASURE", measurePanel);
		mainContainer.add("VIEW", viewPanel);
		mainContainer.add("CALIBRATE", calibratePanel);
		getContentPane().add(mainContainer);
		cardLayout.show(mainContainer, "MAIN_MENU");
		
		setVisible(true);
	}
	
    public void changeScreen(String screenName) {
        if (cardLayout != null && mainContainer != null) {
            cardLayout.show(mainContainer, screenName);
        }
    }
}
