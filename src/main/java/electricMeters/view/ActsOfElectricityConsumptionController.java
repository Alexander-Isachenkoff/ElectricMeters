package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;

public class ActsOfElectricityConsumptionController {

    @FXML
    private JsonTable actsTable;
    @FXML
    private JsonTable actStringsTable;

    @FXML
    private void initialize() {
        actsTable.addSelectedListener(jsonObject -> {
            if (jsonObject != null) {
                actStringsTable.setParams(jsonObject.get("YEAR"), jsonObject.get("MONTH"));
            } else {
                actStringsTable.setParams(-1, -1);
            }
            actStringsTable.reload();
        });
        actsTable.reload();
    }

    @FXML
    private void onActDelete() {

    }

}
