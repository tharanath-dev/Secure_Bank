package com.securebank.model;

import java.sql.Timestamp;

/**
 * Module I & II: Model representing a one-time password reset token.
 */
public class PasswordResetToken {
    private int tokenId;
    private int userId;
    private String tokenHash;
    private Timestamp expiryTime;
    private boolean used;
    private Timestamp createdAt;

    public PasswordResetToken() {
    }

    public PasswordResetToken(int tokenId, int userId, String tokenHash, Timestamp expiryTime, boolean used, Timestamp createdAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiryTime = expiryTime;
        this.used = used;
        this.createdAt = createdAt;
    }

    public int getTokenId() { return tokenId; }
    public void setTokenId(int tokenId) { this.tokenId = tokenId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public Timestamp getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Timestamp expiryTime) { this.expiryTime = expiryTime; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isExpired() {
        return expiryTime != null && expiryTime.before(new Timestamp(System.currentTimeMillis()));
    }
}

