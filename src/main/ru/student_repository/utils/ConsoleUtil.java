package main.ru.student_repository.utils;

import main.ru.student_repository.console.ConsoleOption;
import main.ru.student_repository.console.exception.IllegalConsoleArgException;

import java.util.*;

public class ConsoleUtil {
    public static final int INVALID_VALUE = -1;

    public static int tryReadInt(Scanner input) {
        try {
            return Integer.parseInt(input.nextLine());
        } catch (NumberFormatException ignored) {
            return INVALID_VALUE;
        }
    }

    public static String readCommandLine(Scanner input) {
        System.out.print("Enter command: ");
        return input.nextLine().trim().toLowerCase();
    }

    public static Map<ConsoleOption, String> getActiveOption(String[] args) {
        Map<ConsoleOption, String> activeArgs = new EnumMap<>(ConsoleOption.class);
        List<String> listArgs = Arrays.asList(args);
        for (String arg : listArgs) {
            if (arg.startsWith("-")) {
                int flagOrdinal = ConsoleOption.getArgOrdinal(arg.toLowerCase());
                if (flagOrdinal == -1) {
                    throw new IllegalConsoleArgException("Illegal console option " + flagOrdinal);
                }
                ConsoleOption option = ConsoleOption.values()[flagOrdinal];
                int argIndex = listArgs.indexOf(arg);
                if (argIndex == (listArgs.size() - 1) || listArgs.get(argIndex + 1).startsWith("-")) {
                    activeArgs.put(option, "true");
                } else if (argIndex + 1 != listArgs.size() && !listArgs.get(argIndex + 1).startsWith("-")) {
                    activeArgs.put(option, listArgs.get(argIndex + 1));
                }
            }
        }
        return activeArgs;
    }
}
