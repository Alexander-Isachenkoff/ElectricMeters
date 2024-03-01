package electricMeters;

import electricMeters.core.DbHandler;
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
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ProfileHourlyReport {

    private static final String TEMPLATE_XLSX = "templates/ProfileReportTeml.xlsx";

    public static void createAndWrite(int profileId) {
        JSONObject report = createReport(profileId);
        String fileName = String.format("%s_%s.xlsx", report.getString("METER_NUMBER"), report.getString("METER_CONSUMER"));
        fileName = fileName.replaceAll("[/:]", " ");
        File file = new File(fileName);
        write(report, file);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject createReport(int profileId) {
        JSONObject profileJson = DbHandler.getInstance().selectById("PROFILES_VW", profileId);
        List<JSONObject> jsonObjects = DbHandler.getInstance().runSqlSelectFile("ProfileHourlyReport.sql", profileId);
        LocalDate date = LocalDate.parse(jsonObjects.get(0).getString("date"), DateTimeFormatter.ofPattern("dd.MM.yy"));
        String monthDate = date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " " + date.getYear();
        return new JSONObject()
                .put("date", monthDate)
                .put("METER_CONSUMER", profileJson.getString("METER_CONSUMER"))
                .put("METER_NUMBER", String.valueOf(profileJson.get("METER_NUMBER")))
                .put("data", jsonObjects);
    }

    public static void write(JSONObject json, File file) {
        try (Workbook workbook = WorkbookFactory.create(Main.class.getResourceAsStream(TEMPLATE_XLSX))) {
            Sheet sheet = workbook.getSheetAt(0);
            workbook.setSheetName(0, json.getString("METER_NUMBER"));
            String meteringPoint = String.format("%s, счетчик %s", json.getString("METER_CONSUMER"), json.getString("METER_NUMBER"));
            sheet.getRow(1).getCell(3).setCellValue(meteringPoint);
            sheet.getRow(5).getCell(1).setCellValue(json.getString("date"));

            JSONArray array = (JSONArray) json.get("data");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
