package dev.worldlink.multiplayer;

/** Zustandsautomat einer Freigabe. Keine Minecraft-Abhaengigkeit. */
public final class SessionState {
    public enum State { CLOSED, OPEN }

    private State state = State.CLOSED;
    private int port = -1;

    public synchronized void open(int port) {
        if (state == State.OPEN) throw new IllegalStateException("Bereits freigegeben");
        if (port <= 0 || port > 65535) throw new IllegalArgumentException("Ungueltiger Port: " + port);
        this.port = port;
        this.state = State.OPEN;
    }

    public synchronized void close() {
        state = State.CLOSED;
        port = -1;
    }

    public synchronized boolean isOpen() { return state == State.OPEN; }
    public synchronized int port() { return port; }
}
