package dev.worldlink.client;

import dev.worldlink.friends.FriendService;
import dev.worldlink.gui.FriendsScreen;
import dev.worldlink.gui.ShareWorldScreen;
import dev.worldlink.multiplayer.WorldSharing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

public class WorldLinkClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof PauseScreen) || client.getSingleplayerServer() == null) return;

            Button share = Button.builder(shareLabel(), b -> {
                        if (!WorldSharing.session().isOpen()) {
                            WorldSharing.open();
                            b.setMessage(shareLabel());
                            return;
                        }
                        Minecraft.getInstance().setScreen(new ShareWorldScreen(screen));
                    })
                    .bounds(scaledWidth / 2 - 102, 8, 204, 20)
                    .build();
            Screens.getWidgets(screen).add(share);

            Button closeWorld = Button.builder(Component.literal("WorldLink: Welt schliessen"), b -> {
                        WorldSharing.close();
                        b.setMessage(shareLabel());
                        Minecraft.getInstance().setScreen(new ShareWorldScreen(screen));
                    })
                    .bounds(scaledWidth / 2 - 102, 30, 204, 20)
                    .build();
            closeWorld.active = WorldSharing.session().isOpen();
            Screens.getWidgets(screen).add(closeWorld);

            Button friends = Button.builder(Component.literal("WorldLink: Freunde"), b ->
                            Minecraft.getInstance().setScreen(new FriendsScreen(screen)))
                    .bounds(scaledWidth / 2 - 102, 52, 204, 20)
                    .build();
            Screens.getWidgets(screen).add(friends);
        });
    }

    private static Component shareLabel() {
        return Component.literal(WorldSharing.session().isOpen()
                ? "WorldLink: Welt schliessen (Port " + WorldSharing.session().port() + ")"
                : "WorldLink: Welt freigeben");
    }
}
