package electricMeters;

import electricMeters.core.PowerRateParser;
import electricMeters.core.controls.JsonComboBox;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PriceCategory3Controller {
    @FXML
    private JsonTable table;
    @FXML
    private JsonComboBox rateTypeCmb;
    @FXML
    private JsonComboBox voltageLevelCmb;
    @FXML
    private MonthComboBox monthCmb;
    @FXML
    private ComboBox<Integer> yearCmb;

    @FXML
    private void initialize() {
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().collect(Collectors.toList()));
        yearCmb.getSelectionModel().select((Integer) LocalDate.now().getYear());

        rateTypeCmb.reload();
        rateTypeCmb.getSelectionModel().selectLast();

        voltageLevelCmb.reload();
        voltageLevelCmb.getSelectionModel().selectFirst();

        table.setSqlFile("HOURLY_RATE_VALUES.sql");

        onApply();
    }

    @FXML
    private void onApply() {
        table.setParams(
                yearCmb.getSelectionModel().getSelectedItem(),
                monthCmb.getSelectionModel().getSelectedItem().getValue(),
                rateTypeCmb.getValue().getInt("ID"),
                voltageLevelCmb.getValue().getInt("ID")
        );
        table.reload();
    }

    @FXML
    private void onAdd() throws IOException {
        File file = new FileChooser().showOpenDialog(null);
        if (file != null) {
            PowerRateParser.readAndInsertPowerRates(file);
        }
    }
}
