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

public class DbHandler {

    private static final String CON_STR = "jdbc:sqlite:ver1.db";
    private static DbHandler instance;
    private final Connection connection;

    private DbHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        connection = DriverManager.getConnection(CON_STR);
    }

    public static synchronized DbHandler getInstance() throws SQLException {
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

    //    // Добавление продукта в БД
    //    public void addProduct(Product product) {
    //        // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
    //        try (PreparedStatement statement = this.connection.prepareStatement(
    //                "INSERT INTO Products(`good`, `price`, `category_name`) " +
    //                        "VALUES(?, ?, ?)")) {
    //            statement.setObject(1, product.good);
    //            statement.setObject(2, product.price);
    //            statement.setObject(3, product.category_name);
    //            // Выполняем запрос
    //            statement.execute();
    //        } catch (SQLException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    // Удаление продукта по id
    //    public void deleteProduct(int id) {
    //        try (PreparedStatement statement = this.connection.prepareStatement(
    //                "DELETE FROM Products WHERE id = ?")) {
    //            statement.setObject(1, id);
    //            // Выполняем запрос
    //            statement.execute();
    //        } catch (SQLException e) {
    //            e.printStackTrace();
    //        }
    //    }

}
