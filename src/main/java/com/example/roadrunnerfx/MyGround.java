package com.example.roadrunnerfx;

import java.awt.Toolkit;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class MyGround extends Box implements Runnable {
	private volatile boolean running = false;
	private boolean active = false;
	private final double MAX_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private final double MAX_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private final double START_SPEED = 5;
	private double speedOfGround = START_SPEED;

	public MyGround(double x, double y, double z) {
		super(x, y, z);
		PhongMaterial pm = new PhongMaterial();

		pm.setDiffuseMap(new Image(getClass().getResourceAsStream("Images/Road.jpeg")));
		this.setMaterial(pm);
		this.translateXProperty().set(MAX_WIDTH / 2);
		this.translateYProperty().set(MAX_HEIGHT / 2);
		this.translateZProperty().set(500);

	}

	public void startThread() {
		running = true;
		active = true;
		Thread t = new Thread(this,"MyGroundSphere");
		t.start();
	}

	public void stopThread() {
		running = false;
	}

	public void pauseThread() {
		active = false;
	}

	public void continueThread() {
		active = true;
	}

	@Override
	public void run() {
		while (running) {
			if (active) {
				Platform.runLater(() -> {
					this.setDepth(this.getDepth() + speedOfGround);

				});

				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public double getSpeedOfGround() {
		return speedOfGround;
	}

	public void setSpeedOfGround(double speedOfGround) {
		this.speedOfGround = speedOfGround;
	}
}
