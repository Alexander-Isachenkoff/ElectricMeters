package electricMeters.controls;

import electricMeters.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TableWrapper extends VBox {
    
    @FXML
    private Label titleLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    
    private final BooleanProperty addEnabled = new SimpleBooleanProperty(true);
    private final BooleanProperty deleteEnabled = new SimpleBooleanProperty(true);
    
    public TableWrapper() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/TableWrapper.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        addButton.visibleProperty().bind(addEnabled);
        addButton.managedProperty().bind(addEnabled);
        deleteButton.visibleProperty().bind(deleteEnabled);
        deleteButton.managedProperty().bind(deleteEnabled);
    }
    
    public void setOnAdd(EventHandler<ActionEvent> onAdd) {
        addButton.setOnAction(onAdd);
    }
    
    public void setOnDelete(EventHandler<ActionEvent> onDelete) {
        deleteButton.setOnAction(onDelete);
    }
    
    public EventHandler<ActionEvent> getOnAdd() {
        return addButton.getOnAction();
    }
    
    public EventHandler<ActionEvent> getOnDelete() {
        return deleteButton.getOnAction();
    }
    
    private JsonTable getTable() {
        return (JsonTable) getChildren().filtered(node -> node instanceof JsonTable).stream().findFirst().orElse(null);
    }
    
    public String getTitle() {
        return titleLabel.getText();
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public boolean isAddEnabled() {
        return addEnabled.get();
    }
    
    public void setAddEnabled(boolean addEnabled) {
        this.addEnabled.set(addEnabled);
    }
    
    public boolean isDeleteEnabled() {
        return deleteEnabled.get();
    }
    
    public void setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled.set(deleteEnabled);
    }
}
