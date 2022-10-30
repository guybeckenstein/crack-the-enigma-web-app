import jar.common.StartPrimaryStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URL;

public class AgentMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getClassLoader().getResource("main/app.fxml"); // Get fxml file resource
        StartPrimaryStage.start(primaryStage, url);
    }

    public static void main(String[] args) {
        Thread.currentThread().setName("jar/clients/agent/main");
        Application.launch(args);
    }
}
