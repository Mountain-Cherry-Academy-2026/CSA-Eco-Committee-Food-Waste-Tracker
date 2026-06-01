package controller;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.swing.SwingUtilities;
import window.MeasurePanel;
import window.CalibratePanel;

public class MeasureController implements Runnable {

    private MeasurePanel measurePanel;
    private CalibratePanel calibratePanel;
    
    private boolean isRunning;
    private SerialPort serialPort;
    private String selectedPortName;
    private long tareValue;
    private double calibrationFactor;
    private boolean isCalibrateMode;

    public MeasureController(MeasurePanel panel, String portName, long tareValue, double calibrationFactor) {
        this.measurePanel = panel;
        this.selectedPortName = portName;
        this.tareValue = tareValue;
        this.calibrationFactor = calibrationFactor;
        this.isRunning = true;
        this.isCalibrateMode = false;
    }

    public MeasureController(CalibratePanel panel, String portName, long tareValue, double calibrationFactor) {
        this.calibratePanel = panel;
        this.selectedPortName = portName;
        this.tareValue = tareValue;
        this.calibrationFactor = calibrationFactor;
        this.isRunning = true;
        this.isCalibrateMode = true;
    }

    public void stopMeasurement() {
        this.isRunning = false;
        if (serialPort != null) {
            try {
                serialPort.close();
                serialPort = null;
            } catch (Exception e) {
            }
        }
    }

    public void run() {
        InputStream in = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        
        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(selectedPortName);
            serialPort = (SerialPort) portId.open("ArduinoMeasureApp", 2000);
            
            serialPort.setSerialPortParams(115200, 
                    SerialPort.DATABITS_8, 
                    SerialPort.STOPBITS_1, 
                    SerialPort.PARITY_NONE);

            System.out.println("ê¨å˜: " + selectedPortName);

            in = serialPort.getInputStream();
            isr = new InputStreamReader(in);
            reader = new BufferedReader(isr);
            
            while (isRunning) {
                if (reader.ready()) {
                    String rawStr = reader.readLine();
                    if (rawStr == null) continue;
                    
                    rawStr = rawStr.trim();
                    if (rawStr.length() == 0) continue;

                    if (rawStr.indexOf("HX711") != -1 || rawStr.indexOf("not ready") != -1) {
                        continue; 
                    }

                    long rawData = 0;
                    try {
                        rawData = Long.parseLong(rawStr);
                    } catch (NumberFormatException nfe) {
                        continue;
                    }

                    if (isCalibrateMode) {
                        final long finalRaw = rawData;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (calibratePanel != null) {
                                    calibratePanel.updateRawLabel((double) finalRaw);
                                }
                            }
                        });
                    } else {
                        double calculatedWeight = (double)(rawData - tareValue) / calibrationFactor;
                        if (calculatedWeight < 0) calculatedWeight = 0.0;
                        
                        double roundedWeight = Math.round(calculatedWeight * 100) / 100.0;
                        final double finalWeight = roundedWeight;
                        final long finalRaw = rawData;
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (measurePanel != null) {
                                    measurePanel.updateWeightLabel(finalWeight, finalRaw);
                                }
                            }
                        });
                    }
                } else {
                    try { Thread.sleep(10); } catch (Exception e) {}
                }
            }
        } catch (Exception e) {
            System.out.println("é∏îs: " + selectedPortName);
            e.printStackTrace();
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ex) {}
            try { if (isr != null) isr.close(); } catch (Exception ex) {}
            try { if (in != null) in.close(); } catch (Exception ex) {}
            stopMeasurement();
        }
    }
}