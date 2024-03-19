package electricMeters.view;

import electricMeters.CompanyData;
import electricMeters.Main;
import electricMeters.core.controls.JsonTable;
import electricMeters.util.DateUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class SummaryCostForm {

    private final Stage stage = new Stage();

    @FXML private Label companyNameLabel;
    @FXML private Label voltageLabel;
    @FXML private Label priceCategoryLabel;
    @FXML private Label periodLabel;
    @FXML private Label locationNameLabel;
    @FXML private JsonTable table;
    @FXML private TextField totalTF;
    @FXML private TextField powerTF;
    @FXML private TextField totalWithNdsTF;

    private int year;
    private int month;

    static void show(int year, int month, Window owner) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/summary-cost-edit.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SummaryCostForm controller = loader.getController();
        controller.stage.setScene(new Scene(root));
        controller.stage.initOwner(owner);
        controller.init(year, month);
        controller.stage.show();
    }

    @FXML
    private void initialize() {
        stage.setTitle("Стоимость");
        stage.sizeToScene();
        stage.initModality(Modality.WINDOW_MODAL);

        CompanyData companyData = CompanyData.getCompanyData();
        companyNameLabel.setText(String.format("%s (№%s)", companyData.getConsumerName(), companyData.getContractNumber()));
        voltageLabel.setText(companyData.getVoltageLevelName());
    }

    private void init(int year, int month) {
        this.year = year;
        this.month = month;
        periodLabel.setText(String.format("%s %d", DateUtil.monthName(month), year));
        CompanyData companyData = CompanyData.getCompanyData();
        table.setParams(companyData.getRateTypeID(), companyData.getVoltageLevelID(), month, year);
        table.reload();
    }

    @FXML
    private void onExport() {

    }

}
