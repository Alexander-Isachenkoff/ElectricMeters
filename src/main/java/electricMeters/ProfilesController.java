package electricMeters;

import electricMeters.controls.JsonTable;
import javafx.fxml.FXML;

public class ProfilesController {
    
    @FXML
    private JsonTable table;
    @FXML
    private JsonTable detailsTable;
    
    @FXML
    private void initialize() {
        table.setSqlFile("ProfileView.sql");
        detailsTable.setSqlFile("ProfilesEM.sql");
        
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
    
}
