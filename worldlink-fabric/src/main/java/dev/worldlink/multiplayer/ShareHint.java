package dev.worldlink.multiplayer;

/** Plain-language help shown when a friend cannot connect. */
public final class ShareHint {

    private ShareHint() {}

    /** Explains the usual causes of a timed out connection. */
    public static String forTimeout() {
        return "Connection timed out. Check the port forwarding on the host router.";
    }

    /** Which address the host should send. */
    public static String whichAddress() {
        return "Send the address WorldLink shows, not your own guess.";
    }
}
