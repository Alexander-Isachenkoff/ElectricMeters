package electricMeters.view;

import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.core.controls.FormCollector;
import electricMeters.core.controls.JsonDatePicker;
import electricMeters.core.controls.JsonTable;
import electricMeters.repository.VerificationRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class VerificationEdit implements FormCollector {
    
    private final Stage stage = new Stage();
    private final DbHandler db = DbHandler.getInstance();
    private final VerificationRepository verificationRepository = new VerificationRepository();
    
    @FXML private JsonDatePicker datePicker;
    @FXML private JsonTable metersTable;
    
    private JsonTable tableToReload;
    private int id;
    
    public VerificationEdit() {
        stage.setTitle("Поверка приборов учета");
        stage.initModality(Modality.WINDOW_MODAL);
    }
    
    static VerificationEdit build(Window owner) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/verification-edit.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VerificationEdit form = loader.getController();
        form.stage.setScene(new Scene(root));
        form.stage.initOwner(owner);
        return form;
    }
    
    void showCreate(List<Integer> metersId, JsonTable tableToReload) {
        this.tableToReload = tableToReload;
        List<JSONObject> meters = db.selectAllById("REF_METERS", metersId);
        for (JSONObject meter : meters) {
            meter.put("METER_ID", meter.getInt("ID"));
            meter.remove("ID");
        }
        metersTable.setData(meters);
        datePicker.setValue(LocalDate.now());
        stage.show();
    }
    
    void showEdit(int id, JsonTable tableToReload) {
        this.id = id;
        this.tableToReload = tableToReload;
        JSONObject verification = db.selectById("VERIFICATIONS", id);
        this.fillData(verification);
        metersTable.setParams(id);
        metersTable.reload();
        stage.show();
    }
    
    @FXML
    private void onSave() {
        if (!this.checkRequired()) {
            return;
        }
        JSONObject verification = this.collectData();

        if (id != 0) {
            verification.put("ID", id);
        }
        
        List<Integer> metersId = metersTable.getAllItems().stream()
                .mapToInt(meter -> meter.getInt("METER_ID"))
                .boxed()
                .toList();
        verificationRepository.saveOrUpdate(verification, metersId);
        
        if (tableToReload != null) {
            tableToReload.reload();
        }
        
        stage.close();
    }
    
}
