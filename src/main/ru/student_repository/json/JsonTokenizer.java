package main.ru.student_repository.json;

import main.ru.student_repository.json.buffers.DataCharBuffer;
import main.ru.student_repository.json.buffers.IndexBuffer;

import java.util.Objects;

import static main.ru.student_repository.json.types.TokenTypes.*;

public class JsonTokenizer {
    private int dataPosition = 0;
    private int tokenLength = 0;
    private int tokenIndex = 0;

    private final IndexBuffer tokenBuffer;
    private final DataCharBuffer dataBuffer;

    public JsonTokenizer(DataCharBuffer dataBuffer) {
        Objects.requireNonNull(dataBuffer);
        this.dataBuffer = dataBuffer;
        tokenBuffer = new IndexBuffer(dataBuffer.getLength());
    }

    public void parseToken() {
        skipWhiteSpace();

        tokenBuffer.setPosition(tokenIndex, dataPosition);
        char nextChar = dataBuffer.getChar(dataPosition);

        byte tokenType;
        switch (nextChar) {
            case '{':
                tokenLength = 1;
                tokenType = JSON_CURLY_BRACKET_LEFT;
                break;
            case '}':
                tokenLength = 1;
                tokenType = JSON_CURLY_BRACKET_RIGHT;
                break;
            case '[':
                tokenLength = 1;
                tokenType = JSON_SQUARE_BRACKET_LEFT;
                break;
            case ']':
                tokenLength = 1;
                tokenType = JSON_SQUARE_BRACKET_RIGHT;
                break;
            case ':':
                tokenLength = 1;
                tokenType = JSON_COLON;
                break;
            case ',':
                tokenLength = 1;
                tokenType = JSON_COMMA;
                break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
                tokenLength = parseNumberToken();
                tokenType = JSON_NUMBER_TOKEN;
                break;
            case '"':
                tokenLength = parseStringToken();
                tokenType = JSON_STRING_TOKEN;
                break;
            default:
                throw new RuntimeException("Not defined token type");
        }
        tokenBuffer.setType(tokenIndex, tokenType);
        tokenBuffer.setLength(tokenIndex, tokenLength);
    }

    private void skipWhiteSpace() {
        boolean isWhiteSpace = true;
        while (isWhiteSpace) {
            switch (dataBuffer.getChar(dataPosition)) {
                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    dataPosition++;
                    break;
                default:
                    isWhiteSpace = false;
            }
        }
    }

    private int parseNumberToken() {
        int lengthParsedToken = 1;
        boolean isEndOfNumber = false;
        while (!isEndOfNumber) {
            switch (dataBuffer.getChar(dataPosition + lengthParsedToken)) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                case '.':
                    lengthParsedToken++;
                    break;
                default:
                    isEndOfNumber = true;
            }
        }
        return lengthParsedToken;
    }

    private int parseStringToken() {
        int tempPos = dataPosition;
        boolean endOfString = false;
        while (!endOfString) {
            tempPos++;
            if (dataBuffer.getChar(tempPos) == '"') {
                endOfString = dataBuffer.getChar(tempPos - 1) != '\\';
            }
        }
        tokenBuffer.setPosition(tokenIndex, dataPosition + 1);
        return tempPos - dataPosition - 1; // excluded last "
    }

    public void nextToken() {
        if (tokenBuffer.getTypeByIndex(tokenIndex) == JSON_STRING_TOKEN) {
            dataPosition += tokenLength + 2; //
        } else {
            dataPosition += tokenLength;
        }
        tokenIndex++;
    }

    public boolean hasNextToken() {
        return (dataPosition + tokenLength) < dataBuffer.getLength();
    }

    public int getTokenPosition() {
        return tokenBuffer.getPositionByIndex(tokenIndex);
    }

    public int getTokenLength() {
        return tokenBuffer.getLengthByIndex(tokenIndex);
    }

    public byte getTokenType() {
        return tokenBuffer.getTypeByIndex(tokenIndex);
    }
}
