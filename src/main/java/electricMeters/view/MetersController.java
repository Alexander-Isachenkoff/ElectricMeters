package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;

public class MetersController {
    @FXML
    private JsonTable table;

    @FXML
    private void initialize() {
        table.reload();
    }
}
