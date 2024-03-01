package electricMeters;

import electricMeters.core.controls.JsonComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.json.JSONObject;

public class CompanyController {

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
    private JsonComboBox rateTypeCmb;
    @FXML
    private JsonComboBox voltageLevelCmb;

    @FXML
    private void initialize() {
        rateTypeCmb.reload();
        voltageLevelCmb.reload();
        loadData();
    }

    private void loadData() {
        JSONObject companyData = CompanyData.getCompanyData();
        nameField.setText(companyData.getString("CONSUMER_NAME"));
        contractNumField.setText(companyData.getString("CONTRACT_NUMBER"));
        addressField.setText(companyData.getString("CONSUMER_ADDRESS"));
        phoneField1.setText(companyData.getString("CONSUMER_PHONE_NUMBER_1"));
        phoneField2.setText(companyData.getString("CONSUMER_PHONE_NUMBER_2"));
        responsiblePersonField.setText(companyData.getString("RESPONSIBLE_PERSON"));
        positionField.setText(companyData.getString("POSITION"));
        rateTypeCmb.selectValueById(companyData.optInt("RATE_TYPE_ID"));
        voltageLevelCmb.selectValueById(companyData.optInt("VOLTAGE_LEVEL_ID"));
    }

    @FXML
    private void onSave() {
        JSONObject jsonObject = createJsonObject();
        CompanyData.saveCompanyData(jsonObject);
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
                .put("POSITION", positionField.getText())
                .put("RATE_TYPE_ID", rateTypeCmb.getSelectedId())
                .put("VOLTAGE_LEVEL_ID", voltageLevelCmb.getSelectedId());
    }

}
