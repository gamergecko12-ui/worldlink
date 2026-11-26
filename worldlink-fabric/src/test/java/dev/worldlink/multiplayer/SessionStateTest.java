package dev.worldlink.multiplayer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionStateTest {
    @Test void opensAndCloses() {
        SessionState s = new SessionState();
        s.open(25565);
        assertTrue(s.isOpen());
        assertEquals(25565, s.port());
        s.close();
        assertFalse(s.isOpen());
    }

    @Test void rejectsDoubleOpen() {
        SessionState s = new SessionState();
        s.open(25565);
        assertThrows(IllegalStateException.class, () -> s.open(25566));
    }

    @Test void rejectsBadPort() {
        assertThrows(IllegalArgumentException.class, () -> new SessionState().open(0));
    }
}
