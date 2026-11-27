package dev.worldlink.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.HttpUtil;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public final class WorldSharing {
    private static final SessionState SESSION = new SessionState();

    private static String manualAddress;
    private static String worldName = "Welt";

    private WorldSharing() {}

    public static SessionState session() { return SESSION; }

    /** Adresse aus den Einstellungen. Leer lassen, dann wird sie automatisch ermittelt. */
    public static String manualAddress() { return manualAddress; }

    public static void setManualAddress(String address) { manualAddress = address; }

    public static String worldName() { return worldName; }

    public static void setWorldName(String name) {
        worldName = (name == null || name.isBlank()) ? "Welt" : name;
    }

    /** Gibt die laufende Singleplayer-Welt ueber den integrierten Server frei. */
    public static boolean open() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null || SESSION.isOpen()) return false;
        int port = HttpUtil.getAvailablePort();
        if (!server.publishServer(MinecraftServer.MultiplayerScope.LAN, false, port)) return false;
        SESSION.open(port);
        return true;
    }

    /** Verbindungstext der laufenden Sitzung. Leer, wenn nichts offen ist. */
    public static java.util.Optional<String> joinTarget() {
        if (!SESSION.isOpen()) return java.util.Optional.empty();
        return dev.worldlink.multiplayer.AddressResolver.resolve(manualAddress)
                .map(address -> address + ":" + SESSION.port());
    }

    /**
     * Kickt alle Gaeste. Beendet danach die Freigabe ueber unpublishServer().
     */
    public static void close() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            List<ServerPlayer> players = List.copyOf(server.getPlayerList().getPlayers());
            for (ServerPlayer p : players) {
                if (!server.isSingleplayerOwner(p.nameAndId())) {
                    p.connection.disconnect(Component.literal("WorldLink-Sitzung wurde geschlossen"));
                }
            }
            server.unpublishServer();
        }
        SESSION.close();
    }
}
