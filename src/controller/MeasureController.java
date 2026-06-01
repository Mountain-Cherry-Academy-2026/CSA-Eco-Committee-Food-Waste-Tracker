package controller;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Date;
import javax.swing.SwingUtilities;
import window.MeasurePanel;
import window.CalibratePanel;
import model.WasteRecord;
import dao.WasteRecordDAO;

public class MeasureController implements Runnable {

    private MeasurePanel measurePanel;
    private CalibratePanel calibratePanel;
    
    private boolean isRunning;
    private SerialPort comPort;
    private WasteRecordDAO dao;
    private String selectedPortName;
    private long tareValue;
    private double calibrationFactor;
    private boolean isCalibrateMode;

    public MeasureController(MeasurePanel panel, String portName, long tareValue, double calibrationFactor) {
        this.measurePanel = panel;
        this.selectedPortName = portName;
        this.tareValue = tareValue;
        this.calibrationFactor = calibrationFactor;
        this.dao = new WasteRecordDAO();
        this.isRunning = true;
        this.isCalibrateMode = false;
    }

    public MeasureController(CalibratePanel panel, String portName, long tareValue, double calibrationFactor) {
        this.calibratePanel = panel;
        this.selectedPortName = portName;
        this.tareValue = tareValue;
        this.calibrationFactor = calibrationFactor;
        this.dao = new WasteRecordDAO();
        this.isRunning = true;
        this.isCalibrateMode = true;
    }

    public void stopMeasurement() {
        this.isRunning = false;
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
    }

    public void run() {
        comPort = SerialPort.getCommPort(selectedPortName);
        comPort.setBaudRate(19200);

        if (!comPort.openPort()) return;

        InputStream in = comPort.getInputStream();
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(isr);

        try {
            while (isRunning) {
                String rawStr = reader.readLine();
                if (rawStr == null) break;
                
                rawStr = rawStr.trim();
                if (rawStr.length() == 0) continue;

                long rawData = Long.parseLong(rawStr);

                if (isCalibrateMode) {
                    final double rawDataDouble = (double) rawData;
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (calibratePanel != null) {
                                calibratePanel.updateRawLabel(rawDataDouble);
                            }
                        }
                    });
                } else {
                    double calculatedWeight = (double)(rawData - tareValue) / calibrationFactor;
                    if (calculatedWeight < 0) calculatedWeight = 0.0;
                    
                    double roundedWeight = Math.round(calculatedWeight * 100) / 100.0;
                    final double finalWeight = roundedWeight;

                    WasteRecord record = new WasteRecord(finalWeight, new Date());
                    dao.insertRecord(record);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (measurePanel != null) {
                                measurePanel.updateWeightLabel(finalWeight);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                isr.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            stopMeasurement();
        }
    }
}