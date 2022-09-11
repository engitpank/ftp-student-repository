package main.ru.student_repository.storage;

import main.ru.student_repository.model.Student;

public class StudentTestData {
    private static final String NAME1 = "NAME1";
    private static final String NAME2 = "NAME2";
    private static final String NAME3 = "NAME3";
    private static final String NEW_USER = "NEW_USER";


    public static final int ID_NEW = 1000;
    public static final Student student1 = new Student(NAME1);
    public static final Student student2 = new Student(NAME2);
    public static final Student student3 = new Student(NAME3);
    public static final Student studentTom = new Student("Tom");
    public static final Student studentKate = new Student("Kate Flanders");
    public static final Student studentKate2 = new Student("Kate Wood");

    public static final Student studentNew = new Student(NEW_USER, ID_NEW);

}
