package main.ru.student_repository.json;

import main.ru.student_repository.json.buffers.DataCharBuffer;
import main.ru.student_repository.json.buffers.IndexBuffer;

import java.util.ArrayList;
import java.util.List;

import static main.ru.student_repository.json.types.ElementTypes.*;

public class JsonNavigator<T> {
    private final DataCharBuffer dataBuffer;
    private final IndexBuffer elementBuffer;
    private int elementIndex = 0;

    public JsonNavigator(DataCharBuffer dataBuffer, IndexBuffer elementBuffer) {
        this.dataBuffer = dataBuffer;
        this.elementBuffer = elementBuffer;
    }

    public void next() {
        this.elementIndex++;
    }

    public List<T> parseListObject(StrategyParseObject<T> approach) {
        byte type = elementBuffer.getTypeByIndex(elementIndex);
        if (type == JSON_ARRAY_START) {
            next(); // skip over array start
            List<T> resultList = new ArrayList<>();
            while (elementBuffer.getTypeByIndex(elementIndex) != JSON_ARRAY_END) {
                next(); // skip over object start
                resultList.add(approach.parse());
                next(); // skip over object end
            }
            next(); // skip array start
            return resultList;
        }
        throw new RuntimeException("Incorrect format json-array");
    }

    public String asString() {
        byte stringType = elementBuffer.getTypeByIndex(elementIndex);
        switch (stringType) {
            case JSON_PROPERTY_NAME:
            case JSON_PROPERTY_VALUE_STRING:
            case JSON_ARRAY_VALUE_STRING:
                return new String(dataBuffer.getData(), elementBuffer.getPositionByIndex(elementIndex), elementBuffer.getLengthByIndex(elementIndex));
            default:
                throw new RuntimeException("Don't expected type: " + stringType);
        }
    }

    public int asInt() {
        int value = 0;
        int temPos = elementBuffer.getPositionByIndex(elementIndex);
        int elementLength = elementBuffer.getLengthByIndex(elementIndex);
        for (int i = 0; i < elementLength; i++) {
            value *= 10;
            value += dataBuffer.getChar(temPos) - 48;
            temPos++;
        }
        return value;
    }
}
