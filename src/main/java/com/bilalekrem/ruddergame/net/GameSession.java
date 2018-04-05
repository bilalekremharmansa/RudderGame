package com.bilalekrem.ruddergame.net;

import com.bilalekrem.ruddergame.net.Server.ClientListener;
import com.bilalekrem.ruddergame.game.*;
import com.bilalekrem.ruddergame.game.Game.Move;
import com.bilalekrem.ruddergame.net.Message;
import com.bilalekrem.ruddergame.net.Message.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * In server-side controlling games and controlling message passing
 * between clients are doing in this class. 
 * 
 * @author Bilal Ekrem Harmansa
 */
public class GameSession implements ClientObserver{

    private static final Logger LOGGER = LogManager.getLogger(GameSession.class);
    
    final protected int sessionID;
    Map<Integer, ClientListener> clients;
    final Game game;


    GameSession(int sessionID) {
        this.sessionID = sessionID;
        clients = new HashMap<>();
        game = new RudderGame(); // should be instantinate with parameter.
    }

    /**
     * Preparation just before game start. Initiliazion board, 
     * initiliazion game, matching players and clients etc..
     */
    void initiliaze(ClientListener... cls) {
        Player[] players = new Player[cls.length];
        for (int i = 0; i < cls.length; i++) {
            int clientID = cls[i].ID;
            players[i] = new Player(clientID);

            this.clients.put(clientID, cls[i]);
            cls[i].registerObserver(this);
            cls[i].start();
          
            /** 
             * requesting names of players. When it receives, we will send names to
             * players in start() method and game will start it.
             */
            Message message = new Message(sessionID, MessageType.GREETING, players[i]);
            informPlayer(players[i], message);
        }

        game.initiliazeBoard();
        game.initiliazeGame(players);
    }

    /**
     * Let game start. This method will notify players that
     * game is ready and starting.
     */
	void start() {
        while(!isAllReady()) { /** wait until all players are ready. */ }

        List<Player> players = game.getPlayers();
        Message message = new Message(sessionID, MessageType.PLAYERS, players);
        for (Player player : players) {
            informPlayer(player, message);
        }

    }

    /**
     * If a player's name is not Unknown, we can be sure that a client told
     * its name to Server and changed its name with given name.
     */
    private boolean isAllReady() {
        for (Player player : game.getPlayers()) {
            if(player.name.equals("Unknown")) return false;
        }
        return true;
    }

     /**
     * When a player make a move, other players should be notified
     * from this move. This method ensures to notify each player
     * in game from a move.
     * 
     * @param player to be informed
     * @param message to be send to player.
     */
	void informPlayer(Player player, Message message) {
		ClientListener client = clients.get(player.ID);
        client.send(message);
    }

    /**
     * This methods informs players, however not informs sender.
     * 
     * @param message that will inform to players.
     */
    private void informOtherPlayers(Message message) {
        for (Player player : game.getPlayers()) {
            if(player.ID == message.senderID()) continue;
            informPlayer(player, message);
        }
    }

    /**
     * Each game session will be register itself to a client. As each 
     * game session is also a ClientListener, If a message comes from
     * client, it will be notified to GameSession as well.
     * 
     * @param message that clients send to session.
     */
    @Override
	public void update(Message message) {
		switch(message.type()) {
            case GREETING:
                game.getPlayers()
                            .stream()
                            .filter(p->p.ID == message.senderID())
                            .findFirst().ifPresent(player -> {
                                Player clientPlayer = message.content(Player.class);
                                player.name = clientPlayer.name;
                            });
                break;
            case MOVE:
                Move move = message.content(Move.class);
                boolean result = game.move(move);

                if(result) informOtherPlayers(message);
                else {
                    LOGGER.fatal("VALIDATE PACKAGES! Something went wrong.");
                }
                break;
            case DISCONNECT:
                /** Stopping ClientListener thread and removes from GameSession */
                clients.get(message.senderID()).stop();
                clients.remove(message.senderID());
                break;
            default: //discard
                break;
        }
	}

}