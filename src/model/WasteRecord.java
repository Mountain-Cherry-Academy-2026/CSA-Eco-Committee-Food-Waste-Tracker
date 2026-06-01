package model;

import java.util.Date;

public class WasteRecord {
    private int historyId;
    private Date recordTime;
    private double weightG;
    private int trayType;
    private int studentId;
    private int grade;

    public WasteRecord() {}

    public WasteRecord(Date recordTime, double weightG, int trayType, int grade) {
        this.recordTime = recordTime;
        this.weightG = weightG;
        this.trayType = trayType;
        this.grade = grade;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public Date getRecordTime() { return recordTime; }
    public void setRecordTime(Date recordTime) { this.recordTime = recordTime; }

    public double getWeightG() { return weightG; }
    public void setWeightG(double weightG) { this.weightG = weightG; }

    public int getTrayType() { return trayType; }
    public void setTrayType(int trayType) { this.trayType = trayType; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getGrade() { return grade; }
    public void setGrade(int grade) { this.grade = grade; }
}