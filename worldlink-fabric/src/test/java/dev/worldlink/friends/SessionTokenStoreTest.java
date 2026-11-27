package dev.worldlink.friends;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessionTokenStoreTest {

    private static final UUID ANNA = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test void tokenIsSingleUse() {
        SessionTokenStore store = new SessionTokenStore();
        String token = store.issue(ANNA, 1000L, 5000L);
        assertEquals(Optional.of(ANNA), store.consume(token, 1500L));
        assertEquals(Optional.empty(), store.consume(token, 1500L));
        assertEquals(0, store.size());
    }

    @Test void expiredTokenFails() {
        SessionTokenStore store = new SessionTokenStore();
        String token = store.issue(ANNA, 1000L, 100L);
        assertEquals(Optional.empty(), store.consume(token, 2000L));
    }

    @Test void revokeBlocksUse() {
        SessionTokenStore store = new SessionTokenStore();
        String token = store.issue(ANNA, 1000L, 5000L);
        store.revoke(token);
        assertEquals(Optional.empty(), store.consume(token, 1500L));
    }

    @Test void revokeAllForPerson() {
        SessionTokenStore store = new SessionTokenStore();
        store.issue(ANNA, 1000L, 5000L);
        store.issue(ANNA, 1000L, 5000L);
        UUID other = UUID.randomUUID();
        store.issue(other, 1000L, 5000L);
        store.revokeAllFor(ANNA);
        assertEquals(1, store.size());
    }

    @Test void pruneRemovesOldEntries() {
        SessionTokenStore store = new SessionTokenStore();
        store.issue(ANNA, 1000L, 100L);
        store.pruneExpired(2000L);
        assertEquals(0, store.size());
    }

    @Test void tokensDiffer() {
        SessionTokenStore store = new SessionTokenStore();
        assertNotEquals(store.issue(ANNA, 0L, 1000L), store.issue(ANNA, 0L, 1000L));
    }
}
