package electricMeters;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.Setter;

public class ProgressController {
    
    @FXML
    private Label label;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button endButton;
    
    @Setter
    private int totalCount;
    
    @FXML
    private void initialize() {
        progressBar.setProgress(0);
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            endButton.setDisable(newValue.doubleValue() != 1);
        });
    }
    
    @FXML
    private void onEnd() {
        progressBar.getScene().getWindow().hide();
    }
    
    public void setDone(int count) {
        progressBar.setProgress((double) count / totalCount);
        label.setText(count + " из " + totalCount);
    }
    
}
