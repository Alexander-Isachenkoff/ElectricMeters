package electricMeters.core.controls;

import electricMeters.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.io.IOException;

public class ComboBoxPlus<T> extends AnchorPane implements Required {

    private final BooleanProperty clearable = new SimpleBooleanProperty(true);
    private final BooleanProperty required = new SimpleBooleanProperty(false);
    @FXML
    @Delegate(excludes = Control.class)
    private ComboBox<T> comboBox;
    @FXML
    private Button clearButton;
    @FXML
    private Text requiredSign;

    @Getter
    @Setter
    private String name = "";

    public ComboBoxPlus() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/controls/ComboBoxPlus.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearButton.visibleProperty().bind(valueProperty().isNotNull().and(clearable));
        requiredSign.visibleProperty().bind(required.and(valueProperty().isNull()));
    }

    @FXML
    private void clear() {
        comboBox.getSelectionModel().clearSelection();
    }

    public boolean isClearable() {
        return clearable.get();
    }

    public void setClearable(boolean clearable) {
        this.clearable.set(clearable);
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
        return getValue() == null;
    }

}
