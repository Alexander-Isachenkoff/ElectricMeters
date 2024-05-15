package electricMeters.core.controls;

import electricMeters.core.UtilAlert;
import org.json.JSONObject;

import java.lang.reflect.Field;

public interface FormCollector {

    default void fillData(JSONObject json) {
        for (Field field : this.getClass().getDeclaredFields()) {
            Object object;
            try {
                field.setAccessible(true);
                object = field.get(this);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (object instanceof JsonField<?> jsonField) {
                jsonField.setValue(json);
            }
        }
    }

    default boolean checkRequired() {
        for (Field field : this.getClass().getDeclaredFields()) {
            Object object;
            try {
                field.setAccessible(true);
                object = field.get(this);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (object instanceof Required required) {
                if (required.isRequired() && required.isEmpty()) {
                    String message = "Обязательное поле \"%s\" не заполнено".formatted(required.getName());
                    UtilAlert.showWarning(message);
                    return false;
                }
            }
        }
        return true;
    }

    default JSONObject collectData() {
        JSONObject jsonObject = new JSONObject();
        for (Field field : this.getClass().getDeclaredFields()) {
            Object object;
            try {
                field.setAccessible(true);
                object = field.get(this);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (object instanceof JsonField<?> jsonField) {
                jsonObject.put(jsonField.getKey(), jsonField.getValue());
            }
        }
        return jsonObject;
    }
    
}
