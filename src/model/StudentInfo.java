package model;

public class StudentInfo {
    private String name;
    private int grade;

    public StudentInfo(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() { return name; }
    public int getGrade() { return grade; }
}