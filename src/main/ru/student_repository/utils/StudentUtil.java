package main.ru.student_repository.utils;

import main.ru.student_repository.json.JsonNavigator;
import main.ru.student_repository.json.JsonParser;
import main.ru.student_repository.json.buffers.DataCharBuffer;
import main.ru.student_repository.model.Student;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StudentUtil {
    public static String ListToJsonString(Collection<Student> Students) {
        return String.format("{\"students\":[%s]}",
                Students.stream()
                        .map(StudentUtil::toJsonString)
                        .collect(Collectors.joining(",")));
    }

    public static String toJsonString(Student student) {
        return String.format("{\"id\":%d,\"name\":\"%s\"}", student.getId(), student.getName());
    }

    public static String formatToOutputString(List<Student> list) {
        StringBuilder result = new StringBuilder(list.size());
        for (Student student : list) {
            result.append(String.format("%-3d - %20s\r\n", student.getId(), student.getName()));
        }
        return result.toString();
    }

    public static List<Student> parseJsonStudentsList(DataCharBuffer dataBuffer) {
        JsonParser jsonParser = new JsonParser(dataBuffer);
        jsonParser.parseObject();
        JsonNavigator<Student> root = new JsonNavigator<>(dataBuffer, jsonParser.getElementBuffer());
        root.next(); // skip over object begin
        root.next(); // skip property-name
        return root.parseListObject(() -> {
            root.next(); // property name
            int id = root.asInt();
            root.next(); // id skip
            root.next(); // property name
            String name = root.asString();
            root.next(); // name skip
            return new Student(name, id);
        });
    }
}
