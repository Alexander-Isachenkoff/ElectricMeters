package electricMeters;

import electricMeters.report.ProfileHourlyReport;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

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