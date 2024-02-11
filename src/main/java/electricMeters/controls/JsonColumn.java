package electricMeters.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.converter.DateStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.function.Function;

@Setter
@Getter
public class JsonColumn extends TableColumn<JSONObject, Object> {
    
    private String field;
    private Pos alignment;
    private DataType dataType = DataType.DEFAULT;
    
    public JsonColumn() {
        this.setCellValueFactory(param -> {
            JSONObject jsonObject = param.getValue();
            Object value = jsonObject.has(field) ? dataType.dataConverter.apply(jsonObject.get(field)) : "";
            return new SimpleObjectProperty<>(value);
        });
        this.setCellFactory(param -> new TableCell<JSONObject, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    String text = dataType.stringConverter.apply(item);
                    setText(text);
                } else {
                    setText("");
                }
            }
        });
        styleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.endsWith(getAlignmentStyleString(this.alignment))) {
                setAlignment(this.alignment);
            }
        });
    }
    
    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
        setStyle((getStyle() == null ? "" : getStyle()) + getAlignmentStyleString(alignment));
    }
    
    private static String getAlignmentStyleString(Pos alignment) {
        return "-fx-alignment: " + alignment + ";";
    }
    
    @AllArgsConstructor
    public enum DataType {
        DEFAULT(),
        REAL(Function.identity(), Constants.decimalFormat::format),
        DATE(
                object -> Constants.dateConverter.fromString(object.toString()),
                object -> Constants.dateConverter.toString((Date) object)
        );
        
        private final Function<Object, Object> dataConverter;
        private final Function<Object, String> stringConverter;
        
        DataType() {
            this.dataConverter = Function.identity();
            this.stringConverter = Object::toString;
        }
        
        private static class Constants {
            private static final DateStringConverter dateConverter = new DateStringConverter("dd.MM.yy");
            private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");
        }
    }
    
}