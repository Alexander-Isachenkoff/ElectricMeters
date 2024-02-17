package electricMeters;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;

public class MetersController {
    @FXML
    private JsonTable table;

    @FXML
    private void initialize() {
        table.setSqlFile("Meters.sql");
        table.reload();
    }
}
