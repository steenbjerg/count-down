package dk.stonemountain.count.down;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CounterCell extends ListCell<Counter> {
  public enum UserActionType {
    NEW_RECORD,
    CHANGED_RECORD,
    DELETED_RECORD
  }

  public record UserAction(UserActionType type, Counter counter) {}

  private static final DateTimeFormatter EXPIRATION_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM yyyy HH:mm");

  private final Node node;

  @FXML Label alarmTime;
  @FXML Label countDown;
  @FXML Label unitLabel;
  @FXML Label title;
  @FXML private ProgressBar progress;
  @FXML private Pane status;

  private Consumer<UserAction> userActionConsumer;

  public static enum CountDownUnit {
    SECONDS("Seconds", ChronoUnit.SECONDS),
    MINUTES("Minutes", ChronoUnit.MINUTES),
    HOURS("Hours", ChronoUnit.HOURS),
    DAYS("Days", ChronoUnit.DAYS),
    YEARS("Years", ChronoUnit.YEARS);

    private final String label;
    private final ChronoUnit timeUnit;

    CountDownUnit(String label, ChronoUnit timeUnit) {
      this.label = label;
      this.timeUnit = timeUnit;
    }

    public String getLabel() {
      return label;
    }

    public ChronoUnit getTimeUnit() {
      return timeUnit;
    }
  }

  public CounterCell(Consumer<UserAction> userActionConsumer) {
    super();
    this.userActionConsumer = userActionConsumer;

    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setController(this);
      loader.setLocation(this.getClass().getResource("/fxml/count-down-cell.fxml"));
      node = loader.load(this.getClass().getResourceAsStream("/fxml/count-down-cell.fxml"));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load fxml", e);
    }
  }

  @FXML
  public void init() {}

  @FXML
  void doDelete(ActionEvent event) {
    var counter = getItem();
    if (counter != null) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.initOwner(this.getScene().getWindow());
      alert.setTitle("Confirm");
      alert.setHeaderText("Delete counter?");
      alert.setContentText("This cannot be undone.");

      alert
          .showAndWait()
          .ifPresent(
              buttonType -> {
                if (buttonType == ButtonType.OK) {
                  userActionConsumer.accept(new UserAction(UserActionType.DELETED_RECORD, counter));
                }
              });
    }
  }

  @FXML
  void doConfig(ActionEvent event) {
    var counter = getItem();
    if (counter != null) {
      var newCounter =
          new Counter(
              counter.getDescription(),
              counter.getExpiration(),
              counter.getTimeLeft(),
              counter.getTimeUnit(),
              counter.getStartTime());
      var dialog = new CounterDialog(newCounter);
      dialog.initOwner(this.getScene().getWindow());
      dialog
          .showAndWait()
          .ifPresent(
              c -> {
                counter.setDescription(c.getDescription());
                counter.setExpiration(c.getExpiration());
                counter.setTimeLeft(c.getTimeLeft());
                counter.setTimeUnit(c.getTimeUnit());
                counter.setStartTime(c.getStartTime());
                counter.setProgress(c.getProgress());
                counter.refresh();
                userActionConsumer.accept(new UserAction(UserActionType.CHANGED_RECORD, counter));
              });
    }
  }

  @Override
  protected void updateItem(Counter counter, boolean empty) {
    super.updateItem(counter, empty);

    // Always clear bindings from the previous item first
    title.textProperty().unbind();
    alarmTime.textProperty().unbind();
    countDown.textProperty().unbind();
    unitLabel.textProperty().unbind();

    if (empty || counter == null) {
      setGraphic(null);
    } else {
      title.textProperty().bind(counter.descriptionProperty());

      alarmTime
          .textProperty()
          .bind(
              Bindings.createStringBinding(
                  () ->
                      counter.getExpiration() != null
                          ? counter.getExpiration().format(EXPIRATION_FORMAT)
                          : "",
                  counter.expirationProperty()));

      countDown
          .textProperty()
          .bind(
              Bindings.createStringBinding(
                  () -> formatTimeLeft(counter.getTimeLeft(), counter.getTimeUnit(), counter),
                  counter.timeLeftProperty(),
                  counter.timeUnitProperty()));

      unitLabel
          .textProperty()
          .bind(
              Bindings.createStringBinding(
                  () ->
                      counter.getTimeUnit() != null ? capitalize(counter.getTimeUnit().name()) : "",
                  counter.timeUnitProperty()));

      progress.progressProperty().bind(counter.progressProperty());

      status
          .backgroundProperty()
          .bind(
              Bindings.createObjectBinding(
                  () ->
                      switch (counter.getState()) {
                        case ACTIVE -> new Background(new BackgroundFill(Color.GREEN, null, null));
                        case COMPLETED -> new Background(new BackgroundFill(Color.RED, null, null));
                      },
                  counter.stateProperty()));

      setGraphic(node);
    }
  }

  private String formatTimeLeft(Duration duration, ChronoUnit unit, Counter counter) {
    if (duration == null || unit == null) return "";
    if (duration.isNegative()) return "0";
    return switch (unit) {
      case YEARS -> {
        if (counter.getExpiration() != null
            && counter.getExpiration().isAfter(LocalDateTime.now())) {
          yield String.valueOf(
              ChronoUnit.YEARS.between(LocalDateTime.now(), counter.getExpiration()));
        } else {
          yield "0";
        }
      }
      case DAYS -> String.valueOf(duration.toDays());
      case HOURS -> String.valueOf(duration.toHours());
      case MINUTES -> String.valueOf(duration.toMinutes());
      case SECONDS -> String.valueOf(duration.toSeconds());
      default -> String.valueOf(duration.toDays());
    };
  }

  private String capitalize(String s) {
    if (s == null || s.isEmpty()) return s;
    return s.charAt(0) + s.substring(1).toLowerCase();
  }
}
