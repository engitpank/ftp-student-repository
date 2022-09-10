package main.ru.student_repository.storage;

import main.ru.student_repository.model.Student;

import java.util.List;

public interface Storage<T> {
    T create(Student student);

    Student get(int studentId);

    boolean delete(int studentId);

    // ORDERED name desc
    List<Student> getAllByName(String name);

    // ORDERED name desc
    List<Student> getAllSorted();
}
