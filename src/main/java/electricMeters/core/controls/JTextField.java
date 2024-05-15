package electricMeters.core.controls;

import electricMeters.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.json.JSONObject;

import java.io.IOException;

public class JTextField extends AnchorPane implements RequiredJsonField<String> {

    private final BooleanProperty required = new SimpleBooleanProperty(false);
    @FXML
    @Delegate(excludes = Control.class)
    private TextField textField;
    @FXML
    private StackPane requiredSign;

    @Getter
    @Setter
    private String name = "";
    @Getter
    @Setter
    private String key = "";

    public JTextField() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/controls/JTextField.fxml"));
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
        requiredSign.visibleProperty().bind(required.and(textField.textProperty().isEmpty()));
        Tooltip tooltip = new Tooltip("Обязательное поле");
        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(requiredSign, tooltip);
    }

    @Override
    public boolean isRequired() {
        return required.get();
    }

    public void setRequired(boolean required) {
        this.required.set(required);
    }

    @Override
    public boolean isEmpty() {
        return textField.getText().isEmpty();
    }

    @Override
    public String getValue() {
        return getText();
    }

    @Override
    public void setValue(JSONObject json) {
        setText(String.valueOf(json.get(getKey())));
    }

}
