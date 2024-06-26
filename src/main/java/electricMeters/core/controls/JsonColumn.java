package electricMeters.core.controls;

import electricMeters.core.DataType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.function.Consumer;

@Setter
@Getter
public class JsonColumn extends TableColumn<JSONObject, Object> {

    private String field;
    private Pos alignment;
    private DataType dataType = DataType.DEFAULT;
    private String format;
    private DisplayType displayType = DisplayType.DEFAULT;
    private Mask mask;
    private Consumer<String> hyperlinkAction = s -> {};

    public JsonColumn() {
        this.setEditable(false);
        this.setCellValueFactory(param -> {
            JSONObject jsonObject = param.getValue();
            if (jsonObject.has(field)) {
                return new SimpleObjectProperty<>(dataType.convertFromDB(jsonObject.get(field)));
            } else {
                return null;
            }
        });
        this.setCellFactory(param -> new JsonEditCell(this));
        this.setOnEditCommit(event -> {
            JsonTable table = getJsonTable();
            JSONObject jsonObject = table
                    .getItems()
                    .get(event.getTablePosition().getRow());
            jsonObject.put(field, event.getNewValue());
            table.getChangeRowListener().accept(jsonObject);
            table.refresh();
        });
        styleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.endsWith(getAlignmentStyleString(this.alignment))) {
                setAlignment(this.alignment);
            }
        });
    }

    private static String getAlignmentStyleString(Pos alignment) {
        return "-fx-alignment: " + alignment + ";";
    }

    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
        setStyle((getStyle() == null ? "" : getStyle()) + getAlignmentStyleString(alignment));
    }

    private JsonTable getJsonTable() {
        return ((JsonTable) getTableView());
    }
    
    public enum DisplayType {
        DEFAULT, CHECK_BOX, HYPERLINK
    }
    
}
