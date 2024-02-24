package electricMeters.core.controls;

import electricMeters.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import lombok.experimental.Delegate;

import java.io.IOException;

public class ClearableJComboBox extends AnchorPane {

    private final BooleanProperty clearable = new SimpleBooleanProperty(true);
    @FXML
    @Delegate(excludes = Control.class)
    private JsonComboBox comboBox;
    @FXML
    private Button clearButton;

    public ClearableJComboBox() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/controls/ClearableJComboBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearButton.visibleProperty().bind(comboBox.getSelectionModel().selectedItemProperty().isNotNull().and(clearable));
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

}
