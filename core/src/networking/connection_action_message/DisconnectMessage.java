package networking.connection_action_message;

import networking.ConnectionActionMessage;
import org.json.JSONObject;

public class DisconnectMessage extends ConnectionActionMessage {
   public DisconnectMessage() {
       super();
       header.put("connectionAction", "DISCONNECT");
   }
}
