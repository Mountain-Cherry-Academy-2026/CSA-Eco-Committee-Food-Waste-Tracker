package controller;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Date;
import javax.swing.SwingUtilities;
import window.MeasurePanel;
import model.WasteRecord;
import dao.WasteRecordDAO;

public class MeasureController implements Runnable {

    private MeasurePanel panel;
    private boolean isRunning = true;
    private SerialPort comPort;
    private WasteRecordDAO dao;

    private final long TARE_VALUE = 8388608;          
    private final double CALIBRATION_FACTOR = -7050.0; 

    public MeasureController(MeasurePanel panel) {
        this.panel = panel;
        this.dao = new WasteRecordDAO();
    }

    public void stopMeasurement() {
        this.isRunning = false;
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
    }

    public void run() {
        if (SerialPort.getCommPorts().length == 0) return;
        
        comPort = SerialPort.getCommPorts()[0]; 
        comPort.setBaudRate(9600);

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

                double calculatedWeight = (rawData - TARE_VALUE) / CALIBRATION_FACTOR;
                if (calculatedWeight < 0) calculatedWeight = 0.0;
                
                double roundedWeight = Math.round(calculatedWeight * 100) / 100.0;
                final double finalWeight = roundedWeight;

                WasteRecord record = new WasteRecord(finalWeight, new Date());

                dao.insertRecord(record);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        panel.updateWeightLabel(finalWeight);
                    }
                });
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