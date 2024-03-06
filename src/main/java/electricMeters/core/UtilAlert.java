package electricMeters.core;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class UtilAlert {
    
    public static boolean showDeleteConfirmation() {
        return showDeleteConfirmation(1);
    }
    
    public static boolean showDeleteConfirmation(int count) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", new ButtonType("Да", ButtonBar.ButtonData.YES), new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.setTitle("Подтверждение");
        String headerText = (count > 1) ? String.format("Удалить записи? (%d шт.)", count) : "Удалить запись?";
        alert.setHeaderText(headerText);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES;
    }
    
}
