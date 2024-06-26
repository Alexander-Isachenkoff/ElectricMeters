package electricMeters.view;

import electricMeters.core.UtilAlert;
import electricMeters.core.controls.JsonColumn;
import electricMeters.core.controls.JsonTable;
import electricMeters.service.PeakHoursService;
import electricMeters.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class PeakHoursForm {
    
    @FXML
    private JsonTable yearsTable;
    @FXML
    private ListView<Month> monthsList;
    @FXML
    private JsonTable monthsTable;
    @FXML
    private JsonColumn filePathColumn;
    
    @FXML
    private void initialize() {
        filePathColumn.setHyperlinkAction(PeakHoursService::openFile);
        monthsList.getItems().addAll(Month.values());
        monthsList.setCellFactory(param -> new ListCell<>() {
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
        monthsList.getSelectionModel().select(LocalDate.now().getMonth());
        
        yearsTable.addSelectedListener(jsonObject -> updateMonthsTable());
        monthsList.getSelectionModel().selectedItemProperty().addListener((observableValue, old, newMonth) -> updateMonthsTable());
        
        Callback<ListView<Month>, ListCell<Month>> cellFactory = monthsList.getCellFactory();
        monthsList.setCellFactory(monthListView -> {
            ListCell<Month> cell = cellFactory.call(monthListView);
            cell.indexProperty().addListener((observableValue, number, t1) -> {
                if (t1.intValue() == 0) {
                    cell.getStyleClass().add("first-cell");
                }
                if (t1.intValue() == monthListView.getItems().size() - 1) {
                    cell.getStyleClass().add("last-cell");
                }
            });
            return cell;
        });
        
        yearsTable.reload();
    }
    
    private void updateMonthsTable() {
        JSONObject selectedYear = yearsTable.getSelectedItem();
        if (selectedYear != null) {
            int year = selectedYear.getInt("YEAR");
            int month = monthsList.getSelectionModel().getSelectedItem().getValue();
            monthsTable.setParams(year, month);
            monthsTable.reload();
        } else {
            monthsTable.setParams(-1, -1);
            monthsTable.clear();
        }
    }
    
    @FXML
    private void onImport() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Книга Microsoft Excel", "*.xlsx", "*.xls");
        fileChooser.getExtensionFilters().add(xlsxFilter);
        fileChooser.setSelectedExtensionFilter(xlsxFilter);
        List<File> files = fileChooser.showOpenMultipleDialog(yearsTable.getScene().getWindow());
        if (files == null) {
            return;
        }
        for (File file : files) {
            try {
                PeakHoursService.readAndInsertPeakHours(file);
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        }
        yearsTable.reload();
    }
    
    @FXML
    private void onDelete() {
        List<JSONObject> selectedItems = yearsTable.getSelectedItems();
        if (UtilAlert.showDeleteConfirmation(selectedItems.size())) {
            for (JSONObject item : selectedItems) {
                PeakHoursService.delete(item.getInt("ID"));
            }
            yearsTable.reload();
        }
    }
    
}
