package electricMeters.service;

import electricMeters.core.DbHandler;
import electricMeters.util.DateUtil;
import javafx.scene.control.Alert;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PeakHoursService {
    
    public static String FOLDER = "User Data/Peak Hours/";
    
    public static void readAndInsertPeakHours(File file) throws IOException {
        List<JSONObject> peakHours = PeakHoursParser.readPeakHours(file);
        if (!peakHours.isEmpty()) {
            Files.createDirectories(Paths.get(FOLDER));
            Files.copy(file.toPath(), Paths.get(FOLDER, file.getName()));
            int year = DateUtil.toLocalDate(peakHours.get(0).getString("DATE")).getYear();
            JSONObject peakYear = new JSONObject()
                    .put("YEAR", year)
                    .put("FILE_NAME", file.getName());
            int id = DbHandler.getInstance().insert(peakYear, "PEAK_HOURS_YEARS");
            peakHours.forEach(peakHour -> peakHour.put("YEAR_ID", id));
            DbHandler.getInstance().insertList(peakHours, "PEAK_HOURS");
        }
    }
    
    public static void openFile(String fileName) {
        try {
            File file = Paths.get(FOLDER, fileName).toFile();
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                String message = String.format("Файл \"%s\" не найден!", file.getAbsolutePath());
                new Alert(Alert.AlertType.ERROR, message).showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void delete(int yearId) {
        JSONObject jsonObject = DbHandler.getInstance().selectById("PEAK_HOURS_YEARS", yearId);
        if (jsonObject != null) {
            if (jsonObject.has("FILE_NAME")) {
                try {
                    Files.deleteIfExists(Paths.get(FOLDER, jsonObject.getString("FILE_NAME")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            DbHandler.getInstance().delete(yearId, "PEAK_HOURS_YEARS");
        }
    }
    
}
