package electricMeters.report;

import electricMeters.CompanyData;
import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.util.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

public class ProfileHourlyReport {

    private static final String TEMPLATE_XLSX = "templates/Профиль.xlsx";

    public static void createAndWrite(int profileId) {
        JSONObject report = createReport(profileId);
        String fileName = String.format("%s_%s.xlsx", report.getString("METER_NUMBER"), report.getString("METER_CONSUMER"));
        fileName = fileName.replaceAll("[/:]", " ");
        File file = new File(fileName);
        write(report, file);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject createReport(int profileId) {
        JSONObject profileJson = DbHandler.getInstance().selectById("PROFILES_VW", profileId);
        List<JSONObject> reportStrings = DbHandler.getInstance().runSqlSelectFile("ProfileHourlyReport.sql", profileId);
        LocalDate date = DateUtil.toLocalDate(reportStrings.get(0).getString("DATE"));
        CompanyData companyData = CompanyData.getCompanyData();
        return new JSONObject()
                .put("CONSUMER_NAME", companyData.getConsumerName())
                .put("CONTRACT_NUMBER", companyData.getContractNumber())
                .put("VOLTAGE_LEVEL_NAME", companyData.getVoltageLevelName())
                .put("YEAR", date.getYear())
                .put("MONTH", date.getMonth().getValue())
                .put("METER_CONSUMER", profileJson.getString("METER_CONSUMER"))
                .put("METER_NUMBER", String.valueOf(profileJson.get("METER_NUMBER")))
                .put("strings", reportStrings);
    }

    public static void write(JSONObject report, File file) {
        try (Workbook workbook = WorkbookFactory.create(Main.class.getResourceAsStream(TEMPLATE_XLSX))) {
            Sheet sheet = workbook.getSheetAt(0);

            String headerText = String.format("%s (№%s)", report.getString("CONSUMER_NAME"), report.getString("CONTRACT_NUMBER"));
            sheet.getRow(0).getCell(3).setCellValue(headerText);

            workbook.setSheetName(0, report.getString("METER_NUMBER"));
            String meteringPoint = String.format("%s, счетчик %s", report.getString("METER_CONSUMER"), report.getString("METER_NUMBER"));
            sheet.getRow(1).getCell(3).setCellValue(meteringPoint);

            sheet.getRow(2).getCell(3).setCellValue(report.getString("VOLTAGE_LEVEL_NAME"));

            int month = report.getInt("MONTH");
            int year = report.getInt("YEAR");
            sheet.getRow(5).getCell(1).setCellValue(String.format("%s %d", DateUtil.monthName(month), year));

            JSONArray array = (JSONArray) report.get("strings");
            for (int i = 0; i < array.length(); i++) {
                JSONObject line = array.getJSONObject(i);
                Row row = sheet.getRow(i + 8);
                row.getCell(0).setCellValue(DateUtil.toLocalDate(line.getString("DATE")));
                for (int h = 0; h < 24; h++) {
                    double value = line.getDouble("T_" + h);
                    row.getCell(h + 1).setCellValue(value);
                }
            }
            file.createNewFile();
            file.deleteOnExit();
            workbook.write(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
