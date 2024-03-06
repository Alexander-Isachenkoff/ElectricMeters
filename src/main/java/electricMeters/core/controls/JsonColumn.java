package electricMeters.core.controls;

import electricMeters.core.DataType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Setter
@Getter
public class JsonColumn extends TableColumn<JSONObject, Object> {

    private String field;
    private Pos alignment;
    private DataType dataType = DataType.DEFAULT;
    private String format;
    private DisplayType displayType = DisplayType.DEFAULT;
    private Mask mask;

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
            TableView<JSONObject> tableView = event.getTableView();
            tableView
                    .getItems()
                    .get(event.getTablePosition().getRow())
                    .put(field, event.getNewValue());
            tableView.refresh();
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

    public enum DisplayType {
        DEFAULT, CHECK_BOX
    }
    
}
