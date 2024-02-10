package electricMeters;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableView;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class MainController {

    @FXML
    private TableView<JSONObject> table;
    @FXML
    private TableView<JSONObject> detailsTable;
    private DbHandler db;

    {
        try {
            db = DbHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        table.getColumns().setAll(TableInfo.createColumns("ProfilesEMInfo.xml"));

        List<JSONObject> profiles = db.getAllFrom("ProfileView");
        table.getItems().setAll(profiles);

        detailsTable.getColumns().add(UtilTable.createColumn("id", "id", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("Дата", "date", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("Время", "time", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("aPos", "aPos", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("aNeg", "aNeg", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("rPos", "rPos", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("rNeg", "rNeg", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("Статус", "status", 100, Pos.CENTER_LEFT));
        detailsTable.getColumns().add(UtilTable.createColumn("id профиля", "profileEMInfo", 100, Pos.CENTER_LEFT));

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int id = newValue.getInt("id");
                detailsTable.getItems().setAll(db.runSqlSelect("SELECT * FROM ProfilesEM WHERE profileEMInfo = " + id));
            } else {
                detailsTable.getItems().clear();
            }
        });

    }

}
