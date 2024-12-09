package com.ppfs.ppfs_libs.service.logger;

import java.util.logging.Level;

public class LogEntry {
    final String prefix;
    final Level level;
    final String message;
    final Throwable throwable;

    public LogEntry(String prefix, Level level, String message, Throwable throwable) {
        this.prefix = prefix;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
    }
}
