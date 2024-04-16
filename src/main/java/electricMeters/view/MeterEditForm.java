package electricMeters.view;

import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.core.controls.FormCollector;
import electricMeters.core.controls.JTextField;
import electricMeters.core.controls.JsonField;
import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONObject;

import java.io.IOException;

public class MeterEditForm implements FormCollector {
    
    private final Stage stage = new Stage();
    
    @FXML
    @JsonField(field = "LOCATION")
    private JTextField locationTF;
    @FXML
    @JsonField(field = "LOCATION_NAME")
    private TextField locationNameTF;
    @FXML
    @JsonField(field = "METER_NUMBER")
    private JTextField numberTF;
    @FXML
    @JsonField(field = "DATA_TRANS_DEVICE")
    private TextField dataTransDeviceTF;
    @FXML
    @JsonField(field = "DATA_TRANS_DEVICE_ADDRESS")
    private TextField dataTransDeviceAddressTF;
    @FXML
    @JsonField(field = "IP_ADDRESS")
    private TextField ipAddressTF;
    @FXML
    @JsonField(field = "COM_PORT")
    private TextField comPortTF;
    @FXML
    @JsonField(field = "YEAR_OF_MANUFACTURE")
    private TextField yearTF;
    @FXML
    @JsonField(field = "IS_TECHNICAL")
    private CheckBox isTechnicalChb;
    @FXML
    @JsonField(field = "IS_COMMERCIAL")
    private CheckBox isCommercialChb;
    @FXML
    @JsonField(field = "IS_PAID")
    private CheckBox isPaidChb;
    
    private JSONObject initialJson;
    private JsonTable tableToReload;
    
    static void showCreate(JsonTable tableToReload, Window owner) {
        MeterEditForm form = loadForm(tableToReload, owner);
        form.stage.show();
    }
    
    static void showEdit(JSONObject meterJson, JsonTable tableToReload, Window owner) {
        MeterEditForm form = loadForm(tableToReload, owner);
        form.fillData(meterJson);
        form.stage.show();
    }
    
    private static MeterEditForm loadForm(JsonTable tableToReload, Window owner) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/meter-edit.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MeterEditForm controller = loader.getController();
        controller.tableToReload = tableToReload;
        controller.stage.setScene(new Scene(root));
        controller.stage.initOwner(owner);
        return controller;
    }
    
    @FXML
    private void initialize() {
        stage.setTitle("Редактирование прибора учета");
        stage.initModality(Modality.WINDOW_MODAL);
    }
    
    @Override
    public void fillData(JSONObject meterJson) {
        FormCollector.super.fillData(meterJson);
        this.initialJson = meterJson;
    }
    
    @FXML
    private void onSave() {
        if (!checkRequired()) {
            return;
        }
        JSONObject json = collectData();
        if (initialJson == null) {
            int id = DbHandler.getInstance().insert(json, "REF_METERS");
            tableToReload.reloadFocused(id);
        } else {
            json.put("ID", initialJson.get("ID"));
            DbHandler.getInstance().update(json, "REF_METERS");
            tableToReload.reloadFocused();
        }
        stage.close();
    }
    
}
