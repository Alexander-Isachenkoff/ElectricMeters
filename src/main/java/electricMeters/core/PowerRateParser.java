package electricMeters.core;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PowerRateParser {
    
    private static final int ROWS_PER_HOURLY_POWER_RATE = 139;
    private static final int ROW_PER_ONE_TABLE = 34;
    private static final int FIRST_CAP_RATE_ROW = 144;
    
    public static void readAndInsertPowerRates(File file) throws IOException {
        List<JSONObject> powerRates = readPowerRate(file);
        for (JSONObject powerRate : powerRates) {
            insertPowerRate(powerRate);
        }
    }
    
    public static List<JSONObject> readPowerRate(File file) throws IOException {
        List<JSONObject> powerRates = new ArrayList<>();
        try (Workbook book = WorkbookFactory.create(file)) {
            Matcher m = Pattern.compile(".*(?<year>\\d{4})").matcher(book.getSheetName(0));
            m.find();
            int year = Integer.parseInt(m.group("year"));
            for (int i = 0; i < book.getNumberOfSheets(); i++) {
                int month = i + 1;
                List<JSONObject> monthPowerRates = parsePowerRates(book.getSheetAt(i));
                monthPowerRates.forEach(json -> json.put("MONTH", month).put("YEAR", year));
                powerRates.addAll(monthPowerRates);
            }
        }
        return powerRates;
    }
    
    private static List<JSONObject> parsePowerRates(Sheet sheet) {
        List<JSONObject> result = new ArrayList<>();
        
        for (int i = 1; i <= 6; i++) {
            String value = sheet.getRow(FIRST_CAP_RATE_ROW + (i - 1) * ROWS_PER_HOURLY_POWER_RATE).getCell(8).getStringCellValue();
            double capRate = Double.parseDouble(value.replace(",", "."));
            JSONObject json = new JSONObject()
                    .put("POWER_RATE_TYPE_ID", i)
                    .put("CAP_RATE", capRate);
            List<JSONObject> hourlyRates = parseHourlyPowerRates(sheet, i);
            json.put("hourlyRates", hourlyRates);
            result.add(json);
        }
        
        return result;
    }
    
    private static List<JSONObject> parseHourlyPowerRates(Sheet sheet, Integer code) {
        List<JSONObject> result = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            JSONObject json = new JSONObject().put("VOLTAGE_LEVEL_ID", i);
            List<JSONObject> values = parseHourlyPowerRateValues(sheet, code, i);
            json.put("values", values);
            result.add(json);
        }
        return result;
    }
    
    private static List<JSONObject> parseHourlyPowerRateValues(Sheet sheet, Integer code, int voltageId) {
        List<JSONObject> result = new ArrayList<>();
        
        int startRow = 10 + ROWS_PER_HOURLY_POWER_RATE * (code - 1) + (voltageId - 1) * ROW_PER_ONE_TABLE;
        for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
            if (sheet.getRow(startRow + dayOfMonth - 1).getCell(0).getNumericCellValue() != 0) {
                JSONObject json = new JSONObject().put("DAY_OF_MONTH", dayOfMonth);
                for (int hour = 0; hour < 24; hour++) {
                    double value = sheet.getRow(startRow + dayOfMonth - 1).getCell(hour + 1).getNumericCellValue();
                    json.put("T_" + hour, value);
                }
                result.add(json);
            }
        }
        
        return result;
    }
    
    private static void insertPowerRate(JSONObject powerRate) {
        DbHandler db = DbHandler.getInstance();
        JSONArray hourlyRates = (JSONArray) powerRate.remove("hourlyRates");
        int powerRateId = db.insert(powerRate, "POWER_RATE");
        for (Object hourlyRate : hourlyRates) {
            JSONObject hourlyRateJson = (JSONObject) hourlyRate;
            hourlyRateJson.put("POWER_RATE_ID", powerRateId);
            JSONArray values = (JSONArray) hourlyRateJson.remove("values");
            int hourlyPowerRateId = db.insert(hourlyRateJson, "HOURLY_POWER_RATE");
            for (Object value : values) {
                JSONObject valueJson = (JSONObject) value;
                valueJson.put("HOURLY_POWER_RATE_ID", hourlyPowerRateId);
            }
            db.insertList(values, "HOURLY_RATE_VALUES");
        }
    }
    
}
