package dev.worldlink.multiplayer;

import dev.worldlink.friends.NatProbe;

import java.util.List;
import java.util.Optional;

/**
 * Picks the address used in the share text.
 * Reihenfolge: manuell gesetzt (Internet) vor automatischer lokaler Adresse.
 */
public final class AddressResolver {

    private AddressResolver() {}

    /**
     * @param manualAddress Adresse aus den Einstellungen, darf null sein
     * @return Adresse fuer den Verbindungstext, oder leer wenn keine gefunden wurde
     */
    public static Optional<String> resolve(String manualAddress) {
        if (manualAddress != null && !manualAddress.isBlank()) return Optional.of(manualAddress.trim());
        List<String> local = NatProbe.localAddresses();
        if (!local.isEmpty()) return Optional.of(local.get(0));
        Optional<String> loopback = Optional.of("127.0.0.1");
        return loopback;
    }

    /** Hinweis fuer die Anzeige, damit der Host weiss, was er verschickt. */
    public static String hint(String address) {
        if (address.startsWith("127.") || address.startsWith("localhost")) {
            return "Only reachable on this computer";
        }
        boolean local = NatProbe.localAddresses().contains(address);
        return local ? "Reachable on your own network" : "Reachable over the internet";
    }
}
