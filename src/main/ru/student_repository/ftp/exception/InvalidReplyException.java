package main.ru.student_repository.ftp.exception;

public class InvalidReplyException extends FtpClientException {
    public InvalidReplyException(int replyCode, String message) {
        super(replyCode, message);
    }

    public InvalidReplyException(String message) {
        super(message);
    }
}
