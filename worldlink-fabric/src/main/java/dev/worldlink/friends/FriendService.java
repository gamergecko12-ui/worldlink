package dev.worldlink.friends;

import dev.worldlink.multiplayer.WorldSharing;
import dev.worldlink.shared.Friend;
import dev.worldlink.shared.FriendRegistry;
import dev.worldlink.shared.Invite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Verbindet das Freundesmodell mit der Minecraft-Sitzung. */
public final class FriendService {

    private static final FriendService INSTANCE = new FriendService();
    private static final long INVITE_TTL_MILLIS = 10L * 60L * 1000L;

    private final FriendRegistry registry = new FriendRegistry();
    private final LocalTransport transport = new LocalTransport();
    private final SessionTokenStore tokens = new SessionTokenStore();
    private final JoinCode joinCode = new JoinCode(tokens);
    private final UUID selfId = UUID.randomUUID();
    private String selfName = "You";
    private Runnable onChange = () -> {};

    private FriendService() {
        transport.onMessage(message -> onChange.run());
    }

    public static FriendService get() { return INSTANCE; }

    public UUID selfId() { return selfId; }
    public FriendRegistry registry() { return registry; }
    public SessionTokenStore tokens() { return tokens; }
    public JoinCode joinCode() { return joinCode; }
    public void onChange(Runnable callback) { this.onChange = callback; }

    public static String statusText() {
        return WorldSharing.session().isOpen()
                ? "Your world is open (port " + WorldSharing.session().port() + ")"
                : "No world open";
    }

    public static Optional<Integer> hostedPort() {
        return WorldSharing.session().isOpen()
                ? Optional.of(WorldSharing.session().port())
                : Optional.empty();
    }

    /** Aktualisiert den eigenen Status im Freundesmodell. */
    public void syncSelf(String name) {
        this.selfName = name;
        registry.updatePresence(selfId, true, WorldSharing.session().isOpen(),
                WorldSharing.session().isOpen() ? WorldSharing.session().port() : -1);
        onChange.run();
    }

    /** Verschickt eine Einladung an einen Freund. Gibt den Text fuer die GUI zurueck. */
    public String invite(UUID friendId, String friendName) {
        if (!WorldSharing.session().isOpen()) return "Your world is not open";
        long now = System.currentTimeMillis();
        Invite invite = new Invite(UUID.randomUUID(), selfId, selfName, friendId,
                now, now + INVITE_TTL_MILLIS);
        registry.createInvite(friendId, invite);
        tokens.issue(friendId, now, INVITE_TTL_MILLIS);
        transport.send("INVITE " + invite.code());
        return "Code for " + friendName + ": " + invite.code();
    }

    /** Entfernt alle offenen Einladungen und Token einer Person. */
    public void revoke(UUID friendId) {
        registry.allInvites().stream()
                .filter(i -> i.toId().equals(friendId))
                .forEach(i -> tokens.revoke(i.code()));
        tokens.revokeAllFor(friendId);
        onChange.run();
    }

    public List<Friend> friends() { return registry.friends(); }

    public String status() { return statusText(); }
}
