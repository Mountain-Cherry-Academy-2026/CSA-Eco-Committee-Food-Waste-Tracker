package window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class SelectPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Window window;
    private JTextField hiddenKeyField;
    private double currentWeight = 0.0;

    public SelectPanel(final Window window) {
        this.window = window;
        setLayout(new BorderLayout());

        Font titleFont = new Font("Arial", Font.BOLD, 50);
        Font buttonFont = new Font("Arial", Font.BOLD, 35);

        JLabel titleLabel = new JLabel("Select Tray / Bowl Type", JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));
        titleLabel.setFont(titleFont);
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonGrid = new JPanel(new GridLayout(1, 3, 30, 0));
        buttonGrid.setBorder(BorderFactory.createEmptyBorder(20, 50, 60, 50));

        JButton btn0 = new JButton("<html><center>[ 0 ]<br><br>Tray Only<br>(Plate)</center></html>");
        JButton btn1 = new JButton("<html><center>[ 1 ]<br><br>Tray + Bowl<br>(Plate + Bowl)</center></html>");
        JButton btn2 = new JButton("<html><center>[ 2 ]<br><br>Bowl Only<br>(Bowl)</center></html>");

        btn0.setFont(buttonFont);
        btn1.setFont(buttonFont);
        btn2.setFont(buttonFont);

        buttonGrid.add(btn0);
        buttonGrid.add(btn1);
        buttonGrid.add(btn2);
        add(buttonGrid, BorderLayout.CENTER);

        hiddenKeyField = new JTextField(0);
        hiddenKeyField.setBorder(null);
        hiddenKeyField.setBackground(new Color(0,0,0,0));
        add(hiddenKeyField, BorderLayout.SOUTH);

        btn0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleSelection(0); }
        });
        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleSelection(1); }
        });
        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleSelection(2); }
        });

        hiddenKeyField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                
                if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_2) {
                    int selectedCode = keyCode - KeyEvent.VK_0;
                    handleSelection(selectedCode);
                } else if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD2) {
                    int selectedCode = keyCode - KeyEvent.VK_NUMPAD0;
                    handleSelection(selectedCode);
                }
            }
        });

        this.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        hiddenKeyField.requestFocusInWindow();
                    }
                });
            }
            public void ancestorRemoved(AncestorEvent event) {}
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    public void setMeasuredWeight(double weight) {
        this.currentWeight = weight;
        System.out.println("[SelectPanel] Received weight: " + weight + "g");
    }

    private void handleSelection(int typeCode) {
        System.out.println("[SelectPanel] Selected type code: " + typeCode);
        
        ScanPanel scanPanel = (ScanPanel) window.getPanel("SCAN_PANEL"); 
        
        if (scanPanel != null) {
            scanPanel.setMeasuredWeight(this.currentWeight);
            scanPanel.setTrayType(typeCode);
        }
        window.changeScreen("SCAN_PANEL");
    }
}