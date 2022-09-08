package main.ru.student_repository.json.buffers;

public class IndexBuffer {
    private final int[] position;
    private final int[] length;
    private final byte[] type;
    int size;

    public IndexBuffer(int capacity) {
        this.length = new int[capacity];
        this.position = new int[capacity];
        this.type = new byte[capacity];
        this.size = capacity;
    }

    public void setPosition(int index, int position) {
        this.position[index] = position;
    }

    public void setType(int index, byte type) {
        this.type[index] = type;
    }

    public void setLength(int index, int length) {
        this.length[index] = length;
    }

    public int getPositionByIndex(int index) {
        return position[index];
    }

    public int getLengthByIndex(int index) {
        return length[index];
    }

    public byte getTypeByIndex(int index) {
        return type[index];
    }

    public int getSize() {
        return size;
    }
}
