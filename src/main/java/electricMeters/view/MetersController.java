package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.json.JSONObject;

import java.util.List;

public class MetersController {
    
    @FXML
    private JsonTable table;

    @FXML
    private void initialize() {
        table.setOnDoubleClick(this::showEditForm);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem verificationMenuItem = new MenuItem("Поверка");
        verificationMenuItem.setOnAction(actionEvent -> {
            List<JSONObject> selectedItems = table.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                List<Integer> metersId = selectedItems.stream()
                        .mapToInt(meter -> meter.getInt("ID"))
                        .boxed()
                        .toList();
                VerificationEdit form = VerificationEdit.build(table.getScene().getWindow());
                form.showCreate(metersId, table);
            }
        });
        contextMenu.getItems().add(verificationMenuItem);
        table.setContextMenu(contextMenu);
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

    @FXML
    private void onDelete() {
        table.deleteSelectedItemsWithConfirmation("REF_METERS");
    }

}
