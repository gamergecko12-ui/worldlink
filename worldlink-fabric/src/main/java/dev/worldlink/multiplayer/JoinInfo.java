package dev.worldlink.multiplayer;

import java.util.Objects;

/**
 * Baut den Text, den der Host kopiert und an einen Freund schickt.
 * Reines Datenmodell ohne Minecraft, damit es testbar bleibt.
 */
public record JoinInfo(String address, int port, String worldName) {

    public JoinInfo {
        Objects.requireNonNull(address, "address");
        if (address.isBlank()) throw new IllegalArgumentException("Adresse fehlt");
        if (port <= 0 || port > 65535) throw new IllegalArgumentException("Ungueltiger Port: " + port);
    }

    /** Das, was der Gast in "Server-Adresse" eintraegt. */
    public String joinTarget() {
        return address + ":" + port;
    }

    /** Fertige Nachricht fuer Discord oder den Chat. */
    public String shareMessage() {
        return "WorldLink: Welt \"" + worldName + "\" ist offen.\n"
                + "Tritt bei ueber: " + joinTarget();
    }

    /** Version fuer gefaehrliche Anzeigen, ohne den Port zu verstecken. */
    public String shortLabel() {
        return worldName + " - " + joinTarget();
    }
}
