package dev.worldlink.friends;

import dev.worldlink.shared.Friend;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Einladen muss auch ohne bestehende Freundschaft gehen. */
class FriendsTest {

    @Test void invitingSomeoneWhoIsNotAFriendWorks() {
        Friend hosting = new Friend(java.util.UUID.randomUUID(), "Stranger", true, true, 57971);
        assertFalse(hosting.name().isEmpty());
    }

    @Test void joinCodeAcceptsAnAddress() {
        assertTrue(JoinCode.looksLikeAddress("192.168.1.42:57971"));
        assertFalse(JoinCode.looksLikeAddress("a1b2c3d4"));
    }
}
