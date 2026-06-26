package dk.stonemountain.count.down;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Window;

import java.util.logging.Logger;

public class CelebrationPopup extends Popup {
    private static final Logger LOG = Logger.getLogger(CelebrationPopup.class.getName());

    private final StringProperty messageProperty = new SimpleStringProperty();
    private final StackPane content;

    public CelebrationPopup() {
        var graphic = HappyGraphicFactory.create();

        var text = new Label();
        text.textProperty().bind(messageProperty);
        text.setTextFill(Color.WHITE);
        text.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        text.setEffect(new DropShadow(10, Color.color(0, 0, 0, 0.8)));

        var backBtn = new Button("Back");
        backBtn.setDefaultButton(true);
        backBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        var centerBox = new VBox(16, graphic, text, backBtn);
        centerBox.setAlignment(Pos.CENTER);

        content = new StackPane(centerBox);
        content.setStyle(
                "-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 16; -fx-padding:"
                    + " 24;");

        this.getContent().add(content);
        this.setAutoHide(false); // keep until user dismisses

        backBtn.setOnAction(e -> this.hide());
    }

    private void setPosition(Window window) {
        double pw = content.getWidth();
        double ph = content.getHeight();
        double x = window.getX() + (window.getWidth() - pw) / 2;
        double y = window.getY() + (window.getHeight() - ph) / 2;
        this.setX(x);
        this.setY(y);
        LOG.info(
                String.format(
                        "Popup centered at x=%s, y=%s (actual size: %sx%s, window position: %sx%s,"
                            + " window size: %sx%s)",
                        x,
                        y,
                        pw,
                        ph,
                        window.getX(),
                        window.getY(),
                        window.getWidth(),
                        window.getHeight()));
    }

    public void show(Window window, String message) {
        messageProperty.set(message);
        this.show(window);
        Platform.runLater(() -> setPosition(window));
    }
}
