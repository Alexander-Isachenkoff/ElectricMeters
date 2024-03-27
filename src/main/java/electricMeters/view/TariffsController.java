package electricMeters.view;

import electricMeters.core.controls.ClearableJComboBox;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import electricMeters.service.PowerRateParser;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
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
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().toList());

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
        Integer rateTypeId = rateTypeCmb.getValue() != null ? rateTypeCmb.getValue().getInt("ID") : null;
        Integer voltageLevelId = voltageLevelCmb.getValue() != null ? voltageLevelCmb.getValue().getInt("ID") : null;
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

    @FXML
    private void onImport() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Книга Microsoft Excel", "*.xlsx", "*.xls");
        fileChooser.getExtensionFilters().add(xlsxFilter);
        fileChooser.setSelectedExtensionFilter(xlsxFilter);
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (file != null) {
            PowerRateParser.readAndInsertPowerRates(file);
        }
    }

}
