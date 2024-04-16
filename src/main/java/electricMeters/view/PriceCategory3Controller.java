package electricMeters.view;

import electricMeters.core.controls.ComboBoxPlus;
import electricMeters.core.controls.JsonComboBox;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import javafx.fxml.FXML;

import java.time.LocalDate;
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
    private ComboBoxPlus<Integer> yearCmb;

    @FXML
    private void initialize() {
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().toList());
        yearCmb.getSelectionModel().select((Integer) LocalDate.now().getYear());

        rateTypeCmb.reload();
        rateTypeCmb.getSelectionModel().selectLast();

        voltageLevelCmb.reload();
        voltageLevelCmb.getSelectionModel().selectFirst();

        table.setSqlFile("TAR3_HOURLY_RATE_VALUES.sql");

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

}
