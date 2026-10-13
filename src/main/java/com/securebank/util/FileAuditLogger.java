package com.securebank.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Module IV: File I/O using Character Streams and File class.
 * 
 * Appends audit entries of all critical bank operations (login, deposit, withdraw,
 * transfer, account activate/deactivate) to a persistent text log file.
 */
public class FileAuditLogger {

    private static final String LOG_DIR = "bank_logs";
    private static final String LOG_FILE = "bank_logs" + File.separator + "bank_audit.log";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        // Module IV: File class usage for directory and file management
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(LOG_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("[FileAuditLogger] Unable to initialize log file: " + e.getMessage());
        }
    }

    /**
     * Module IV: Character stream logging with FileWriter and BufferedWriter.
     * Synchronized to prevent concurrent thread log interleaving.
     */
    public static synchronized void logAction(String action, String userOrAccount, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String logEntry = String.format("[%s] [ACTION: %-15s] [TARGET: %-15s] %s%n",
                timestamp, action, userOrAccount, details);

        File file = new File(LOG_FILE);
        // Ensure parent directory exists
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            // Module IV: FileWriter with append=true (character stream)
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(logEntry);
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println("[FileAuditLogger] Error writing to audit log: " + e.getMessage());
        } finally {
            // Module III & IV: Clean closure of character streams in finally block
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException ignored) {
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static String getLogFilePath() {
        return new File(LOG_FILE).getAbsolutePath();
    }
}

