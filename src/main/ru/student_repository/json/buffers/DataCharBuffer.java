package main.ru.student_repository.json.buffers;

import java.util.Objects;

public class DataCharBuffer {
    private final char[] data;
    private final int length;

    public DataCharBuffer(char[] data) {
        Objects.requireNonNull(data);
        this.data = data;
        this.length = data.length;
    }

    public char getChar(int index) {
        return data[index];
    }

    public int getLength() {
        return length;
    }

    public char[] getData() {
        return data;
    }
}
