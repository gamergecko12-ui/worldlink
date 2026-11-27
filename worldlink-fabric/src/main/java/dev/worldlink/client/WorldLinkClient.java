package dev.worldlink.client;

import dev.worldlink.multiplayer.WorldSharing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

public class WorldLinkClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof PauseScreen) || client.getSingleplayerServer() == null) return;

            Button button = Button.builder(label(), b -> {
                        if (WorldSharing.session().isOpen()) WorldSharing.close();
                        else WorldSharing.open();
                        b.setMessage(label());
                    })
                    .bounds(scaledWidth / 2 - 102, 8, 204, 20)
                    .build();
            Screens.getWidgets(screen).add(button);
        });
    }

    private static Component label() {
        return Component.literal(WorldSharing.session().isOpen()
                ? "WorldLink: Welt schliessen (Port " + WorldSharing.session().port() + ")"
                : "WorldLink: Welt freigeben");
    }
}
