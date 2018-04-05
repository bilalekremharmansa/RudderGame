package com.bilalekrem.ruddergame.net;

import java.io.IOException;

import com.bilalekrem.ruddergame.util.MessageDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Server and client are talking each other with JSON.
 * For example JSON sends a data in JSON format. Client
 * needs to convert it to Message object and same thing
 * for messaging client to server. This class maps 
 * JSON to object and Object to JSON.
 * 
 * @author Bilal Ekrem Harmansa
 */
class MessageParser {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Message.class, new MessageDeserializer());
        mapper.registerModule(module);

        mapper.setSerializationInclusion(Include.NON_NULL);
    }
    /**
     * Converts JSON format to Message object.
     * 
     * @param JSON data to be vonverted
     * 
     * @return Message object that converted from JSON data.
     */
    static Message read(byte[] JSON) throws IOException {
        return mapper.readValue(JSON, Message.class);
    }

    /**
     * Converts Message object to bytes in JSON format.
     * 
     * @param message to be converted
     * 
     * @return converted byte array in JSON format
     */
    static byte[] write(Message message) throws IOException {
        return mapper.writeValueAsBytes(message);
    }
}