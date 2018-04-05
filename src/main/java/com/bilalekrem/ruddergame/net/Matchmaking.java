package com.bilalekrem.ruddergame.net;

import com.bilalekrem.ruddergame.net.Server.ClientListener;
import com.bilalekrem.ruddergame.game.Game.GameType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class waits for players to join and match them into a game.
 * When a player get in the queue, Matchmaking waits for other
 * players. After enough player receive, creates a game session. 
 * Game session will be notify them to game is ready.
 * 
 * @author Bilal Ekrem Harmansa 
 */
public abstract class Matchmaking extends ServerThread {
    private static final Logger LOGGER = LogManager.getLogger(Matchmaking.class);

    private Server server;

    /** In constructor, registering Server to Matchmakin that
     * defines which Server's users will be matched.
     */
    Matchmaking(Server server) {
        super();
        this.server = server;
    }

    /** 
     * Each game has different rules and, also, there might be
     * more than two players to starting game. This method returns 
     * how many players needs to play.
     * 
     * @return how many players needs to start to game.
     */
    abstract int numberOfRequiredPlayers();
    
    /**
     * Thread will controll Clients in queue. If enough players are in
     * queue, game session can be created.
     */
    @Override
    public void run() {
        int requiredSize = numberOfRequiredPlayers();
        ClientListener[] clients = new ClientListener[requiredSize];
        int index = 0;
        
        int sessionID = 1;
        while(run) {
            if(!server.queue.isEmpty() && index < requiredSize) {
                ClientListener head = server.queue.poll(); 
                clients[index++] = head;
            }else if(index == requiredSize) {
                GameSession session = new GameSession(sessionID++);
                session.initiliaze(clients);
                /** ClientListener buffer could be cleaned in here. */
                index = 0;
                session.start();
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                LOGGER.error("Thread could not interrupted.");
            }
        }
    }

}

/**
 * Using abstract factory design pattern in here. GameType is
 * passing to Server and Server needs to create Matchmaking object
 * for controlling queue. Factory class manages creating Matchmaking
 * class by using game type.
 * 
 * src: 
 * github.com/iluwatar/java-design-patterns/tree/master/abstract-factory
 * 
 * @author Bilal Ekrem Harmansa
 */
class MatchmakingFactory {

    public static Matchmaking create(GameType type, Server server) {
        switch (type) {
            case RUDDER:
                return new RudderGameMatchmaking(server);
            default:
                throw new IllegalArgumentException("Unknown game type.");
        }
    }
}



    