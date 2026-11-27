package dev.worldlink.gui;

import dev.worldlink.friends.FriendService;
import dev.worldlink.shared.Friend;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Freundesliste mit Einladen-Button. Oberflaeche bleibt, Transport wechselt in Phase 3. */
public class FriendsScreen extends Screen {

    private static final int ROW_HEIGHT = 26;
    private static final int LIST_TOP = 60;

    private final Screen parent;
    private String statusLine = FriendService.get().status();

    public FriendsScreen(Screen parent) {
        super(Component.literal("WorldLink - Freunde"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        List<Friend> friends = FriendService.get().friends();
        int x = this.width / 2 - 150;
        int y = LIST_TOP;

        for (Friend friend : friends) {
            boolean hosting = friend.hosting();
            Component label = Component.literal(friend.name() + (hosting ? " - offen" : ""));

            Button invite = Button.builder(Component.literal("Einladen"), b -> {
                        this.statusLine = FriendService.get().invite(friend.id(), friend.name());
                        this.rebuildWidgets();
                    })
                    .bounds(x + 150, y, 100, 20)
                    .build();
            invite.active = FriendService.get().registry().friend(friend.id()).isPresent();
            this.addRenderableWidget(invite);

            this.addRenderableWidget(Button.builder(label, b -> {})
                    .bounds(x, y, 145, 20)
                    .build());
            y += ROW_HEIGHT;
        }

        this.addRenderableWidget(Button.builder(Component.literal("Beitreten"), b -> {
                    this.statusLine = "Code eingeben: siehe README (Phase 2b)";
                    this.rebuildWidgets();
                })
                .bounds(this.width / 2 - 150, this.height - 78, 300, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Freund hinzufuegen"), b -> {
                    this.statusLine = "Anfragen kommen in Phase 3 aus der Cloud";
                    this.rebuildWidgets();
                })
                .bounds(this.width / 2 - 150, this.height - 54, 145, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Zurueck"), b -> this.onClose())
                .bounds(this.width / 2 + 5, this.height - 54, 145, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        graphics.centeredText(this.font, Component.literal(this.statusLine),
                this.width / 2, 38, 0xA0A0A0);
        if (FriendService.get().friends().isEmpty()) {
            graphics.centeredText(this.font, Component.literal("Noch keine Freunde eingetragen"),
                    this.width / 2, LIST_TOP + 6, 0x808080);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
