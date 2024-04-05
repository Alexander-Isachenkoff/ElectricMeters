package electricMeters.service;

import electricMeters.util.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class PeakHoursParser {
    
    static List<JSONObject> readPeakHours(File file) throws IOException {
        List<JSONObject> peakHours = new ArrayList<>();
        try (Workbook book = WorkbookFactory.create(file)) {
            book.sheetIterator().forEachRemaining(sheet -> {
                int rowNum = 8;
                while (true) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null || row.getCell(0) == null) {
                        break;
                    }
                    String stringDate = row.getCell(0).getStringCellValue();
                    if (stringDate.isEmpty()) {
                        break;
                    }
                    String date = DateUtil.convert(stringDate, DateUtil.DATE_FORMAT, DateUtil.DB_DATE_FORMAT);
                    int hour = (int) row.getCell(1).getNumericCellValue();
                    JSONObject peakHour = new JSONObject()
                            .put("DATE", date)
                            .put("HOUR", hour);
                    peakHours.add(peakHour);
                    rowNum++;
                }
            });
        }
        return peakHours;
    }
    
}
