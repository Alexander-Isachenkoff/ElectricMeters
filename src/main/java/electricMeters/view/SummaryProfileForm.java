package electricMeters.view;

import electricMeters.CompanyData;
import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.core.controls.JsonTable;
import electricMeters.report.SummaryProfileReport;
import electricMeters.util.DateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONObject;

import java.io.IOException;

public class SummaryProfileForm {

    private final Stage stage = new Stage();

    @FXML private Label companyNameLabel;
    @FXML private Label voltageLabel;
    @FXML private Label priceCategoryLabel;
    @FXML private Label periodLabel;
    @FXML private Label locationNameLabel;
    @FXML private JsonTable table;
    @FXML private TextField totalTF;
    @FXML private TextField powerTF;

    private int year;
    private int month;

    static void show(int year, int month, Window owner) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/summary-profile-edit.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SummaryProfileForm controller = loader.getController();
        controller.stage.setScene(new Scene(root));
        controller.stage.initOwner(owner);
        controller.init(year, month);
        controller.stage.show();
    }

    @FXML
    private void initialize() {
        stage.setTitle("Суммарный профиль");
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
        table.setParams(month, year);
        table.reload();

        new Thread(() -> {
            JSONObject jsonObject = DbHandler.getInstance().runSqlSelectFile("SummaryProfileTotalConsumption.sql", month, year).get(0);
            double total = jsonObject.getDouble("VALUE");
            Platform.runLater(() -> {
                totalTF.setText(String.format("%.3f", total));
            });
        }).start();
        
        new Thread(() -> {
            JSONObject jsonObject = DbHandler.getInstance().runSqlSelectFile("SumPeakPower.sql", year, month).get(0);
            double power = jsonObject.getDouble("POWER");
            Platform.runLater(() -> {
                powerTF.setText(String.format("%.3f", power));
            });
        }).start();
    }

    @FXML
    private void onExport() {
        CompanyData companyData = CompanyData.getCompanyData();
        JSONObject report = new JSONObject()
                .put("CONSUMER_NAME", companyData.getConsumerName())
                .put("CONTRACT_NUMBER", companyData.getContractNumber())
                .put("VOLTAGE_LEVEL_NAME", companyData.getVoltageLevelName())
                .put("YEAR", year)
                .put("MONTH", month)
                .put("strings", table.getAllItems());
        SummaryProfileReport.write(report);
    }

}
