package model;

import java.util.Date;

public class WasteRecord {
    private double weightG;
    private Date recordTime;

    public WasteRecord() {
    }

    public WasteRecord(double weightG, Date recordTime) {
        this.weightG = weightG;
        this.recordTime = recordTime;
    }

    public double getWeightG() {
        return weightG;
    }

    public void setWeightG(double weightG) {
        this.weightG = weightG;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
}