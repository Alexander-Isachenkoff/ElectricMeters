package electricMeters.core.controls;

import org.json.JSONObject;

interface JsonField<T> {

    String getKey();

    void setValue(JSONObject json);

    T getValue();

}
