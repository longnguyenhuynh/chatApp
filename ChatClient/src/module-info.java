module ChatClient {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.jfoenix;
    requires mysql.connector.java;
    opens Controller to javafx.fxml;
    opens Client;
}