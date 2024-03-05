package electricMeters.report;

import electricMeters.Main;
import electricMeters.core.DbHandler;
import electricMeters.util.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ActOfConsumptionReport {
    
    private static final String TEMPLATE_XLSX = "templates/Акт расхода электроэнергии.xlsx";
    private static final String FONT_NAME = "Times New Roman";
    private static final short FONT_SIZE = (short) 12;
    
    public static void createAndWrite(int year, int month) {
        JSONObject report = createReport(year, month);
        String fileName = String.format("Акт расхода электроэнергии (%s %d).xlsx", DateUtil.monthName(month), year);
        File file = new File(fileName);
        write(report, file);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static JSONObject createReport(int year, int month) {
        List<JSONObject> actStrings = DbHandler.getInstance().runSqlSelectFile("ACT_CONSUMPTION_STRS.sql", year, month, year, month);
        return new JSONObject()
                .put("year", year)
                .put("month", month)
                .put("strings", actStrings);
    }
    
    private static void write(JSONObject json, File file) {
        try (Workbook wb = WorkbookFactory.create(Main.class.getResourceAsStream(TEMPLATE_XLSX))) {
            
            Font baseFont = wb.createFont();
            baseFont.setFontName(FONT_NAME);
            baseFont.setFontHeightInPoints(FONT_SIZE);
            
            CellStyle style = wb.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);
            style.setFont(baseFont);
            
            Sheet sheet = wb.getSheetAt(0);
            String month = DateUtil.monthName(json.getInt("month")).toLowerCase();
            
            RichTextString richString = new XSSFRichTextString(String.format("за %s %d года", month, json.getInt("year")));
            Font uFont = wb.createFont();
            uFont.setFontName(FONT_NAME);
            uFont.setFontHeightInPoints(FONT_SIZE);
            uFont.setUnderline(Font.U_SINGLE);
            richString.applyFont(baseFont);
            richString.applyFont(3, month.length() + 8, uFont);
            sheet.getRow(4).getCell(1).setCellValue(richString);
            
            JSONArray strings = json.getJSONArray("strings");
            
            sheet.shiftRows(11, 18, strings.length(), true, false);
            
            for (int i = 0; i < strings.length(); i++) {
                Row row = sheet.createRow(10 + i);
                JSONObject strJson = strings.getJSONObject(i);
                row.createCell(0).setCellValue(strJson.getInt("NPP"));
                row.createCell(1).setCellValue(strJson.optString("LOCATION"));
                if (strJson.has("METER_NUMBER")) {
                    row.createCell(2).setCellValue(strJson.getString("METER_NUMBER"));
                }
                if (strJson.has("READINGS_VALUE")) {
                    row.createCell(4).setCellValue(strJson.getDouble("READINGS_VALUE"));
                }
                if (strJson.has("PREV_READINGS")) {
                    row.createCell(5).setCellValue(strJson.getDouble("PREV_READINGS"));
                }
                row.createCell(6).setCellValue(strJson.optDouble("READINGS_DIFFERENCE", 0));
                if (strJson.has("CALC_COEFFICIENT")) {
                    row.createCell(7).setCellValue(strJson.getInt("CALC_COEFFICIENT"));
                }
                if (strJson.has("CALC_BY_METER")) {
                    row.createCell(8).setCellValue(strJson.getDouble("CALC_BY_METER"));
                }
                
                for (int j = 0; j <= 8; j++) {
                    row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(style);
                }
            }
            
            file.createNewFile();
            file.deleteOnExit();
            wb.write(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
