package com.example.roadrunnerfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;


public class StartMenu extends Application {

	private Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("startMenu.fxml"));

		this.scene = new Scene(root);
		primaryStage.setScene(scene);
		this.scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		JMetro jmetro=new JMetro(Style.DARK);
		jmetro.setScene(this.scene);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
