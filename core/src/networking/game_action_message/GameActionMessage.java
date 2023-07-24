package networking.game_action_message;

import networking.JsonMessage;
import org.json.JSONObject;

public class GameActionMessage extends JsonMessage {
    public GameActionMessage() {
        super();
        this.header.put("clientMessageCategory", "GAME_ACTION");
    }
}
