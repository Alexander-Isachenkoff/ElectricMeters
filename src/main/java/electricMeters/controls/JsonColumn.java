package electricMeters.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Setter
@Getter
public class JsonColumn extends TableColumn<JSONObject, Object> {
    
    private String field;
    @Getter
    private Pos alignment;
    
    public JsonColumn() {
        this.setCellValueFactory(param -> {
            JSONObject jsonObject = param.getValue();
            Object value = jsonObject.has(field) ? jsonObject.get(field) : "";
            return new SimpleObjectProperty<>(value);
        });
        styleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.endsWith(getAlignmentStyleString(this.alignment))) {
                setAlignment(this.alignment);
            }
        });
    }
    
    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
        setStyle(getStyle() + getAlignmentStyleString(alignment));
    }
    
    private static String getAlignmentStyleString(Pos alignment) {
        return "-fx-alignment: " + alignment + ";";
    }
    
}
