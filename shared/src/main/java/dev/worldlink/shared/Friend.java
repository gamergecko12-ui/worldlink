package dev.worldlink.shared;

import java.util.Objects;
import java.util.UUID;

/** Ein Freund im WorldLink-Netz. Reines Datenmodell, ohne Minecraft. */
public record Friend(UUID id, String name, boolean online, boolean hosting, int port) {

    public Friend {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        if (port < -1 || port > 65535) throw new IllegalArgumentException("Ungueltiger Port: " + port);
    }

    public static Friend offline(UUID id, String name) {
        return new Friend(id, name, false, false, -1);
    }

    /** Adresse, die im Client eingetragen wird. Solange kein Tunnel laeuft, die Host-Adresse. */
    public String joinAddress(String hostAddr) {
        return hostAddr + ":" + port;
    }
}
