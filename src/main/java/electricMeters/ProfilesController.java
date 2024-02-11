package electricMeters;

import electricMeters.controls.JsonTable;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class ProfilesController {
    
    @FXML
    private JsonTable table;
    @FXML
    private JsonTable detailsTable;
    
    @FXML
    private void initialize() {
        table.setSqlFile("ProfileView.sql");
        detailsTable.setSqlFile("ProfilesEM.sql");
        
        table.addSelectedListener(newValue -> {
            if (newValue != null) {
                int id = newValue.getInt("id");
                detailsTable.setParams(id);
                detailsTable.reload();
            } else {
                detailsTable.clear();
            }
        });
        
        table.reload();
    }
    
    @FXML
    private void onProfileAdd() {
        List<File> files = new FileChooser().showOpenMultipleDialog(table.getScene().getWindow());
        for (File file : files) {
            JSONObject json = ProfileParser.readDataFromFile(file);
            DbHandler.getInstance().insert(json, "ProfileEMInfo");
            table.reload();
        }
    }
    
    @FXML
    private void onProfileDelete() {
        JSONObject item = table.getSelectedItem();
        if (item != null) {
            if (UtilAlert.showDeleteConfirmation()) {
                DbHandler.getInstance().delete(item.getInt("id"), "ProfileEMInfo");
                table.reload();
            }
        }
    }
    
}
