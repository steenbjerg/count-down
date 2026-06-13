package dk.stonemountain.count.down;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Counter {
  public enum State {
    ACTIVE,
    COMPLETED,
  }

  private StringProperty description = new SimpleStringProperty();
  private ObjectProperty<LocalDateTime> expiration = new SimpleObjectProperty<>();
  private ObjectProperty<Duration> timeLeft = new SimpleObjectProperty<>();
  private ObjectProperty<ChronoUnit> timeUnit = new SimpleObjectProperty<>();
  private ObjectProperty<Double> progress = new SimpleObjectProperty<>();
  private ObjectProperty<LocalDateTime> startTime = new SimpleObjectProperty<>();
  private ObjectProperty<State> state = new SimpleObjectProperty<>();

  public Counter() {
    this.description.set("");
    this.expiration.set(null);
    this.timeLeft.set(null);
    this.timeUnit.set(null);
    this.progress.set(0.0);
    this.startTime.set(LocalDateTime.now());
  }

  public Counter(
      String description,
      LocalDateTime expiration,
      Duration timeLeft,
      ChronoUnit timeUnit,
      LocalDateTime startTime) {
    this.description.set(description);
    this.expiration.set(expiration);
    this.timeLeft.set(timeLeft);
    this.timeUnit.set(timeUnit);
    this.startTime.set(startTime);
    updateProgress();
    updateState();
  }

  private void updateProgress() {
    if (expiration.get() != null && expiration.get().isAfter(LocalDateTime.now())) {
      var totalDuration = Duration.between(startTime.get(), expiration.get());
      var elapsedDuration = Duration.between(startTime.get(), LocalDateTime.now());
      this.progress.set(elapsedDuration.toNanos() / (double) totalDuration.toNanos());
    } else {
      this.progress.set(1.0);
    }
  }

  private void updateState() {
    if (expiration.get() != null && expiration.get().isAfter(LocalDateTime.now())) {
      this.state.set(State.ACTIVE);
    } else {
      this.state.set(State.COMPLETED);
    }
  }

  public String getDescription() {
    return description.get();
  }

  public void setDescription(String description) {
    this.description.set(description);
  }

  public StringProperty descriptionProperty() {
    return description;
  }

  public LocalDateTime getExpiration() {
    return expiration.get();
  }

  public void setExpiration(LocalDateTime expiration) {
    this.expiration.set(expiration);
  }

  public ObjectProperty<LocalDateTime> expirationProperty() {
    return expiration;
  }

  public Duration getTimeLeft() {
    return timeLeft.get();
  }

  public void setTimeLeft(Duration timeLeft) {
    this.timeLeft.set(timeLeft);
  }

  public ObjectProperty<Duration> timeLeftProperty() {
    return timeLeft;
  }

  public ChronoUnit getTimeUnit() {
    return timeUnit.get();
  }

  public void setTimeUnit(ChronoUnit timeUnit) {
    this.timeUnit.set(timeUnit);
  }

  public ObjectProperty<ChronoUnit> timeUnitProperty() {
    return timeUnit;
  }

  public ObjectProperty<Double> progressProperty() {
    return progress;
  }

  public ObjectProperty<LocalDateTime> startTimeProperty() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime.set(startTime);
  }

  public void setProgress(double progress) {
    this.progress.set(progress);
  }

  public double getProgress() {
    return progress.get();
  }

  public LocalDateTime getStartTime() {
    return startTime.get();
  }

  public State getState() {
    return state.get();
  }

  public void setState(State state) {
    this.state.set(state);
  }

  public ObjectProperty<State> stateProperty() {
    return state;
  }

  public void refresh() {
    LocalDateTime now = LocalDateTime.now();
    if (expiration.get() != null) {
      Duration remaining = Duration.between(now, expiration.get());
      timeLeft.set(remaining.isNegative() ? Duration.ZERO : remaining);
    }
    updateProgress();
    updateState();
  }

  @Override
  public String toString() {
    return ("Counter{"
        + "description="
        + description
        + ", startTime="
        + startTime
        + ", progress="
        + progress
        + ", expiration="
        + expiration
        + ", state="
        + state
        + ", timeLeft="
        + timeLeft
        + ", timeUnit="
        + timeUnit
        + '}');
  }
}
