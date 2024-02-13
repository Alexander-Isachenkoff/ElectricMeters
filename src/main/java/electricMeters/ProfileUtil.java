package electricMeters;

import electricMeters.core.DbHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class ProfileUtil {
    
    static void readAndSave(File file) {
        JSONObject json = ProfileParser.readDataFromFile(file);
        saveProfile(json);
    }
    
    static void saveProfile(JSONObject json) {
        JSONArray childs = (JSONArray) json.remove("childs");
        int id = DbHandler.getInstance().insert(json, "ProfileEMInfo");
        for (Object child : childs) {
            ((JSONObject) child).put("profileEMInfo", id);
        }
        DbHandler.getInstance().insertList(childs, "ProfilesEM");
    }
    
}
