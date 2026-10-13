package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Immutable calculated result for a student. */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double OVERALL_PASS_PERCENT = 33.0;

    private final double totalObtained, totalMax, percentage, gpa;
    private final Grade grade;
    private final ResultStatus status;

    private Result(double o, double m, double p, double gpa, Grade g, ResultStatus s) {
        totalObtained = o; totalMax = m; percentage = p; this.gpa = gpa; grade = g; status = s;
    }

    public static Result calculate(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty())
            return new Result(0, 0, 0, 0, Grade.F, ResultStatus.NOT_AVAILABLE);
        double obtained = subjects.stream().mapToDouble(Subject::getObtainedMarks).sum();
        double max = subjects.stream().mapToDouble(Subject::getMaxMarks).sum();
        double pct = round(obtained / max * 100.0);
        boolean allPassed = subjects.stream().allMatch(Subject::isPassed);
        ResultStatus status = (allPassed && pct >= OVERALL_PASS_PERCENT) ? ResultStatus.PASS : ResultStatus.FAIL;
        Grade grade = status == ResultStatus.PASS ? Grade.fromPercentage(pct) : Grade.F;
        int credits = subjects.stream().mapToInt(Subject::getCredits).sum();
        double points = subjects.stream().mapToDouble(s -> s.getGrade().getGradePoint() * s.getCredits()).sum();
        double gpa = round(points / credits);
        return new Result(obtained, max, pct, gpa, grade, status);
    }

    private static double round(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getTotalObtained() { return totalObtained; }
    public double getTotalMax() { return totalMax; }
    public double getPercentage() { return percentage; }
    public double getGpa() { return gpa; }
    public Grade getGrade() { return grade; }
    public ResultStatus getStatus() { return status; }
}
