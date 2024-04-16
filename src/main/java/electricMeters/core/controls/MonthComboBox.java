package electricMeters.core.controls;

import electricMeters.util.DateUtil;
import javafx.scene.control.ListCell;

import java.time.LocalDate;
import java.time.Month;

public class MonthComboBox extends ComboBoxPlus<Month> {
    
    public MonthComboBox() {
        this.getItems().addAll(Month.values());
        this.setCellFactory(param -> new ListCell<Month>() {
            @Override
            protected void updateItem(Month item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(DateUtil.monthName(item));
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
