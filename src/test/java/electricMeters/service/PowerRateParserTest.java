package electricMeters.service;

import electricMeters.DbTest;
import electricMeters.core.DbHandler;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PowerRateParserTest extends DbTest {
    
    @Test
    void test() throws IOException {
        // act
        File file = new File("src/test/resources/electricMeters/tarrifes/2023.xls");
        PowerRateParser.readAndInsertPowerRates(file);
        
        // verify
        List<JSONObject> tar1MonthlyRate = DbHandler.getInstance().getAllFrom("TAR1_MONTHLY_RATE");
        assertEquals(66, tar1MonthlyRate.size());
        
        List<JSONObject> tar2VoltageRates = DbHandler.getInstance().getAllFrom("TAR2_VOLTAGE_RATES");
        assertEquals(264, tar2VoltageRates.size());
    }
    
}