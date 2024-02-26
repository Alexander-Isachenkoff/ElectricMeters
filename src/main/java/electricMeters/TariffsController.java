package electricMeters;

import electricMeters.core.controls.ClearableJComboBox;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TariffsController {

    @FXML
    private JsonTable table;
    @FXML
    private ClearableJComboBox rateTypeCmb;
    @FXML
    private ClearableJComboBox voltageLevelCmb;
    @FXML
    private MonthComboBox monthCmb;
    @FXML
    private ComboBox<Integer> yearCmb;

    @FXML
    private void initialize() {
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().collect(Collectors.toList()));

        rateTypeCmb.reload();
        rateTypeCmb.getSelectionModel().selectLast();

        voltageLevelCmb.reload();
        voltageLevelCmb.getSelectionModel().selectFirst();

        onApply();
    }

    @FXML
    private void onApply() {
        Integer year = yearCmb.getSelectionModel().getSelectedItem();
        Integer month = monthCmb.getSelectionModel().getSelectedItem() != null ? monthCmb.getSelectionModel().getSelectedItem().getValue() : null;
        Integer rateTypeId = rateTypeCmb.getSelectedItem() != null ? rateTypeCmb.getSelectedItem().getInt("ID") : null;
        Integer voltageLevelId = voltageLevelCmb.getSelectedItem() != null ? voltageLevelCmb.getSelectedItem().getInt("ID") : null;
        table.setParams(
                year,
                year,
                month,
                month,
                rateTypeId,
                rateTypeId,
                voltageLevelId,
                voltageLevelId
        );
        table.reload();
    }

}
