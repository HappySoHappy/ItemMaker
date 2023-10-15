package me.howandev.itemmaker;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class ServerTest {
    protected ServerMock server;
    protected ItemMakerPlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ItemMakerPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
