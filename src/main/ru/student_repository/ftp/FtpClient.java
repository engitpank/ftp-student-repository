package main.ru.student_repository.ftp;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static main.ru.student_repository.ftp.FtpCommand.RETRIEVE;

public class FtpClient implements AutoCloseable {

    private FtpControlConnection controlConnection;

    private static final int DEFAULT_PORT = 21;

    private boolean enablePassiveMode = true;

    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public FtpClient(String host, String user, String password) {
        this(host, DEFAULT_PORT, user, password);
    }

    public FtpClient(String host, int port, String user, String password) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void connect() {
        try {
            controlConnection = new FtpControlConnection(host, port);
            controlConnection.login(user, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FtpDataConnection getDataConnect() throws IOException {
        if (enablePassiveMode) {
            String data = controlConnection.getDataForPassiveConnection();
            String[] ip = data.split(",");
            int port = Integer.parseInt(ip[ip.length - 2]) * 256 + Integer.parseInt(ip[ip.length - 1]);
            String host = Arrays.stream(ip).limit(4).collect(Collectors.joining("."));
            return new FtpDataConnection(host, port);
        }
        Socket socket = controlConnection.getSocketForActiveConnection();
        return new FtpDataConnection(socket);
    }

    public void writeString(String data, String filename) throws IOException {
        try (FtpDataConnection dataConnection = getDataConnect()) {
            controlConnection.sendCommand(FtpCommand.STORE, filename);
            dataConnection.writeToServer(data);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void writeBytes(byte[] data, String filename) throws IOException {
        try (FtpDataConnection dataConnection = getDataConnect()) {
            controlConnection.sendCommand(FtpCommand.STORE, filename);
            dataConnection.writeToServer(data);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void writeFile(Path filePath) throws IOException {
        try (FtpDataConnection dataConnection = getDataConnect()) {
            controlConnection.sendCommand(FtpCommand.STORE, filePath.toString());
            dataConnection.writeToServer(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public String readAsString(Path filePath) throws IOException {
        try (FtpDataConnection dataConnection = getDataConnect()) {
            controlConnection.sendCommand(RETRIEVE, filePath.toString());
            int code = controlConnection.getReply();
            if (FtpReply.TRANSFER_COMPLETE != code) {
                throw new RuntimeException("Don't excepted reply: " + code);
            }
            return dataConnection.readFromServer();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void close() {
        try {
            controlConnection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
