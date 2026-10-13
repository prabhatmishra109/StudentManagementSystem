package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String name, rollNumber, course;
    private int semester;
    private final List<Subject> subjects = new ArrayList<>();

    public Student(String id, String name, String rollNumber, String course, int semester) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID required");
        this.id = id.trim();
        setName(name); setRollNumber(rollNumber); setCourse(course); setSemester(semester);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) {
        if (n == null || n.isBlank()) throw new IllegalArgumentException("Name required");
        name = n.trim();
    }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String r) {
        if (r == null || r.isBlank()) throw new IllegalArgumentException("Roll number required");
        rollNumber = r.trim();
    }
    public String getCourse() { return course; }
    public void setCourse(String c) {
        if (c == null || c.isBlank()) throw new IllegalArgumentException("Course required");
        course = c.trim();
    }
    public int getSemester() { return semester; }
    public void setSemester(int s) {
        if (s < 1 || s > 12) throw new IllegalArgumentException("Semester must be 1-12");
        semester = s;
    }

    public List<Subject> getSubjects() { return Collections.unmodifiableList(subjects); }

    /** Adds a subject, or updates marks if a subject with same name exists. */
    public void addOrUpdateSubject(Subject s) {
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getName().equalsIgnoreCase(s.getName())) { subjects.set(i, s); return; }
        }
        subjects.add(s);
    }
    public boolean removeSubject(String name) {
        return subjects.removeIf(s -> s.getName().equalsIgnoreCase(name));
    }

    public Result getResult() { return Result.calculate(subjects); }
    public double getPercentage() { return getResult().getPercentage(); }
    public double getTotalObtained() { return getResult().getTotalObtained(); }
    public boolean isPassed() { return getResult().getStatus() == ResultStatus.PASS; }
    public boolean hasMarks() { return !subjects.isEmpty(); }

    @Override public String toString() {
        return String.format("%-8s %-20s %-10s %-12s Sem %-2d  %6.2f%%  %s",
                id, name, rollNumber, course, semester, getPercentage(), getResult().getStatus());
    }
}
