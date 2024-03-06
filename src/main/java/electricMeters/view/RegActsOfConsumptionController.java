package electricMeters.view;

import electricMeters.YearMonthInputForm;
import electricMeters.core.DbHandler;
import electricMeters.core.UtilAlert;
import electricMeters.core.controls.JsonTable;
import electricMeters.report.ActOfConsumptionReport;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.json.JSONObject;

import java.util.List;

public class RegActsOfConsumptionController {

    @FXML
    private JsonTable actsTable;
    @FXML
    private JsonTable actStringsTable;

    @FXML
    private void initialize() {
        MenuItem print = new MenuItem("Печать");
        print.setOnAction(actionEvent -> {
            JSONObject selectedItem = actsTable.getSelectedItem();
            ActOfConsumptionReport.createAndWrite(selectedItem.getInt("YEAR"), selectedItem.getInt("MONTH"));
        });
        actsTable.setContextMenu(new ContextMenu(print));
        actsTable.addSelectedListener(jsonObject -> {
            if (jsonObject != null) {
                actStringsTable.setParams(jsonObject.get("YEAR"), jsonObject.get("MONTH"), jsonObject.get("YEAR"), jsonObject.get("MONTH"));
            } else {
                actStringsTable.setParams(-1, -1, -1, -1);
            }
            actStringsTable.reload();
        });
        actsTable.setOnDoubleClick(this::edit);
        actsTable.reload();
    }

    @FXML
    private void onAddAct() {
        YearMonthInputForm form = YearMonthInputForm.instance("Создание акта расхода электроэнергии");
        ButtonType result = form.showAndWait();
        if (result == ButtonType.OK) {
            ActOfConsumptionEditController.show(form.getYear(), form.getMonth().getValue(), actsTable, actsTable.getScene().getWindow());
        }
    }

    @FXML
    private void onEditAct() {
        JSONObject item = actsTable.getSelectedItem();
        edit(item);
    }

    private void edit(JSONObject jsonObject) {
        ActOfConsumptionEditController.show(jsonObject.getInt("YEAR"), jsonObject.getInt("MONTH"), actsTable, actsTable.getScene().getWindow());
    }

    @FXML
    private void onDeleteAct() {
        List<JSONObject> selectedActs = actsTable.getSelectedItems();
        if (!selectedActs.isEmpty()) {
            if (UtilAlert.showDeleteConfirmation(selectedActs.size())) {
                String sql = "DELETE FROM METERS_READINGS WHERE YEAR = ? AND MONTH = ?";
                for (JSONObject act : selectedActs) {
                    DbHandler.getInstance().runSqlUpdate(sql, act.getInt("YEAR"), act.getInt("MONTH"));
                }
                actsTable.reload();
            }
        }
    }

}
