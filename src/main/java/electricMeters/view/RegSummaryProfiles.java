package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import org.json.JSONObject;

public class RegSummaryProfiles {

    @FXML
    private JsonTable table;

    @FXML
    private void initialize() {
        table.setOnDoubleClick(this::show);
        table.reload();
    }

    @FXML
    private void onShow() {
        JSONObject item = table.getSelectedItem();
        if (item != null) {
            show(item);
        }
    }

    private void show(JSONObject item) {
        SummaryProfileForm.show(item.getInt("YEAR"), item.getInt("MONTH"), table.getScene().getWindow());
    }

}
