package electricMeters.view;

import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.core.controls.JsonTable;
import electricMeters.util.DateUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ActOfConsumptionEditController {

    private final Stage stage = new Stage();
    private final IntegerProperty year = new SimpleIntegerProperty();
    @FXML
    private TextField yearField;
    @FXML
    private TextField monthField;
    @FXML
    private JsonTable metersTable;
    private TextFormatter<Integer> month;

    public static void show(int year, int month) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/act-of-electricity-consumption-edit.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ActOfConsumptionEditController controller = loader.getController();
        controller.stage.setScene(new Scene(root));
        controller.init(year, month);
        controller.stage.show();
    }

    @FXML
    private void initialize() {
        stage.setTitle("Акт расхода электроэнергии");
        stage.sizeToScene();
        stage.initModality(Modality.WINDOW_MODAL);

        yearField.textProperty().bind(year.asString());
        month = new TextFormatter<>(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == null) {
                    return "";
                } else {
                    return DateUtil.intToMonthName(object);
                }
            }

            @Override
            public Integer fromString(String string) {
                return month.getValue();
            }
        });
        monthField.setTextFormatter(month);

        metersTable.setEditable(true);
    }

    private void init(int year, int month) {
        this.year.set(year);
        this.month.setValue(month);
        metersTable.setParams(year, month, year, month);
        metersTable.reload();
    }

    @FXML
    private void onSave() {
        List<JSONObject> jsonObjectsToUpdate = metersTable.getItems().stream()
                .filter(json -> json.has("METERS_READINGS_ID"))
                .map(json -> toMetersReadings(json).put("ID", json.getInt("METERS_READINGS_ID")))
                .collect(Collectors.toList());

        List<JSONObject> jsonObjectsToInsert = metersTable.getItems().stream()
                .filter(json -> !json.has("METERS_READINGS_ID"))
                .map(this::toMetersReadings)
                .collect(Collectors.toList());

        for (JSONObject jsonObject : jsonObjectsToUpdate) {
            DbHandler.getInstance().update(jsonObject, "METERS_READINGS");
        }
        if (!jsonObjectsToInsert.isEmpty()) {
            DbHandler.getInstance().insertList(new JSONArray(jsonObjectsToInsert), "METERS_READINGS");
        }

        stage.close();
    }

    private JSONObject toMetersReadings(JSONObject json) {
        return new JSONObject()
                .put("YEAR", year.get())
                .put("MONTH", month.getValue())
                .put("METER_ID", json.getInt("METER_ID"))
                .put("READINGS_VALUE", json.opt("READINGS_VALUE"));
    }

}
