package dev.worldlink.friends;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Haelt die Zugangstoken einer Sitzung. Jeder Beitritt bekommt ein eigenes,
 * zeitlich begrenztes Token. Reines Java, damit es testbar bleibt.
 */
public final class SessionTokenStore {

    private static final long DEFAULT_TTL_MILLIS = 10L * 60L * 1000L;

    private final Map<String, Entry> tokens = new LinkedHashMap<>();
    private final SecureRandom random = new SecureRandom();

    private record Entry(UUID invited, long expiresAtMillis) {}

    /** Erzeugt ein Token, das an genau diese Einladung gebunden ist. */
    public synchronized String issue(UUID invitedId, long nowMillis, long ttlMillis) {
        Objects.requireNonNull(invitedId, "invitedId");
        if (ttlMillis <= 0) throw new IllegalArgumentException("ttl muss positiv sein");
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(64);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        String token = sb.toString();
        tokens.put(token, new Entry(invitedId, nowMillis + ttlMillis));
        return token;
    }

    public String issue(UUID invitedId, long nowMillis) {
        return issue(invitedId, nowMillis, DEFAULT_TTL_MILLIS);
    }

    /** Prueft ein Token und entfernt es. Ein Token gilt nur einmal. */
    public synchronized Optional<UUID> consume(String token, long nowMillis) {
        if (token == null) return Optional.empty();
        Entry entry = tokens.get(token);
        if (entry == null) return Optional.empty();
        tokens.remove(token);
        if (nowMillis >= entry.expiresAtMillis()) return Optional.empty();
        return Optional.of(entry.invited());
    }

    public synchronized void revoke(String token) {
        tokens.remove(token);
    }

    /** Widerruft alle Token einer Person. */
    public synchronized void revokeAllFor(UUID id) {
        tokens.values().removeIf(e -> e.invited().equals(id));
    }

    public synchronized void pruneExpired(long nowMillis) {
        tokens.values().removeIf(e -> nowMillis >= e.expiresAtMillis());
    }

    public synchronized int size() { return tokens.size(); }
}
