package util;

import model.Result;
import model.Student;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.Map;

public class CsvExporter {
    private static String esc(String v) {
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) return "\"" + v.replace("\"", "\"\"") + "\"";
        return v;
    }

    public static void export(Map<Student, Integer> ranked, Iterable<Student> all, String path) throws IOException {
        Path p = Paths.get(path);
        if (p.getParent() != null) Files.createDirectories(p.getParent());
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(p))) {
            w.println("Rank,ID,Name,Roll No,Course,Semester,Subjects,Obtained,Maximum,Percentage,GPA,Grade,Status");
            for (Student s : all) {
                Result r = s.getResult();
                Integer rank = ranked.get(s);
                w.printf("%s,%s,%s,%s,%s,%d,%d,%.2f,%.2f,%.2f,%.2f,%s,%s%n",
                        rank == null ? "-" : rank, esc(s.getId()), esc(s.getName()), esc(s.getRollNumber()),
                        esc(s.getCourse()), s.getSemester(), s.getSubjects().size(), r.getTotalObtained(),
                        r.getTotalMax(), r.getPercentage(), r.getGpa(), r.getGrade().getLabel(), r.getStatus());
            }
        }
    }
}
