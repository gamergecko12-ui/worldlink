package dev.worldlink.friends;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ermittelt die lokalen Adressen, unter denen der Host erreichbar ist.
 * Erste Stufe des Verbindungsaufbaus; der Tunnel folgt in Phase 2b.
 */
public final class NatProbe {

    private NatProbe() {}

    /** Alle nicht-lokalen IPv4-Adressen dieses Rechners. Leer, wenn keine gefunden wird. */
    public static List<String> localAddresses() {
        List<String> result = new ArrayList<>();
        try {
            for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!nic.isUp() || nic.isLoopback()) continue;
                for (InetAddress addr : Collections.list(nic.getInetAddresses())) {
                    if (addr.isLoopbackAddress() || addr.isLinkLocalAddress()) continue;
                    String host = addr.getHostAddress();
                    if (host.contains(":")) continue;
                    result.add(host);
                }
            }
        } catch (SocketException e) {
            return List.of();
        }
        return List.copyOf(result);
    }

    /** Erreichbar im eigenen Netz? */
    public static boolean reachableLocally(int port, int timeoutMillis) {
        String host = localAddresses().stream().findFirst().orElse("127.0.0.1");
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), timeoutMillis);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
