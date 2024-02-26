package electricMeters.core.controls;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthComboBox extends ComboBox<Month> {
    
    public MonthComboBox() {
        this.getItems().addAll(Month.values());
        this.setCellFactory(param -> new ListCell<Month>() {
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
        this.setButtonCell(this.getCellFactory().call(null));
        selectCurrent();
    }
    
    public void selectCurrent() {
        this.getSelectionModel().select(LocalDate.now().getMonth());
    }
    
}
