package electricMeters.view;

import electricMeters.CompanyData;
import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.core.controls.JsonTable;
import electricMeters.report.ActOfConsumptionReport;
import electricMeters.util.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class ActOfConsumptionEditController {

    private final Stage stage = new Stage();
    @FXML
    private Label contractNumLabel;
    @FXML
    private Label companyNameLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label monthLabel;
    @FXML
    private JsonTable metersTable;

    private JsonTable tableToReload;

    private int year;
    private int month;

    static void show(int year, int month, JsonTable tableToReload, Window owner) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/act-of-electricity-consumption-edit.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ActOfConsumptionEditController controller = loader.getController();
        controller.stage.setScene(new Scene(root));
        controller.stage.initOwner(owner);
        controller.init(year, month, tableToReload);
        controller.stage.show();
    }

    @FXML
    private void initialize() {
        stage.setTitle("Акт расхода электроэнергии");
        stage.sizeToScene();
        stage.initModality(Modality.WINDOW_MODAL);
        metersTable.setEditable(true);
        
        JSONObject companyData = CompanyData.getCompanyData();
        companyNameLabel.setText(companyData.getString("CONSUMER_NAME"));
        contractNumLabel.setText(companyData.getString("CONTRACT_NUMBER"));
        addressLabel.setText(companyData.getString("CONSUMER_ADDRESS"));
        phoneLabel.setText(String.format("телефон: %s, %s", companyData.getString("CONSUMER_PHONE_NUMBER_1"), companyData.getString("CONSUMER_PHONE_NUMBER_2")));
        
        metersTable.setChangeRowListener(jsonObject -> {
            double prevReadings = jsonObject.optDouble("PREV_READINGS");
            double calcCoefficient = jsonObject.optInt("CALC_COEFFICIENT", 1);
            if (jsonObject.has("READINGS_VALUE")) {
                double difference = jsonObject.optDouble("READINGS_VALUE", 0) - prevReadings;
                jsonObject.put("READINGS_DIFFERENCE", difference);
                jsonObject.put("CALC_BY_METER", difference * calcCoefficient);
            } else {
                jsonObject.remove("READINGS_DIFFERENCE");
                jsonObject.remove("CALC_BY_METER");
            }
            metersTable.refresh();
        });
    }

    private void init(int year, int month, JsonTable tableToReload) {
        this.tableToReload = tableToReload;
        this.year = year;
        this.month = month;
        yearLabel.setText(String.valueOf(year));
        monthLabel.setText(DateUtil.monthName(month).toLowerCase());
        metersTable.setParams(year, month, year, month);
        metersTable.reload();
    }

    @FXML
    private void onSave() {
        save();
        tableToReload.reloadFocused();
        stage.close();
    }

    private void save() {
        List<JSONObject> dataToSave = metersTable.getItems().stream()
                .map(this::toMetersReadings)
                .toList();

        List<JSONObject> dataToUpdate = dataToSave.stream()
                .filter(json -> json.has("ID"))
                .toList();

        List<JSONObject> dataToInsert = dataToSave.stream()
                .filter(json -> !json.has("ID"))
                .toList();

        if (!dataToUpdate.isEmpty()) {
            DbHandler.getInstance().updateList(dataToUpdate, "METERS_READINGS");
        }
        if (!dataToInsert.isEmpty()) {
            DbHandler.getInstance().insertList(new JSONArray(dataToInsert), "METERS_READINGS");
        }
    }

    private JSONObject toMetersReadings(JSONObject json) {
        JSONObject result = new JSONObject()
                .put("YEAR", year)
                .put("MONTH", month)
                .put("METER_ID", json.getInt("METER_ID"))
                .put("READINGS_VALUE", json.opt("READINGS_VALUE"));
        if (json.has("METERS_READINGS_ID")) {
            result.put("ID", json.getInt("METERS_READINGS_ID"));
        }
        return result;
    }

    @FXML
    private void onExport() {
        save();
        ActOfConsumptionReport.createAndWrite(year, month);
    }

}
