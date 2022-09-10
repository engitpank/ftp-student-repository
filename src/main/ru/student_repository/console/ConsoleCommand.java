package main.ru.student_repository.console;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public enum ConsoleCommand {
    INVALID_COMMAND("Invalid command. Check command list"),

    LOGIN("Enter login-data separated by a space (username password 127.0.0.1:21): "),

    GET_LIST("Get a list of students by name: "),

    GET_STUDENT("Get student information by id: "),

    ADD_STUDENT("Add student with name: "),

    DEL_STUDENT("Delete student by id: "),

    EXIT("Exit"),
    ;

    private final String desc;
    private static final String FORMAT_STRING_COMMAND = "%-18s - %-25s\r\n";

    ConsoleCommand(String desc) {
        this.desc = desc;
    }

    public static boolean isCommand(String command) {
        String com = command.toUpperCase(Locale.ROOT);
        return Arrays.stream(ConsoleCommand.values())
                .skip(1) // skip INVALID_COMMAND
                .anyMatch(c -> c.name().equals(com));
    }

    public static String getAllCommands() {
        return String.format(FORMAT_STRING_COMMAND, "[KEY_COMMAND]", "[DESCRIPTION]")
                + Arrays.stream(ConsoleCommand.values())
                .skip(2) // skip INVALID_COMMAND and LOGIN
                .map(c -> String.format(FORMAT_STRING_COMMAND, c.name(), c.desc))
                .collect(Collectors.joining());
    }

    public String getDesc() {
        return desc;
    }
}
