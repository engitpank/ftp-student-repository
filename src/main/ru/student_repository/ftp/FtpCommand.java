package main.ru.student_repository.ftp;

public enum FtpCommand {
    PASS,
    PASV,
    PORT,
    PWD,
    QUIT,
    RETR,
    STOR,
    USER;

    public static final FtpCommand DATA_PORT = PORT;
    public static final FtpCommand PASSIVE = PASV;
    public static final FtpCommand PASSWORD = PASS;
    public static final FtpCommand RETRIEVE = RETR;
    public static final FtpCommand STORE = STOR;
    public static final FtpCommand USERNAME = USER;

    public final String getCommand() {
        return this.name();
    }
}
