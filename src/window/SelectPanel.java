package window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

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

        final JButton btn0 = new JButton();
        btn0.setLayout(new BorderLayout());
        JTextArea txt0 = new JTextArea("[ 0 ]\n\nTray Only\n(Plate)");
        txt0.setFont(buttonFont);
        txt0.setEditable(false);
        txt0.setOpaque(false);
        txt0.setFocusable(false);
        txt0.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { btn0.doClick(); }
        });
        btn0.add(txt0, BorderLayout.CENTER);

        final JButton btn1 = new JButton();
        btn1.setLayout(new BorderLayout());
        JTextArea txt1 = new JTextArea("[ 1 ]\n\nTray + Bowl\n(Plate + Bowl)");
        txt1.setFont(buttonFont);
        txt1.setEditable(false);
        txt1.setOpaque(false);
        txt1.setFocusable(false);
        txt1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { btn1.doClick(); }
        });
        btn1.add(txt1, BorderLayout.CENTER);

        final JButton btn2 = new JButton();
        btn2.setLayout(new BorderLayout());
        JTextArea txt2 = new JTextArea("[ 2 ]\n\nBowl Only\n(Bowl)");
        txt2.setFont(buttonFont);
        txt2.setEditable(false);
        txt2.setOpaque(false);
        txt2.setFocusable(false);
        txt2.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { btn2.doClick(); }
        });
        btn2.add(txt2, BorderLayout.CENTER);

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