package model;

import java.io.Serializable;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double PASS_PERCENT = 33.0;

    private String name;
    private double maxMarks;
    private double obtainedMarks;
    private int credits;

    public Subject(String name, double maxMarks, double obtainedMarks, int credits) {
        setName(name);
        if (maxMarks <= 0) throw new IllegalArgumentException("Max marks must be positive");
        this.maxMarks = maxMarks;
        setObtainedMarks(obtainedMarks);
        setCredits(credits);
    }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Subject name required");
        this.name = name.trim();
    }
    public double getMaxMarks() { return maxMarks; }
    public double getObtainedMarks() { return obtainedMarks; }
    public void setObtainedMarks(double m) {
        if (m < 0 || m > maxMarks)
            throw new IllegalArgumentException("Marks must be between 0 and " + maxMarks);
        this.obtainedMarks = m;
    }
    public int getCredits() { return credits; }
    public void setCredits(int c) {
        if (c <= 0) throw new IllegalArgumentException("Credits must be positive");
        this.credits = c;
    }
    public double getPercentage() { return obtainedMarks / maxMarks * 100.0; }
    public boolean isPassed() { return getPercentage() >= PASS_PERCENT; }
    public Grade getGrade() { return isPassed() ? Grade.fromPercentage(getPercentage()) : Grade.F; }

    @Override public String toString() {
        return String.format("%-22s %6.1f / %-6.1f  %s", name, obtainedMarks, maxMarks, getGrade().getLabel());
    }
}
