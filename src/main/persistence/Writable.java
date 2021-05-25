package persistence;

import org.json.JSONObject;

public interface Writable {
    // EFFECT: returns this as json object
    JSONObject toJson();
}
