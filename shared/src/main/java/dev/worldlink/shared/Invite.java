package dev.worldlink.shared;

import java.util.Objects;
import java.util.UUID;

/** Eine Einladung in eine Welt. id ist zugleich der Beitrittscode. */
public record Invite(UUID id, UUID fromId, String fromName, UUID toId, long createdAtMillis, long expiresAtMillis) {

    public Invite {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(fromId, "fromId");
        Objects.requireNonNull(toId, "toId");
        if (expiresAtMillis <= createdAtMillis) throw new IllegalArgumentException("Ablauf liegt vor dem Erstellen");
    }

    public boolean isExpired(long nowMillis) {
        return nowMillis >= expiresAtMillis;
    }

    /** Kurzcode, den der Gast eintippen kann, statt Adresse und Port. */
    public String code() {
        return id.toString().substring(0, 8).replace("-", "");
    }

    /** Ist der Code gueltig? Nur Hex-Zeichen, genau 8 Stellen. */
    public static boolean isValidCode(String raw) {
        if (raw == null || raw.length() != 8) return false;
        for (int i = 0; i < raw.length(); i++) {
            char c = Character.toLowerCase(raw.charAt(i));
            boolean hex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
            if (!hex) return false;
        }
        return true;
    }
}
