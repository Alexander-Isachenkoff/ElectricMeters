package electricMeters.repository;

import electricMeters.core.DbHandler;
import org.json.JSONObject;

import java.util.List;

public class VerificationRepository {
    
    private final DbHandler db = DbHandler.getInstance();
    
    public void saveOrUpdate(JSONObject verification, List<Integer> metersId) {
        int id;
        if (verification.has("ID")) {
            id = verification.getInt("ID");
            db.update(verification, "VERIFICATIONS");
        } else {
            id = db.insert(verification, "VERIFICATIONS");
        }
        
        deleteVerificationMeters(id);
        
        List<JSONObject> meters = metersId.stream()
                .map(meterId -> new JSONObject()
                        .put("METER_ID", meterId)
                        .put("VERIFICATION_ID", id))
                .toList();
        db.insertList(meters, "VERIFICATION_METERS");
    }
    
    public void deleteVerificationMeters(int verificationId) {
        List<JSONObject> toDelete = db.runSqlSelectFile("VERIFICATION_METERS_VW.sql", verificationId);
        db.deleteList(toDelete, "VERIFICATION_METERS");
    }
    
}
