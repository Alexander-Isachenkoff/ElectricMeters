package electricMeters.view;

import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.core.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONObject;

import java.io.IOException;

public class MeterEditForm implements FormCollector {
    
    private final Stage stage = new Stage();
    
    @FXML private JTextField locationTF;
    @FXML private JTextField locationNameTF;
    @FXML private JTextField numberTF;
    @FXML private JTextField dataTransDeviceTF;
    @FXML private JTextField dataTransDeviceAddressTF;
    @FXML private JTextField ipAddressTF;
    @FXML private JTextField comPortTF;
    @FXML private JTextField yearTF;
    @FXML private JTextField calcCoefficientTF;
    @FXML private JCheckBox isTechnicalChb;
    @FXML private JCheckBox isCommercialChb;
    @FXML private JCheckBox isPaidChb;
    
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
    public void fillData(JSONObject json) {
        FormCollector.super.fillData(json);
        this.initialJson = json;
    }
    
    @FXML
    private void onSave() {
        if (!checkRequired()) {
            return;
        }
        JSONObject json = collectData();
        if (initialJson == null) {
            int id = DbHandler.getInstance().insert(json, "REF_METERS");
            tableToReload.reloadAndSelect(id);
        } else {
            json.put("ID", initialJson.get("ID"));
            DbHandler.getInstance().update(json, "REF_METERS");
            tableToReload.reload();
        }
        stage.close();
    }
    
}
