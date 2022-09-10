package main.ru.student_repository.console;

import java.util.Arrays;
import java.util.Objects;

public enum ConsoleOption {
    USERNAME("-user"),
    PASSWORD("-pass"),
    HOST("-host"),
    ACTIVE_DATA_MODE("-adm"),
    FILEPATH("-f"),
    ;

    private final String command;

    ConsoleOption(String command) {
        this.command = command;
    }

    public static int getArgOrdinal(String option) {
        return Arrays.stream(ConsoleOption.values())
                .filter(a -> Objects.equals(a.command, option))
                .map(Enum::ordinal)
                .findFirst()
                .orElse(-1);
    }
}
