package dev.worldlink.gui;

import dev.worldlink.multiplayer.AddressResolver;
import dev.worldlink.multiplayer.CopyService;
import dev.worldlink.multiplayer.JoinInfo;
import dev.worldlink.multiplayer.WorldSharing;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/** Share screen: shows the address, copies it, and explains how a friend joins. */
public class ShareWorldScreen extends Screen {

    private final Screen parent;
    private String feedback = "Copy the address and send it to a friend";

    public ShareWorldScreen(Screen parent) {
        super(Component.literal("WorldLink - Share World"));
        this.parent = parent;
    }

    private static Optional<JoinInfo> currentInfo() {
        if (!WorldSharing.session().isOpen()) return Optional.empty();
        return AddressResolver.resolve(WorldSharing.manualAddress())
                .map(address -> new JoinInfo(address, WorldSharing.session().port(), WorldSharing.worldName()));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 150;

        Optional<JoinInfo> info = currentInfo();
        if (info.isPresent()) {
            JoinInfo join = info.get();
            this.addRenderableWidget(Button.builder(Component.literal("Copy address"), b -> {
                        CopyService.copy(join.shareMessage());
                        this.feedback = "Copied: " + join.joinTarget();
                        this.rebuildWidgets();
                    })
                    .bounds(x, this.height - 112, 300, 20)
                    .build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Paste address to join"), b -> {
                    this.feedback = "This button joins later, via the WorldLink tunnel";
                    this.rebuildWidgets();
                })
                .bounds(x, this.height - 84, 300, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.onClose())
                .bounds(x, this.height - 56, 300, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
        graphics.centeredText(this.font, Component.literal(this.feedback),
                this.width / 2, 38, 0xFFA0A0A0);

        Optional<JoinInfo> info = currentInfo();
        if (info.isEmpty()) {
            graphics.centeredText(this.font, Component.literal("Your world is not open"),
                    this.width / 2, 70, 0xFFE0A0A0);
            return;
        }
        JoinInfo join = info.get();
        graphics.centeredText(this.font, Component.literal(join.joinTarget()),
                this.width / 2, 70, 0xFF80FF80);
        graphics.centeredText(this.font, Component.literal(AddressResolver.hint(join.address())),
                this.width / 2, 86, 0xFF808080);
        graphics.centeredText(this.font,
                Component.literal("Friend enters this under Server Address"),
                this.width / 2, 108, 0xFF808080);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
