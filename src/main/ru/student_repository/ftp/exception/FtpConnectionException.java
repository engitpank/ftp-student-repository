package main.ru.student_repository.ftp.exception;

import main.ru.student_repository.ftp.FtpClient;

public class FtpConnectionException extends FtpClientException {
    public FtpConnectionException(String message) {
        super(message);
    }
}
