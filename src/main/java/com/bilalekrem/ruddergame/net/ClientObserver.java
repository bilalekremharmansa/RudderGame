package com.bilalekrem.ruddergame.net;

/**
 * ClientObserver gives an interface to notify client that
 * changing. If object changed, its requiring to notify other clients.
 * 
 * @author Bilal Ekrem Harmansa
 */
public interface ClientObserver {

    /**
     * When an object state change, object will call this method. With this
     * way Observer will be notified.
     * 
     * @param message content of changing.
     */
    public void update(Message message);
} 
