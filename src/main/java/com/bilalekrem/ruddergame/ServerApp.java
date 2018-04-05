package com.bilalekrem.ruddergame;

import com.bilalekrem.ruddergame.game.Game.GameType;
import com.bilalekrem.ruddergame.net.Server;

public class ServerApp {
    public static final int PORT = 15123;
    public static void main(String[] args) {
        try{
            Server.getInstance().build(PORT).welcome(GameType.RUDDER);
        }catch(Exception ex) {
            System.out.println(ex);
        }
    }
}
