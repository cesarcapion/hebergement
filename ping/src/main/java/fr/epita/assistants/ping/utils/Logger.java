package fr.epita.assistants.ping.utils;

import jakarta.resource.spi.ConfigProperty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Logger {
    private static final String RESET_TEXT = "\u001B[0m";
    private static final String RED_TEXT = "\u001B[31m";
    private static final String GREEN_TEXT = "\u001B[32m";
    static String logPath = System.getenv().getOrDefault("LOG_FILE", "resources/logs/logs.txt");
    static String errorPath = System.getenv().getOrDefault("ERROR_LOG_FILE", "resources/logs/error.txt");


    private static String timestamp() {
        return new SimpleDateFormat("dd/MM/yy - HH:mm:ss")
                .format(Calendar.getInstance().getTime());
    }
    public static void logSuccess(String message) {
        String logMessage = GREEN_TEXT + "[" + timestamp() + "]" + " " + message + RESET_TEXT;
        log(logPath, logMessage, false);
    }
    public static void logInfo(String message) {
        String logMessage = "[" + timestamp() + "]" + " " + message + RESET_TEXT;
        log(logPath, logMessage, false);
    }
    public static void logError(String message) {
        String logMessage = RED_TEXT + "[" + timestamp() + "]" + " " + message + RESET_TEXT;
        log(System.getenv(errorPath), logMessage, true);
    }

    private static void log(String path, String message, boolean err) {
        try {
            if (path != null) {
                Files.writeString(
                        Paths.get(path),
                        message + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND
                );
            } else {
                if (err)
                    System.err.println(message);
                else
                    System.out.println(message);
            }
        } catch (IOException e) {
            if (err)
                System.err.println(message);
            else
                System.out.println(message);
        }
    }

}
