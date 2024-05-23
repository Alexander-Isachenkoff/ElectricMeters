package electricMeters.view;

import electricMeters.core.controls.DatePickerPlus;
import electricMeters.core.controls.JsonTable;
import electricMeters.util.DateUtil;
import javafx.fxml.FXML;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Optional;

public class Verifications {
    
    @FXML private DatePickerPlus datePickerFrom;
    @FXML private DatePickerPlus datePickerTo;
    @FXML private JsonTable mainTable;
    @FXML private JsonTable detailsTable;
    
    @FXML
    private void initialize() {
        datePickerFrom.setValue(LocalDate.now().minusYears(1));
        datePickerTo.setValue(LocalDate.now());
        
        mainTable.setOnDoubleClick(this::showEditForm);
        mainTable.addSelectedListener(newValue -> {
            if (newValue != null) {
                int id = newValue.getInt("ID");
                detailsTable.setParams(id);
                detailsTable.reload();
            } else {
                detailsTable.setParams(-1);
                detailsTable.clear();
            }
        });
        
        onApply();
    }
    
    @FXML
    private void onApply() {
        String dateFrom = Optional.ofNullable(datePickerFrom.getValue())
                .map(DateUtil.DB_DATE_FORMAT::format)
                .orElse(null);
        String dateTo = Optional.ofNullable(datePickerTo.getValue())
                .map(DateUtil.DB_DATE_FORMAT::format)
                .orElse(null);
        mainTable.setParams(dateFrom, dateTo);
        mainTable.reload();
    }
    
    @FXML
    private void onEdit() {
        JSONObject selectedItem = mainTable.getSelectedItem();
        if (selectedItem != null) {
            showEditForm(selectedItem);
        }
    }
    
    private void showEditForm(JSONObject item) {
        VerificationEdit form = VerificationEdit.build(mainTable.getScene().getWindow());
        form.showEdit(item.getInt("ID"), mainTable);
    }
    
    @FXML
    private void onDelete() {
        mainTable.deleteSelectedItemsWithConfirmation("VERIFICATIONS");
    }
    
}
