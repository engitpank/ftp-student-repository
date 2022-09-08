package main.ru.student_repository.json;

import main.ru.student_repository.json.buffers.DataCharBuffer;
import main.ru.student_repository.json.buffers.IndexBuffer;

import static main.ru.student_repository.json.types.TokenTypes.*;
import static main.ru.student_repository.json.types.ElementTypes.*;

public class JsonParser {
    private int elementIndex = 0;
    private final JsonTokenizer tokenizer;
    private final IndexBuffer elementBuffer;

    public JsonParser(DataCharBuffer dataCharBuffer) {
        this.tokenizer = new JsonTokenizer(dataCharBuffer);
        this.elementBuffer = new IndexBuffer(dataCharBuffer.getLength());
    }

    public void parseObject() {
        if (!tokenizer.hasNextToken()) {
            throw new RuntimeException("The tokenizer hasn't token");
        }

        tokenizer.parseToken();
        assertThisTokenType(tokenizer.getTokenType(), JSON_CURLY_BRACKET_LEFT);
        setElementData(tokenizer, JSON_OBJECT_START);

        tokenizer.nextToken();
        tokenizer.parseToken();
        byte tokenType = tokenizer.getTokenType();
        while (tokenType != JSON_CURLY_BRACKET_RIGHT) {
            assertThisTokenType(tokenType, JSON_STRING_TOKEN);
            setElementData(tokenizer, JSON_PROPERTY_NAME);

            tokenizer.nextToken();
            tokenizer.parseToken();
            tokenType = tokenizer.getTokenType();
            assertThisTokenType(tokenType, JSON_COLON);

            tokenizer.nextToken();
            tokenizer.parseToken();
            tokenType = tokenizer.getTokenType();

            switch (tokenType) {
                case JSON_STRING_TOKEN:
                    setElementData(tokenizer, JSON_PROPERTY_VALUE_STRING);
                    break;
                case JSON_NUMBER_TOKEN:
                    setElementData(tokenizer, JSON_PROPERTY_VALUE_NUMBER);
                    break;
                case JSON_CURLY_BRACKET_LEFT:
                    parseObject();
                    break;
                case JSON_SQUARE_BRACKET_LEFT:
                    parseArray();
                    break;
            }
            tokenizer.nextToken();
            tokenizer.parseToken();
            tokenType = tokenizer.getTokenType();
            if (tokenType == JSON_COMMA) {
                tokenizer.nextToken();
                tokenizer.parseToken();
                tokenType = tokenizer.getTokenType();
            }
        }
        setElementData(tokenizer, JSON_OBJECT_END);
    }

    private void parseArray() {
        setElementData(tokenizer, JSON_ARRAY_START);

        tokenizer.nextToken();
        tokenizer.parseToken();
        while (tokenizer.getTokenType() != JSON_SQUARE_BRACKET_RIGHT) {

            byte tokenType = tokenizer.getTokenType();
            switch (tokenType) {
                case JSON_CURLY_BRACKET_LEFT:
                    parseObject();
                    break;
                case JSON_STRING_TOKEN:
                    setElementData(tokenizer, JSON_ARRAY_VALUE_STRING);
                    break;
                case JSON_NUMBER_TOKEN:
                    setElementData(tokenizer, JSON_ARRAY_VALUE_NUMBER);
            }

            tokenizer.nextToken();
            tokenizer.parseToken();
            tokenType = tokenizer.getTokenType();
            if (tokenType == JSON_COMMA) {
                tokenizer.nextToken();
                tokenizer.parseToken();
            }
        }
        setElementData(tokenizer, JSON_ARRAY_END);
    }

    private void setElementData(JsonTokenizer tokenizer, byte type) {
        elementBuffer.setPosition(elementIndex, tokenizer.getTokenPosition());
        elementBuffer.setLength(elementIndex, tokenizer.getTokenLength());
        elementBuffer.setType(elementIndex, type);
        elementIndex++;
    }

    private void assertThisTokenType(byte actualTokenType, byte expectedTokenType) {
        if (actualTokenType != expectedTokenType) {
            throw new RuntimeException("Token type mismatch: Expected " + expectedTokenType + " but found " + actualTokenType);
        }
    }

    public IndexBuffer getElementBuffer() {
        return elementBuffer;
    }
}
