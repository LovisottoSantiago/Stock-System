module org.tasks.stocksystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javapos;

    opens org.tasks.stocksystem to javafx.fxml;
    exports org.tasks.stocksystem;
    exports gui;
    opens gui to javafx.fxml;
}