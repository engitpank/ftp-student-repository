package main.ru.student_repository.utils;

import main.ru.student_repository.json.buffers.DataCharBuffer;
import main.ru.student_repository.model.Student;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.List;

public class StudentUtilTest {

    private final static String jsonString = "{\n" +
            "  \"students\": [\n" +
            "    {\n" +
            "      \"id\": 1,\n" +
            "      \"name\": \"Student1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 2,\n" +
            "      \"name\": \"Student2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 3,\n" +
            "      \"name\": \"Student3\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private final Student student1 = new Student("Student1", 1);
    private final Student student2 = new Student("Student2", 2);
    private final Student student3 = new Student("Student3", 3);

    @Test
    public void testParseJsonStudentsList() {
        List<Student> actual = StudentUtil.parseJsonStudentsList(new DataCharBuffer(jsonString.toCharArray()));
        Assert.assertEquals(actual, Lists.newArrayList(student1, student2, student3));
    }
}