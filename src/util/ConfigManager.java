package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String FILE_NAME = "config.properties";
    private Properties props;
    
    public ConfigManager() {
        props = new Properties();
        loadConfig();
    }

    public void loadConfig() {
        FileInputStream fis = null;
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                fis = new FileInputStream(file);
                props.load(fis);
                
                if (props.getProperty("studentDbUrl") == null) {
                    props.setProperty("studentDbUrl", "jdbc:sqlite:/STDID.db");
                    props.setProperty("historyDbUrl", "jdbc:sqlite:/REC.db");
                    props.setProperty("historyTable", "waste_records");
                    saveConfig();
                }
            } else {
                props.setProperty("aPortName", "COM3");
                props.setProperty("bPortName", "COM4");
                props.setProperty("tareValue", "255550");
                props.setProperty("scaleFactor", "208.0");
                props.setProperty("studentDbUrl", "jdbc:sqlite:/STDID.db");
                props.setProperty("historyDbUrl", "jdbc:sqlite:/REC.db");
                props.setProperty("historyTable", "waste_records");
                saveConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) { try { fis.close(); } catch (Exception e) {} }
        }
    }
    
    public void setConfig(String aport, long tare, double scale) {
        props.setProperty("aPortName", aport);
        props.setProperty("tareValue", String.valueOf(tare));
        props.setProperty("scaleFactor", String.valueOf(scale));
        saveConfig();
    }
    
    public void setConfig(String aport, String bport, long tare, double scale) {
        props.setProperty("aPortName", aport);
        props.setProperty("bPortName", bport);
        props.setProperty("tareValue", String.valueOf(tare));
        props.setProperty("scaleFactor", String.valueOf(scale));
        saveConfig();
    }
    
    public void saveConfig() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(FILE_NAME);
            props.store(fos, "Arduino Scale Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) { try { fos.close(); } catch (Exception e) {} }
        }
    }
	
    public String getAPortName() { return props.getProperty("aPortName"); }
    public String getBPortName() { return props.getProperty("bPortName"); }
    public long getTareValue() { return Long.parseLong(props.getProperty("tareValue")); }
    public double getScaleFactor() { return Double.parseDouble(props.getProperty("scaleFactor")); }
    public String getStudentDbUrl() { return props.getProperty("studentDbUrl"); }
    public String getHistoryDbUrl() { return props.getProperty("historyDbUrl"); }
    public String getHistoryTable() { return props.getProperty("historyTable"); }
}