package electricMeters.view;

import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

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
        table.setParams(
                monthCmb.getSelectionModel().getSelectedItem().getValue(),
                yearCmb.getSelectionModel().getSelectedItem()
        );
        table.reload();
    }

}
