package electricMeters.core.controls;

import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
public class JCheckBox extends CheckBox implements JsonField<Integer> {

    private String key = "";

    @Override
    public void setValue(JSONObject json) {
        this.setSelected(json.optInt(key) == 1);
    }

    @Override
    public Integer getValue() {
        return isSelected() ? 1 : 0;
    }

}
