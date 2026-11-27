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

    private WorldSharing() {}

    public static SessionState session() { return SESSION; }

    /** Gibt die laufende Singleplayer-Welt ueber den integrierten Server frei. */
    public static boolean open() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null || SESSION.isOpen()) return false;
        int port = HttpUtil.getAvailablePort();
        if (!server.publishServer(MinecraftServer.MultiplayerScope.LAN, false, port)) return false;
        SESSION.open(port);
        return true;
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
