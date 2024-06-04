package electricMeters.core.controls;

import electricMeters.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class ComboBoxPlus<T> extends AnchorPane implements Required {

    private final BooleanProperty clearable = new SimpleBooleanProperty(true);
    private final BooleanProperty required = new SimpleBooleanProperty(false);
    @FXML
    protected ComboBox<T> comboBox;
    @FXML
    private Button clearButton;
    @FXML
    private Text requiredSign;
    @FXML
    private StackPane buttonParent;

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
        buttonParent.mouseTransparentProperty().bind(clearButton.visibleProperty().not().and(requiredSign.visibleProperty().not()));
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
    
    public T getValue() {
        return comboBox.getValue();
    }
    
    public ObjectProperty<T> valueProperty() {
        return comboBox.valueProperty();
    }
    
    public <E> ObservableList<T> getItems() {
        return comboBox.getItems();
    }
    
    public void setCellFactory(Callback<ListView<T>, ListCell<T>> cellFactory) {
        comboBox.setCellFactory(cellFactory);
    }
    
    public Callback<ListView<T>, ListCell<T>> getCellFactory() {
        return comboBox.getCellFactory();
    }
    
    public SingleSelectionModel<T> getSelectionModel() {
        return comboBox.getSelectionModel();
    }
    
    public void setButtonCell(ListCell<T> buttonCell) {
        comboBox.setButtonCell(buttonCell);
    }
    
}
