package electricMeters;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ProfileHourlyReportTest {

    @Test
    void test() {
        JSONObject report = ProfileHourlyReport.createReport(5);
        System.out.println(report);
    }

    @Test
    void testWrite() throws IOException {
        JSONObject report = ProfileHourlyReport.createReport(5);
        File file = new File("report.xlsx");
        ProfileHourlyReport.write(report, file);
        Desktop.getDesktop().open(file);
    }
}