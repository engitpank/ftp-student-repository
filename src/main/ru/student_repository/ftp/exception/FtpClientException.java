package main.ru.student_repository.ftp.exception;

public class FtpClientException extends RuntimeException {
    private final int replyCode;

    public FtpClientException(int replyCode, String message) {
        super(message);
        this.replyCode = replyCode;
    }

    public FtpClientException(String message) {
        this(-1, message);
    }

    public int getReplyCode() {
        return replyCode;
    }
}
