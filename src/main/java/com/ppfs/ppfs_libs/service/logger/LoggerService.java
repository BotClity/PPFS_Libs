package com.ppfs.ppfs_libs.service.logger;

import com.ppfs.ppfs_libs.PPFS_Libs;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.*;

public class LoggerService {
    private final static Plugin plugin = PPFS_Libs.getInstance();
    private final Logger centralLogger;
    private final BlockingQueue<LogEntry> logQueue;
    private final ExecutorService executor;
    private final Map<String, PaperLogger> loggerMap;
    @Getter
    @Setter
    private boolean debugMode = false;
    private volatile boolean running = true;

    private LoggerService() {
        this.centralLogger = Logger.getLogger("PPFSS");
        this.logQueue = new LinkedBlockingQueue<>(100);
        this.executor = Executors.newSingleThreadExecutor();
        this.loggerMap = new ConcurrentHashMap<>();
        startProcessingQueue();
    }

    private static final class InstanceHolder {
        private static final LoggerService instance = new LoggerService();
    }

    public static LoggerService getInstance() {
        return InstanceHolder.instance;
    }

    private void startProcessingQueue() {
        executor.submit(() -> {
            while (running) {
                try {
                    LogEntry entry = logQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (entry != null) {
                        PaperLogger logger = loggerMap.get(entry.prefix);
                        if (logger != null) {
                            logger.getLogger().log(entry.level, entry.message, entry.throwable);
                        } else {
                            centralLogger.log(entry.level, entry.message, entry.throwable);
                        }
                    }
                } catch (Exception e) {
                    centralLogger.log(Level.SEVERE, "Error in LoggerService queue processing", e);
                }
            }
        });
    }

    public synchronized PaperLogger getOrCreateLogger(String prefix, boolean writeToFile, boolean writeToConsole) {
        return loggerMap.computeIfAbsent(prefix, key -> createLogger(key, writeToFile, writeToConsole));
    }

    private PaperLogger createLogger(String prefix, boolean writeToFile, boolean writeToConsole) {
        Logger logger = Logger.getLogger(prefix);

        logger.setUseParentHandlers(false);

        try {
            File logsDir = new File(plugin.getDataFolder(), "logs");
            if (!logsDir.exists() && !logsDir.mkdirs()) {
                centralLogger.log(Level.WARNING, "Failed to create logs directory for plugin.");
            }

            if (writeToFile) {
                logger.addHandler(createFileHandler(logsDir, prefix));
            }

            if (writeToConsole) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter() {
                    @Override
                    public synchronized String format(LogRecord record) {
                        return String.format("[%s] %s%n", record.getLoggerName(), super.formatMessage(record));
                    }
                });
                logger.addHandler(consoleHandler);
            }
        } catch (IOException e) {
            centralLogger.log(Level.SEVERE, "Failed to create log file for: " + prefix, e);
        }

        return new PaperLogger(prefix, logger, writeToFile, writeToConsole);
    }




    private FileHandler createFileHandler(File logsDir, String prefix) throws IOException {
        String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new java.util.Date());

        String sanitizedPrefix = prefix.replaceAll("[^a-zA-Z0-9]", "_");

        String logFileName = String.format("%s/%s_%s.log", logsDir.getAbsolutePath(), sanitizedPrefix, dateTime);
        FileHandler fileHandler = new FileHandler(logFileName, true);
        fileHandler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return String.format("[%s] %s%n", record.getLoggerName(), super.formatMessage(record));
            }
        });

        return fileHandler;
    }



    public void send(LogEntry entry) {
        logQueue.add(entry);
    }

    public void sendDebug(LogEntry entry) {
        if (debugMode) {
            logQueue.add(entry);
        }
    }

    public void sendSync(LogEntry entry){
        centralLogger.log(entry.level, entry.message, entry.throwable);
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                centralLogger.log(Level.WARNING, "LoggerService executor did not shut down in time.");
            }
        } catch (InterruptedException e) {
            centralLogger.log(Level.SEVERE, "LoggerService shutdown interrupted.", e);
        }
        loggerMap.values().stream().map(PaperLogger::getLogger).forEach(logger -> {
            for (Handler handler : logger.getHandlers()) {
                handler.close();
            }
        });
    }
}
