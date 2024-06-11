package electricMeters.view;

import electricMeters.Main;
import electricMeters.core.controls.ComboBoxPlus;
import electricMeters.core.controls.JsonTable;
import electricMeters.core.controls.MonthComboBox;
import electricMeters.core.controls.TableHeader;
import electricMeters.report.ProfileHourlyReport;
import electricMeters.service.ProfileUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ProfilesController {
    
    @FXML private MonthComboBox monthCmb;
    @FXML private ComboBoxPlus<Integer> yearCmb;
    @FXML private TableHeader mainTableHeader;
    @FXML private JsonTable mainTable;
    @FXML private JsonTable detailsTable;
    @FXML private JsonTable detailsTable1;
    
    @FXML
    private void initialize() {
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().toList());
        yearCmb.getSelectionModel().selectLast();
        
        mainTableHeader.addToolButton("import.png", this::onImport);
        
        mainTable.addSelectedListener(newValue -> {
            for (JsonTable childTable : Arrays.asList(detailsTable, detailsTable1)) {
                if (newValue != null) {
                    int id = newValue.getInt("ID");
                    childTable.setParams(id);
                    childTable.reload();
                } else {
                    childTable.clear();
                }
            }
        });
        
        mainTable.addStylePredicate(item -> item.optInt("HAS_STATUS") == 1, "text-error");
        
        ContextMenu contextMenu = new ContextMenu();
        MenuItem reportItem = new MenuItem("Сформировать отчёт");
        reportItem.setOnAction(event -> {
            int id = mainTable.getSelectedItem().getInt("ID");
            ProfileHourlyReport.createAndWrite(id);
        });
        contextMenu.getItems().add(reportItem);
        mainTable.setContextMenu(contextMenu);
        
        onApply();
    }
    
    @SneakyThrows
    @FXML
    private void onImport() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt");
        fileChooser.getExtensionFilters().add(txtFilter);
        fileChooser.setSelectedExtensionFilter(txtFilter);
        List<File> files = fileChooser.showOpenMultipleDialog(mainTable.getScene().getWindow());
        
        if (files == null) {
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/progress.fxml"));
        VBox root = loader.load();
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.sizeToScene();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        
        ProgressController controller = loader.getController();
        
        new Thread(() -> {
            AtomicInteger count = new AtomicInteger(0);
            Platform.runLater(() -> {
                controller.setTotalCount(files.size());
                controller.setDone(0);
            });
            for (File file : files) {
                ProfileUtil.readAndSave(file);
                count.incrementAndGet();
                Platform.runLater(() -> controller.setDone(count.get()));
            }
            mainTable.reload();
        }).start();
    }
    
    @FXML
    private void onApply() {
        Integer month = (monthCmb.getValue() != null) ? monthCmb.getValue().getValue() : null;
        mainTable.setParams(yearCmb.getValue(), month);
        mainTable.reload();
    }
    
    @FXML
    private void onProfileDelete() {
        mainTable.deleteSelectedItemsWithConfirmation("PROFILES");
    }
    
}
