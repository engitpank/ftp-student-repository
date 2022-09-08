package main.ru.student_repository.ftp;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import static main.ru.student_repository.ftp.FtpCommand.*;

public class FtpControlConnection implements AutoCloseable {
    private static final int DEFAULT_PORT = 21;
    private static final int CODE_LENGTH = 3;

    private final Socket controlConnection;
    private final InputStream response;
    private final OutputStream request;
    private final BufferedReader bufferedReader;

    private String dataForPassiveConnection;
    private final OutputStreamWriter requestWriter;

    public FtpControlConnection(String host) throws IOException {
        this(host, DEFAULT_PORT);
    }

    public FtpControlConnection(String host, int port) throws IOException {
        Objects.requireNonNull(host);
        controlConnection = new Socket(host, port);
        response = controlConnection.getInputStream();
        request = controlConnection.getOutputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(response));
        requestWriter = new OutputStreamWriter(request);

        System.out.println(controlConnection.getLocalSocketAddress());

    }

    public void login(String user, String password) throws IOException {
        if (FtpReply.SERVER_READY != getReply()) {
            throw new RuntimeException("Server doesn't available");
        }

        sendCommand(USERNAME, user);
        if (FtpReply.EXCEPT_PASSWORD != getReply()) {
            throw new RuntimeException("Invalid login");
        }

        sendCommand(PASSWORD, password);
        if (FtpReply.LOGIN_SUCCESSFUL != getReply()) {
            throw new RuntimeException("Invalid password");
        }
    }

    public String getDataForPassiveConnection() throws IOException {
        sendCommand(PASSIVE);
        if (FtpReply.ENTERING_PASSIVE_MODE != getReply()) {
            getReply();
        }
        return dataForPassiveConnection;
    }

    public Socket getSocketForActiveConnection() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            int port = serverSocket.getLocalPort();

            StringBuilder arg = new StringBuilder();
            arg.append(hostAddress.replace(".", ","));
            arg.append(",");
            int num = port >>> 8;
            arg.append(num);
            arg.append(",");
            num = port & 0xff;
            arg.append(num);
            sendCommand(DATA_PORT, arg.toString());
            return serverSocket.accept();
        }
    }


    public int getReply() throws IOException {
        String response = bufferedReader.readLine();
        if (response == null) {
            throw new RuntimeException("Connection closed without code");
        } else if (response.length() < CODE_LENGTH) {
            throw new RuntimeException("Unsupported server reply: " + response);
        }

        int replyCode;
        try {
            replyCode = Integer.parseInt(response.substring(0, CODE_LENGTH));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Could not parse response code. " + response);
        }

        if (FtpReply.isPositivePreliminary(replyCode)) {
            getReply();
        } else if (FtpReply.ENTERING_PASSIVE_MODE == replyCode) {
            dataForPassiveConnection = response.substring(response.indexOf('(') + 1, response.indexOf(')'));
        }
        return replyCode;
    }

    public void sendCommand(FtpCommand command) {
        try {
            requestWriter.write(String.format("%s\n", command.getCommand()));
            requestWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCommand(FtpCommand command, String arg) {
        try {
            requestWriter.write(String.format("%s %s\n", command.getCommand(), arg));
            requestWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        request.close();
        response.close();
        controlConnection.close();
    }
}
