package electricMeters.core;

import electricMeters.Main;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sqlite.JDBC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
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

    public List<JSONObject> getAllFrom(String tableName) {
        return runSqlSelect("SELECT * FROM " + tableName);
    }

    public JSONObject selectById(String tableName, int id) {
        String sql = String.format("SELECT * FROM %s WHERE ID = ?", tableName);
        List<JSONObject> jsonObjects = runSqlSelect(sql, id);
        return jsonObjects.get(0);
    }

    public List<JSONObject> runSqlSelectFile(String sqlFile, Object... params) {
        try {
            Path path = Paths.get(Main.class.getResource("sql/" + sqlFile).toURI());
            String sql = String.join("\n", Files.readAllLines(path));
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
            return toJSON(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<JSONObject> toJSON(ResultSet resultSet) {
        List<JSONObject> list = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                JSONObject json = new JSONObject();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String key = metaData.getColumnName(i);
                    json.put(key, resultSet.getObject(i));
                }
                list.add(json);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public int insert(JSONObject json, String table) {
        List<String> fields = json.keySet().stream().sorted().collect(Collectors.toList());
        String insertFields = fields.stream()
                .map(key -> "'" + key + "'")
                .collect(Collectors.joining(", "));
        String insertParams = fields.stream()
                .map(key -> "?")
                .collect(Collectors.joining(", "));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, insertFields, insertParams);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            for (int i = 0; i < fields.size(); i++) {
                statement.setObject(i + 1, json.get(fields.get(i)));
            }
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertList(JSONArray jsonArray, String table) {
        Set<String> fieldsSet = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            fieldsSet.addAll(jsonArray.getJSONObject(i).keySet());
        }
        List<String> fields = new ArrayList<>(fieldsSet);

        String insertFields = fields.stream()
                .map(key -> "'" + key + "'")
                .collect(Collectors.joining(", "));
        String insertParamsLine = fields.stream()
                .map(key -> "?")
                .collect(Collectors.joining(", ", "(", ")"));
        String insertParamsClause = jsonArray.toList().stream()
                .map(object -> insertParamsLine)
                .collect(Collectors.joining(",\n"));
        String sql = String.format("INSERT INTO %s (%s) VALUES %s", table, insertFields, insertParamsClause);

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                for (int j = 0; j < fields.size(); j++) {
                    String key = fields.get(j);
                    int parameterIndex = i * fields.size() + j + 1;
                    Object value = json.has(key) ? json.get(key) : null;
                    statement.setObject(parameterIndex, value);
                }
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
