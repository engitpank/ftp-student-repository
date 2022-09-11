package main.ru.student_repository.utils;

import main.ru.student_repository.ftp.FtpClient;
import main.ru.student_repository.ftp.exception.InvalidReplyException;

import java.io.IOException;

public class FtpUtil {

    public static FtpClient getConnection(String host, String username, String password, Boolean isPassiveMode, Integer activeModePort) throws IOException, IllegalArgumentException, InvalidReplyException {
        FtpClient client;
        if (isValidHost(host) && isValidUsername(username) && isValidPassword(password)) {
            if (containsValidPort(host)) {
                String ip = host.split(":")[0];
                int port = Integer.parseInt(host.split(":")[1]);
                client = new FtpClient(ip, port, username, password);
            } else {
                client = new FtpClient(host, username, password);
            }
            client.setPassiveMode(isPassiveMode);
            client.setActiveModePort(activeModePort);
            return client;
        }

        throw new IllegalArgumentException("Illegal argument for FtpClient: " + host + ", " + username + ", " + password);
    }

    public static boolean isValidHost(String host) {
        String[] ip;
        String port = null;
        if (host.split(":").length == 1) { // for value 121.0.0.1
            ip = host.split("\\.");
        } else if (host.split(":").length == 2) { // for value 121.0.0.1:21
            port = host.split(":")[1];
            ip = host.split(":")[0].split("\\.");
        } else {
            return false;
        }
        boolean result;
        try {
            if (ip.length == 4) {
                result = 255 >= Integer.parseInt(ip[0]) && Integer.parseInt(ip[0]) >= 0
                        && 255 >= Integer.parseInt(ip[1]) && Integer.parseInt(ip[1]) >= 0
                        && 255 >= Integer.parseInt(ip[2]) && Integer.parseInt(ip[2]) >= 0
                        && 255 >= Integer.parseInt(ip[3]) && Integer.parseInt(ip[3]) >= 0;
                if (port != null) {
                    return result && Integer.parseInt(port) > 0 && Integer.parseInt(port) <= 65536;
                }
                return result;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    public static boolean containsValidPort(String host) {
        if (host.split(":").length == 2) { // for value 121.0.0.1:21
            String port = host.split(":")[1];
            try {
                return Integer.parseInt(port) > 0 && Integer.parseInt(port) <= 65536;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() > 0;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() > 0;
    }
}
