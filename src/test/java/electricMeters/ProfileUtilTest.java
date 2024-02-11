package electricMeters;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProfileUtilTest {
    
    @Test
    void readAndSave() {
        DbHandler.getInstance();
        Stream.of(new File("src/test/resources/electricMeters/testProfiles").listFiles())
                //.parallel()
                .forEach(ProfileUtil::readAndSave);
    }
    
}