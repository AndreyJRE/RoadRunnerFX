module com.example.roadrunnerfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires org.jfxtras.styles.jmetro;

    exports com.example.roadrunnerfx;
    exports com.example.roadrunnerfx.controllers;
    opens com.example.roadrunnerfx to javafx.fxml,javafx.graphics;
    opens com.example.roadrunnerfx.controllers to javafx.fxml,javafx.graphics;


}