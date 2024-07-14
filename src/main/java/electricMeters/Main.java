package electricMeters;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Locale.setDefault(Locale.forLanguageTag("ru"));
        Parent root = new FXMLLoader(getClass().getResource("fxml/main.fxml")).load();
        primaryStage.setTitle("Учет показаний электрических счетчиков");
        //оставляю ерунду чисто для проверки
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
