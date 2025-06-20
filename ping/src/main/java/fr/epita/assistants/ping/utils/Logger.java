package fr.epita.assistants.ping.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@ApplicationScoped
public class Logger {
    private static final String RESET_TEXT = "\u001B[0m";
    private static final String RED_TEXT = "\u001B[31m";
    private static final String GREEN_TEXT = "\u001B[32m";

    @ConfigProperty(name = "LOG_FILE", defaultValue = "logs.txt")
    String logPath;

    @ConfigProperty(name = "ERROR_LOG_FILE", defaultValue = "error.txt")
    String errorPath;

    private String timestamp() {
        return new SimpleDateFormat("dd/MM/yy - HH:mm:ss")
                .format(Calendar.getInstance().getTime());
    }

    public void logSuccess(String message) {
        String logMessage = GREEN_TEXT + "[" + timestamp() + "] " + message + RESET_TEXT + "\n";
        log(logPath, logMessage, false);
    }

    public void logInfo(String message) {
        String logMessage = "[" + timestamp() + "] " + message + RESET_TEXT+ "\n";
        log(logPath, logMessage, false);
    }

    public void logError(String message) {
        String logMessage = RED_TEXT + "[" + timestamp() + "] " + message + RESET_TEXT+ "\n";
        log(errorPath, logMessage, true);
    }

    private void log(String path, String message, boolean err) {
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
