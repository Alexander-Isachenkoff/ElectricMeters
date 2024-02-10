package electricMeters.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Setter
@Getter
public class JsonColumn extends TableColumn<JSONObject, Object> {
    
    private String field;
    
    public JsonColumn() {
        this.setCellValueFactory(param -> {
            JSONObject jsonObject = param.getValue();
            Object value = jsonObject.has(field) ? jsonObject.get(field) : "";
            return new SimpleObjectProperty<>(value);
        });
    }
    
}
