package com.example.roadrunnerfx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;

import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;

public class ObjectCreater implements Runnable {
	private final int START_SPEED = 1000;

	private SubScene scene;
	private volatile boolean running = false;
	private Sphere mainSphere;
	private HashMap<Sphere, Integer> mapOfSpheres;
	private Group myGroup;
	private boolean active = false;
	private int speed = START_SPEED;
	private RoadRunner roadRunner;
	private Thread thread;
	private int oldScore;

	public ObjectCreater(Group root, Sphere sphere, SubScene scene) {
		this.scene = scene;
		this.mainSphere = sphere;
		this.myGroup = root;
		this.oldScore = 0;
		mapOfSpheres = new HashMap<>();

	}

	public void startThread() {
		running = true;
		active = true;
		thread = new Thread(this, "ObjectCreaterThread");
		thread.start();

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

	public void run() {
		while (running) {
			if (active) {
				Platform.runLater(() -> {
					createSphere();
					deleteSphere();

				});
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					running = false;
					e.printStackTrace();
				}
			}
		}
	}

	synchronized public void createSphere() {
		switch (Levels.getCurrentLevel()) {
		case LEVEL_2:
			playLevel_2();
			break;
		case LEVEL_3:
			playLevel_3();
			break;
		case LEVEL_4:
			playLevel_3();
			break;
		case LEVEL_5:
			playLevel_3();
			break;
		default:
			playLevel_1();
			break;
		}

	}

	private void playLevel_3() {
		Random r = new Random();
		int randomDistance = r.nextInt(301)+1700;
		double sceneWidth = scene.getWidth() / 2;

		ObjectCreaterPosition obc = choosePosition();
		Sphere s = new Sphere(25);
		Color color;
		int randColor = r.nextInt(14)+1;
		if (randColor >= 1 && randColor <= 4) {
			color = Color.YELLOW;
		} else if (randColor >= 5 && randColor <= 13) {
			color = Color.RED;
		} else {
			color = Color.BLUE;

		}

		PhongMaterial pm = new PhongMaterial(color);
		s.setMaterial(pm);

		int randomLine = r.nextInt(3)+1;
		double xPositionOfSphere = 0;
		switch (randomLine) {
		case 1:
			xPositionOfSphere = sceneWidth - 240;
			break;
		case 2:
			xPositionOfSphere = sceneWidth;
			break;
		case 3:
			xPositionOfSphere = sceneWidth + 240;
			break;
		}
		s.setTranslateX(xPositionOfSphere);
		if (obc == ObjectCreaterPosition.GROUND) {
			s.setTranslateY(scene.getHeight() / 2 - 25);
		} else {
			s.setTranslateY(scene.getHeight() / 2 - 100);
		}
		s.setTranslateZ(mainSphere.getTranslateZ() + randomDistance);
		myGroup.getChildren().add(s);
		mapOfSpheres.put(s, randomLine);
		if (color == Color.BLUE || color == Color.RED) {
			createMovingSphere(s, randomLine);
		}
	}

	private void playLevel_2() {
		Random r = new Random();
		int randomDistance = r.nextInt(301)+1700;
		double sceneWidth = scene.getWidth() / 2;

		ObjectCreaterPosition obc = choosePosition();
		Sphere s = new Sphere(25);
		Color color;
		int randColor = r.nextInt(14)+1;
		if (randColor >= 1 && randColor <= 4) {
			color = Color.YELLOW;
		} else if (randColor >= 5 && randColor <= 13) {
			color = Color.RED;
		} else {
			color = Color.BLUE;

		}

		PhongMaterial pm = new PhongMaterial(color);
		s.setMaterial(pm);

		int randomLine = r.nextInt(3)+1;
		double xPositionOfSphere = 0;
		switch (randomLine) {
		case 1:
			xPositionOfSphere = sceneWidth - 240;
			break;
		case 2:
			xPositionOfSphere = sceneWidth;
			break;
		case 3:
			xPositionOfSphere = sceneWidth + 240;
			break;
		}
		s.setTranslateX(xPositionOfSphere);
		if (obc == ObjectCreaterPosition.GROUND) {
			s.setTranslateY(scene.getHeight() / 2 - 25);
		} else {
			s.setTranslateY(scene.getHeight() / 2 - 100);
		}
		s.setTranslateZ(mainSphere.getTranslateZ() + randomDistance);
		myGroup.getChildren().add(s);
		mapOfSpheres.put(s, randomLine);
		if (color == Color.BLUE) {
			createMovingSphere(s, randomLine);
		}

	}

	private void playLevel_1() {
		Random r = new Random();
		int randomDistance = r.nextInt(300)+1700;
		double sceneWidth = scene.getWidth() / 2;

		ObjectCreaterPosition obc = choosePosition();
		Sphere s = new Sphere(25);
		Color color;
		int randColor = r.nextInt(14)+1;
		if (randColor >= 1 && randColor <= 4) {
			color = Color.YELLOW;
		} else if (randColor >= 5 && randColor <= 13) {
			color = Color.RED;
		} else {
			color = Color.BLUE;
		}

		PhongMaterial pm = new PhongMaterial(color);
		s.setMaterial(pm);

		int randomLine = r.nextInt(3)+1;
		double xPositionOfSphere = 0;

		switch (randomLine) {
		case 1:
			xPositionOfSphere = sceneWidth - 240;
			break;
		case 2:
			xPositionOfSphere = sceneWidth;
			break;
		case 3:
			xPositionOfSphere = sceneWidth + 240;
			break;
		}
		s.setTranslateX(xPositionOfSphere);
		if (obc == ObjectCreaterPosition.GROUND) {
			s.setTranslateY(scene.getHeight() / 2 - 25);
		} else {
			s.setTranslateY(scene.getHeight() / 2 - 100);
		}

		s.setTranslateZ(mainSphere.getTranslateZ() + randomDistance);
		myGroup.getChildren().add(s);
		mapOfSpheres.put(s, randomLine);

	}

