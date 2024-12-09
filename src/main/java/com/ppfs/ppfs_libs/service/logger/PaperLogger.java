package com.ppfs.ppfs_libs.service.logger;


import lombok.Getter;
import lombok.Setter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PaperLogger {
    private final static LoggerService loggerService = LoggerService.getInstance();
    private final String prefix;
    @Getter
    private final Logger logger;
    @Getter
    @Setter
    private boolean writeToFile;
    @Getter
    @Setter
    private boolean writeToConsole;


    protected PaperLogger(String prefix, Logger logger, boolean writeToFile, boolean writeToConsole) {
        this.prefix = prefix;
        this.logger = logger;
        this.writeToFile = writeToFile;
        this.writeToConsole = writeToConsole;
    }

    public void info(String message) {
        loggerService.send(new LogEntry(prefix, Level.INFO, prefix + message, null));
    }

    public void info(String format, Object... args) {
        loggerService.send(new LogEntry(prefix, Level.INFO, prefix + String.format(format, args), null));
    }

    public void warning(String message) {
        loggerService.send(new LogEntry(prefix, Level.WARNING, prefix + message, null));
    }

    public void warning(String format, Object... args) {
        loggerService.send(new LogEntry(prefix, Level.WARNING, prefix + String.format(format, args), null));
    }

    public void error(String message) {
        loggerService.send(new LogEntry(prefix, Level.SEVERE, prefix + message, null));
    }

    public void error(String format, Object... args) {
        loggerService.send(new LogEntry(prefix, Level.SEVERE, prefix + String.format(format, args), null));
    }

    public void error(String message, Throwable throwable) {
        loggerService.send(new LogEntry(prefix, Level.SEVERE, prefix + message, throwable));
    }

    public void error(String format, Throwable throwable, Object... args) {
        loggerService.send(new LogEntry(prefix, Level.SEVERE, prefix + String.format(format, args), throwable));
    }

    public void debug(String message) {
        loggerService.sendDebug(new LogEntry(prefix, Level.INFO, prefix + "DEBUG: " + message, null));

    }

    public void debug(String format, Object... args) {
        loggerService.sendDebug(new LogEntry(prefix, Level.INFO, prefix + "DEBUG: " + String.format(format, args), null));

    }

    public void syncError(String message) {
        loggerService.sendSync(new LogEntry(prefix, Level.SEVERE, prefix + message, null));
    }

    public void syncError(String message, Throwable throwable) {
        loggerService.sendSync(new LogEntry(prefix, Level.SEVERE, prefix + message, throwable));
    }

}