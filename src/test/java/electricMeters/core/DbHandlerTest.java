package electricMeters.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DbHandlerTest {
    
    public static final DbHandler DB = DbHandler.getInstance();
    
    @Test
    void runSqlSelectFile() {
        for (int id = 1; id <= 49; id++) {
            DB.runSqlSelectFile("PROFILE_STRS_VW.sql", id);
        }
    }
    
}
