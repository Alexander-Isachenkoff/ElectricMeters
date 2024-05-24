package electricMeters.core.controls;

import electricMeters.core.ExcelUtil;
import electricMeters.Main;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;

public class TableHeader extends HBox {

    private static final int SEARCH_FIELD_WIDTH = 150;
    private final BooleanProperty importEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty addEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty showEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty editEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty deleteEnabled = new SimpleBooleanProperty(false);
    private final ObjectProperty<JsonTable> table = new SimpleObjectProperty<>();

    @FXML private Label titleLabel;
    @FXML private Label countLabel;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button showButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button importButton;
    @FXML private HBox toolBar;

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
        showButton.visibleProperty().bind(showEnabled);
        showButton.managedProperty().bind(showEnabled);
        editButton.visibleProperty().bind(editEnabled);
        editButton.managedProperty().bind(editEnabled);
        deleteButton.visibleProperty().bind(deleteEnabled);
        deleteButton.managedProperty().bind(deleteEnabled);
        importButton.visibleProperty().bind(importEnabled);
        importButton.managedProperty().bind(importEnabled);

        searchField.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!searchField.isVisible()) {
                searchField.clear();
            }
        });
        searchField.setVisible(false);
        searchField.setManaged(false);

        table.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.filterProperty().unbindBidirectional(searchField.textProperty());
            }
            newValue.filterProperty().bind(searchField.textProperty());
            countLabel.textProperty().unbind();
            countLabel.textProperty().bind(Bindings.size(getTable().getItems()).asString("(%d)"));
        });
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
                new KeyFrame(Duration.millis(0), new KeyValue(searchField.prefWidthProperty(), visible ? 0 : SEARCH_FIELD_WIDTH)),
                new KeyFrame(Duration.millis(100), new KeyValue(searchField.prefWidthProperty(), visible ? SEARCH_FIELD_WIDTH : 0))
        );
        if (visible) {
            searchField.setVisible(true);
            searchField.setManaged(true);
            searchField.requestFocus();
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

    public void addToolButton(String iconFileName, Runnable onAction) {
        Button button = new Button();
        button.getStyleClass().add("tool-button");
        button.setOnAction(event -> onAction.run());
        button.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("icons/" + iconFileName))));
        toolBar.getChildren().add(0, button);
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
    
    public EventHandler<ActionEvent> getOnEdit() {
        return editButton.getOnAction();
    }
    
    public void setOnEdit(EventHandler<ActionEvent> onEdit) {
        editButton.setOnAction(onEdit);
    }

    public EventHandler<ActionEvent> getOnDelete() {
        return deleteButton.getOnAction();
    }

    public void setOnDelete(EventHandler<ActionEvent> onDelete) {
        deleteButton.setOnAction(onDelete);
    }

    public EventHandler<ActionEvent> getOnImport() {
        return importButton.getOnAction();
    }

    public void setOnImport(EventHandler<ActionEvent> onImport) {
        importButton.setOnAction(onImport);
    }

    public EventHandler<ActionEvent> getOnShow() {
        return showButton.getOnAction();
    }

    public void setOnShow(EventHandler<ActionEvent> onShow) {
        showButton.setOnAction(onShow);
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
    
    public boolean isEditEnabled() {
        return editEnabled.get();
    }
    
    public void setEditEnabled(boolean enabled) {
        this.editEnabled.set(enabled);
    }

    public boolean isDeleteEnabled() {
        return deleteEnabled.get();
    }

    public void setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled.set(deleteEnabled);
    }

    public boolean isImportEnabled() {
        return importEnabled.get();
    }

    public BooleanProperty importEnabledProperty() {
        return importEnabled;
    }

    public void setImportEnabled(boolean importEnabled) {
        this.importEnabled.set(importEnabled);
    }

    public boolean isShowEnabled() {
        return showEnabled.get();
    }

    public void setShowEnabled(boolean showEnabled) {
        this.showEnabled.set(showEnabled);
    }
}
