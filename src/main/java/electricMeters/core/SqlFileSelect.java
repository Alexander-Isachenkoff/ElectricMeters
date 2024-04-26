package electricMeters.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Builder
@AllArgsConstructor
public class SqlFileSelect {

    private final String sqlFile;
    private final List<?> params;

    public SqlFileSelect(String sqlFile, Object... params) {
        this.sqlFile = sqlFile;
        this.params = List.of(params);
    }

    public CompletableFuture<List<JSONObject>> runAsync() {
        return CompletableFuture.supplyAsync(this::run);
    }

    public List<JSONObject> run() {
        return DbHandler.getInstance().runSqlSelectFile(sqlFile, params.toArray());
    }

}
