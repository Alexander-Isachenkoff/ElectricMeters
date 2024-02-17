package electricMeters;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MainController {
    
    private static final Map<String, String> menuFxmlMap;
    
    static {
        menuFxmlMap = new LinkedHashMap<>();
       //menuFxmlMap.put("Акты", "fxml/empty.fxml");
        //menuFxmlMap.put("Инфо", "fxml/empty.fxml");
        //menuFxmlMap.put("Приборы учета", "fxml/empty.fxml");
        menuFxmlMap.put("Записи", "fxml/profiles.fxml");
        menuFxmlMap.put("Третья ценовая категория", "fxml/3_price_category.fxml");
        //menuFxmlMap.put("Показатели", "fxml/empty.fxml");
        //menuFxmlMap.put("Тарифы", "fxml/empty.fxml");
    }

    @FXML
    private Menu menu;
    @FXML
    private TabPane tabPane;
    
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
        for (String name : menuFxmlMap.keySet()) {
            MenuItem menuItem = new MenuItem(name);
            menuItem.setOnAction(event -> showNewOrSwitchTab(name));
            menu.getItems().add(menuItem);
        }
    }
    
    private void showNewOrSwitchTab(String name) {
        Optional<Tab> optionalTab = tabPane.getTabs().stream()
                .filter(tab -> tab.getText().equals(name))
                .findFirst();
        if (optionalTab.isPresent()) {
            tabPane.getSelectionModel().select(optionalTab.get());
        } else {
            Parent load = loadFxml(menuFxmlMap.get(name));
            newTab(name, load);
        }
    }
    
    private void newTab(String title, Parent load) {
        Tab signInTab = new Tab(title);
        signInTab.setContent(load);
        tabPane.getTabs().add(signInTab);
        tabPane.getSelectionModel().select(signInTab);
    }

}
