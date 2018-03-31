package com.bilalekrem.ruddergame.net;

import com.bilalekrem.ruddergame.net.Server.ClientListener;

class RudderGameMatchmaking extends Matchmaking{

    RudderGameMatchmaking(Server server) {
        super(server);
    }

	@Override
	int numberOfRequiredPlayers() {
		return 2;
	}

	@Override
	GameSession generateSession(ClientListener... clients) {
        // TODO AFTER session
        return null;
	}

}