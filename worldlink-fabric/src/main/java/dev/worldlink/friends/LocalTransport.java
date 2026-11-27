package dev.worldlink.friends;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Phase-2-Transport: Alles laeuft im selben Prozess. Damit laesst sich das
 * Freundesmenue ohne Cloud testen. In Phase 3 wird das durch den echten
 * Transport zum WorldLink-Cloud-Dienst ersetzt, ohne die GUI zu aendern.
 */
public final class LocalTransport {

    private Consumer<String> sink = message -> {};

    public void onMessage(Consumer<String> sink) {
        this.sink = Objects.requireNonNull(sink);
    }

    /** Schickt eine Nachricht an den anderen "Client". Hier: an uns selbst. */
    public void send(String message) {
        sink.accept(message);
    }
}
