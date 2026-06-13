package dk.stonemountain.count.down;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class CounterDialog extends Dialog<Counter> {
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  @FXML private TextField header;
  @FXML private DatePicker expirationDate;
  @FXML private TextField expirationTime;
  @FXML private DatePicker startDate;
  @FXML private TextField startTime;
  @FXML private ChoiceBox<ChronoUnit> timeUnit;

  private final Counter counter;

  /** Opens the dialog pre-populated with an existing {@link Counter} for editing. */
  public CounterDialog(Counter counter) {
    if (counter == null) {
      this.counter = new Counter();
    } else {
      this.counter = counter;
    }

    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setController(this);
      loader.setLocation(getClass().getResource("/fxml/counter-form.fxml"));
      Node node = loader.load();

      setTitle(counter == null ? "Add Counter" : "Edit Counter");
      setHeaderText(null);
      getDialogPane().setContent(node);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load counter-form.fxml", e);
    }
  }

  @FXML
  private void initialize() {
    setResultConverter(
        buttonType -> buttonType == ButtonType.OK ? updateCounterExpiration(counter) : null);
    timeUnit
        .getItems()
        .addAll(
            ChronoUnit.SECONDS,
            ChronoUnit.MINUTES,
            ChronoUnit.HOURS,
            ChronoUnit.DAYS,
            ChronoUnit.YEARS);
    timeUnit.setValue(ChronoUnit.SECONDS);
    timeUnit.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(ChronoUnit unit) {
            if (unit == null) return "";
            String name = unit.name();
            return name.charAt(0) + name.substring(1).toLowerCase();
          }

          @Override
          public ChronoUnit fromString(String s) {
            return ChronoUnit.valueOf(s.toUpperCase());
          }
        });

    header.setText(counter.descriptionProperty().get());
    timeUnit.setValue(counter.timeUnitProperty().get());
    expirationDate.setValue(
        counter.getExpiration() != null ? counter.getExpiration().toLocalDate() : LocalDate.now());
    expirationTime.setText(
        counter.getExpiration() != null
            ? counter.getExpiration().toLocalTime().format(TIME_FORMATTER)
            : "00:00");
    startDate.setValue(
        counter.getStartTime() != null ? counter.getStartTime().toLocalDate() : LocalDate.now());
    startTime.setText(
        counter.getStartTime() != null
            ? counter.getStartTime().toLocalTime().format(TIME_FORMATTER)
            : "00:00");

    // get ok button and disable property
    getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
    okButton
        .disableProperty()
        .bind(
            Bindings.createBooleanBinding(
                () ->
                    expirationDate.getValue() == null
                        || expirationTime.getText().isBlank()
                        || !expirationTime.getText().matches("^\\d{2}:\\d{2}$")
                        || startDate.getValue() == null
                        || startTime.getText().isBlank()
                        || !startTime.getText().matches("^\\d{2}:\\d{2}$")
                        || header.getText().isBlank()
                        || timeUnit.getValue() == null,
                expirationDate.valueProperty(),
                expirationTime.textProperty(),
                timeUnit.valueProperty(),
                header.textProperty()));
  }

  private Counter updateCounterExpiration(Counter counter) {
    String[] parts = expirationTime.getText().split(":");
    int hours = Integer.parseInt(parts[0]);
    int minutes = Integer.parseInt(parts[1]);
    counter.setExpiration(
        LocalDateTime.of(expirationDate.getValue(), LocalTime.of(hours, minutes)));
    parts = startTime.getText().split(":");
    hours = Integer.parseInt(parts[0]);
    minutes = Integer.parseInt(parts[1]);
    counter.setStartTime(LocalDateTime.of(startDate.getValue(), LocalTime.of(hours, minutes)));
    counter.setDescription(header.getText());
    counter.setTimeUnit(timeUnit.getValue());
    return counter;
  }
}
