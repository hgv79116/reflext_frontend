package networking;

public class ConnectionActionMessage extends JsonMessage {
    public ConnectionActionMessage() {
        super();
        header.put("clientMessageCategory", "CONNECTION_ACTION");
    }
}
