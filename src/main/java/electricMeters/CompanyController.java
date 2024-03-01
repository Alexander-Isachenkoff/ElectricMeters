package electricMeters;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class CompanyController {

    private static final String COMPANY_JSON_FILE = "company.json";

    @FXML
    private TextField nameField;
    @FXML
    private TextField contractNumField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField1;
    @FXML
    private TextField phoneField2;
    @FXML
    private TextField responsiblePersonField;
    @FXML
    private TextField positionField;

    @FXML
    private void initialize() throws IOException {
        loadData();
    }

    private void loadData() throws IOException {
        Path path = Paths.get(COMPANY_JSON_FILE);
        if (Files.notExists(path)) {
            onSave();
        }
        JSONObject jsonObject = new JSONObject(String.join("\n", Files.readAllLines(path)));
        nameField.setText(jsonObject.getString("CONSUMER_NAME"));
        contractNumField.setText(jsonObject.getString("CONTRACT_NUMBER"));
        addressField.setText(jsonObject.getString("CONSUMER_ADDRESS"));
        phoneField1.setText(jsonObject.getString("CONSUMER_PHONE_NUMBER_1"));
        phoneField2.setText(jsonObject.getString("CONSUMER_PHONE_NUMBER_2"));
        responsiblePersonField.setText(jsonObject.getString("RESPONSIBLE_PERSON"));
        positionField.setText(jsonObject.getString("POSITION"));
    }

    @FXML
    private void onSave() throws IOException {
        JSONObject jsonObject = createJsonObject();
        Files.write(Paths.get(COMPANY_JSON_FILE), Collections.singletonList(jsonObject.toString(2)));
        loadData();
    }

    private JSONObject createJsonObject() {
        return new JSONObject()
                .put("CONSUMER_NAME", nameField.getText())
                .put("CONTRACT_NUMBER", contractNumField.getText())
                .put("CONSUMER_ADDRESS", addressField.getText())
                .put("CONSUMER_PHONE_NUMBER_1", phoneField1.getText())
                .put("CONSUMER_PHONE_NUMBER_2", phoneField2.getText())
                .put("RESPONSIBLE_PERSON", responsiblePersonField.getText())
                .put("POSITION", positionField.getText());
    }

}
