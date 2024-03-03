package electricMeters;

import electricMeters.core.controls.MonthComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.IntStream;

public class YearMonthInputForm {

    private final Stage stage = new Stage();
    public ComboBox<Integer> yearCmb;
    public MonthComboBox monthCmb;
    @Getter
    private ButtonType result;

    public static YearMonthInputForm instance(String title) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/year-month-input.fxml"));
        Parent load;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        YearMonthInputForm controller = fxmlLoader.getController();
        controller.stage.setScene(new Scene(load));
        controller.stage.setTitle(title);
        return controller;
    }

    @FXML
    private void initialize() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.sizeToScene();

        int year = LocalDate.now().getYear();
        yearCmb.getItems().setAll(IntStream.rangeClosed(year - 10, year).boxed().toList());
        yearCmb.getSelectionModel().select((Integer) year);
    }

    public ButtonType showAndWait() {
        stage.showAndWait();
        return result;
    }

    @FXML
    private void onOk() {
        result = ButtonType.OK;
        stage.close();
    }

    @FXML
    private void onCancel() {
        result = ButtonType.CANCEL;
        stage.close();
    }

    public int getYear() {
        return yearCmb.getValue();
    }

    public Month getMonth() {
        return monthCmb.getValue();
    }

}
