package electricMeters.view;

import electricMeters.CompanyData;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import electricMeters.report.SummaryProfileReport;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.stream.IntStream;

public class SummaryProfileController {

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
        table.setParams(getIntMonth(), getYear());
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
        CompanyData companyData = CompanyData.getCompanyData();
        JSONObject report = new JSONObject()
                .put("CONSUMER_NAME", companyData.getConsumerName())
                .put("CONTRACT_NUMBER", companyData.getContractNumber())
                .put("VOLTAGE_LEVEL_NAME", companyData.getVoltageLevelName())
                .put("YEAR", getYear())
                .put("MONTH", getIntMonth())
                .put("strings", table.getAllItems());
        SummaryProfileReport.write(report);
    }

}
