package electricMeters;

import org.junit.jupiter.api.Test;

import java.util.List;

class TableInfoTest {

    @Test
    void load() {
        List<ColumnInfo> list = TableInfo.load("ProfilesEMInfo.xml");
        System.out.println();
    }
}