package electricMeters.controls;

import electricMeters.DbHandler;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

@Getter
public class JsonTable extends TableView<JSONObject> {
    
    @Setter
    private String sqlFile;
    private Object[] params = new Object[0];
    
    public void reload() {
        try {
            List<JSONObject> objects = DbHandler.getInstance().runSqlSelectFile(sqlFile, params);
            this.getItems().setAll(objects);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setParams(Object... params) {
        this.params = params;
    }
    
    public void clear() {
        getItems().clear();
    }
    
}
