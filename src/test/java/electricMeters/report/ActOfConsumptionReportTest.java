package electricMeters.report;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActOfConsumptionReportTest {

    @Test
    void createAndWrite() {
        ActOfConsumptionReport.createAndWrite(2023, 11);
    }

}