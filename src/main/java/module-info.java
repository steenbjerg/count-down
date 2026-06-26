module dk.stonemountain.count.down {
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.prefs;
    requires jakarta.json.bind;
    requires jakarta.json;

    opens dk.stonemountain.count.down to
            javafx.fxml,
            javafx.graphics;

    exports dk.stonemountain.count.down;
}
