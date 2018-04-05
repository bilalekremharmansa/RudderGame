package com.bilalekrem.ruddergame.net;

import org.junit.*;

@Ignore
public class ClientTest {
    private final int PORT = 15600;

    static Client client;
    @Before
    public void construct() {
        
    }

    @After
    public void deconstruct() {
        client.stop();
    }

    @Ignore
    public void testCreateAndConnect() {
        client = new Client("127.0.0.1", PORT);
        
    }
}