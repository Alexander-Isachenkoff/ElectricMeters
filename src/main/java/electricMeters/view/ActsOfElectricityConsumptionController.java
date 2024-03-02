package electricMeters.view;

import electricMeters.YearMonthInputForm;
import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import org.json.JSONObject;

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
    private void onAddAct() {
        YearMonthInputForm form = YearMonthInputForm.instance("Создание акта расхода электроэнергии");
        ButtonType result = form.showAndWait();
        if (result == ButtonType.OK) {
            ActOfConsumptionEditController.show(form.getYear(), form.getMonth().getValue());
        }
    }

    @FXML
    private void onEditAct() {
        JSONObject item = actsTable.getSelectedItem();
        ActOfConsumptionEditController.show(item.getInt("YEAR"), item.getInt("MONTH"));
    }

    @FXML
    private void onDeleteAct() {
        //actsTable.deleteSelectedItemsWithConfirmation("REG_ACTS_CONSUMPTION");
    }

}
