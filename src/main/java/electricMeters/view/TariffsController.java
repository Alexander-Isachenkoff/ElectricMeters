package electricMeters.view;

import electricMeters.core.controls.ComboBoxPlus;
import electricMeters.core.controls.JsonComboBox;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import electricMeters.service.PowerRateParser;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.IntStream;

public class TariffsController {

    @FXML private JsonTable tariffsTable;
    @FXML private JsonTable hoursTable;
    @FXML private JsonComboBox rateTypeCmb;
    @FXML private JsonComboBox voltageLevelCmb;
    @FXML private MonthComboBox monthCmb;
    @FXML private ComboBoxPlus<Integer> yearCmb;

    @FXML
    private void initialize() {
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().toList());

        rateTypeCmb.reload();
        rateTypeCmb.getSelectionModel().selectLast();

        voltageLevelCmb.reload();
        voltageLevelCmb.getSelectionModel().selectFirst();

        tariffsTable.addSelectedListener(jsonObject -> {
            if (jsonObject != null) {
                hoursTable.setParams(
                        jsonObject.getInt("YEAR"),
                        jsonObject.getInt("MONTH"),
                        jsonObject.getInt("TYPE_ID"),
                        jsonObject.getInt("VL_ID")
                );
                hoursTable.reload();
            } else {
                hoursTable.setParams(-1, -1, -1, -1);
                hoursTable.clear();
            }
        });

        onApply();
    }

    @FXML
    private void onApply() {
        Integer year = yearCmb.getSelectionModel().getSelectedItem();
        Integer month = monthCmb.getSelectionModel().getSelectedItem() != null ? monthCmb.getSelectionModel().getSelectedItem().getValue() : null;
        Integer rateTypeId = rateTypeCmb.getValue() != null ? rateTypeCmb.getValue().getInt("ID") : null;
        Integer voltageLevelId = voltageLevelCmb.getValue() != null ? voltageLevelCmb.getValue().getInt("ID") : null;
        tariffsTable.setParams(
                year,
                year,
                month,
                month,
                rateTypeId,
                rateTypeId,
                voltageLevelId,
                voltageLevelId
        );
        tariffsTable.reload();
    }

    @FXML
    private void onImport() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Книга Microsoft Excel", "*.xlsx", "*.xls");
        fileChooser.getExtensionFilters().add(xlsxFilter);
        fileChooser.setSelectedExtensionFilter(xlsxFilter);
        File file = fileChooser.showOpenDialog(tariffsTable.getScene().getWindow());
        if (file != null) {
            PowerRateParser.readAndInsertPowerRates(file);
            tariffsTable.reload();
        }
    }

}
