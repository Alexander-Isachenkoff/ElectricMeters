package test3;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import org.json.JSONObject;

public class UtilTable {

    static TableColumn<JSONObject, Object> createColumn(String title, String field, int width, Pos alignment) {
        TableColumn<JSONObject, Object> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setStyle("-fx-alignment: " + alignment);
        column.setCellValueFactory(param -> {
            JSONObject jsonObject = param.getValue();
            Object value = jsonObject.has(field) ? jsonObject.get(field) : "";
            return new SimpleObjectProperty<>(value);
        });
        return column;
    }

}
