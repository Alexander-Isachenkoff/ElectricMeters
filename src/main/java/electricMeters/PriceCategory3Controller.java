package electricMeters;

import electricMeters.core.DbHandler;
import electricMeters.core.controls.JsonComboBox;
import electricMeters.core.controls.JsonTable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PriceCategory3Controller {
    
    @FXML
    private JsonTable table;
    @FXML
    private JsonComboBox rateTypeCmb;
    @FXML
    private JsonComboBox voltageLevelCmb;
    @FXML
    private ComboBox<Month> monthCmb;
    @FXML
    private ComboBox<Integer> yearCmb;
    
    @FXML
    private void initialize() {
        monthCmb.getItems().addAll(Month.values());
        yearCmb.getItems().addAll(IntStream.rangeClosed(2018, LocalDate.now().getYear()).boxed().collect(Collectors.toList()));
        table.setSqlFile("HOURLY_RATE_VALUES.sql");
    }
    
    @FXML
    private void onApply() {
        table.setParams(
                yearCmb.getSelectionModel().getSelectedItem(),
                monthCmb.getSelectionModel().getSelectedItem().getValue() + 1,
                rateTypeCmb.getSelectedItem().getInt("ID"),
                voltageLevelCmb.getSelectedItem().getInt("ID")
        );
        table.reload();
    }
    
    @FXML
    private void onAdd() throws IOException {
        File file = new FileChooser().showOpenDialog(null);
        if (file != null) {
            List<JSONObject> powerRates = readPowerRate(file);
            DbHandler db = DbHandler.getInstance();
            for (JSONObject powerRate : powerRates) {
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
                        db.insert(valueJson, "HOURLY_RATE_VALUES");
                    }
                }
            }
        }
    }
    
    private List<JSONObject> readPowerRate(File file) throws IOException {
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
    
    private List<JSONObject> parsePowerRates(Sheet sheet) {
        List<JSONObject> types = DbHandler.getInstance().getAllFrom("POWER_RATE_TYPES");
        Map<String, Integer> ids = types.stream().collect(Collectors.toMap(
                json -> json.getString("CODE"),
                json -> json.getInt("ID")
        ));
        Map<Integer, String> codes = types.stream().collect(Collectors.toMap(
                json -> json.getInt("ID"),
                json -> json.getString("CODE")
        ));
        
        String value = sheet.getRow(839).getCell(8).getStringCellValue();
        double capRate = Double.parseDouble(value.replace(",", "."));
        JSONObject pr10000no = new JSONObject()
                .put("POWER_RATE_TYPE_ID", ids.get("СТАВКА_10000_БЕЗ_ПЕР"))
                .put("CAP_RATE", capRate);
        List<JSONObject> result = Collections.singletonList(pr10000no);
        for (JSONObject json : result) {
            List<JSONObject> hourlyRates = parseHourlyPowerRates(sheet, codes.get(json.getInt("POWER_RATE_TYPE_ID")));
            json.put("hourlyRates", hourlyRates);
        }
        return result;
    }
    
    private List<JSONObject> parseHourlyPowerRates(Sheet sheet, String code) {
        List<JSONObject> result = new ArrayList<>();
        result.add(new JSONObject().put("VOLTAGE_LEVEL_ID", 1));
        for (JSONObject json : result) {
            List<JSONObject> values = parseHourlyPowerRateValues(sheet, code, json.getInt("VOLTAGE_LEVEL_ID"));
            json.put("values", values);
        }
        return result;
    }
    
    private List<JSONObject> parseHourlyPowerRateValues(Sheet sheet, String code, int voltageId) {
        List<JSONObject> result = new ArrayList<>();
        
        Map<String, Integer> map = new HashMap<>();
        map.put("СТАВКА_10000_БЕЗ_ПЕР", 705);
        
        int startRow = map.get(code) + (voltageId - 1) * 34;
        for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
            JSONObject json = new JSONObject().put("DAY_OF_MONTH", dayOfMonth);
            for (int hour = 0; hour < 24; hour++) {
                double value = sheet.getRow(startRow + dayOfMonth - 1).getCell(hour + 1).getNumericCellValue();
                json.put("T_" + hour, value);
            }
            result.add(json);
        }
        return result;
    }
    
}
