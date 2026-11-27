package dev.worldlink.friends;

import dev.worldlink.shared.Friend;
import dev.worldlink.shared.Invite;

import java.util.Objects;
import java.util.Optional;

/**
 * Loest einen Einladungscode in eine Verbindungsadresse auf.
 * Ohne Cloud ist der Code nur lokal aufloesbar; Phase 3 haengt den Dienst davor.
 */
public final class JoinCode {

    private final SessionTokenStore tokens;

    public JoinCode(SessionTokenStore tokens) {
        this.tokens = Objects.requireNonNull(tokens);
    }

    /** Nimmt entweder einen 8-stelligen Code oder bereits "host:port". */
    public static boolean looksLikeAddress(String input) {
        return input != null && input.contains(":") && !Invite.isValidCode(input);
    }

    /** Baut die Adresse, die im Multiplayer-Menue eingetragen wird. */
    public String resolve(String input, String hostAddress) {
        if (input == null || input.isBlank()) throw new IllegalArgumentException("Eingabe fehlt");
        String trimmed = input.trim();
        if (looksLikeAddress(trimmed)) return trimmed;
        if (!Invite.isValidCode(trimmed)) throw new IllegalArgumentException("Code ist ungueltig");
        return hostAddress + ":"
                + tokens.consume(trimmed, System.currentTimeMillis())
                        .map(id -> "ok")
                        .orElseThrow(() -> new IllegalArgumentException("Code ist abgelaufen"));
    }

    /** Klartext-Hinweis fuer die GUI, ohne Zugangsdaten zu verraten. */
    public String describe(Friend friend) {
        Optional<Integer> port = friend.hosting() ? Optional.of(friend.port()) : Optional.empty();
        return port.map(p -> friend.name() + " hostet auf Port " + p)
                .orElse(friend.name() + " hostet gerade nicht");
    }
}
