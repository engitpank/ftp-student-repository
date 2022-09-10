package main.ru.student_repository.ftp;

import main.ru.student_repository.ftp.exception.FtpConnectionException;
import main.ru.student_repository.ftp.exception.InvalidReplyException;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import static main.ru.student_repository.ftp.FtpCommand.*;

public class FtpControlConnection implements AutoCloseable {
    private static final int CODE_LENGTH = 3;

    private final Socket controlConnection;
    private final InputStream response;
    private final OutputStream request;
    private final BufferedReader bufferedReader;

    private String dataForPassiveConnection;
    private final OutputStreamWriter requestWriter;

    private int replyCode;

    public FtpControlConnection(String host, int port) throws IOException {
        Objects.requireNonNull(host);
        controlConnection = new Socket(host, port);
        response = controlConnection.getInputStream();
        request = controlConnection.getOutputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(response));
        requestWriter = new OutputStreamWriter(request);
    }

    public void login(String user, String password) throws IOException, InvalidReplyException {
        if (FtpReply.SERVER_READY != getReply()) {
            throw new InvalidReplyException(replyCode, "Server doesn't available");
        }

        sendCommand(USERNAME, user);
        if (FtpReply.EXCEPT_PASSWORD != getReply()) {
            throw new InvalidReplyException(replyCode, "Invalid login");
        }

        sendCommand(PASSWORD, password);
        if (FtpReply.LOGIN_SUCCESSFUL != getReply()) {
            throw new InvalidReplyException(replyCode, "Invalid password");
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
        System.out.println(response);
        if (response == null) {
            throw new FtpConnectionException("Connection closed without reply code");
        }
        int length = response.length();
        if (length < CODE_LENGTH) {
            throw new InvalidReplyException("Unsupported server reply: ");
        }
        try {
            replyCode = Integer.parseInt(response.substring(0, CODE_LENGTH));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Could not parse response code. " + response);
        }

        if (FtpReply.CLOSING_CONTROL_CONNECTION == replyCode) {
            throw new FtpConnectionException("Broke connection. Server not available");
        }

        if (length > CODE_LENGTH && response.charAt(CODE_LENGTH) == '-') {
            do {
                response = bufferedReader.readLine();
                if (response == null) {
                    throw new FtpConnectionException("Connection closed without reply code");
                }
                length = response.length();
                System.out.println(response);
            } while (!(length > CODE_LENGTH && response.charAt(CODE_LENGTH) != '-' && Character.isDigit(response.charAt(0))));
        } else if (FtpReply.isPositivePreliminary(replyCode)) {
            getReply();
        } else if (FtpReply.ENTERING_PASSIVE_MODE == replyCode) {
            dataForPassiveConnection = response.substring(response.indexOf('(') + 1, response.indexOf(')'));
        }
        return replyCode;
    }

    public void sendCommand(FtpCommand command) throws IOException {
        requestWriter.write(String.format("%s\n", command.getCommand()));
        requestWriter.flush();
    }

    public void sendCommand(FtpCommand command, String arg) throws IOException {
        requestWriter.write(String.format("%s %s\n", command.getCommand(), arg));
        requestWriter.flush();
    }

    @Override
    public void close() throws IOException {
        request.close();
        response.close();
        controlConnection.close();
    }
}
