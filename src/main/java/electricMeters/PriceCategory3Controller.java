package electricMeters;

import electricMeters.core.controls.JsonComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PriceCategory3Controller {

    @FXML
    private JsonComboBox rateTypeCmb;
    @FXML
    private JsonComboBox voltageLevelCmb;
    @FXML
    private ComboBox<Month> monthCmb;
    @FXML
    private ComboBox<Integer> yearCmb;

    @FXML
    private void initialize() {
        monthCmb.getItems().addAll(Month.values());
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().collect(Collectors.toList()));
    }

    @FXML
    private void onApply() {

    }
}
