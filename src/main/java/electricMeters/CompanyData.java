package electricMeters;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class CompanyData {

    private static final String COMPANY_JSON_FILE = "company.json";

    public static JSONObject getCompanyData() {
        Path path = Paths.get(COMPANY_JSON_FILE);
        if (Files.notExists(path)) {
            saveCompanyData(createEmptyCompanyData());
        }
        try {
            return new JSONObject(String.join("\n", Files.readAllLines(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCompanyData(JSONObject jsonObject) {
        try {
            Files.write(Paths.get(COMPANY_JSON_FILE), Collections.singletonList(jsonObject.toString(2)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONObject createEmptyCompanyData() {
        return new JSONObject()
                .put("CONSUMER_NAME", "")
                .put("CONTRACT_NUMBER", "")
                .put("CONSUMER_ADDRESS", "")
                .put("CONSUMER_PHONE_NUMBER_1", "")
                .put("CONSUMER_PHONE_NUMBER_2", "")
                .put("RESPONSIBLE_PERSON", "")
                .put("POSITION", "")
                .put("POWER_RATE_TYPE_ID", 0)
                .put("VOLTAGE_LEVEL_ID", 0);
    }

}
