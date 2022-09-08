package main.ru.student_repository.ftp;

public class FtpReply {

    public static final int SERVER_READY = 220;

    public static final int LOGIN_SUCCESSFUL = 230;
    public static final int EXCEPT_PASSWORD = 331;
    public static final int NOT_LOGGED_IN = 530;

    public static final int CLOSING_CONTROL_CONNECTION = 421;
    public static final int SERVICE_NOT_AVAILABLE = 221;

    public static final int OPEN_DATA_CONNECTION = 225;
    public static final int ENTERING_PASSIVE_MODE = 227;
    public static final int CANNOT_OPENED_DATA_CONNECTION = 425;

    public static final int TRANSFER_STARTING = 125;
    public static final int TRANSFER_COMPLETE = 226;

    public static final int FILE_STATUS_OK = 150;
    public static final int FILE_UNAVAILABLE = 450;
    public static final int FILE_NOT_FOUND = 550;

    public static boolean isPositivePreliminary(int codeReply) {
        return codeReply >= 100 && codeReply < 200;
    }

    public static boolean isPositiveCompletion(int codeReply) {
        return codeReply >= 200 && codeReply < 300;
    }

    public static boolean isIntermediateReply(int codeReply) {
        return codeReply >= 300 && codeReply < 400;
    }

    public static boolean isTransientNegativeCompletion(int codeReply) {
        return codeReply >= 400 && codeReply < 500;
    }

    public static boolean isPermanentNegativeCompletion(int codeReply) {
        return codeReply >= 500 && codeReply < 600;
    }
}
