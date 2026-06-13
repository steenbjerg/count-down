package dk.stonemountain.count.down;

import java.util.List;
import java.util.logging.Logger;
import javafx.application.Application;

public class Main {
  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    LOG.info(() -> String.format("Arguments: %s", List.of(args)));
    LOG.info(
        () ->
            String.format(
                "Starting up: command = %s, command line = %s",
                ProcessHandle.current().info().command(),
                ProcessHandle.current().info().commandLine()));

    // Launch the JavaFX application
    Application.launch(JavaFXApplication.class, args);
  }
}
