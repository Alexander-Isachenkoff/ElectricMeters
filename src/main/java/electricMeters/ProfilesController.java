package electricMeters;

import electricMeters.core.controls.JsonTable;
import electricMeters.core.DbHandler;
import electricMeters.core.UtilAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfilesController {
    
    @FXML
    private JsonTable mainTable;
    @FXML
    private JsonTable detailsTable;
    
    @FXML
    private void initialize() {
        mainTable.setSqlFile("ProfileView.sql");
        detailsTable.setSqlFile("ProfilesEM.sql");
        
        mainTable.addSelectedListener(newValue -> {
            if (newValue != null) {
                int id = newValue.getInt("id");
                detailsTable.setParams(id);
                detailsTable.reload();
            } else {
                detailsTable.clear();
            }
        });
        
        mainTable.reload();
    }
    
    @SneakyThrows
    @FXML
    private void onProfileAdd() {
        List<File> files = new FileChooser().showOpenMultipleDialog(mainTable.getScene().getWindow());
        
        if (files == null) {
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/progress.fxml"));
        VBox root = loader.load();
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.sizeToScene();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        
        ProgressController controller = loader.getController();
        
        new Thread(() -> {
            AtomicInteger count = new AtomicInteger(0);
            Platform.runLater(() -> {
                controller.setTotalCount(files.size());
                controller.setDone(0);
            });
            for (File file : files) {
                ProfileUtil.readAndSave(file);
                count.incrementAndGet();
                Platform.runLater(() -> controller.setDone(count.get()));
            }
            mainTable.reload();
        }).start();
    }
    
    @FXML
    private void onProfileDelete() {
        List<JSONObject> items = mainTable.getSelectedItems();
        if (!items.isEmpty()) {
            if (UtilAlert.showDeleteConfirmation()) {
                for (JSONObject item : items) {
                    DbHandler.getInstance().delete(item.getInt("id"), "ProfileEMInfo");
                }
                mainTable.reload();
            }
        }
    }
    
}
