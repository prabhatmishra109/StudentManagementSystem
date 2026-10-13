import model.*;
import service.ResultService;
import service.StudentService;
import storage.FileStorage;
import util.CsvExporter;
import util.InputValidator;

import java.util.*;

public class Main {
    private static final StudentService students = new StudentService(new FileStorage("data/students.dat"));
    private static final ResultService results = new ResultService();
    private static final InputValidator in = new InputValidator(new Scanner(System.in));

    public static void main(String[] args) {
        System.out.println("Loaded " + students.getAll().size() + " student record(s).");
        while (true) {
            printMenu();
            int choice = in.intInRange("Select an option: ", 1, 14);
            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> printList(students.getAll());
                    case 3 -> search();
                    case 4 -> update();
                    case 5 -> marks();
                    case 6 -> generateResult();
                    case 7 -> rankList();
                    case 8 -> toppers();
                    case 9 -> printList(results.passed(students.getAll()));
                    case 10 -> printList(results.failed(students.getAll()));
                    case 11 -> sort();
                    case 12 -> delete();
                    case 13 -> export();
                    case 14 -> { System.out.println("Data saved. Goodbye!"); return; }
                }
            } catch (IllegalArgumentException | NoSuchElementException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("""

                ===== STUDENT RESULT MANAGEMENT SYSTEM =====
                 1. Add Student               8. View Topper(s)
                 2. View All Students         9. Filter Passed Students
                 3. Search Student           10. Filter Failed Students
                 4. Update Student Details   11. Sort Students
                 5. Enter / Update Marks     12. Delete Student
                 6. Generate Student Result  13. Export Results to CSV
                 7. Display Rank List        14. Exit""");
    }

    private static void printList(List<Student> list) {
        if (list.isEmpty()) { System.out.println("No students found."); return; }
        System.out.printf("%-8s %-20s %-10s %-12s %-6s %8s  %s%n", "ID", "Name", "Roll", "Course", "Sem", "Percent", "Status");
        list.forEach(System.out::println);
    }

    private static void addStudent() {
        String id = in.text("Student ID: ");
        if (students.findById(id).isPresent()) throw new IllegalArgumentException("ID already exists");
        String name = in.text("Name: ");
        String roll = in.text("Roll number: ");
        String course = in.text("Course: ");
        int sem = in.intInRange("Semester (1-12): ", 1, 12);
        students.add(new Student(id, name, roll, course, sem));
        System.out.println("Student added.");
        if (in.yes("Enter marks now?")) enterMarksFor(id);
    }

    private static void search() {
        System.out.println("1. By ID  2. By Roll number  3. By Name");
        int t = in.intInRange("Choose: ", 1, 3);
        String q = in.text("Search: ");
        switch (t) {
            case 1 -> students.findById(q).ifPresentOrElse(Main::showDetails, () -> System.out.println("Not found."));
            case 2 -> students.findByRoll(q).ifPresentOrElse(Main::showDetails, () -> System.out.println("Not found."));
            default -> printList(students.searchByName(q));
        }
    }

    private static void showDetails(Student s) {
        System.out.printf("ID: %s | Name: %s | Roll: %s | Course: %s | Semester: %d%n",
                s.getId(), s.getName(), s.getRollNumber(), s.getCourse(), s.getSemester());
        s.getSubjects().forEach(sub -> System.out.println("   " + sub));
    }

    private static void update() {
        String id = in.text("Student ID to update: ");
        Student s = students.findById(id).orElseThrow(() -> new NoSuchElementException("Student not found"));
        showDetails(s);
        System.out.println("Leave blank to keep current value.");
        String name = in.optional("New name: ");
        String roll = in.optional("New roll number: ");
        String course = in.optional("New course: ");
        String sem = in.optional("New semester: ");
        Integer semester = null;
        if (!sem.isEmpty()) {
            try { semester = Integer.parseInt(sem); } catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid semester"); }
        }
        students.update(id, name, roll, course, semester);
        System.out.println("Student updated.");
    }

    private static void marks() { enterMarksFor(in.text("Student ID: ")); }

    private static void enterMarksFor(String id) {
        Student s = students.findById(id).orElseThrow(() -> new NoSuchElementException("Student not found"));
        int n = in.intInRange("How many subjects to enter/update? ", 1, 20);
        for (int i = 1; i <= n; i++) {
            System.out.println("Subject " + i + (s.getSubjects().isEmpty() ? "" : " (same name updates existing)"));
            String name = in.text("  Name: ");
            double max = in.doubleInRange("  Maximum marks: ", 1, 1000);
            double got = in.doubleInRange("  Obtained marks: ", 0, max);
            int credits = in.intInRange("  Credits (1-10): ", 1, 10);
            students.setMarks(id, new Subject(name, max, got, credits));
        }
        System.out.println("Marks saved.");
    }

    private static void generateResult() {
        Student s = students.findById(in.text("Student ID: ")).orElseThrow(() -> new NoSuchElementException("Student not found"));
        Result r = s.getResult();
        showDetails(s);
        if (r.getStatus() == ResultStatus.NOT_AVAILABLE) { System.out.println("No marks entered yet."); return; }
        Integer rank = results.rankList(students.getAll()).get(s);
        System.out.printf("Total: %.2f / %.2f%nPercentage: %.2f%%%nGPA: %.2f / 10%nGrade: %s%nStatus: %s%nRank: %s%n",
                r.getTotalObtained(), r.getTotalMax(), r.getPercentage(), r.getGpa(),
                r.getGrade().getLabel(), r.getStatus(), rank);
    }

    private static void rankList() {
        Map<Student, Integer> ranks = results.rankList(students.getAll());
        if (ranks.isEmpty()) { System.out.println("No results yet."); return; }
        System.out.printf("%-5s %-8s %-20s %8s  %-5s %s%n", "Rank", "ID", "Name", "Percent", "Grade", "Status");
        ranks.forEach((s, r) -> System.out.printf("%-5d %-8s %-20s %7.2f%%  %-5s %s%n",
                r, s.getId(), s.getName(), s.getPercentage(), s.getResult().getGrade().getLabel(), s.getResult().getStatus()));
        DoubleSummaryStatistics st = results.stats(students.getAll());
        System.out.printf("Average: %.2f%%  Highest: %.2f%%  Lowest: %.2f%%%n", st.getAverage(), st.getMax(), st.getMin());
    }

    private static void toppers() {
        System.out.println("1. Topper(s)  2. Top 3  3. Top 10");
        int t = in.intInRange("Choose: ", 1, 3);
        List<Student> all = students.getAll();
        printList(switch (t) { case 1 -> results.toppers(all); case 2 -> results.topN(all, 3); default -> results.topN(all, 10); });
    }

    private static void sort() {
        System.out.println("1. Name A-Z  2. Name Z-A  3. Marks high-low  4. Marks low-high");
        String[] modes = {"NAME_ASC", "NAME_DESC", "MARKS_DESC", "MARKS_ASC"};
        printList(results.sort(students.getAll(), modes[in.intInRange("Choose: ", 1, 4) - 1]));
    }

    private static void delete() {
        String id = in.text("Student ID to delete: ");
        if (!in.yes("Are you sure?")) return;
        System.out.println(students.delete(id) ? "Deleted." : "Student not found.");
    }

    private static void export() {
        try {
            CsvExporter.export(results.rankList(students.getAll()), students.getAll(), "data/results.csv");
            System.out.println("Exported to data/results.csv");
        } catch (java.io.IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
}
