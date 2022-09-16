package com.example.roadrunnerfx;

import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.example.roadrunnerfx.controllers.SceneController;
import javafx.embed.swing.SwingFXUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;

import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class RoadRunner {

    private final int MAX_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int MAX_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final int TABLE_WIDTH = 700;
    private final int TABLE_HEIGHT = 5;
    private final int TABLE_DEPTH = 12000;

    private Label scoreLabel;
    private int score;
    private Camera camera;
    private Group root;
    private MySphere sp;
    private MyGround ground;
    private ObjectCreater objectCreater;
    private Node[] pointlights;
    private Scene scene;
    private boolean paused;

    private Scene pauseScene;
    private ImageView imagePause;
    private SceneController sceneController;
    private Image image;
    private VBox vBox;
    private boolean firstView;
    private Scene gameOverScene;
    private SubScene subSceneCenter;
    private Label levelLabel;


    private ProgressBar progressBar;

    private int clickedPaused;

    public RoadRunner(SceneController sceneController) {
        this.sceneController = sceneController;
        initialize();
    }

    public void initialize() {
        JMetro jmetro = new JMetro(Style.LIGHT);
        Levels.setStart();
        paused = false;
        firstView = false;
        clickedPaused = 0;
        vBox = new VBox();

        root = new Group();
        subSceneCenter = new SubScene(root, MAX_WIDTH, MAX_HEIGHT, true, SceneAntialiasing.BALANCED);


        camera = new PerspectiveCamera();

        subSceneCenter.setCamera(camera);
        subSceneCenter.setFill(Color.GRAY);
        ground = new MyGround(TABLE_WIDTH, TABLE_HEIGHT, TABLE_DEPTH);
        sp = new MySphere(camera, 20, this);

        camera.setTranslateY(-50);
        camera.setTranslateZ(1000);

        camera.setNearClip(0.01);
        camera.setFarClip(3000);
        rotateCameraDown();

        score = 0;
        scoreLabel = new Label("SCORE: " + score);
        scoreLabel.setAlignment(Pos.CENTER);

        levelLabel = new Label(Levels.getCurrentLevel().getLevelName());
        levelLabel.setFont(new Font("Arial", 40));

        HBox labelBox = new HBox();

        progressBar = new ProgressBar(0);

        progressBar.setMinHeight(20);
        progressBar.setMinWidth(200);


        Button changeViewButton = createViewButton();
        changeViewButton.getStyleClass().add("buttons");
        HBox.setMargin(levelLabel, new Insets(0, 50, 0, 0));
        HBox.setMargin(scoreLabel, new Insets(0, 50, 0, 0));
        HBox.setMargin(changeViewButton, new Insets(0, 50, 0, 0));

        labelBox.setAlignment(Pos.CENTER);
        scoreLabel.setFont(new Font("Arial", 40));
        labelBox.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        labelBox.getChildren().addAll(levelLabel, scoreLabel, changeViewButton, progressBar);

        scoreLabel.getStyleClass().add("scoreLabel");
        vBox.getChildren().addAll(labelBox, subSceneCenter);

        scene = new Scene(vBox, MAX_WIDTH, MAX_HEIGHT, true, SceneAntialiasing.BALANCED);

        // scene.setCamera(camera);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        root.getChildren().addAll(sp, ground);

        // root.getChildren().addAll(pointlights);

        objectCreater = new ObjectCreater(root, sp, subSceneCenter);

        pauseScene = createPauseMenu();
        gameOverScene = createGameOverScene();

        sceneController.addScreen(SceneName.GAME_SCENE, scene);
        sceneController.addScreen(SceneName.PAUSE_SCENE, pauseScene);
        sceneController.addScreen(SceneName.GAMEOVER_SCENE, gameOverScene);

        objectCreater.setRoadRunner(this);
        sp.setObjectCreater(objectCreater);
        sp.setMyGround(ground);
        createSceneListener();
        createScoreLabelListener();
        gameOverScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

    }

    public void startThreads() {

        objectCreater.startThread();
        sp.startThread();
        ground.startThread();
    }

    public void stopThreads() {
        objectCreater.stopThread();
        sp.stopThread();
        ground.stopThread();
        sceneController.activate(SceneName.GAMEOVER_SCENE);

    }

    public void pauseThreads() {
        objectCreater.pauseThread();
        sp.pauseThread();
        ground.pauseThread();
        changeToPause();
        this.paused = true;

    }

    public void continueThreads() {
        sceneController.activate(SceneName.GAME_SCENE);
        objectCreater.continueThread();
        sp.continueThread();
        ground.continueThread();

        paused = false;

    }

    public void rotateCameraLeft() {
        camera.setRotationAxis(Rotate.Y_AXIS);
        camera.setRotate(camera.getRotate() - 0.5);
    }

    public void rotateCameraRight() {
        camera.setRotationAxis(Rotate.Y_AXIS);
        camera.setRotate(camera.getRotate() + 0.5);
    }

    public void rotateCameraDown() {
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setTranslateY(camera.getTranslateY() + 16);
        camera.setRotate(camera.getRotate() - 6.6);

    }

    public void rotateCameraUp() {
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(camera.getRotate() + 0.3);
        camera.setTranslateY(camera.getTranslateY() - 3);

    }

    public Node[] createLights() {
        PointLight pl = new PointLight();
        PointLight pl1 = new PointLight();
        pl1.getTransforms().add(new Translate(MAX_WIDTH, 0, 0));
        return new Node[]{pl1, pl};
    }

    public Node createStartStopButtons() {
        HBox buttons = new HBox();
        Button start = new Button("Start");
        Button stop = new Button("Stop");
        start.setStyle(" -fx-background-color: #00ff00");
        stop.setStyle(" -fx-background-color: #ff0000");

        stop.setVisible(false);
        start.setVisible(true);

        buttons.getChildren().addAll(start, stop);
        buttons.setTranslateX(400);
        buttons.setTranslateY(180);

        return buttons;

    }

    public Scene createPauseMenu() {
        Pane p = new Pane();
        image = null;
        imagePause = new ImageView(image);
        imagePause.setFitWidth(MAX_WIDTH);
        imagePause.setFitHeight(MAX_HEIGHT);

        VBox pauseRoot = new VBox(5);
        Label paused = new Label("Paused");

        paused.setFont(new Font("Arial", 70));
        paused.setTextFill(Color.RED);
        pauseRoot.getChildren().add(paused);
        pauseRoot.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        pauseRoot.setAlignment(Pos.CENTER);
        pauseRoot.setPadding(new Insets(20));
        pauseRoot.setPrefSize(MAX_WIDTH, MAX_HEIGHT);

        p.getChildren().addAll(imagePause, pauseRoot);
        Scene scene = new Scene(p, MAX_WIDTH, MAX_HEIGHT);
        return scene;

    }

    public void saveAsPng() throws AWTException {

        WritableImage image = scene.snapshot(null);
        Path path = Paths.get("/Users/AndreyStoyanov/Desktop/JavaFX/RoadRunnerFX/src/resources/gamePauseImage.png");
        File file = path.toFile();

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeToPause() {

        try {
            saveAsPng();
            BufferedImage bi = ImageIO.read(
                    Paths.get("/Users/AndreyStoyanov/Desktop/JavaFX/RoadRunnerFX/src/resources/gamePauseImage.png")
                            .toFile());
            image = SwingFXUtils.toFXImage(bi, null);
            imagePause.setImage(image);
            sceneController.activate(SceneName.PAUSE_SCENE);
        } catch (AWTException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clickPause() {
        if (clickedPaused == 0) {
            this.pauseThreads();
            clickedPaused++;

        } else {
            this.continueThreads();
            clickedPaused--;
        }
    }

    public void updatePositionOnResizing() {

        subSceneCenter.setWidth(scene.getWindow().getWidth());
        subSceneCenter.setHeight(scene.getWindow().getHeight());
        switch (sp.getOnLine()) {
            case 1:
                sp.setTranslateX(subSceneCenter.getWidth() / 2 - 240);

                break;
            case 2:
                sp.setTranslateX(subSceneCenter.getWidth() / 2);
                break;
            case 3:
                sp.setTranslateX(subSceneCenter.getWidth() / 2 + 240);
                break;
            default:
                break;
        }
        sp.setTranslateY(subSceneCenter.getHeight() / 2 - 25);
        ground.setTranslateX(subSceneCenter.getWidth() / 2);
        ground.setTranslateY(subSceneCenter.getHeight() / 2);
        objectCreater.updatePositionOnResizing();

    }

    public void changeToFirstThirdPersonView() {
        // TranslateTransition switchViewTransition = new
        // TranslateTransition(Duration.seconds(0.1), camera);
        if (!firstView && sp.isJumpOn()) {
//			switchViewTransition = new TranslateTransition(Duration.seconds(0.1), camera);
//			switchViewTransition.setCycleCount(1);
//			switchViewTransition.setFromY(camera.getTranslateY());
//			switchViewTransition.setFromZ(camera.getTranslateZ());
//			switchViewTransition.setToY(camera.getTranslateY() + 170);
//			switchViewTransition.setToZ(camera.getTranslateZ() + 700);
//			switchViewTransition.play();
            camera.setTranslateY(camera.getTranslateY() + 170);
            camera.setTranslateZ(camera.getTranslateZ() + 700);
            firstView = true;
        } else {
            if (sp.isJumpOn()) {
                camera.setTranslateY(camera.getTranslateY() - 170);
                camera.setTranslateZ(camera.getTranslateZ() - 700);
                firstView = false;
            }
        }
    }

    public Button createViewButton() {
        Button changeViewButton = new Button("Change View");
        changeViewButton.setFont(new Font("Arial", 25));
        changeViewButton.setFocusTraversable(false);
        changeViewButton.setOnAction(event -> {
            changeToFirstThirdPersonView();
        });

        return changeViewButton;

    }

    public Scene createGameOverScene() {
        VBox vb = new VBox();
        vb.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        vb.setAlignment(Pos.CENTER);

        Label gameOverLabel = new Label("GAME OVER!!!");

        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setFont(new Font("Arial", 70));
        Button playAgainButton = new Button("Play Again");
        playAgainButton.setFont(new Font("Arial", 70));
        playAgainButton.getStyleClass().add("buttons");

        playAgainButton.setOnAction(event -> {
            initialize();
            sceneController.activate(SceneName.GAME_SCENE);
            startThreads();
        });

        vb.getChildren().addAll(gameOverLabel, playAgainButton);
        VBox.setMargin(gameOverLabel, new Insets(0, 0, 50, 0));
        Scene scene = new Scene(vb, this.scene.getWidth(), this.scene.getHeight(), true, SceneAntialiasing.BALANCED);

        return scene;
    }

    public void createSceneListener() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case A:
                    sp.moveLeft(240);
                    break;
                case LEFT:
                    sp.moveLeft(240);
                    break;
                case D:
                    sp.moveRight(240);
                    break;
                case RIGHT:
                    sp.moveRight(240);
                    break;
                case SPACE:
                    sp.jump();
                    break;
                case ESCAPE:
                    clickPause();
                    break;
                case UP:
                    sp.jump();
                    break;
                case V:
                    sp.shoot();
                    break;
                default:
                    break;

            }

        });
        pauseScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    clickPause();
                    break;

                default:
                    break;
            }
        });
    }

    private void createScoreLabelListener() {
        scoreLabel.textProperty().addListener(event -> {
            objectCreater.level_up();

        });
    }

    public void finishGame() {
        stopThreads();

    }

    public int getMAX_WIDTH() {
        return MAX_WIDTH;
    }

    public int getMAX_HEIGHT() {
        return MAX_HEIGHT;
    }

    public int getTABLE_WIDTH() {
        return TABLE_WIDTH;
    }

    public int getTABLE_HEIGHT() {
        return TABLE_HEIGHT;
    }

    public int getTABLE_DEPTH() {
        return TABLE_DEPTH;
    }

    public Camera getCamera() {
        return camera;
    }

    public Group getRoot() {
        return root;
    }

    public MyGround getGround() {
        return ground;
    }

    public ObjectCreater getObjectCreater() {
        return objectCreater;
    }

    public Node[] getPointlights() {
        return pointlights;
    }

    public Scene getScene() {
        return scene;
    }

    public MySphere getSp() {
        return sp;
    }

    public Scene getPauseScene() {

        return pauseScene;
    }

    public int getScore() {
        return score;
    }

    public void addToScore(int x) {
        if (!(x < 0 && score % 100 == 0)) {
            score += x;
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isFirstView() {
        return firstView;
    }

    public Label getLevelLabel() {
        return levelLabel;
    }

    public void setLevelLabel(Label levelLabel) {
        this.levelLabel = levelLabel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

}
