package dk.stonemountain.count.down;

import dk.stonemountain.count.down.CounterCell.UserAction;
import dk.stonemountain.count.down.util.JsonbHelper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

public class ApplicationController {
  private static final Logger LOG = Logger.getLogger(ApplicationController.class.getName());

  @FXML private ListView<Counter> counters;

  private ObservableList<Counter> counterList = FXCollections.observableArrayList();
  private Timeline ticker;

  @FXML
  private void initialize() {
    LOG.info("Application starting up");
    counters.setItems(counterList);
    counters.setCellFactory(listView -> new CounterCell(this::handleUserAction));

    loadCounters();

    // Attach completion listeners to loaded counters
    counterList.forEach(this::attachCompletionListener);

    ticker =
        new Timeline(
            new KeyFrame(
                javafx.util.Duration.seconds(1), e -> counterList.forEach(Counter::refresh)));
    ticker.setCycleCount(Timeline.INDEFINITE);
    ticker.play();
  }

  private void handleUserAction(UserAction action) {
    LOG.info(() -> String.format("User action: %s", action.type()));
    switch (action.type()) {
      case DELETED_RECORD -> counterList.remove(action.counter());
      default -> {}
    }
    // make changes persistenkt
    storeCounters();
  }

  private void storeCounters() {
    try {
      var json = JsonbHelper.toJson(counterList);
      var prefs = Preferences.userRoot().node("/countdown");
      prefs.put("counters", json);
      prefs.sync();
      LOG.info(() -> String.format("Stored %d counters", counterList.size()));
    } catch (Exception e) {
      LOG.severe(() -> String.format("Failed to store counters: %s", e.getMessage()));
    }
  }

  ParameterizedType listType =
      new ParameterizedType() {
        public Type[] getActualTypeArguments() {
          return new Type[] {Counter.class};
        }

        public Type getRawType() {
          return List.class;
        }

        public Type getOwnerType() {
          return null;
        }
      };

  private void loadCounters() {
    try {
      var prefs = Preferences.userRoot().node("/countdown");
      var json = prefs.get("counters", null);
      if (json != null) {
        List<Counter> counters = JsonbHelper.fromJson(json, listType);
        LOG.info(() -> String.format("Loaded %d counters", counters.size()));
        counterList.setAll(counters);
        counterList.forEach(Counter::refresh);
      }
      LOG.info(() -> String.format("Stored %d counters", counterList.size()));
    } catch (Exception e) {
      LOG.severe(() -> String.format("Failed to store counters: %s", e.getMessage()));
    }
  }

  private void attachCompletionListener(Counter c) {
    c.stateProperty()
        .addListener(
            (obs, oldState, newState) -> {
              if (oldState == Counter.State.ACTIVE && newState == Counter.State.COMPLETED) {
                // Show a small celebratory overlay when a counter completes
                String message =
                    (c.getDescription() == null || c.getDescription().isBlank())
                        ? "Done!"
                        : c.getDescription() + " reached 0!";
                showCelebration(message);
              }
            });
  }

  private void showCelebration(String message) {
    // Build a lightweight overlay: vector "happy" graphic with text over it (no emoji font needed)
    var graphic = HappyGraphicFactory.create();

    var text = new Label(message);
    text.setTextFill(Color.WHITE);
    text.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
    text.setEffect(new DropShadow(10, Color.color(0, 0, 0, 0.8)));

    var backBtn = new Button("Back");
    backBtn.setDefaultButton(true);
    backBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    var centerBox = new VBox(16, graphic, text, backBtn);
    centerBox.setAlignment(Pos.CENTER);

    var content = new StackPane(centerBox);
    content.setStyle(
        "-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 16; -fx-padding: 24;");

    var popup = new Popup();
    popup.getContent().add(content);
    popup.setAutoHide(false); // keep until user dismisses

    backBtn.setOnAction(e -> popup.hide());

    var window = counters.getScene().getWindow();
    popup.show(window);

    // Center after showing (so we have actual layout bounds)
    Platform.runLater(
        () -> {
          double pw = content.getWidth();
          double ph = content.getHeight();
          double x = window.getX() + (window.getWidth() - pw) / 2;
          double y = window.getY() + (window.getHeight() - ph) / 2;
          popup.setX(x);
          popup.setY(y);
        });
  }

  @FXML
  private void doQuit() {
    ticker.stop();
    try {
      JavaFXApplication.getInstance().stop();
    } catch (Exception e) {
      LOG.severe(() -> String.format("Failed to quit: %s", e.getMessage()));
    }
  }

  @FXML
  private void doAbout() {}

  @FXML
  private void doAddCounter(ActionEvent event) {
    var dialog = new CounterDialog(null);
    dialog.initOwner(counters.getScene().getWindow());
    dialog
        .showAndWait()
        .ifPresent(
            c -> {
              c.refresh();
              attachCompletionListener(c);
              counterList.add(c);
              counters.getSelectionModel().select(c);
              storeCounters();
            });
  }
}
