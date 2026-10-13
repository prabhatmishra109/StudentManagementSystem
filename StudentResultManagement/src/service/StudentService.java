package service;

import model.Student;
import model.Subject;
import storage.FileStorage;

import java.io.IOException;
import java.util.*;

/** CRUD operations. HashMap gives O(1) lookup by ID. */
public class StudentService {
    private final List<Student> students;
    private final Map<String, Student> byId = new HashMap<>();
    private final FileStorage storage;

    public StudentService(FileStorage storage) {
        this.storage = storage;
        this.students = storage.load();
        for (Student s : students) byId.put(key(s.getId()), s);
    }

    private static String key(String id) { return id.trim().toLowerCase(); }

    public List<Student> getAll() { return Collections.unmodifiableList(students); }

    public void add(Student s) {
        if (byId.containsKey(key(s.getId()))) throw new IllegalArgumentException("Student ID already exists: " + s.getId());
        if (findByRoll(s.getRollNumber()).isPresent())
            throw new IllegalArgumentException("Roll number already exists: " + s.getRollNumber());
        students.add(s); byId.put(key(s.getId()), s); persist();
    }

    public Optional<Student> findById(String id) { return Optional.ofNullable(byId.get(key(id))); }

    public Optional<Student> findByRoll(String roll) {
        return students.stream().filter(s -> s.getRollNumber().equalsIgnoreCase(roll.trim())).findFirst();
    }

    public List<Student> searchByName(String part) {
        String p = part.trim().toLowerCase();
        return students.stream().filter(s -> s.getName().toLowerCase().contains(p)).toList();
    }

    public void update(String id, String name, String roll, String course, Integer semester) {
        Student s = findById(id).orElseThrow(() -> new NoSuchElementException("Student not found: " + id));
        if (roll != null && !roll.isBlank()) {
            findByRoll(roll).filter(o -> o != s).ifPresent(o -> {
                throw new IllegalArgumentException("Roll number already used by " + o.getId());
            });
            s.setRollNumber(roll);
        }
        if (name != null && !name.isBlank()) s.setName(name);
        if (course != null && !course.isBlank()) s.setCourse(course);
        if (semester != null) s.setSemester(semester);
        persist();
    }

    public void setMarks(String id, Subject subject) {
        Student s = findById(id).orElseThrow(() -> new NoSuchElementException("Student not found: " + id));
        s.addOrUpdateSubject(subject); persist();
    }

    public boolean delete(String id) {
        Student s = byId.remove(key(id));
        if (s == null) return false;
        students.remove(s); persist(); return true;
    }

    private void persist() {
        try { storage.save(students); }
        catch (IOException e) { System.out.println("Error saving data: " + e.getMessage()); }
    }
}
