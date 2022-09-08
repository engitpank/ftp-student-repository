package main.ru.student_repository.json.types;

public class ElementTypes {
    public static final byte JSON_OBJECT_START = 1;
    public static final byte JSON_OBJECT_END = 2;
    public static final byte JSON_ARRAY_START = 3;
    public static final byte JSON_ARRAY_VALUE_STRING = 4;
    public static final byte JSON_ARRAY_VALUE_NUMBER = 6;
    public static final byte JSON_ARRAY_END = 9;
    public static final byte JSON_PROPERTY_NAME = 10;
    public static final byte JSON_PROPERTY_VALUE_STRING = 11;
    public static final byte JSON_PROPERTY_VALUE_NUMBER = 13;
}
