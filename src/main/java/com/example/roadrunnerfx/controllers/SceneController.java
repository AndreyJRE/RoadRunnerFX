package com.example.roadrunnerfx.controllers;

import java.util.HashMap;


import com.example.roadrunnerfx.SceneName;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {
	private HashMap<SceneName, Scene> screenMap = new HashMap<>();
	private Stage main;

	public SceneController(Stage main) {
		this.main = main;
	}

	public void addScreen(SceneName name, Scene scene) {
		screenMap.put(name, scene);
	}

	public void removeScreen(SceneName name) {
		screenMap.remove(name);
	}

	public void activate(SceneName name) {
		main.setScene(screenMap.get(name));
	}
}
