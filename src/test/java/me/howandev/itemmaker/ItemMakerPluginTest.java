package me.howandev.itemmaker;

import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemMakerPluginTest extends ServerTest {
    @Test
    public void shouldFirePlayerJoinEvent() {
        server.addPlayer();

        server.getPluginManager().assertEventFired(PlayerJoinEvent.class);
    }
}