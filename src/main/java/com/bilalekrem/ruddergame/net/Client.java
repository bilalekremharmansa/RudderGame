package com.bilalekrem.ruddergame.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Client and Server's are endpoints in a network. Client makes request
 * Server process it and make a callback and so on. This class represents
 * Client in this secanario. 
 * 
 * Also, it get extends from ServerThread that ability of creating thread
 * and controlling it.
 * 
 * @author Bilal Ekrem Harmansa
 */
public class Client extends ServerThread{
    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    final Socket socket; 
    final ObjectOutputStream os;
    final ObjectInputStream is;

    /**
     * when a message comes to client, some class might want to be
     * knowledged about the message. With this implementation, the class
     * (observer) will be notified.
     */
    private ClientObserver observer;

    /**
     * Constructor method, if caller wants to create a new Socket with
     * host ip adress and port. This method creates an instance and arranges
     * streams with that.
     */
    public Client(String host, int port) {
        ObjectOutputStream tempOS = null;
        ObjectInputStream tempIS = null;
        Socket tempSocket = null;
        try{
            tempSocket = new Socket(host, port);
            tempOS = new ObjectOutputStream(tempSocket.getOutputStream());
            tempIS = new ObjectInputStream(tempSocket.getInputStream());
        }catch(IOException ex) {
            LOGGER.error("Socket could'nt create for given host {} and port {}", host, port);

        }
        socket = tempSocket;
        os = tempOS;
        is = tempIS;

    }

    /**
     * Constructor method, if caller wants to register a Socket which is
     * already created. This method connects Socket and the Client.
     */
    public Client(Socket socket) {
        this.socket = socket;
            
        /** src : stackoverflow.com/a/25710246/5929406 */
        ObjectOutputStream tempOS = null;
        ObjectInputStream tempIS = null;
        try{
            tempOS = new ObjectOutputStream(socket.getOutputStream());
            tempIS = new ObjectInputStream(socket.getInputStream());
        }catch(IOException ex) {
            LOGGER.error("Client {}:{} stream could not be created", 
                                            socket.getInetAddress(), socket.getPort() );
        }

        os = tempOS;
        is = tempIS;
    }


    public void registerObserver(ClientObserver observer) {
        this.observer = observer;
    }

    /**
     * While thread is working, do thing is run() method. As long as Socket
     * isConnected, receives Message's from Socket.
     */
    @Override
    public void run() {
        /** run until before calling stop() and client is connected  */
        while(run && socket.isConnected()) {
            try{
                byte[] JSON = (byte[]) is.readObject();
                Message message = MessageParser.read(JSON);
                if(observer != null) observer.update(message);
                 
                Thread.sleep(1000);
            }catch(IOException ex) {
                LOGGER.error("Message could not read from stream in {}", socket.getInetAddress());
                stop();
                try {
					socket.close();
				} catch (IOException e) {
                    LOGGER.error("Error occured while closing socket");

				}
                } catch (ClassNotFoundException e) {
                    // you will always found byte[] object.
                } catch (InterruptedException e) {
                    LOGGER.fatal("Error occured while stopping thread.");
                    stop();
                }
        }
    }
    
    /**
     * This method sends a message to Socket with related with this Client.
     * 
     * @param message which will be sended. 
     */
    public void send(Message message) {
        if(socket.isConnected()) {
            try{
                os.writeObject(MessageParser.write(message));
            }catch(IOException ex) {
                LOGGER.error("Message could not send into {}", socket.getInetAddress());
            }
        }
    }

}