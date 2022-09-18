package com.example.roadrunnerfx.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.roadrunnerfx.RoadRunner;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;



public class StartController implements Initializable {
	private RoadRunner rr;
	private Scene gameScene;
	private Stage stage;

	@FXML
	private Button startButton;

	@FXML
	void startClicked(ActionEvent event) {

		stage.show();
		closeStage(event);
		rr.startThreads();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		stage = new Stage();
		SceneController sceneController = new SceneController(stage);
		this.rr = new RoadRunner(sceneController);
		
		this.gameScene = rr.getScene();
		stage.setScene(gameScene);
		stage.setOnCloseRequest(e -> {
			rr.stopThreads();
			Platform.exit();
			System.exit(0);
		});
		stage.widthProperty().addListener((x, y, z) -> {
			this.gameScene = rr.getScene();
			rr.updatePositionOnResizing();
		});
		stage.heightProperty().addListener((x, y, z) -> {
			this.gameScene = rr.getScene();
			rr.updatePositionOnResizing();
		});
		startButton.getStyleClass().add("buttons");

		

	}

	private void closeStage(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}
}
