package electricMeters.view;

import electricMeters.Main;
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

public class MeterEditForm {
    
    private final Stage stage = new Stage();
    
    @FXML private TextField locationTF;
    @FXML private TextField locationNameTF;
    @FXML private TextField numberTF;
    @FXML private TextField dataTransDeviceTF;
    @FXML private TextField dataTransDeviceAddressTF;
    @FXML private TextField ipAddressTF;
    @FXML private TextField comPortTF;
    @FXML private TextField yearTF;
    @FXML private CheckBox isTechnicalChb;
    @FXML private CheckBox isCommercialChb;
    @FXML private CheckBox isPaidChb;
    
    @FXML
    private void initialize() {
        stage.setTitle("Редактирование прибора учета");
        stage.initModality(Modality.WINDOW_MODAL);
    }
    
    static void showCreate(JsonTable tableToReload, Window owner) {
        MeterEditForm form = loadForm(owner);
        form.stage.show();
    }
    
    static void showEdit(JSONObject meterJson, JsonTable tableToReload, Window owner) {
        MeterEditForm form = loadForm(owner);
        form.fillData(meterJson);
        form.stage.show();
    }
    
    private static MeterEditForm loadForm(Window owner) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/meter-edit.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MeterEditForm controller = loader.getController();
        controller.stage.setScene(new Scene(root));
        controller.stage.initOwner(owner);
        return controller;
    }
    
    private void fillData(JSONObject meterJson) {
    
    }
    
}
