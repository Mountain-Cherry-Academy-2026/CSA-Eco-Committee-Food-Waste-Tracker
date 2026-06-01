package window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainMenuPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Window window;

    public MainMenuPanel(final Window window) {
        this.window = window;
        setLayout(new BorderLayout());
        
        Font defaultFont60 = new Font("Arial", Font.BOLD, 60);
        Font defaultFont30 = new Font("Arial", Font.BOLD, 30);
        
        JLabel title = new JLabel("Food Waste Tracker", JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 30, 0));
        title.setFont(defaultFont60);
        add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        JButton btnMeasure = new JButton("Measure");
        JButton btnView = new JButton("View");
        JButton btnCali = new JButton("Calibrate");
        JButton btnExit = new JButton("Exit");
        
        btnMeasure.setFont(defaultFont30);
        btnView.setFont(defaultFont30);
        btnCali.setFont(defaultFont30);
        btnExit.setFont(defaultFont30);
        
        menuPanel.add(btnMeasure);
        menuPanel.add(btnView);
        menuPanel.add(btnCali);
        menuPanel.add(btnExit);

        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        bottomContainer.add(menuPanel);
        add(bottomContainer, BorderLayout.SOUTH);

        btnMeasure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainMenuPanel.this.window.changeScreen("MEASURE");
            }
        });

        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(MainMenuPanel.this.window, "Preparing");
                MainMenuPanel.this.window.changeScreen("VIEW");
            }
        });
        
        btnCali.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainMenuPanel.this.window.changeScreen("CALIBRATE");
            }
        });
        
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}