package electricMeters.core.controls;

import electricMeters.Main;
import electricMeters.util.DateUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;

public class DatePickerPlus extends AnchorPane implements Required {
    
    private final BooleanProperty clearable = new SimpleBooleanProperty(true);
    private final BooleanProperty required = new SimpleBooleanProperty(false);
    
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text requiredSign;
    @FXML
    private Button clearButton;
    
    @Getter
    @Setter
    private String name = "";
    
    public DatePickerPlus() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/controls/DatePickerPlus.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    private void initialize() {
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                if (localDate != null) {
                    return DateUtil.toString(localDate);
                } else {
                    return "";
                }
            }
            
            @Override
            public LocalDate fromString(String s) {
                if (s.isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(s, DateUtil.DATE_FORMAT);
                } catch (RuntimeException e) {
                    return datePicker.getValue();
                }
            }
        });
        datePicker.getEditor().setTextFormatter(new TextFormatter<>(datePicker.getConverter()));
        datePicker.valueProperty().bindBidirectional(
                (ObjectProperty<LocalDate>) datePicker.getEditor().getTextFormatter().valueProperty()
        );
        
        clearButton.visibleProperty().bind(valueProperty().isNotNull().and(clearable));
        requiredSign.visibleProperty().bind(required.and(valueProperty().isNull()));
        Tooltip tooltip = new Tooltip("Обязательное поле");
        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(requiredSign, tooltip);
    }
    
    @FXML
    public void clear() {
        setValue(null);
    }
    
    @Override
    public boolean isEmpty() {
        return getValue() == null;
    }
    
    @Override
    public boolean isRequired() {
        return required.get();
    }
    
    public void setRequired(boolean required) {
        this.required.set(required);
    }
    
    public String getStringValue() {
        LocalDate value = getValue();
        return (value != null) ? value.format(DateUtil.DB_DATE_FORMAT) : null;
    }
    
    public LocalDate getValue() {
        return datePicker.getValue();
    }
    
    public void setValue(LocalDate localDate) {
        datePicker.setValue(localDate);
    }
    
    public ObjectProperty<LocalDate> valueProperty() {
        return datePicker.valueProperty();
    }
    
}
