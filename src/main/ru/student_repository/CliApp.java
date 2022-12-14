package main.ru.student_repository;

import main.ru.student_repository.console.ConsoleCommand;
import main.ru.student_repository.console.ConsoleOption;
import main.ru.student_repository.ftp.FtpClient;
import main.ru.student_repository.ftp.exception.InvalidReplyException;
import main.ru.student_repository.json.buffers.DataCharBuffer;
import main.ru.student_repository.model.Student;
import main.ru.student_repository.storage.Storage;
import main.ru.student_repository.storage.StudentStorage;
import main.ru.student_repository.utils.StudentUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static main.ru.student_repository.console.ConsoleCommand.*;
import static main.ru.student_repository.utils.ConsoleUtil.*;
import static main.ru.student_repository.utils.FtpUtil.getConnection;
import static main.ru.student_repository.utils.StudentUtil.formatToOutputString;
import static main.ru.student_repository.utils.StudentUtil.parseJsonStudentsList;

public class CliApp {
    private static final String DEFAULT_PATH = "index.json";
    private static String filePath;
    private static boolean passiveDataMode = true;
    private static Storage<Student> storage;
    private static String username;
    private static String password;
    private static String host;
    private static Integer activeModePort;

    public static void main(String[] args) {
        Scanner consoleInput = new Scanner(System.in);
        init(args);
        while (!haveLoginData()) {
            System.out.print(LOGIN.getDesc());
            String userInput = consoleInput.nextLine();
            if (userInput.trim().toUpperCase().equals(EXIT.name())) {
                System.exit(0);
            }
            String[] loginData = userInput.split(" ");
            if (loginData.length != 3) {
                System.out.println("Incorrect login-data. Please try again");
            } else {
                username = loginData[0];
                password = loginData[1];
                host = loginData[2];
                storage = getRemoteStorage();
                if (storage == null) {
                    resetLoginData();
                }
            }
        }
        System.out.println(ConsoleCommand.getAllCommands());

        String line = readCommandLine(consoleInput);
        while (!Objects.equals(line, EXIT.getCommand())) {

            ConsoleCommand command = ConsoleCommand.getConsoleCommand(line);

            if (INVALID_COMMAND == command) {
                System.out.println(INVALID_COMMAND.getDesc());
                System.out.println(ConsoleCommand.getAllCommands());
            } else if (haveLoginData()) {
                switch (command) {
                    case GET_LIST: {
                        System.out.print(GET_LIST.getDesc());
                        String name = consoleInput.nextLine();
                        System.out.println(formatToOutputString(storage.getAllByName(name)));
                        break;
                    }
                    case ADD_STUDENT: {
                        System.out.print(ADD_STUDENT.getDesc());
                        String name = consoleInput.nextLine();
                        Student student = storage.create(new Student(name));
                        System.out.println("Student #" + student.getId() + ": " + student.getName() + " saved success");
                        sendToRemoteStorage();
                        break;
                    }
                    case DEL_STUDENT: {
                        System.out.print(DEL_STUDENT.getDesc());
                        int id = tryReadInt(consoleInput);
                        if (id != INVALID_VALUE && storage.delete(id)) {
                            System.out.println("Delete success");
                        } else {
                            System.out.println("Incorrect id. Please try again");
                        }
                        sendToRemoteStorage();
                        break;
                    }
                    case GET_STUDENT: {
                        System.out.print(GET_STUDENT.getDesc());
                        int id = tryReadInt(consoleInput);
                        Student student = storage.get(id);
                        if (id == INVALID_VALUE) {
                            System.out.println("Incorrect id. Please try again");
                        } else if (student == null) {
                            System.out.println("There's no student with ID: " + id);
                        } else {
                            System.out.println("Student info: id " + id + " " + student.getName());
                        }
                        break;
                    }
                    case LOGIN: {
                        System.out.println("You're already logged in");
                        break;
                    }
                    default:
                        System.out.println("Unsupported command. Write /help ");
                        break;
                }
            }
            line = readCommandLine(consoleInput);
        }
        System.out.println(EXIT.getDesc());
        System.exit(0);
    }

    private static void init(String[] args) {
        Map<ConsoleOption, String> activeOption = getActiveOption(args);
        for (Map.Entry<ConsoleOption, String> option : activeOption.entrySet()) {
            switch (option.getKey()) {
                case USERNAME:
                    username = option.getValue();
                    break;
                case PASSWORD:
                    password = option.getValue();
                    break;
                case HOST:
                    host = option.getValue();
                    break;
                case FILEPATH:
                    filePath = option.getValue();
                    break;
                case ACTIVE_DATA_MODE:
                    passiveDataMode = false;
                    activeModePort = option.getValue() != null ? Integer.valueOf(option.getValue()) : null;
                    break;
            }
        }
        if (haveLoginData()) {
            storage = getRemoteStorage();
            if (storage == null) {
                System.exit(1);
            }
        }
    }

    private static void sendToRemoteStorage() {
        try (FtpClient client = getConnection(host, username, password, passiveDataMode, activeModePort)) {

            client.writeString(StudentUtil.ListToJsonString(storage.getAllSorted()), filePath);
        } catch (IOException | IllegalArgumentException | InvalidReplyException e) {
            System.out.println("Failed to send to the server. Please try login again");
        }
    }

    private static Storage<Student> getRemoteStorage() {
        DataCharBuffer dataBuffer;
        try (FtpClient client = getConnection(host, username, password, passiveDataMode, activeModePort)) {

            dataBuffer = new DataCharBuffer(client.readAsString(DEFAULT_PATH).toCharArray());
        } catch (IOException | IllegalArgumentException | InvalidReplyException e) {
            System.out.println("Failed to download from the server: " + e.getMessage());
            return null;
        }
        List<Student> students = parseJsonStudentsList(dataBuffer);
        Storage<Student> storage = new StudentStorage();
        students.forEach(storage::create);
        return storage;
    }

    private static boolean haveLoginData() {
        return username != null && password != null && host != null;
    }

    private static void resetLoginData() {
        username = null;
        password = null;
        host = null;
    }
}
