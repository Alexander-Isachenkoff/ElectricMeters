package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.json.JSONObject;

public class MetersController {
    
    @FXML
    private JsonTable table;

    @FXML
    private void initialize() {
        table.setOnDoubleClick(this::showEditForm);
        table.reload();
    }
    
    @FXML
    private void onAdd() {
        MeterEditForm.showCreate(table, table.getScene().getWindow());
    }
    
    @FXML
    private void onEdit() {
        JSONObject selectedMeter = table.getSelectedItem();
        if (selectedMeter != null) {
            showEditForm(selectedMeter);
        }
    }
    
    private void showEditForm(JSONObject selectedMeter) {
        MeterEditForm.showEdit(selectedMeter, table, table.getScene().getWindow());
    }

    public void onDelete() {
        table.deleteSelectedItemsWithConfirmation("REF_METERS");
    }

}
