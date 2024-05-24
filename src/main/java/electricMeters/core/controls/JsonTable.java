package electricMeters.core.controls;

import electricMeters.core.DbHandler;
import electricMeters.core.UtilAlert;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class JsonTable extends TableView<JSONObject> {

    private final StringProperty filter = new SimpleStringProperty("");
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    @Getter
    @Setter
    private String sqlFile;
    @Getter
    @Setter
    private String tableName;
    private Object[] params = new Object[0];
    private boolean isLoading;
    @Getter
    private List<JSONObject> allItems = new ArrayList<>();

    @Getter
    @Setter
    private Consumer<JSONObject> onDoubleClick = jsonObject -> {};
    
    @Getter
    @Setter
    private Consumer<JSONObject> changeRowListener = jsonObject -> {};

    private String lastInputText = "";
    
    public JsonTable() {
        progressIndicator.setMaxSize(60, 60);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().setCellSelectionEnabled(true);
        filter.addListener((observable, oldValue, newValue) -> updateVisibleItems());

        this.setOnKeyPressed(e -> {
            TablePosition<JSONObject, ?> editingCell = getEditingCell();
            if (editingCell == null) {
                if (e.getCode().isLetterKey() || e.getCode().isDigitKey()) {
                    lastInputText = e.getText();
                    TablePosition<JSONObject, ?> focusedCellPosition = getFocusModel().getFocusedCell();
                    edit(focusedCellPosition.getRow(), focusedCellPosition.getTableColumn());
                }
            }
        });

        this.setRowFactory(table -> {
            TableRow<JSONObject> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    JSONObject jsonObject = row.getItem();
                    onDoubleClick.accept(jsonObject);
                }
            });
            return row;
        });
    }
    
    String getAndDropLastInputText() {
        String result = lastInputText;
        lastInputText = "";
        return result;
    }

    private static boolean hasValue(JSONObject json, String value) {
        return json.keySet().stream()
                .anyMatch(key -> String.valueOf(json.get(key)).toLowerCase().contains(value.toLowerCase()));
    }

    private void updateVisibleItems() {
        List<JSONObject> filteredItems = allItems.stream()
                .filter(json -> hasValue(json, filter.getValue()))
                .toList();
        getItems().setAll(filteredItems);
        getSelectionModel().selectFirst();
    }

    public StringProperty filterProperty() {
        return filter;
    }

    public void reloadFocused() {
        JSONObject selectedItem = getSelectedItem();
        if (selectedItem != null) {
            int id = selectedItem.getInt("ID");
            reloadFocused(id);
        } else {
            reload();
        }
    }

    public void reloadFocused(int id) {
        reload(() -> {
            getItems().stream()
                    .filter(json -> json.get("ID").equals(id)).findFirst()
                    .ifPresent(json -> getSelectionModel().select(json));
        });
    }

    public void reload() {
        reload(() -> {});
    }

    public void reload(Runnable onReload) {
        showProgress();
        CompletableFuture.supplyAsync(() -> {
            List<JSONObject> result;
            if (sqlFile != null) {
                isLoading = true;
                result = DbHandler.getInstance().runSqlSelectFile(sqlFile, params);
                isLoading = false;
            } else if (tableName != null) {
                isLoading = true;
                result = DbHandler.getInstance().getAllFrom(tableName);
                isLoading = false;
            } else {
                throw new RuntimeException("Не указан sql-файл или название таблицы");
            }
            return result;
        })
        .thenAccept(this::setData)
        .thenRun(onReload);
    }

    public void setData(List<JSONObject> data) {
        allItems = data;
        Platform.runLater(() -> {
            updateVisibleItems();
            hideProgress();
        });
    }

    public void setParams(Object... params) {
        this.params = params;
    }

    public void clear() {
        allItems.clear();
        getItems().clear();
    }

    public void addSelectedListener(Consumer<JSONObject> onSelected) {
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onSelected.accept(newValue));
    }

    public JSONObject getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    public List<JSONObject> getSelectedItems() {
        return getSelectionModel().getSelectedItems();
    }

    public List<JsonColumn> getJsonColumns() {
        return getColumns().stream().map(col -> (JsonColumn) col).toList();
    }

    private void hideProgress() {
        setEffect(null);
        ((Pane) getParent()).getChildren().remove(progressIndicator);
    }

    private void showProgress() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                if (isLoading) {
                    Platform.runLater(() -> {
                        setEffect(new GaussianBlur(4));
                        if (getParent() instanceof StackPane) {
                            ObservableList<Node> children = ((StackPane) getParent()).getChildren();
                            if (!children.contains(progressIndicator)) {
                                children.add(progressIndicator);
                            }
                        }
                    });
                }
            }
        }, 100);
    }

    public void deleteSelectedItemsWithConfirmation(String tableName) {
        List<JSONObject> items = this.getSelectedItems();
        if (!items.isEmpty()) {
            if (UtilAlert.showDeleteConfirmation(items.size())) {
                DbHandler.getInstance().deleteList(items, tableName);
            }
            this.reload();
        }
    }

}
