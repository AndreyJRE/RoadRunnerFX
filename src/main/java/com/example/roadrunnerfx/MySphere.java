package com.example.roadrunnerfx;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Camera;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

public class MySphere extends Sphere implements Runnable {
    private final int MAX_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int MAX_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final double START_TURNING_SPEED = 0.265;
    private final double START_JUMP_SPEED = 0.3;
    private final double START_ROTATE_SPEED = -0.5;
    private final double START_SPEED = 2;

    private final Camera camera;
    private TranslateTransition jumpTransition;
    private volatile boolean running = false;
    private int onLine;
    private boolean jumpOn = true;
    private boolean turnOn = true;
    private Rotate r;
    private Transform transform = new Rotate();
    private boolean active = false;

    private double speed = START_SPEED;
    private double rotateSpeed = START_ROTATE_SPEED;
    private double turningSpeed = START_TURNING_SPEED;
    private double jumpSpeed = START_JUMP_SPEED;
    private RoadRunner roadRunner;
    private ObjectCreater objectCreater;
    private MyGround myGround;
    private double sphereRadius;
    volatile private ArrayList<Sphere> shootSpheres;

    public MySphere(Camera camera, double rad, RoadRunner rr) {
        super(rad);

        this.sphereRadius = rad;
        this.roadRunner = rr;
        PhongMaterial pm = new PhongMaterial();
        pm.setDiffuseMap(new Image(getClass().getResourceAsStream("Images/ballImage.png")));
        this.setMaterial(pm);
        this.camera = camera;
        this.onLine = 2;
        this.setTranslateX(MAX_WIDTH / 2);
        this.setTranslateY(MAX_HEIGHT / 2 - 25);
        shootSpheres = new ArrayList<>();

    }

    public void startThread() {
        running = true;
        active = true;
        new Thread(this, "MySphereThread").start();

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

    public void moveRight(int x) {
        if (onLine != 3 && turnOn && !roadRunner.isPaused()) {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(turningSpeed), this);
            TranslateTransition translateRight = new TranslateTransition(Duration.seconds(turningSpeed), this);
            TranslateTransition translateRightCamera = new TranslateTransition(Duration.seconds(turningSpeed), camera);
            rotateTransition.setCycleCount(1);
            rotateTransition.setAxis(Rotate.Z_AXIS);
            rotateTransition.setByAngle(360);
            translateRight.setCycleCount(1);
            translateRight.setByX(x);
            translateRightCamera.setCycleCount(1);
            translateRightCamera.setByX(x);

            rotateTransition.play();
            translateRight.play();
            translateRightCamera.play();

            turnOn = false;
            translateRight.setOnFinished(event -> {

                turnOn = true;
            });
            onLine++;
        }

    }

