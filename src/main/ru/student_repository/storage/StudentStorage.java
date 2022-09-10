package main.ru.student_repository.storage;

import main.ru.student_repository.model.Student;

import java.util.*;
import java.util.stream.Collectors;

public class StudentStorage implements Storage<Student> {

    private int counter = 0;
    private final Map<Integer, Student> map = new HashMap<>();

    @Override
    public Student create(Student student) {
        Objects.requireNonNull(student, "Student must not be null");
        if (student.isNew()) {
            student.setId(++counter);
        } else if (student.getId() > counter) {
            counter = student.getId(); // move the counter to the largest index
        }
        map.put(student.getId(), student);
        return student;
    }

    @Override
    public Student get(int studentId) {
        return map.get(studentId);
    }

    @Override
    public boolean delete(int studentId) {
        return map.remove(studentId) != null;
    }

    @Override
    public List<Student> getAllByName(String name) {
        return new ArrayList<>(map.values()).stream()
                .filter(s -> s.getName().toUpperCase(Locale.ROOT).contains(name.toUpperCase(Locale.ROOT)))
                .sorted(Comparator.comparing(Student::getName).thenComparing(Student::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> getAllSorted() {
        List<Student> sortedList = new ArrayList<>(map.values());
        sortedList.sort(Comparator.comparing(Student::getName).thenComparing(Student::getId));
        return sortedList;
    }
}
