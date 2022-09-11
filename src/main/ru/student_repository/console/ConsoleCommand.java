package main.ru.student_repository.console;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ConsoleCommand {
    INVALID_COMMAND("Check command list", "help"),

    LOGIN("Enter login-data separated by a space (username password 127.0.0.1:21): ", "login"),

    GET_LIST("Get a list of students by name: ", "get-list"),

    GET_STUDENT("Get student information by id: ", "get"),

    ADD_STUDENT("Add student with name: ", "add"),

    DEL_STUDENT("Delete student by id: ", "del"),

    EXIT("Exit", "exit"),
    ;

    private final String desc;
    private final String command;
    private static final String FORMAT_STRING_COMMAND = "%-18s - %-25s\r\n";

    ConsoleCommand(String desc, String command) {
        this.desc = desc;
        this.command = command;
    }

    public static ConsoleCommand getConsoleCommand(String com) {
        return Arrays.stream(ConsoleCommand.values())
                .filter(c -> c.command.equals(com))
                .findFirst()
                .orElse(INVALID_COMMAND);
    }

    public static String getAllCommands() {
        return String.format(FORMAT_STRING_COMMAND, "[COMMAND]", "[DESCRIPTION]")
                + Arrays.stream(ConsoleCommand.values())
                .skip(2) // skip INVALID_COMMAND and LOGIN
                .map(c -> String.format(FORMAT_STRING_COMMAND, c.command, c.desc))
                .collect(Collectors.joining());
    }

    public String getDesc() {
        return desc;
    }

    public String getCommand() {
        return command;
    }
}
