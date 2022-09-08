package main.ru.student_repository.ftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

public class FtpDataConnection implements AutoCloseable {
    private final Socket dataConnection;

    private final InputStream response;
    private final OutputStream request;


    public FtpDataConnection(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        this.dataConnection = serverSocket.accept();
        this.response = dataConnection.getInputStream();
        this.request = dataConnection.getOutputStream();
        serverSocket.close();
    }

    public FtpDataConnection(Socket dataConnection) throws IOException {
        Objects.requireNonNull(dataConnection);
        this.dataConnection = dataConnection;
        this.response = dataConnection.getInputStream();
        this.request = dataConnection.getOutputStream();
    }

    public FtpDataConnection(String host, int port) throws IOException {
        Objects.requireNonNull(host);
        this.dataConnection = new Socket(host, port);
        this.response = dataConnection.getInputStream();
        this.request = dataConnection.getOutputStream();
    }

    public void writeToServer(Path filePath) throws IOException {
        if (Files.exists(filePath)) {
            request.write(Files.readAllBytes(filePath));
            request.flush();
        } else {
            throw new IOException("Don't found file with filepath: " + filePath);
        }
    }

    public void writeToServer(byte[] data) throws IOException {
        request.write(data);
        request.flush();
    }

    public void writeToServer(String data) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(request, StandardCharsets.UTF_8));
        bufferedWriter.write(data);
        bufferedWriter.flush();
    }

    public String readFromServer() throws IOException {
        InputStreamReader input = new InputStreamReader(response, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(input);
        return bufferedReader.lines().collect(Collectors.joining());
    }

    @Override
    public void close() throws IOException {
        request.close();
        response.close();
        dataConnection.close();
    }
}
