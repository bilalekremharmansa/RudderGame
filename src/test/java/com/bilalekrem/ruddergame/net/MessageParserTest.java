package com.bilalekrem.ruddergame.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.bilalekrem.ruddergame.game.Game.Move;
import com.bilalekrem.ruddergame.net.Message.MessageType;
import com.bilalekrem.ruddergame.util.Location;
import com.bilalekrem.ruddergame.util.RudderGameLocation;
import com.bilalekrem.ruddergame.util.Segment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageParserTest {
    
    private static final Logger LOGGER = LogManager.getLogger(MessageParserTest.class);

    static ObjectMapper mapper;
    @Before
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Ignore
    public void testParserWrite(){
        try {
            String test = "hey";
            Message message = new Message(15, MessageType.DISCONNECT, test);
            byte[] parsed = MessageParser.write(message);
            String JSON = "{\"sender\":15,\"type\":\"DISCONNECT\",\"content\":\"hey\"}";
            String parsedStr = new String(parsed);
     
            assertEquals(parsedStr, JSON);
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}


    }
    @Ignore
    public void testParserRead(){
       
        try {   
            Message message = new Message(15, MessageType.DISCONNECT, "hey");
            String JSON = "{\"sender\":15,\"type\":\"DISCONNECT\",\"content\":\"hey\"}";
            byte[] byteJSON = JSON.getBytes();
            Message message2 = MessageParser.read(byteJSON);
    
            assertEquals(message, message2);
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}


    }

    @Ignore
    public void testParserLocation(){
       
        try {
            Location location = new RudderGameLocation(Segment.A, 1);
            Move move = new Move().doer(15).from(location);

            Message message = new Message(15, MessageType.MOVE, move);
            String JSON = new String(MessageParser.write(message));
            byte[] byteJSON = JSON.getBytes();

            Message message2 = MessageParser.read(byteJSON);
            Location loc2 = message2.content(Move.class).from;

            boolean isInstance = loc2 instanceof RudderGameLocation;
            assertEquals(true, isInstance);
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}


    }

   
}