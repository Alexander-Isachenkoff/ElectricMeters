package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.json.JSONObject;

import java.util.List;

public class MetersController {
    
    @FXML private JsonTable metersTable;
    @FXML private JsonTable verificationsTable;
    
    @FXML
    private void initialize() {
        metersTable.addSelectedListener(newValue -> {
            if (newValue != null) {
                int id = newValue.getInt("ID");
                verificationsTable.setParams(id);
                verificationsTable.reload();
            } else {
                verificationsTable.setParams(-1);
                verificationsTable.clear();
            }
        });
        metersTable.setOnDoubleClick(this::showEditForm);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem verificationMenuItem = new MenuItem("Поверка");
        verificationMenuItem.setOnAction(actionEvent -> {
            List<JSONObject> selectedItems = metersTable.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                List<Integer> metersId = selectedItems.stream()
                        .mapToInt(meter -> meter.getInt("ID"))
                        .boxed()
                        .toList();
                VerificationEdit form = VerificationEdit.build(metersTable.getScene().getWindow());
                form.showCreate(metersId, metersTable);
            }
        });
        contextMenu.getItems().add(verificationMenuItem);
        metersTable.setContextMenu(contextMenu);
        
        verificationsTable.setOnDoubleClick(this::showEditVerification);
        
        metersTable.reload();
    }
    
    @FXML
    private void onAdd() {
        MeterEditForm.showCreate(metersTable, metersTable.getScene().getWindow());
    }
    
    @FXML
    private void onEdit() {
        JSONObject selectedMeter = metersTable.getSelectedItem();
        if (selectedMeter != null) {
            showEditForm(selectedMeter);
        }
    }
    
    private void showEditForm(JSONObject selectedMeter) {
        MeterEditForm.showEdit(selectedMeter, metersTable, metersTable.getScene().getWindow());
    }

    @FXML
    private void onDelete() {
        metersTable.deleteSelectedItemsWithConfirmation("REF_METERS");
    }
    
    @FXML
    private void onVerificationEdit() {
        JSONObject verification = verificationsTable.getSelectedItem();
        if (verification != null) {
            showEditVerification(verification);
        }
    }
    
    private void showEditVerification(JSONObject verification) {
        VerificationEdit form = VerificationEdit.build(verificationsTable.getScene().getWindow());
        form.showEdit(verification.getInt("ID"), metersTable);
    }
    
    @FXML
    private void onVerificationDelete() {
        verificationsTable.deleteSelectedItemsWithConfirmation("VERIFICATIONS");
    }
    
}
