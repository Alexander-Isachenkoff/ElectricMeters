package electricMeters.core;

import electricMeters.Main;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DbHandler {

    public static String DB_URL = "jdbc:sqlite:ver1.db";
    private static DbHandler instance;
    private final Connection connection;

    private DbHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DB_URL, config.toProperties());
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
        if (jsonObjects.isEmpty()) {
            return null;
        }
        return jsonObjects.get(0);
    }
    
    public List<JSONObject> selectAllById(String tableName, Collection<Integer> id) {
        String inClause = id.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        String sql = String.format("SELECT * FROM %s WHERE ID in (%s)", tableName, inClause);
        return runSqlSelect(sql);
    }

    public CompletableFuture<List<JSONObject>> runSqlSelectFileAsync(String sqlFile, Object... params) {
        return CompletableFuture.supplyAsync(() -> runSqlSelectFile(sqlFile, params));
    }

    public List<JSONObject> runSqlSelectFile(String sqlFile, Object... params) {
        try (InputStream is = Main.class.getResourceAsStream("sql/" + sqlFile)) {
            if (is == null) {
                return new ArrayList<>();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String sql = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                return runSqlSelect(sql, params);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<JSONObject> runSqlSelect(String sql, Object... params) {
        System.out.println(sql + "; " + Arrays.toString(params));
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
            String[] columnNames = new String[metaData.getColumnCount()];
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }
            while (resultSet.next()) {
                JSONObject json = new JSONObject();
                for (int i = 0; i < columnNames.length; i++) {
                    String key = columnNames[i];
                    json.put(key, resultSet.getObject(i + 1));
                }
                list.add(json);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    
    public void runSqlUpdate(String sql, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                statement.setObject(i, params[i-1]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(JSONObject json, String table) {
        List<String> fields = json.keySet().stream().sorted().toList();
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

    public void update(JSONObject json, String table) {
        List<String> fields = new ArrayList<>(json.keySet());
        String setClause = fields.stream()
                .map(key -> key + " = ?")
                .collect(Collectors.joining(", "));

        String sql = String.format("UPDATE %s SET %s WHERE ID = ?", table, setClause);

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            for (int i = 0; i < fields.size(); i++) {
                statement.setObject(i + 1, json.get(fields.get(i)));
            }
            statement.setObject(fields.size() + 1, json.getInt("ID"));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void insertList(List<JSONObject> list, String table) {
        insertList(new JSONArray(list), table);
    }

    public void insertList(JSONArray jsonArray, String table) {
        List<String> fields = IntStream.range(0, jsonArray.length())
                .mapToObj(i -> jsonArray.getJSONObject(i).keySet())
                .flatMap(Collection::stream)
                .distinct()
                .toList();
        String insertFields = String.join(", ", fields);
        String insertParams = fields.stream()
                .map(key -> "?")
                .collect(Collectors.joining(", "));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, insertFields, insertParams);
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        final int BATCH_SIZE = 100;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int k = 1; k <= jsonArray.length(); k++) {
                JSONObject json = jsonArray.getJSONObject(k - 1);
                for (int i = 0; i < fields.size(); i++) {
                    statement.setObject(i + 1, json.opt(fields.get(i)));
                }
                statement.addBatch();
                if (k % BATCH_SIZE == 0 || k == jsonArray.length()) {
                    statement.executeBatch();
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateList(List<JSONObject> jsonObjects, String table) {
        List<String> fields = jsonObjects.stream()
                .flatMap(json -> json.keySet().stream())
                .distinct()
                .toList();

        String setClause = fields.stream()
                .map(key -> key + " = ?")
                .collect(Collectors.joining(", "));

        String sql = String.format("UPDATE %s SET %s WHERE ID = ?", table, setClause);

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        final int BATCH_SIZE = 50;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int k = 1; k <= jsonObjects.size(); k++) {
                JSONObject json = jsonObjects.get(k - 1);
                for (int i = 0; i < fields.size(); i++) {
                    statement.setObject(i + 1, json.opt(fields.get(i)));
                }
                statement.setObject(fields.size() + 1, json.getInt("ID"));
                statement.addBatch();
                if (k % BATCH_SIZE == 0 || k == jsonObjects.size()) {
                    statement.executeBatch();
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id, String table) {
        String sql = "DELETE FROM " + table + " WHERE ID = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY")) {
                UtilAlert.showWarning("Невозможно удалить запись, так как на нее есть ссылка!");
            } else {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void deleteList(List<JSONObject> jsonObjects, String table) {
        String sql = "DELETE FROM " + table + " WHERE ID = ?";
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        final int BATCH_SIZE = 50;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= jsonObjects.size(); i++) {
                JSONObject json = jsonObjects.get(i - 1);
                statement.setObject(1, json.getInt("ID"));
                statement.addBatch();
                if (i % BATCH_SIZE == 0 || i == jsonObjects.size()) {
                    statement.executeBatch();
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY")) {
                UtilAlert.showWarning("Невозможно удалить запись, так как на нее есть ссылка!");
            } else {
                throw new RuntimeException(e);
            }
        }
    }

}
