package CBoss.jobService;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class CBossJobResult {

    private String message;
    private JsonElement jsonMessage;

    public CBossJobResult() {

    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setJsonMessage(JsonElement json) {
        this.jsonMessage = json;
    }

    public void setJsonMessage(String jsonStr) {
        Gson gson = new Gson();
        try {
            this.jsonMessage = gson.fromJson(jsonStr, JsonElement.class);
        } catch (Exception e) {
            this.message = jsonStr;

        }
    }

    public String getMessage() {
        return this.message;
    }

    public JsonElement getJsonMessage() {
        return this.jsonMessage;
    }
}

