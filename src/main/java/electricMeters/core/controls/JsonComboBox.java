package electricMeters.core.controls;

import electricMeters.core.DbHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.ComboBoxListCell;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonComboBox extends ComboBox<JSONObject> {

    @Getter
    @Setter
    private String sqlFile;
    @Getter
    @Setter
    private String tableName;
    @Getter
    @Setter
    private String field;
    private boolean loaded;

    public JsonComboBox() {
        addEventHandler(ComboBox.ON_SHOWING, event -> reload());
        setCellFactory(param -> new ComboBoxListCell<JSONObject>() {
            @Override
            public void updateItem(JSONObject item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(item.has(field) ? String.valueOf(item.get(field)) : "");
                } else {
                    setText("");
                }
            }
        });
        setButtonCell(getCellFactory().call(null));
        MenuItem menuItem = new MenuItem("Очистить");
        menuItem.setOnAction(event -> getSelectionModel().clearSelection());
        setContextMenu(new ContextMenu(menuItem));
    }

    public void reload() {
        if (!loaded) {
            List<JSONObject> jsonObjects = new ArrayList<>();
            if (sqlFile != null) {
                jsonObjects = DbHandler.getInstance().runSqlSelectFile(sqlFile);
            } else if (tableName != null) {
                jsonObjects = DbHandler.getInstance().getAllFrom(tableName);
            }
            getItems().setAll(jsonObjects);
            loaded = true;
        }
    }

    public JSONObject getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

}
