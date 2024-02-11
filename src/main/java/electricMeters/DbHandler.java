package electricMeters;

import org.json.JSONObject;
import org.sqlite.JDBC;
import org.sqlite.jdbc4.JDBC4ResultSet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DbHandler {

    private static final String CON_STR = "jdbc:sqlite:ver1.db";
    private static DbHandler instance;
    private final Connection connection;

    private DbHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            connection = DriverManager.getConnection(CON_STR);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized DbHandler getInstance() {
        if (instance == null) {
            instance = new DbHandler();
        }
        return instance;
    }

    private static Object getValue(JDBC4ResultSet resultSet, String key, String typeName) throws SQLException {
        switch (typeName) {
            case "INTEGER":
                return resultSet.getInt(key);
            case "TEXT":
                return resultSet.getString(key);
            case "REAL":
                return resultSet.getDouble(key);
            default:
                return JSONObject.NULL;
        }
    }

    public List<JSONObject> getAllFrom(String tableName) {
        return runSqlSelect("SELECT * FROM " + tableName);
    }
    
    public List<JSONObject> runSqlSelectFile(String sqlFile, Object... params) {
        try {
            Path path = Paths.get(Main.class.getResource("sql/" + sqlFile).toURI());
            String sql = String.join(" ", Files.readAllLines(path));
            return runSqlSelect(sql, params);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<JSONObject> runSqlSelect(String sql, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                statement.setObject(i, params[i-1]);
            }
            ResultSet resultSet = statement.executeQuery();
            return toJSON((JDBC4ResultSet) resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<JSONObject> toJSON(JDBC4ResultSet resultSet) {
        List<JSONObject> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                JSONObject json = new JSONObject();
                for (int i = 1; i <= resultSet.getColumnCount(); i++) {
                    String key = resultSet.getColumnName(i);
                    String typeName = resultSet.getColumnTypeName(i);
                    Object value = getValue(resultSet, key, typeName);
                    json.put(key, value);
                }
                list.add(json);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void insert(JSONObject json, String table) {
        List<String> fields = json.keySet().stream().sorted().collect(Collectors.toList());
        String insertFields = fields.stream()
                .map(key -> "'" + key + "'")
                .collect(Collectors.joining(", "));
        String insertParams = fields.stream()
                .map(key -> "?")
                .collect(Collectors.joining(", "));
        String sql = String.format("INSERT INTO %s (%s) VALUES (" + insertParams + ")", table, insertFields);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            for (int i = 0; i < fields.size(); i++) {
                statement.setObject(i + 1, json.get(fields.get(i)));
            }
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void delete(int id, String table) {
        try (PreparedStatement statement = this.connection.prepareStatement("DELETE FROM " + table + " WHERE id = ?")) {
            statement.setObject(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
