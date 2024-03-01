package electricMeters.core;

import electricMeters.service.PowerRateParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class PowerRateParserTest {
    
    @Test
    void test() throws IOException {
        File file = new File("src/test/resources/electricMeters/tarrifes/2023.xls");
        PowerRateParser.readAndInsertPowerRates(file);
    }
    
}