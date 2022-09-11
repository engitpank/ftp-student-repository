package main.ru.student_repository.ftp;

import main.ru.student_repository.ftp.exception.InvalidReplyException;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static main.ru.student_repository.ftp.FtpCommand.RETRIEVE;
import static main.ru.student_repository.ftp.FtpReply.FILE_STATUS_OK;

public class FtpClient implements AutoCloseable {

    private FtpControlConnection controlConnection;

    private static final int DEFAULT_PORT = 21;

    private boolean isPassiveMode = true;

    private final String host;
    private final int port;
    private Integer activeModePort = null;
    private final String user;
    private final String password;

    public FtpClient(String host, String user, String password) throws IOException {
        this(host, DEFAULT_PORT, user, password);
    }

    public FtpClient(String host, int port, String user, String password) throws IOException, InvalidReplyException {
        Objects.requireNonNull(host);
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        connect();
    }

    private void connect() throws IOException {
        controlConnection = new FtpControlConnection(host, port);
        controlConnection.login(user, password);
    }

    private FtpDataConnection getDataConnect() throws IOException {
        if (isPassiveMode) {
            String data = controlConnection.getDataForPassiveConnection();
            String[] ip = data.split(",");
            int port = Integer.parseInt(ip[ip.length - 2]) * 256 + Integer.parseInt(ip[ip.length - 1]);
            String host = Arrays.stream(ip).limit(4).collect(Collectors.joining("."));
            return new FtpDataConnection(host, port);
        }
        Socket socket = controlConnection.getSocketForActiveConnection(activeModePort);
        return new FtpDataConnection(socket);
    }

    public void writeString(String data, String filename) throws IOException {
        try (FtpDataConnection dataConnection = getDataConnect()) {
            controlConnection.sendCommand(FtpCommand.STORE, filename);
            dataConnection.writeToServer(data);
        }
    }

    public String readAsString(String filePath) throws IOException {
        try (FtpDataConnection dataConnection = getDataConnect()) {
            controlConnection.sendCommand(RETRIEVE, filePath);
            int code = controlConnection.getReply();
            if (FtpReply.TRANSFER_COMPLETE != code && FILE_STATUS_OK != code) {
                throw new InvalidReplyException(code, "Don't expected code: " + code);
            }
            return dataConnection.readFromServer();
        }
    }

    public void close() throws IOException {
        controlConnection.close();
    }

    public void setPassiveMode(boolean passiveMode) {
        isPassiveMode = passiveMode;
    }

    public void setActiveModePort(Integer activeModePort) {
        this.activeModePort = activeModePort;
    }
}
