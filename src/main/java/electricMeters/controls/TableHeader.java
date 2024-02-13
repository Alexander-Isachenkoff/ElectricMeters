package electricMeters.controls;

import electricMeters.ExcelUtil;
import electricMeters.Main;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.IOException;

public class TableHeader extends HBox {

    private final BooleanProperty addEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty deleteEnabled = new SimpleBooleanProperty(false);
    private final ObjectProperty<JsonTable> table = new SimpleObjectProperty<>();
    @FXML
    private Label titleLabel;
    @FXML
    private Label countLabel;
    private final ListChangeListener<? super JSONObject> listChangeListener = c -> updateCount();
    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    public TableHeader() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/controls/TableHeader.fxml"));
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

        searchField.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!searchField.isVisible()) {
                searchField.clear();
            }
        });
        searchField.setVisible(false);
        searchField.setManaged(false);

        table.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getItems().removeListener(listChangeListener);
                oldValue.filterProperty().unbindBidirectional(searchField.textProperty());
            }
            newValue.getItems().addListener(listChangeListener);
            newValue.filterProperty().bind(searchField.textProperty());
        });
    }

    private void updateCount() {
        if (getTable() != null) {
            countLabel.setText("(" + getTable().getItems().size() + ")");
        }
    }

    @FXML
    private void onReload() {
        if (getTable() != null) {
            getTable().reload();
        }
    }

    @FXML
    private void onSearch() {
        boolean visible = !searchField.isVisible();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(searchField.prefWidthProperty(), visible ? 0 : 120)),
                new KeyFrame(Duration.millis(100), new KeyValue(searchField.prefWidthProperty(), visible ? 120 : 0))
        );
        if (visible) {
            searchField.setVisible(true);
            searchField.setManaged(true);
        } else {
            timeline.setOnFinished(event -> {
                searchField.setVisible(false);
                searchField.setManaged(false);
            });
        }
        timeline.play();
    }

    @FXML
    private void onExportExcel() {
        ExcelUtil.exportExcel(getTable());
    }

    public JsonTable getTable() {
        return table.get();
    }

    public void setTable(JsonTable table) {
        this.table.set(table);
    }

    public ObjectProperty<JsonTable> tableProperty() {
        return table;
    }

    public EventHandler<ActionEvent> getOnAdd() {
        return addButton.getOnAction();
    }

    public void setOnAdd(EventHandler<ActionEvent> onAdd) {
        addButton.setOnAction(onAdd);
    }

    public EventHandler<ActionEvent> getOnDelete() {
        return deleteButton.getOnAction();
    }

    public void setOnDelete(EventHandler<ActionEvent> onDelete) {
        deleteButton.setOnAction(onDelete);
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
