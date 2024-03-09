package electricMeters.view;

import electricMeters.CompanyData;
import electricMeters.core.controls.JsonComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    private Label stateLabel;

    @FXML
    private void initialize() {
        stateLabel.setText("");
        rateTypeCmb.reload();
        voltageLevelCmb.reload();
        loadData();
    }

    private void loadData() {
        CompanyData companyData = CompanyData.getCompanyData();
        nameField.setText(companyData.getConsumerName());
        contractNumField.setText(companyData.getContractNumber());
        addressField.setText(companyData.getConsumerAddress());
        phoneField1.setText(companyData.getConsumerPhoneNumber1());
        phoneField2.setText(companyData.getConsumerPhoneNumber2());
        responsiblePersonField.setText(companyData.getResponsiblePerson());
        positionField.setText(companyData.getPosition());
        rateTypeCmb.selectValueById(companyData.getRateTypeID());
        voltageLevelCmb.selectValueById(companyData.getVoltageLevelID());
        
        nameField.textProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        contractNumField.textProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        addressField.textProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        phoneField1.textProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        phoneField2.textProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        responsiblePersonField.textProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        positionField.textProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        rateTypeCmb.valueProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
        voltageLevelCmb.valueProperty().addListener((observableValue, s, t1) -> setStateNotSaved());
    }
    
    private void setStateNotSaved() {
        stateLabel.setText("Изменения не сохранены");
        stateLabel.getStyleClass().remove("text-success");
        stateLabel.getStyleClass().add("text-error");
    }
    
    private void setStateSaved() {
        stateLabel.setText("Изменения сохранены");
        stateLabel.getStyleClass().remove("text-error");
        stateLabel.getStyleClass().add("text-success");
    }

    @FXML
    private void onSave() {
        CompanyData companyData = createCompanyData();
        companyData.save();
        setStateSaved();
        loadData();
    }

    private CompanyData createCompanyData() {
        return new CompanyData(
                nameField.getText(),
                contractNumField.getText(),
                addressField.getText(),
                phoneField1.getText(),
                phoneField2.getText(),
                responsiblePersonField.getText(),
                positionField.getText(),
                rateTypeCmb.getSelectedId(),
                voltageLevelCmb.getSelectedId()
        );
    }

}
