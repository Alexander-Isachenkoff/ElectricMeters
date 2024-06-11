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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    private String lastInputText;
    
    private final List<Pair<Predicate<JSONObject>, String>> stylesPredicates = new ArrayList<>();
    
    public JsonTable() {
        progressIndicator.setMaxSize(60, 60);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().setCellSelectionEnabled(true);
        filter.addListener((observable, oldValue, newValue) -> updateVisibleItems());
        
        this.setPlaceholder(new Label("Нет данных"));

        this.setOnKeyPressed(this::onKeyPressed);

        this.setRowFactory(table -> {
            TableRow<JSONObject> row = new TableRow<>() {
                @Override
                protected void updateItem(JSONObject item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        return;
                    }
                    stylesPredicates.forEach(pair -> {
                        if (pair.getKey().test(item)) {
                            getStyleClass().add(pair.getValue());
                        } else {
                            getStyleClass().remove(pair.getValue());
                        }
                    });
                }
            };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    JSONObject jsonObject = row.getItem();
                    onDoubleClick.accept(jsonObject);
                }
            });
            return row;
        });
    }
    
    public void addStylePredicate(Predicate<JSONObject> predicate, String styleName) {
        stylesPredicates.add(new Pair<>(predicate, styleName));
    }
    
    String getAndDropLastInputText() {
        String result = lastInputText;
        lastInputText = null;
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
    }

    public StringProperty filterProperty() {
        return filter;
    }
    
    public void reload() {
        JSONObject selectedItem = getSelectedItem();
        if (selectedItem != null && selectedItem.has("ID")) {
            int id = selectedItem.getInt("ID");
            reloadAndSelect(id);
        } else {
            reloadAndSelectFirst();
        }
    }
    
    public void reloadAndSelect(int id) {
        reload(() -> {
            Platform.runLater(() -> {
                getItems().stream()
                        .filter(json -> json.get("ID").equals(id)).findFirst()
                        .ifPresent(json -> {
                            int row = getItems().indexOf(json);
                            getSelectionModel().select(row);
                            getFocusModel().focus(row);
                        });
            });
        });
    }
    
    private void reloadAndSelectFirst() {
        reload(() -> Platform.runLater(() -> getSelectionModel().selectFirst()));
    }
    
    private void reload(Runnable onReload) {
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
                        if (getParent() instanceof StackPane parent) {
                            ObservableList<Node> children = parent.getChildren();
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
    
    private void onKeyPressed(KeyEvent e) {
        if (getEditingCell() != null) {
            return;
        }
        TablePosition<JSONObject, ?> focusedCell = getFocusModel().getFocusedCell();
        if (e.getCode().isLetterKey() || e.getCode().isDigitKey()) {
            lastInputText = e.getText();
            edit(focusedCell.getRow(), focusedCell.getTableColumn());
        } else if (e.getCode() == KeyCode.BACK_SPACE) {
            lastInputText = "";
            edit(focusedCell.getRow(), focusedCell.getTableColumn());
        } else if (e.getCode() == KeyCode.DELETE) {
            for (TablePosition cell : getSelectionModel().getSelectedCells()) {
                JsonColumn column = (JsonColumn) cell.getTableColumn();
                if (column.isEditable()) {
                    String field = column.getField();
                    JSONObject item = getItems().get(cell.getRow());
                    item.remove(field);
                    changeRowListener.accept(item);
                    refresh();
                }
            }
        }
    }
    
}
