package electricMeters.controls;

import electricMeters.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

@Getter
public class JsonTable extends TableView<JSONObject> {

    private final Progress progress = new Progress();
    private final StringProperty filter = new SimpleStringProperty("");
    @Setter
    private String sqlFile;
    private Object[] params = new Object[0];
    private boolean isLoading;
    private List<JSONObject> allItems = new ArrayList<>();

    public JsonTable() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        progress.showProgress();
        new Thread(() -> {
            isLoading = true;
            allItems = DbHandler.getInstance().runSqlSelectFile(sqlFile, params);
            isLoading = false;
            Platform.runLater(() -> {
                updateVisibleItems();
                progress.hideProgress();
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

    private class Progress {

        private Pane parent;
        private int i;
        private boolean showing;

        private void hideProgress() {
            if (showing) {
                setEffect(null);
                parent.getChildren().set(i, JsonTable.this);
                showing = false;
            }
        }

        private void showProgress() {
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isLoading) {
                        Platform.runLater(() -> {
                            setEffect(new GaussianBlur(4));
                            ProgressIndicator progressIndicator = new ProgressIndicator();
                            progressIndicator.setMaxSize(60, 60);
                            parent = (Pane) JsonTable.this.getParent();
                            StackPane stackPane = new StackPane();
                            i = parent.getChildren().indexOf(JsonTable.this);
                            parent.getChildren().set(i, stackPane);
                            stackPane.getChildren().addAll(JsonTable.this, progressIndicator);
                            showing = true;
                        });
                    }
                }
            }, 100);
        }
    }

}
