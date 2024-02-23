package electricMeters.core.controls;

import electricMeters.core.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private List<JSONObject> allItems = new ArrayList<>();

    public JsonTable() {
        progressIndicator.setMaxSize(60, 60);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().setCellSelectionEnabled(true);
        filter.addListener((observable, oldValue, newValue) -> updateVisibleItems());
    }

    private static boolean hasValue(JSONObject json, String value) {
        return json.keySet().stream()
                .anyMatch(key -> String.valueOf(json.get(key)).toLowerCase().contains(value.toLowerCase()));
    }

    private void updateVisibleItems() {
        List<JSONObject> filteredItems = allItems.stream()
                .filter(json -> hasValue(json, filter.getValue()))
                .collect(Collectors.toList());
        getItems().setAll(filteredItems);
    }

    public StringProperty filterProperty() {
        return filter;
    }

    public void reload() {
        showProgress();
        new Thread(() -> {
            isLoading = true;
            if (sqlFile != null) {
                allItems = DbHandler.getInstance().runSqlSelectFile(sqlFile, params);
            } else if (tableName != null) {
                allItems = DbHandler.getInstance().getAllFrom(tableName);
            }
            isLoading = false;
            Platform.runLater(() -> {
                updateVisibleItems();
                hideProgress();
            });
        }).start();
    }

    public void setParams(Object... params) {
        this.params = params;
    }

    public void clear() {
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
        return getColumns().stream().map(col -> (JsonColumn) col).collect(Collectors.toList());
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

}
