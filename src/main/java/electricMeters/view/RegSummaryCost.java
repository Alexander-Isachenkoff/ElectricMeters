package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import org.json.JSONObject;

public class RegSummaryCost {

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
        SummaryCostForm.show(item.getInt("YEAR"), item.getInt("MONTH"), table.getScene().getWindow());
    }

}
