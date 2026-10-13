package com.securebank.service;

import com.securebank.dao.TokenDAO;
import com.securebank.util.FileAuditLogger;

/**
 * Module III: Multithreading & Runnable implementation.
 * 
 * Background daemon thread that periodically wakes up (every 3 minutes)
 * and cleans up expired or used password reset tokens from the database.
 */
public class TokenCleanupTask implements Runnable {

    private static volatile boolean running = false;
    private final TokenDAO tokenDAO;
    private final long sleepIntervalMs;

    public TokenCleanupTask(TokenDAO tokenDAO, long sleepIntervalMs) {
        this.tokenDAO = tokenDAO;
        this.sleepIntervalMs = sleepIntervalMs;
    }

    /**
     * Module III: Background thread run() loop.
     */
    @Override
    public void run() {
        System.out.println("[TokenCleanupTask] Background cleanup thread started.");
        while (running) {
            try {
                int cleaned = tokenDAO.deleteExpiredOrUsedTokens();
                if (cleaned > 0) {
                    FileAuditLogger.logAction("TOKEN_CLEANUP", "SYSTEM", "Purged " + cleaned + " expired/used reset tokens.");
                }
                Thread.sleep(sleepIntervalMs);
            } catch (InterruptedException e) {
                System.out.println("[TokenCleanupTask] Background thread interrupted.");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[TokenCleanupTask] Error during background token purge: " + e.getMessage());
            }
        }
        System.out.println("[TokenCleanupTask] Background cleanup thread terminated.");
    }

    /**
     * Starts the cleanup thread once as a background Daemon thread.
     */
    public static synchronized void startOnce(TokenDAO tokenDAO) {
        if (!running) {
            running = true;
            // Purge every 3 minutes (180,000 ms)
            TokenCleanupTask task = new TokenCleanupTask(tokenDAO, 180_000L);
            Thread worker = new Thread(task, "SecureBank-TokenCleanup-Thread");
            worker.setDaemon(true); // Daemon thread exits when web application stops
            worker.start();
        }
    }

    public static synchronized void stop() {
        running = false;
    }
}

