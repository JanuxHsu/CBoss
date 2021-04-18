package CBoss.netapp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class APIResponse {

    private final int status_code;
    private final String content;
    private final JsonElement content_json;

    public APIResponse(int status_code, String content) {
        this.status_code = status_code;
        this.content = content;
        this.content_json = this.parseToJson(content);
    }

    public JsonElement getContent_json() {
        return this.content_json;
    }

    private JsonElement parseToJson(String raw_input) {
        try {
            return new Gson().fromJson(raw_input, JsonElement.class);
        } catch (Exception e) {
            JsonObject fallbackJson = new JsonObject();
            fallbackJson.addProperty("raw", raw_input);
            return fallbackJson;
        }
    }


    public String to_json() {
        JsonObject res = new JsonObject();
        res.addProperty("status_code", this.status_code);
        res.add("content", this.content_json);
        return res.toString();
    }

}
