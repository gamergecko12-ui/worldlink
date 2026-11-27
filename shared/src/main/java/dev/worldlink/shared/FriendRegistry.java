package dev.worldlink.shared;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Haelt Freunde, Anfragen und Einladungen. Bewusst frei von Netzwerk und Minecraft,
 * damit die Logik testbar bleibt. Der Transport wird spaeter davor gehaengt.
 */
public final class FriendRegistry {

    private final Map<UUID, Friend> friends = new LinkedHashMap<>();
    private final Map<UUID, Invite> invites = new LinkedHashMap<>();
    private final List<UUID> incomingRequests = new ArrayList<>();

    public synchronized void addFriend(Friend friend) {
        friends.put(friend.id(), friend);
    }

    public synchronized boolean removeFriend(UUID id) {
        invites.values().removeIf(i -> i.fromId().equals(id) || i.toId().equals(id));
        return friends.remove(id) != null;
    }

    public synchronized List<Friend> friends() {
        return List.copyOf(friends.values());
    }

    public synchronized Optional<Friend> friend(UUID id) {
        return Optional.ofNullable(friends.get(id));
    }

    /** Setzt den Online-Status samt Host-Information. */
    public synchronized void updatePresence(UUID id, boolean online, boolean hosting, int port) {
        Friend current = friends.get(id);
        if (current == null) return;
        friends.put(id, new Friend(id, current.name(), online, hosting, hosting ? port : -1));
    }

    /** Kurzform ohne Port, fuer Statusmeldungen. */
    public synchronized void updatePresence(UUID id, boolean online, boolean hosting) {
        updatePresence(id, online, hosting, -1);
    }

    public synchronized void requestFrom(UUID id) {
        if (friends.containsKey(id) || incomingRequests.contains(id)) return;
        incomingRequests.add(id);
    }

    public synchronized List<UUID> incomingRequests() {
        return List.copyOf(incomingRequests);
    }

    public synchronized boolean acceptRequest(UUID id, String name) {
        if (!incomingRequests.remove(id)) return false;
        addFriend(Friend.offline(id, name));
        return true;
    }

    public synchronized boolean declineRequest(UUID id) {
        return incomingRequests.remove(id);
    }

    public synchronized Invite createInvite(UUID toId, Invite invite) {
        if (!friends.containsKey(toId)) throw new IllegalArgumentException("Kein Freund mit dieser ID");
        invites.put(invite.id(), invite);
        return invite;
    }

    public synchronized Optional<Invite> pendingInviteFor(UUID toId) {
        return invites.values().stream().filter(i -> i.toId().equals(toId)).findFirst();
    }

    public synchronized boolean consumeInvite(String code) {
        if (!Invite.isValidCode(code)) return false;
        for (Invite invite : List.copyOf(invites.values())) {
            if (invite.code().equalsIgnoreCase(code)) {
                invites.remove(invite.id());
                return true;
            }
        }
        return false;
    }

    public synchronized List<Invite> allInvites() {
        return List.copyOf(invites.values());
    }

    public synchronized void pruneExpired(long nowMillis) {
        invites.values().removeIf(i -> i.isExpired(nowMillis));
        Objects.requireNonNull(friends);
    }
}
