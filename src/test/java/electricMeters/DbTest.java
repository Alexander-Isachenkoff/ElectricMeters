package electricMeters;

import electricMeters.core.DbHandler;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class DbTest {
    
    @BeforeEach
    void setUp() throws IOException {
        String dbFileName = "ver1.db";
        Path tempFile = Files.createTempFile("", dbFileName);
        Files.copy(new File(dbFileName).toPath(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        DbHandler.DB_URL = "jdbc:sqlite:" + tempFile;
        for (String table : List.of(
                "METERS_READINGS",
                "PEAK_HOURS",
                "PEAK_HOURS_YEARS",
                "PROFILE_STRS",
                "PROFILES",
                "TAR3_HOURLY_RATE_VALUES",
                "TAR2_VOLTAGE_RATES",
                "TAR1_MONTHLY_RATE",
                "VERIFICATION_METERS",
                "VERIFICATIONS")) {
            DbHandler.getInstance().runSqlUpdate("DELETE FROM " + table);
        }
    }
    
}
