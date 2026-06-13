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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

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
              counterList.add(c);
              counters.getSelectionModel().select(c);
              storeCounters();
            });
  }
}
