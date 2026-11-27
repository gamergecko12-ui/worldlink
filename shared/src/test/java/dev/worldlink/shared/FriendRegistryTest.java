package dev.worldlink.shared;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FriendRegistryTest {

    private static final UUID ANNA = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID BEN = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test void requestThenAccept() {
        FriendRegistry r = new FriendRegistry();
        r.requestFrom(ANNA);
        assertTrue(r.acceptRequest(ANNA, "Anna"));
        assertEquals(1, r.friends().size());
        assertTrue(r.incomingRequests().isEmpty());
    }

    @Test void declineRemovesRequest() {
        FriendRegistry r = new FriendRegistry();
        r.requestFrom(BEN);
        assertTrue(r.declineRequest(BEN));
        assertFalse(r.declineRequest(BEN));
    }

    @Test void tagsAMeetingRoom() {
        FriendRegistry r = new FriendRegistry();
        r.addFriend(Friend.offline(ANNA, "Anna"));
        r.addFriend(Friend.offline(BEN, "Ben"));
        r.updatePresence(ANNA, true, false, 0);
        r.updatePresence(BEN, false, false, 0);
        Friend other = new Friend(UUID.randomUUID(), "Clara", true, false, 0);
        r.addFriend(other);
        assertEquals(1, r.friends().stream().filter(f -> f.online() && !f.hosting()).count());
        r.removeFriend(BEN);
        assertEquals(2, r.friends().size());
        r.pruneExpired(0L);
        assertEquals(1, r.incomingRequests().size() + 1);
        r.updatePresence(ANNA, true, true, 26000);
        assertEquals(1, r.friends().stream().filter(Friend::hosting).count());
    }

    @Test void inviteRoundTrip() {
        FriendRegistry r = new FriendRegistry();
        r.addFriend(Friend.offline(ANNA, "Anna"));
        Invite invite = new Invite(UUID.randomUUID(), BEN, "Ben", ANNA, 1000L, 2000L);
        r.addFriend(Friend.offline(BEN, "Ben"));
        r.createInvite(ANNA, invite);
        assertEquals(1, r.allInvites().size());
        assertEquals(invite.id(), r.pendingInviteFor(ANNA).orElseThrow().id());
        assertTrue(Invite.isValidCode(invite.code()));
        assertTrue(r.consumeInvite(invite.code()));
        assertFalse(r.consumeInvite(invite.code()));
        assertFalse(Invite.isValidCode("zzzz"));
    }

    @Test void expiredInvitesDisappear() {
        FriendRegistry r = new FriendRegistry();
        r.addFriend(Friend.offline(ANNA, "Anna"));
        Invite invite = new Invite(UUID.randomUUID(), BEN, "Ben", ANNA, 1000L, 2000L);
        r.createInvite(ANNA, invite);
        r.pruneExpired(2500L);
        assertTrue(r.allInvites().isEmpty());
    }

    @Test void joinAddressUsesPort() {
        Friend hosting = new Friend(ANNA, "Anna", true, true, 57971);
        assertEquals("localhost:57971", hosting.joinAddress("localhost"));
    }
}
