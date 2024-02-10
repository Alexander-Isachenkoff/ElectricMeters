package electricMeters;

import javafx.geometry.Pos;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class ColumnInfo {
    @XmlAttribute
    private String title = "";
    @XmlAttribute
    private String field = "";
    @XmlAttribute
    private int width = 100;
    @XmlAttribute
    private Pos alignment = Pos.CENTER_LEFT;
}