	synchronized public void deleteSphere() {

		Iterator<Sphere> it = mapOfSpheres.keySet().iterator();
		while (it.hasNext()) {
			Sphere s = it.next();
			if (s.getTranslateZ() < mainSphere.getTranslateZ() - 250) {
				it.remove();
				myGroup.getChildren().remove(s);

			}
		}

	}

	public boolean intersectsYellowSphere() {

		Iterator<Sphere> it = mapOfSpheres.keySet().iterator();
		while (it.hasNext()) {
			Sphere s = it.next();
			PhongMaterial pm = (PhongMaterial) s.getMaterial();
			if (pm.getDiffuseColor() == Color.YELLOW
					&& s.getBoundsInParent().intersects(mainSphere.getBoundsInParent())) {
				it.remove();
				myGroup.getChildren().remove(s);
				setNewSpeed();
				return true;

			}
		}

		return false;
	}

	public void updatePositionOnResizing() {
		double height = scene.getHeight() / 2 - 25;
		for (Sphere s : mapOfSpheres.keySet()) {
			double xPositionOfSphere = 0;
			double sceneWidth = scene.getWidth() / 2;
			switch (mapOfSpheres.get(s)) {
			case 1:
				xPositionOfSphere = sceneWidth - 240;
				break;
			case 2:
				xPositionOfSphere = sceneWidth;
				break;
			case 3:
				xPositionOfSphere = sceneWidth + 240;
				break;
			}
			s.setTranslateX(xPositionOfSphere);
			s.setTranslateY(height);

		}
	}

	public boolean intersectsRedSphere() {

		Iterator<Sphere> it = mapOfSpheres.keySet().iterator();
		while (it.hasNext()) {
			Sphere s = it.next();
			PhongMaterial pm = (PhongMaterial) s.getMaterial();
			if (pm.getDiffuseColor() == Color.RED && s.getBoundsInParent().intersects(mainSphere.getBoundsInParent())) {
				it.remove();
				myGroup.getChildren().remove(s);

				return true;

			}
		}

		return false;
	}

	public boolean intersectsBlueSphere() {

		Iterator<Sphere> it = mapOfSpheres.keySet().iterator();
		while (it.hasNext()) {
			Sphere s = it.next();
			PhongMaterial pm = (PhongMaterial) s.getMaterial();
			if (pm.getDiffuseColor() == Color.BLUE
					&& s.getBoundsInParent().intersects(mainSphere.getBoundsInParent())) {
				it.remove();
				myGroup.getChildren().remove(s);

				return true;

			}
		}

		return false;
	}

	private ObjectCreaterPosition choosePosition() {
		Random r = new Random();
		int random = r.nextInt(2)+1;
		ObjectCreaterPosition obc = (random == 1) ? ObjectCreaterPosition.GROUND : ObjectCreaterPosition.AIR;
		return obc;
	}

	public void setNewSpeed() {
		if (speed >= 350) {
			speed -= 50;
		}
	}

	public void level_up() {
		Random r = new Random();
		int randomDistance = r.nextInt(301)+1700;
		double sceneWidth = scene.getWidth() / 2;
		int currScore = roadRunner.getScore();

		if (roadRunner.getScore() != 0 && oldScore % 100 > currScore % 100 && oldScore < currScore) {
			Sphere[] spheres = new Sphere[3];
			for (int i = 0; i < spheres.length; i++) {
				spheres[i] = new Sphere(25);
				Color color = Color.BLUE;
				PhongMaterial pm = new PhongMaterial(color);
				spheres[i].setMaterial(pm);
				double xPositionOfSphere = 0;
				switch (i) {
				case 0:
					xPositionOfSphere = sceneWidth - 240;
					break;
				case 1:
					xPositionOfSphere = sceneWidth;
					break;
				case 2:
					xPositionOfSphere = sceneWidth + 240;
					break;
				default:
					break;
				}
				spheres[i].setTranslateX(xPositionOfSphere);
				spheres[i].setTranslateY(scene.getHeight() / 2 - 25);
				spheres[i].setTranslateZ(mainSphere.getTranslateZ() + randomDistance);

			}
			myGroup.getChildren().addAll(spheres);
			mapOfSpheres.put(spheres[0], 1);
			mapOfSpheres.put(spheres[1], 2);
			mapOfSpheres.put(spheres[2], 3);
			Levels.level_up();
			roadRunner.getLevelLabel().setText(Levels.getCurrentLevel().getLevelName());
		}
		oldScore = currScore;

	}

	private void createMovingSphere(Sphere s, int line) {
		TranslateTransition tt = new TranslateTransition(Duration.seconds(1.3), s);
		tt.setCycleCount(30);
		tt.setAutoReverse(true);
		switch (line) {
		case 1:
			tt.setByX(480);
			break;
		case 2:
			break;
		case 3:
			tt.setByX(-480);
			break;
		}
		tt.play();
	}

	public RoadRunner getRoadRunner() {
		return roadRunner;
	}

	public void setRoadRunner(RoadRunner roadRunner) {
		this.roadRunner = roadRunner;
	}

	public HashMap<Sphere, Integer> getAllSpheres() {
		return this.mapOfSpheres;
	}

}
