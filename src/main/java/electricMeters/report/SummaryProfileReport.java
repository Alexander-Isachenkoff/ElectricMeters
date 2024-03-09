package electricMeters.report;

import electricMeters.Main;
import electricMeters.util.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SummaryProfileReport {

    private static final String TEMPLATE_XLSX = "templates/Суммарный профиль.xlsx";

    public static void write(JSONObject report) {
        int month = report.getInt("MONTH");
        int year = report.getInt("YEAR");
        String fileName = String.format("Суммарный профиль (%s %d).xlsx", DateUtil.monthName(month), year);
        File file = new File(fileName);
        try {
            write(report, file);
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(JSONObject report, File file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(Main.class.getResourceAsStream(TEMPLATE_XLSX))) {
            Sheet sheet = workbook.getSheetAt(0);

            String headerText = String.format("%s (№%s)", report.getString("CONSUMER_NAME"), report.getString("CONTRACT_NUMBER"));
            sheet.getRow(0).getCell(3).setCellValue(headerText);

            sheet.getRow(2).getCell(3).setCellValue(report.getString("VOLTAGE_LEVEL_NAME"));

            int month = report.getInt("MONTH");
            int year = report.getInt("YEAR");
            sheet.getRow(5).getCell(1).setCellValue(String.format("%s %d", DateUtil.monthName(month), year));

            JSONArray array = (JSONArray) report.get("strings");
            for (int i = 0; i < array.length(); i++) {
                JSONObject line = array.getJSONObject(i);
                for (int h = 0; h < 24; h++) {
                    double value = line.getDouble("T_" + h);
                    sheet.getRow(i + 8).getCell(h + 1).setCellValue(value);
                }
            }
            file.createNewFile();
            file.deleteOnExit();
            workbook.write(Files.newOutputStream(file.toPath()));
        }
    }

}
