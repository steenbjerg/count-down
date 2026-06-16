package dk.stonemountain.count.down;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/** Utility to build a small vector-based celebratory graphic (smiley + confetti). */
public final class HappyGraphicFactory {
  private HappyGraphicFactory() {}

  public static Node create() {
    // Fixed-size pane so layout bounds are reliable when centering popup
    double size = 220;
    Pane pane = new Pane();
    pane.setPrefSize(size, size);
    pane.setMinSize(size, size);
    pane.setMaxSize(size, size);

    double cx = size / 2.0;
    double cy = size / 2.0;

    // Face
    Circle face = new Circle(cx, cy, 80);
    face.setFill(Color.web("#FFD54F")); // warm yellow
    face.setStroke(Color.web("#F9A825"));
    face.setStrokeWidth(3);

    // Eyes
    Circle leftEye = new Circle(cx - 28, cy - 20, 8, Color.BLACK);
    Circle rightEye = new Circle(cx + 28, cy - 20, 8, Color.BLACK);

    // Smile
    Arc smile = new Arc(cx, cy + 10, 45, 30, 200, 140);
    smile.setFill(Color.TRANSPARENT);
    smile.setStroke(Color.BLACK);
    smile.setStrokeWidth(6);
    smile.setType(ArcType.OPEN);

    // Simple confetti around the face
    Rectangle c1 = rect(cx - 90, cy - 80, "#E91E63", 18, 6, 25);
    Rectangle c2 = rect(cx + 70, cy - 85, "#3F51B5", 16, 6, -20);
    Rectangle c3 = rect(cx - 95, cy + 70, "#4CAF50", 14, 6, 40);
    Rectangle c4 = rect(cx + 80, cy + 65, "#FFC107", 20, 6, -35);
    Rectangle c5 = rect(cx - 10, cy - 95, "#00BCD4", 14, 6, 10);
    Rectangle c6 = rect(cx + 5, cy + 95, "#9C27B0", 18, 6, -15);

    pane.getChildren().addAll(c1, c2, c3, c4, c5, c6, face, leftEye, rightEye, smile);
    return pane;
  }

  private static Rectangle rect(
      double x, double y, String color, double w, double h, double rotate) {
    Rectangle r = new Rectangle(x, y, w, h);
    r.setArcWidth(3);
    r.setArcHeight(3);
    r.setFill(Color.web(color));
    r.setRotate(rotate);
    return r;
  }
}
