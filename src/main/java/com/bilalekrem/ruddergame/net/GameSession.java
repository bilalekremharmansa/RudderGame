package com.bilalekrem.ruddergame.net;

import com.bilalekrem.ruddergame.net.Server.ClientListener;
import com.bilalekrem.ruddergame.net.Message;
import com.bilalekrem.ruddergame.game.Game;
import com.bilalekrem.ruddergame.game.Player;

import java.util.Map;

public abstract class GameSession {
    Map<Player, ClientListener> clients;
    Game game;

    abstract void informPlayer(Player player, Message message);
}