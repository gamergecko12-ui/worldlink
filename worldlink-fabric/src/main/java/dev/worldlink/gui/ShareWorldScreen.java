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

/**
 * Fenster zum Teilen der Welt: zeigt die Verbindungsadresse,
 * kopiert sie auf Knopfdruck und oeffnet dieselbe Welt wieder.
 */
public class ShareWorldScreen extends Screen {

    private final Screen parent;
    private String feedback = "Adresse kopieren und an einen Freund senden";

    public ShareWorldScreen(Screen parent) {
        super(Component.literal("WorldLink - Welt teilen"));
        this.parent = parent;
    }

    /** Verbindungstext aus der laufenden Sitzung, oder leer wenn nichts offen ist. */
    private static Optional<JoinInfo> currentInfo() {
        if (!WorldSharing.session().isOpen()) return Optional.empty();
        String worldName = WorldSharing.worldName();
        return AddressResolver.resolve(WorldSharing.manualAddress())
                .map(address -> new JoinInfo(address, WorldSharing.session().port(), worldName));
    }

    @Override
    protected void init() {
        Optional<JoinInfo> info = currentInfo();
        int x = this.width / 2 - 150;

        if (info.isPresent()) {
            JoinInfo join = info.get();
            this.addRenderableWidget(Button.builder(Component.literal("Adresse kopieren"), b -> {
                        CopyService.copy(join.shareMessage());
                        this.feedback = "Kopiert: " + join.joinTarget();
                        this.rebuildWidgets();
                    })
                    .bounds(x, this.height - 84, 300, 20)
                    .build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Zurueck"), b -> this.onClose())
                .bounds(x, this.height - 56, 300, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        graphics.centeredText(this.font, Component.literal(this.feedback),
                this.width / 2, 38, 0xA0A0A0);

        Optional<JoinInfo> info = currentInfo();
        if (info.isEmpty()) {
            graphics.centeredText(this.font, Component.literal("Deine Welt ist nicht offen"),
                    this.width / 2, 70, 0xE0A0A0);
            return;
        }
        JoinInfo join = info.get();
        graphics.centeredText(this.font, Component.literal(join.joinTarget()),
                this.width / 2, 70, 0x80FF80);
        graphics.centeredText(this.font,
                Component.literal(AddressResolver.hint(join.address())),
                this.width / 2, 86, 0x808080);
        graphics.centeredText(this.font,
                Component.literal("Freund traegt das unter Server-Adresse ein"),
                this.width / 2, 108, 0x808080);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
