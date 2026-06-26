package dk.stonemountain.count.down;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class JavaFXApplication extends Application {
    private static final Logger LOG = Logger.getLogger(JavaFXApplication.class.getName());

    private static Application instance;

    public static Application getInstance() {
        if (instance == null) {
            instance = new JavaFXApplication();
        }
        return instance;
    }

    Stage primaryStage;

    // double width;
    // double height;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        LOG.info(() -> String.format("Application starting up: %s", getParameters().getRaw()));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/application.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/dark.css").toExternalForm());

        primaryStage.setTitle("Count Down");
        primaryStage.setScene(scene);

        var prefs = Preferences.userRoot().node("/countdown");
        double width = prefs.getDouble("window.width", 517);
        double height = prefs.getDouble("window.height", 440);
        LOG.info(() -> String.format("Window size: %.0fx%.0f", width, height));
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        LOG.info(
                () ->
                        String.format(
                                "Application stopping down: %.0fx%.0f",
                                primaryStage.getWidth(), primaryStage.getHeight()));
        // store current size

        var prefs = Preferences.userRoot().node("/countdown");
        prefs.putDouble("window.width", primaryStage.getWidth());
        prefs.putDouble("window.height", primaryStage.getHeight());

        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
