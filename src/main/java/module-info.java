module proiectmap.socialmap {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;
    requires java.management;

    opens proiectmap.socialmap to javafx.fxml;
    exports proiectmap.socialmap;
}