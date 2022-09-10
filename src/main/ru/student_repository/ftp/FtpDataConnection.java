package main.ru.student_repository.ftp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class FtpDataConnection implements AutoCloseable {
    private final Socket dataConnection;

    private final InputStream response;
    private final OutputStream request;

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

    public void writeToServer(String data) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(request, StandardCharsets.UTF_8));
        bufferedWriter.write(data);
        bufferedWriter.flush();
    }

    public String readFromServer() {
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
