package dev.worldlink.multiplayer;

/** Legt Text in die Zwischenablage von Minecraft. */
public final class CopyService {

    private CopyService() {}

    /** Setzt den Verbindungstext in die Zwischenablage. */
    public static boolean copy(String text) {
        net.minecraft.client.Minecraft.getInstance().keyboardHandler.setClipboard(text);
        return true;
    }
}
