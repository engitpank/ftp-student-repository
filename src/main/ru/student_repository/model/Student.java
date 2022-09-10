package main.ru.student_repository.model;

import java.util.Objects;

public class Student {
    private final String name;
    private Integer id;

    public Student(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public Student(String name, int id) {
        Objects.requireNonNull(name);
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (!name.equals(student.name)) return false;
        return id.equals(student.id);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
