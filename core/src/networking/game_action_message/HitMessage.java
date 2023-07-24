package networking.game_action_message;

import networking.JsonMessage;

public class HitMessage extends GameActionMessage {
    public HitMessage(int playerId, int circleId) {
        super();
        this.header.put("gameAction", "HIT");
        this.body.put("circleId", circleId);
        this.body.put("playerId", playerId);
    }
}
