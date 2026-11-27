package dev.worldlink.multiplayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinInfoTest {

    @Test void buildsTarget() {
        JoinInfo info = new JoinInfo("192.168.1.42", 57971, "OneBlock");
        assertEquals("192.168.1.42:57971", info.joinTarget());
    }

    @Test void shareMessageContainsAddress() {
        JoinInfo info = new JoinInfo("192.168.1.42", 57971, "OneBlock");
        String message = info.shareMessage();
        assertTrue(message.contains("192.168.1.42:57971"));
        assertTrue(message.contains("OneBlock"));
    }

    @Test void rejectsBadPort() {
        assertThrows(IllegalArgumentException.class, () -> new JoinInfo("10.0.0.1", 0, "World"));
        assertThrows(IllegalArgumentException.class, () -> new JoinInfo("10.0.0.1", 70000, "World"));
    }

    @Test void rejectsBlankAddress() {
        assertThrows(IllegalArgumentException.class, () -> new JoinInfo("   ", 25565, "World"));
    }

    @Test void manualAddressWins() {
        assertEquals("203.0.113.7", AddressResolver.resolve("203.0.113.7").orElseThrow());
    }

    @Test void resolvesSomething() {
        assertTrue(AddressResolver.resolve(null).isPresent());
    }

    @Test void hintIsPlainEnglish() {
        assertTrue(AddressResolver.hint("127.0.0.1").startsWith("Only reachable"));
    }
}
