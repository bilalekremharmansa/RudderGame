package com.bilalekrem.ruddergame.util;

import java.io.IOException;

import com.bilalekrem.ruddergame.game.Player;
import com.bilalekrem.ruddergame.game.Game.Move;
import com.bilalekrem.ruddergame.net.Message;
import com.bilalekrem.ruddergame.net.Message.MessageType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

/**
 * While working with JSON. I faced up with a problem, Message class has 
 * a field as Object type. This field can easily serializable but 
 * deserializable is not that easy. While deserializabling, Jackson
 * does not know correct type for content object. I am doing it by 
 * manually in here.
 * 
 * References,
 * baeldung.com/jackson-deserialization
 * dzone.com/articles/custom-json-deserialization-with-jackson
 * 
 * @author Bilal Ekrem Harmansa
 * 
 */
public class MessageDeserializer extends StdDeserializer<Message> {

    private static final long serialVersionUID = 2344252363014104427L;

	public MessageDeserializer() {
        this(null);
    }

    public MessageDeserializer(Class<?> vc) {
        super(vc);
    }
    
    @Override
    public Message deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        int senderID = (Integer) ((IntNode) node.get("sender")).numberValue();
        MessageType messageType = MessageType.valueOf(node.get("type").textValue());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode content = node.get("content");
        Object obj;
        // TODO String text için bir şey yaz.
        if(messageType == MessageType.GREETING) {
            obj = mapper.readValue(content.toString(), Player.class);
        }else if (messageType == MessageType.PLAYERS) {
            obj = mapper.readValue(content.toString(), Player[].class);
        }else if (messageType == MessageType.MOVE) {
            obj = mapper.readValue(content.toString(), Move.class);
        }else {
            obj = null;
        }

        return new Message(senderID, messageType, obj);
    }
}