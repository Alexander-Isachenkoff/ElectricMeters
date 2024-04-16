package electricMeters.core.controls;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.lang.reflect.Field;

public interface FormCollector {
    
    default void fillData(JSONObject meterJson) {
        for (Field field : this.getClass().getDeclaredFields()) {
            Object object;
            try {
                field.setAccessible(true);
                object = field.get(this);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (object instanceof TextField textField) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (annotation != null) {
                    String key = annotation.field();
                    if (meterJson.has(key)) {
                        textField.setText(String.valueOf(meterJson.get(key)));
                    }
                }
            }
            if (object instanceof JTextField textField) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (annotation != null) {
                    String key = annotation.field();
                    if (meterJson.has(key)) {
                        textField.setText(String.valueOf(meterJson.get(key)));
                    }
                }
            }
            if (object instanceof CheckBox checkBox) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (annotation != null) {
                    String key = annotation.field();
                    if (meterJson.has(key)) {
                        checkBox.setSelected(meterJson.getInt(key) == 1);
                    }
                }
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
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (annotation != null) {
                    if (required.isRequired() && required.isEmpty()) {
                        String message = "Обязательное поле \"%s\" не заполнено".formatted(required.getName());
                        Alert alert = new Alert(Alert.AlertType.WARNING, message);
                        alert.setTitle("Предупреждение");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                        return false;
                    }
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
            if (object instanceof TextField textField) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (annotation != null) {
                    String key = annotation.field();
                    jsonObject.put(key, textField.getText());
                }
            }
            if (object instanceof JTextField textField) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (annotation != null) {
                    String key = annotation.field();
                    jsonObject.put(key, textField.getText());
                }
            }
            if (object instanceof CheckBox checkBox) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (annotation != null) {
                    String key = annotation.field();
                    jsonObject.put(key, checkBox.isSelected() ? 1 : 0);
                }
            }
        }
        return jsonObject;
    }
    
}