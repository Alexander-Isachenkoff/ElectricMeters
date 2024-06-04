package electricMeters.service;

import electricMeters.DbTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.stream.Stream;

class ProfileUtilTest extends DbTest {
    
    @Test
    void readAndSave() {
        String dir = "src/test/resources/electricMeters/testProfiles";
        Stream.of(new File(dir).listFiles()).forEach(ProfileUtil::readAndSave);
    }
    
}