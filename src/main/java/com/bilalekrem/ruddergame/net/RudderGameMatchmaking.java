package com.bilalekrem.ruddergame.net;


class RudderGameMatchmaking extends Matchmaking{

    RudderGameMatchmaking(Server server) {
        super(server);
    }

	@Override
	int numberOfRequiredPlayers() {
		return 2;
	}
}