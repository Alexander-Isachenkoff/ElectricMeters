package electricMeters.view;

import electricMeters.CompanyData;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.util.stream.IntStream;

public class SummaryCostController {

    @FXML
    private MonthComboBox monthCmb;
    @FXML
    private ComboBox<Integer> yearCmb;
    @FXML
    private JsonTable table;

    @FXML
    private void initialize() {
        monthCmb.getSelectionModel().selectFirst();
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().toList());
        yearCmb.getSelectionModel().select((Integer) LocalDate.now().getYear());
        onApply();
    }

    @FXML
    private void onApply() {
        CompanyData companyData = CompanyData.getCompanyData();
        table.setParams(companyData.getRateTypeID(), companyData.getVoltageLevelID(), getIntMonth(), getYear());
        table.reload();
    }

    private int getYear() {
        return yearCmb.getSelectionModel().getSelectedItem();
    }

    private int getIntMonth() {
        return monthCmb.getSelectionModel().getSelectedItem().getValue();
    }

    @FXML
    private void onExport() {

    }

}
