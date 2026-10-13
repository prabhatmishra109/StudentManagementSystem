package model;

/** Grading rule (percentage based) with grade points for GPA. */
public enum Grade {
    A_PLUS("A+", 90, 10), A("A", 80, 9), B("B", 70, 8), C("C", 60, 7),
    D("D", 50, 6), E("E", 33, 5), F("F", 0, 0);

    private final String label;
    private final double minPercent;
    private final int gradePoint;

    Grade(String label, double minPercent, int gradePoint) {
        this.label = label; this.minPercent = minPercent; this.gradePoint = gradePoint;
    }

    public String getLabel() { return label; }
    public int getGradePoint() { return gradePoint; }

    public static Grade fromPercentage(double p) {
        for (Grade g : values()) if (p >= g.minPercent) return g;
        return F;
    }
}
