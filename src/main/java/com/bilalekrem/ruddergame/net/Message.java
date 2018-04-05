package com.bilalekrem.ruddergame.net;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    public enum MessageType {
        /**
         * GREETING, If server sends a greeting, it sends Player
         * object that contains ID and pieceType.
         * If client sends a greeting, it sends name.
         * 
         * PLAYERS, informations about all players in the game.
         * 
         */
        CONNECT, GREETING, PLAYERS, MOVE,  DISCONNECT
    }

    private final int senderID;
    private final MessageType type;
    private final Object content;

    public Message(@JsonProperty("sender") int senderID, 
                    @JsonProperty("messageType") MessageType type, 
                    @JsonProperty("content") Object content) {
        this.senderID = senderID;
        this.type = type;
        this.content = content;
    }

    /**
     * @return the senderID
     */
    @JsonProperty("sender")
    public int senderID() {
        return senderID;
    }

    /**
     * @return the type
     */
    @JsonProperty("type")
    public MessageType type() {
        return type;
    }

    /**
     * src : stackoverflow.com/a/1555387/5929406
     * 
     * @return cast conten to given clazz type.
     */
    public <T> T content(Class<T> clazz) {
        return clazz.cast(content);
    }

    /**
     * Jackson library does need a get method to seriliaze
     * deseriliaze.
     */
    @JsonProperty("content")
    private Object content() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
       
        if ( !(o instanceof Message) ) return false;

        Message p = (Message)o;

        boolean contentEquality = false;
        if(content != null && p.content != null && content.equals(p.content)) {
            contentEquality = true;
        }else if (content == null && p.content == null)
            contentEquality = true;
        
        return (senderID == p.senderID && type == p.type && contentEquality);
    }

    @Override
    public String toString() {
        String clazz = content == null ? "NoContent" : content.getClass().getSimpleName();
        String typez = type == null ? "NotDefinedType" : type.toString();
        return typez + " - " + clazz + " from " + senderID;
    }


}