    public void moveLeft(int x) {

        if (onLine != 1 && turnOn & !roadRunner.isPaused()) {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(turningSpeed), this);
            TranslateTransition translateRight = new TranslateTransition(Duration.seconds(turningSpeed), this);
            TranslateTransition translateRightCamera = new TranslateTransition(Duration.seconds(turningSpeed), camera);
            rotateTransition.setCycleCount(1);
            rotateTransition.setAxis(Rotate.Z_AXIS);
            rotateTransition.setByAngle(-360);
            translateRight.setCycleCount(1);
            translateRight.setByX(-x);
            translateRightCamera.setCycleCount(1);
            translateRightCamera.setByX(-x);

            rotateTransition.play();
            translateRight.play();
            translateRightCamera.play();

            turnOn = false;
            translateRight.setOnFinished(event -> {

                turnOn = true;
            });

            onLine--;
        }

    }

    public void moveForward(double x) {
        this.setTranslateZ(this.getTranslateZ() + x);
        camera.setTranslateZ(camera.getTranslateZ() + x);
    }

    public void moveBack(int x) {
        this.setTranslateZ(this.getTranslateZ() - x);
        camera.setTranslateZ(camera.getTranslateZ() - x);
    }

    public void jump() {
        jumpTransition = new TranslateTransition(Duration.seconds(jumpSpeed));
        jumpTransition.setAutoReverse(true);
        jumpTransition.setByY(-125);
        jumpTransition.setCycleCount(2);
        jumpTransition.setNode(this);

        if (!roadRunner.isPaused() && jumpOn && roadRunner.isFirstView()) {
            TranslateTransition translateJumpCamera = new TranslateTransition(Duration.seconds(jumpSpeed), camera);
            translateJumpCamera.setAutoReverse(true);
            translateJumpCamera.setByY(-125);
            translateJumpCamera.setCycleCount(2);
            jumpTransition.play();
            translateJumpCamera.play();
            jumpOn = false;
            jumpTransition.setOnFinished(event -> {
                jumpOn = true;
            });

        } else if (!roadRunner.isPaused() && jumpOn && !roadRunner.isFirstView()) {
            jumpTransition.play();
            jumpOn = false;
            jumpTransition.setOnFinished(event -> {
                jumpOn = true;
            });
        }
    }

    public void rotate(double x) {
        r = new Rotate(x, Rotate.X_AXIS);
        transform = transform.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().addAll(transform);

    }

    public void updateScore() {
        roadRunner.getScoreLabel().setText("SCORE: " + roadRunner.getScore());
    }

    public void updateSpeed() {
        if (speed <= 6 && rotateSpeed >= -1.5) {
            speed += 0.2;
            rotateSpeed += -0.04;
        }
        if (myGround.getSpeedOfGround() <= 20) {
            myGround.setSpeedOfGround(myGround.getSpeedOfGround() + 0.8);
        }
        if (turningSpeed >= 0.210) {
            turningSpeed -= 0.006;
        }
        if (jumpSpeed >= 0.260) {
            jumpSpeed -= 0.01;
        }
    }

    @Override
    public void run() {

        while (running) {
            if (active) {
                Platform.runLater(() -> {
                    this.moveForward(speed);
                    this.rotate(rotateSpeed);
                    if (objectCreater.intersectsYellowSphere()) {
                        roadRunner.addToScore(10);
                        ProgressBar pb = roadRunner.getProgressBar();
                        pb.setProgress(pb.getProgress() + 1.0 / 3.0);

                        if (pb.getProgress() == 1) {
                            roadRunner.setShootsscore(roadRunner.getShootsscore() + 1);
                            pb.setProgress(0);
                        }
                        updateSpeed();
                    }
                    if (objectCreater.intersectsRedSphere()) {
                        if (roadRunner.getScore() % 100 == 10) {
                            roadRunner.addToScore(-10);
                        } else {
                            roadRunner.addToScore(-20);
                        }
                        roadRunner.getProgressBar().setProgress(0);

                    }
                    if (objectCreater.intersectsBlueSphere()) {
                        roadRunner.finishGame();
                    }
                    updateScore();
                    checkShootingCollision();

                });

                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    running = false;

                }
            }
        }

    }

    public void shoot() {
        if (roadRunner.getShootsscore() > 0) {
            Sphere shootSphere = new Sphere(sphereRadius / 2);
            shootSpheres.add(shootSphere);
            shootSphere.setTranslateX(this.getTranslateX());
            shootSphere.setTranslateY(this.getTranslateY());
            shootSphere.setTranslateZ(this.getTranslateZ() + 15);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), shootSphere);
            tt.setByZ(2500);
            tt.setCycleCount(1);
            roadRunner.getRoot().getChildren().add(shootSphere);
            tt.play();

            tt.setOnFinished(event -> {
                if (roadRunner.getRoot().getChildren().contains(shootSphere)) {
                    shootSpheres.remove(shootSphere);
                    roadRunner.getRoot().getChildren().remove(shootSphere);
                }
            });

            roadRunner.setShootsscore(roadRunner.getShootsscore() - 1);


        }
    }

    synchronized public void checkShootingCollision() {
        Iterator<Sphere> shootingIterator = shootSpheres.iterator();
        while (shootingIterator.hasNext()) {
            HashMap<Sphere, Integer> allSpheres = objectCreater.getAllSpheres();
            Set<Sphere> listOfSpheres = allSpheres.keySet();
            Iterator<Sphere> it = listOfSpheres.iterator();
            Sphere s1 = shootingIterator.next();
            while (it.hasNext()) {
                Sphere s = it.next();
                PhongMaterial pm = (PhongMaterial) s.getMaterial();
                if (s.getBoundsInParent().intersects(s1.getBoundsInParent()) && pm.getDiffuseColor() == Color.BLUE) {
                    var root = roadRunner.getRoot();
                    root.getChildren().removeAll(s, s1);
                    it.remove();
                    shootingIterator.remove();

                }
            }

        }

    }

    public int getOnLine() {
        return onLine;
    }

    public ObjectCreater getObjectCreater() {
        return objectCreater;
    }

    public void setObjectCreater(ObjectCreater objectCreater) {
        this.objectCreater = objectCreater;
    }

    public MyGround getMyGround() {
        return myGround;
    }

    public void setMyGround(MyGround myGround) {
        this.myGround = myGround;
    }

    public boolean isJumpOn() {
        return jumpOn;
    }

}