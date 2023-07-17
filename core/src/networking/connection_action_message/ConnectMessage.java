package networking.connection_action_message;

import networking.ConnectionActionMessage;
import org.json.JSONObject;

public class ConnectMessage extends ConnectionActionMessage {
    public ConnectMessage(String username) {
        super();
        header.put("connectionAction", "CONNECT");

        JSONObject userInfo = new JSONObject();
        userInfo.put("username", username);
        body.put("userInfo", userInfo);
    }
}
