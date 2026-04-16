package com.fuzentrix.backend.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility for one-way hashing of sensitive tokens (e.g. refresh tokens) before
 * storing them in the database. Uses SHA-256 producing a 44-character Base64 string.
 *
 * <p>Storing a hash instead of the raw token ensures that a database breach
 * does not immediately expose usable session credentials.
 */
public final class TokenHashUtil {

    private TokenHashUtil() {
        // Utility class — do not instantiate
    }

    /**
     * Returns the SHA-256 Base64 hash of the given token string.
     *
     * @param token the plain-text token to hash
     * @return 44-character Base64-encoded SHA-256 hash
     */
    public static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed available in all Java SE implementations
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
