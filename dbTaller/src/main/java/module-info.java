module org.example.dbtaller {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.dbtaller to javafx.fxml;
    exports org.example.dbtaller;
}