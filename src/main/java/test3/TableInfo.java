package test3;


import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class TableInfo {
    @XmlElement(name = "column")
    private List<ColumnInfo> columnInfo;

    public static List<ColumnInfo> load(String file) {
        return FileUtils.loadXmlObject(file, TableInfo.class).columnInfo;
    }

    public static List<TableColumn<JSONObject, Object>> createColumns(String xml) {
        return TableInfo.load(xml).stream()
                .map(info -> UtilTable.createColumn(
                        info.getTitle(),
                        info.getField(),
                        info.getWidth(),
                        info.getAlignment()
                )).collect(Collectors.toList());
    }

}
