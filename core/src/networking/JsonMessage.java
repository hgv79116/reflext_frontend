package networking;

import org.json.JSONObject;

public class JsonMessage {
    protected JSONObject header;
    protected JSONObject body;

    public JsonMessage() {
        header = new JSONObject();
        body = new JSONObject();
    }

    public JsonMessage(String message) {
        JSONObject content = new JSONObject(message);
        header = content.getJSONObject("header");
        body = content.getJSONObject("body");
    }

    public JSONObject getHeader() {
        return header;
    }

    public JSONObject getBody() {
        return body;
    }

    @Override
    public String toString() {
        JSONObject content = new JSONObject();
        content.put("header", header);
        content.put("body", body);
        return content.toString();
    }
}
