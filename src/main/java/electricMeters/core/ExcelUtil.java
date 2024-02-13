package electricMeters.core;

import electricMeters.core.controls.JsonColumn;
import electricMeters.core.controls.JsonTable;
import javafx.collections.ObservableList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ExcelUtil {

    public static void exportExcel(JsonTable table) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet();

            // Заголовок таблицы
            List<JsonColumn> columns = table.getJsonColumns();
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i).getText());
            }

            ObservableList<JSONObject> items = table.getItems();
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.get(i);
                XSSFRow row = sheet.createRow(i + 1);
                for (int j = 0; j < columns.size(); j++) {
                    String key = columns.get(j).getField();
                    if (item.has(key)) {
                        Object value = item.get(key);
                        XSSFCell cell = row.createCell(j);
                        if (value instanceof Double || value instanceof Integer) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(String.valueOf(value));
                        }
                    }
                }
            }

            for (int i = 0; i < columns.size(); i++) {
                sheet.setColumnWidth(i, (int) (columns.get(i).getWidth() * 40));
            }

            File file = new File("export.xlsx");
            workbook.write(Files.newOutputStream(file.toPath()));
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
