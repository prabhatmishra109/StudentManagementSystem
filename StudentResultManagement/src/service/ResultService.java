package service;

import model.Student;

import java.util.*;
import java.util.stream.Collectors;

/** Ranking, toppers, filters, sorting and statistics using Comparator + Streams. */
public class ResultService {

    /** Tie-break: percentage desc, then total obtained desc, then name asc. */
    public static final Comparator<Student> RANK_ORDER =
            Comparator.comparingDouble(Student::getPercentage).reversed()
                    .thenComparing(Comparator.comparingDouble(Student::getTotalObtained).reversed())
                    .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Student> NAME_ASC = Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER);

    /** Dense ranking: equal percentage gets same rank (1,1,2...). Only students with marks are ranked. */
    public LinkedHashMap<Student, Integer> rankList(List<Student> all) {
        List<Student> sorted = all.stream().filter(Student::hasMarks).sorted(RANK_ORDER).toList();
        LinkedHashMap<Student, Integer> ranks = new LinkedHashMap<>();
        int rank = 0; double prev = Double.NaN;
        for (Student s : sorted) {
            if (s.getPercentage() != prev) { rank++; prev = s.getPercentage(); }
            ranks.put(s, rank);
        }
        return ranks;
    }

    public List<Student> toppers(List<Student> all) {
        OptionalDouble max = all.stream().filter(Student::hasMarks).mapToDouble(Student::getPercentage).max();
        if (max.isEmpty()) return List.of();
        return all.stream().filter(s -> s.hasMarks() && s.getPercentage() == max.getAsDouble()).toList();
    }

    public List<Student> topN(List<Student> all, int n) {
        return all.stream().filter(Student::hasMarks).sorted(RANK_ORDER).limit(n).toList();
    }

    public List<Student> passed(List<Student> all) {
        return all.stream().filter(Student::isPassed).sorted(RANK_ORDER).toList();
    }

    public List<Student> failed(List<Student> all) {
        return all.stream().filter(s -> s.hasMarks() && !s.isPassed()).sorted(NAME_ASC).toList();
    }

    public List<Student> sort(List<Student> all, String mode) {
        Comparator<Student> c = switch (mode) {
            case "NAME_DESC" -> NAME_ASC.reversed();
            case "MARKS_DESC" -> RANK_ORDER;
            case "MARKS_ASC" -> RANK_ORDER.reversed();
            default -> NAME_ASC;
        };
        return all.stream().sorted(c).toList();
    }

    public DoubleSummaryStatistics stats(List<Student> all) {
        return all.stream().filter(Student::hasMarks).collect(Collectors.summarizingDouble(Student::getPercentage));
    }
}
