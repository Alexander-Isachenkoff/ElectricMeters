package electricMeters.controls;

import electricMeters.DbHandler;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@Getter
public class JsonTable extends TableView<JSONObject> {
    
    private final Progress progress = new Progress();
    @Setter
    private String sqlFile;
    private Object[] params = new Object[0];
    private boolean isLoading;
    
    public void reload() {
        progress.showProgress();
        new Thread(() -> {
            List<JSONObject> objects;
            isLoading = true;
            objects = DbHandler.getInstance().runSqlSelectFile(sqlFile, params);
            isLoading = false;
            Platform.runLater(() -> {
                this.getItems().setAll(objects);
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
