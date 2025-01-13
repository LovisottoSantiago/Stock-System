module org.tasks.stocksystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.tasks.stocksystem to javafx.fxml;
    exports org.tasks.stocksystem;
}