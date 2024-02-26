package electricMeters;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;

public class MeterReadingsController {

    @FXML
    private JsonTable metersReadingsTable;

    @FXML
    private void initialize() {
        metersReadingsTable.reload();
    }

    @FXML
    private void onAdd() {
        MeterReadingsEditForm.instance().show(metersReadingsTable.getScene().getWindow());
    }

}
