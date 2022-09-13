package com.example.roadrunnerfx;

public enum SceneName {
	GAME_SCENE("GameScene"), PAUSE_SCENE("PauseScene"), GAMEOVER_SCENE("GameOverScene");

	private String name;

	SceneName(String s) {
		name = s;
	}

	public String getName() {
		return name;
	}
}
