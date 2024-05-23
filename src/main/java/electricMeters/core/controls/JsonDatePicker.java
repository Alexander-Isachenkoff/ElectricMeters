package electricMeters.core.controls;

import electricMeters.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.time.LocalDate;

@Setter
@Getter
public class JsonDatePicker extends DatePickerPlus implements RequiredJsonField<LocalDate> {

    private String key = "";
    
    @Override
    public void setValue(JSONObject json) {
        setValue(DateUtil.toLocalDate(json.getString(getKey())));
    }
    
}
