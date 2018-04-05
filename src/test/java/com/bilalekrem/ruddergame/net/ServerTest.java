package com.bilalekrem.ruddergame.net;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import com.bilalekrem.ruddergame.game.Game.GameType;

@Ignore
public class ServerTest {

    private final int PORT = 16825;

    static Server server;
    @Before
    public void construct() {
        server = Server.getInstance();
    }

    @After
    public void deconstruct() {
        server.stop();
    }

    @Test
    public void testSingleton() {
        Server server2 = Server.getInstance();

        boolean referanceEquality = server == server2;

        assertEquals(true, referanceEquality);
    }

    @Test
    public void testBuild() {
        try {
            server.build(PORT).welcome(GameType.RUDDER);

            
            
        }catch(IOException ex) {
            System.out.println(ex.getMessage());
            fail("Server could'not created.");
        }
    
    }
    
}