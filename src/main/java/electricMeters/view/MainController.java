package electricMeters.view;

import electricMeters.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    
    private final Map<String, String> menuFxmlMap = new LinkedHashMap<>();
    private final Map<String, Node> contentCache = new HashMap<>();
    private final List<ListView<?>> listViews = new ArrayList<>();
    public StackPane contentPane;
    public VBox accordion;
    
    private static Parent loadFxml(String s) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(s));
        Parent load;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return load;
    }
    
    @FXML
    private void initialize() {
        InputStream in = Main.class.getResourceAsStream("menu/menu.json");
        String string = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
        JSONObject menuJson = new JSONObject(string);
        
        accordion.getChildren().clear();
        for (Object tab : menuJson.getJSONArray("tabs")) {
            JSONObject tabJson = (JSONObject) tab;
            ListView<String> menuList = new ListView<>();
            menuList.getStyleClass().add("menu-list-view");
            accordion.getChildren().add(new TitledPane(tabJson.getString("title"), menuList));
            for (Object item : tabJson.getJSONArray("items")) {
                JSONObject json = (JSONObject) item;
                String title = json.getString("title");
                menuList.getItems().add(title);
                menuFxmlMap.put(title, json.getString("fxml"));
            }
            menuList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    showFxmlOnContentPane(newValue);
                    System.out.println(newValue);
                    for (ListView<?> listView : listViews) {
                        if (listView != menuList) {
                            listView.getSelectionModel().clearSelection();
                        }
                    }
                }
            });
            listViews.add(menuList);
            menuList.setPrefHeight(menuList.getItems().size() * 33 + 16);
        }
    }
    
    private void showFxmlOnContentPane(String fxml) {
        Node node;
        if (contentCache.containsKey(fxml)) {
            node = contentCache.get(fxml);
        } else {
            node = loadFxml(menuFxmlMap.get(fxml));
            contentCache.put(fxml, node);
        }
        if (node instanceof TabPane) {
            contentPane.setPadding(Insets.EMPTY);
        } else {
            contentPane.setPadding(new Insets(12));
        }
        contentPane.getChildren().setAll(node);
    }
    
}
