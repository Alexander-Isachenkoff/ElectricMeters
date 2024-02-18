package electricMeters;

import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SummaryProfileController {

    @FXML
    private ComboBox<Month> monthCmb;
    @FXML
    private ComboBox<Integer> yearCmb;
    @FXML
    private JsonTable table;

    @FXML
    private void initialize() {
        monthCmb.getItems().addAll(Month.values());
        monthCmb.getSelectionModel().select(LocalDate.now().getMonth());
        monthCmb.setCellFactory(param -> new ListCell<Month>() {
            @Override
            protected void updateItem(Month item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(item.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()));
                } else {
                    setText("");
                }
            }
        });
        monthCmb.setButtonCell(monthCmb.getCellFactory().call(null));

        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().collect(Collectors.toList()));
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
