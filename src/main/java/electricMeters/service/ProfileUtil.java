package electricMeters.service;

import electricMeters.core.DbHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class ProfileUtil {
    
    public static void readAndSave(File file) {
        JSONObject json = ProfileParser.readDataFromFile(file);
        saveProfile(json);
    }
    
    private static void saveProfile(JSONObject json) {
        JSONArray childs = (JSONArray) json.remove("childs");
        int id = DbHandler.getInstance().insert(json, "PROFILES");
        for (Object child : childs) {
            ((JSONObject) child).put("profileEMInfo", id);
        }
        DbHandler.getInstance().insertList(childs, "PROFILE_STRS");
    }
    
}
