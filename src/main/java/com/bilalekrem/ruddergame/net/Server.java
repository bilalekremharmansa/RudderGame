package com.bilalekrem.ruddergame.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.bilalekrem.ruddergame.game.Game.GameType;
import com.bilalekrem.ruddergame.net.Message.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Server class accepts request from clients. When clients reach
 * to server, the Server class adds clients to a queue data structure.
 * 
 * This class has a nested class that called Matchmaking. When enough
 * players exists in queue, Matchmaking creates a GameSession object.
 * Since that, GameSession takes responsibility. 
 * 
 * @author Bilal Ekrem Harmansa
 */
public class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    private ServerSocket server;
    Queue<ClientListener> queue;

    private Server() {
        queue = new LinkedList<>();
    }
    
    /** Singleton object, only one server can be up. */
    private static Server singleton;
    public static Server getInstance() {
        if(singleton == null) {
            singleton = new Server();
        }
        return singleton;
    }

    /**
     * Creates a new ServerSocket to listen from given port.
     * 
     * @param port that will be listened form.
     * 
     * @return caller Server object.
     */
    public Server build (int port) throws IOException {
        // if before creating a new ServerSocket, close the old one.
        if(server != null) server.close();
        server = new ServerSocket(port);
        return this;
    }

    /**
     * This method welcome clients from listened port and put them
     * in game queue. Before starting welcoming, creates a Matchmaking
     * thread. This ensures controlling of queue.
     * 
     * @param type each game has different Matchmaking rules and has
     * different Sessions, determine to correct one using this param.
     */
    public void welcome(GameType type) {
        Matchmaking m = MatchmakingFactory.create(type, this);
        m.start();

        try {
            /** IDs are in 0-9 is reserved for servers. */
            int clientID = 10;
            Message welcomeMessage = new Message(0, MessageType.CONNECT, null);
            while(!this.server.isClosed()) {
                Socket clientSocket = server.accept();
                ClientListener client = new ClientListener(clientID, clientSocket);
                client.send(welcomeMessage);
                clientID++;
                queue.add(client);

                LOGGER.info("Client is connected to Server");

                Thread.sleep(1000);
            }
        } catch(IOException ex ){
            LOGGER.fatal("I/O error occurs while waiting for a client.");
        } catch(InterruptedException ex ){
            LOGGER.error("Thread could not interrupted.");
        }finally{
            // if server is closed or stop listening port, stop thread working.
            m.stop();
        }
    }

    public void stop() {
        try {
            if(server != null && !server.isClosed()) server.close();
        }catch(IOException ex) {
            LOGGER.error("Server could not stopped. Current state is {}", server.isClosed());
        }
    }

    /**
     * This class contains information about clients socket and
     * uses in GameSession to ensure communication between Server and Client.
     */
    class ClientListener extends Client{
        /** Clients socket and streams to write and read from Client.*/
        final int ID;

        public ClientListener(int ID, Socket socket) {
            super(socket);
            this.ID = ID;
        }
        
    }


    
    
    

}