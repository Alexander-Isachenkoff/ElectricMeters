package test3;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableInfoTest {

    @Test
    void load() {
        List<ColumnInfo> list = TableInfo.load("ProfilesEMInfo.xml");
        System.out.println();
    }
}