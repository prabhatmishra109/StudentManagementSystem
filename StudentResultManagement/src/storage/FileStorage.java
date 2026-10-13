package storage;

import model.Student;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/** Persists students using Java serialization (no database). */
public class FileStorage {
    private final Path file;

    public FileStorage(String path) { this.file = Paths.get(path); }

    @SuppressWarnings("unchecked")
    public List<Student> load() {
        if (!Files.exists(file)) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) return new ArrayList<>((List<Student>) obj);
            System.out.println("Warning: data file format invalid, starting empty.");
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("Warning: could not load data (" + e.getMessage() + "). Starting empty.");
        }
        return new ArrayList<>();
    }

    public void save(List<Student> students) throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(tmp)))) {
            out.writeObject(new ArrayList<>(students));
        }
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
    }
}
