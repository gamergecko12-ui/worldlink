package dev.worldlink.multiplayer;

import net.minecraft.client.Minecraft;

/** Legt Text in die Zwischenablage von Minecraft. */
public final class CopyService {

    private CopyService() {}

    /** Setzt den Verbindungstext in die Zwischenablage. */
    public static boolean copy(String text) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
        return true;
    }
}
