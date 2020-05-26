module ChatClient {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.jfoenix;
    opens Controller to javafx.fxml;
    opens Client;
}