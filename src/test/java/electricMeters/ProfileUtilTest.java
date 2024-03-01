package electricMeters;

import electricMeters.core.DbHandler;
import electricMeters.service.ProfileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.stream.Stream;

class ProfileUtilTest {
    
    @Test
    void readAndSave() {
        DbHandler.getInstance();
        Stream.of(new File("src/test/resources/electricMeters/testProfiles").listFiles())
                //.parallel()
                .forEach(ProfileUtil::readAndSave);
    }
    
}