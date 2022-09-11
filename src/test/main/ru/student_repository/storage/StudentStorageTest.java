package main.ru.student_repository.storage;

import main.ru.student_repository.model.Student;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;


public class StudentStorageTest {
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
    private final Storage<Student> storage = new StudentStorage();

    @BeforeMethod
    public void setUp() {
        storage.clear();
        storage.create(student1);
        storage.create(student2);
        storage.create(student3);
    }

    @Test
    public void testCreate() {
        Student actual = storage.create(studentNew);

        Assert.assertEquals(actual, studentNew);
        Assert.assertEquals(4, storage.size());
        Assert.assertEquals(storage.get(ID_NEW), studentNew);
    }

    @Test
    public void testGet() {
        Student actual = storage.get(student1.getId());
        Assert.assertEquals(actual, student1);
    }

    @Test
    public void testDelete() {
        storage.delete(student1.getId());
        Assert.assertNull(storage.get(student1.getId()));
        Assert.assertEquals(storage.size(), 2);
    }

    @Test
    public void testGetAllByName() {
        storage.create(studentTom);
        storage.create(studentKate);
        storage.create(studentKate2);

        Assert.assertEquals(storage.getAllByName("Kate"), Lists.newArrayList(studentKate, studentKate2));
        Assert.assertEquals(storage.getAllByName("Tom"), Lists.newArrayList(studentTom));
    }

    @Test
    public void testGetAllSorted() {
        Assert.assertEquals(storage.getAllSorted(), Lists.newArrayList(student1, student2, student3));
    }

    @Test
    public void testClear() {
        storage.clear();
        Assert.assertEquals(storage.size(), 0);
    }

    @Test
    public void testSize() {
        Assert.assertEquals(storage.size(), 3);
    }
}
