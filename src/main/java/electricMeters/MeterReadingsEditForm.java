package electricMeters;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MeterReadingsEditForm {

    private final Stage stage = new Stage();

    public MeterReadingsEditForm() {
        stage.setTitle("Показания счетчика");
        stage.initModality(Modality.WINDOW_MODAL);
    }

    public static MeterReadingsEditForm instance() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/add-meter-reading-value.fxml"));
        Parent load;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MeterReadingsEditForm controller = fxmlLoader.getController();
        controller.stage.setScene(new Scene(load));
        return controller;
    }

    @FXML
    private void initialize() {

    }

    public void show(Window owner) {
        stage.initOwner(owner);
        stage.show();
    }

}
