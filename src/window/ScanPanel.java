package window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScanPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Window window;
	
	public ScanPanel (final Window window) {
		this.window = window;
        setLayout(new BorderLayout());
        
        Font defaultFont60 = new Font("Arial", Font.BOLD, 60);
        Font defaultFont30 = new Font("Arial", Font.BOLD, 30);
        
        JLabel title = new JLabel("Scan", JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 30, 0));
        title.setFont(defaultFont60);
        add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        JButton btnReturn = new JButton("Return");
        
        btnReturn.setFont(defaultFont30);
        
        menuPanel.add(btnReturn);
        
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        bottomContainer.add(menuPanel);
        add(bottomContainer, BorderLayout.SOUTH);

        btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ScanPanel.this.window.changeScreen("MAIN_MENU");
            }
        });	
	}
}
