package Toasts;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * This class creates an android-like toast
 * */
public abstract class Toast {

    Group root;
    Text text;

    Toast(){}

    /**
     * Creates a toast with a certain message
     *
     * @param ownerStage: The stage to which the toast belongs
     * @param toastMsg: The message which is to be displayed in the toast
     * @param toastDelay: The duration in ms for which the message appears
     * @param fadeDelay: The duration for which the messages fades in and out (in ms)
     * @param fontSize: The font size of the message
     *
     * Much of this method is borrowed from https://stackoverflow.com/a/38373408/6063947
     * */
    public void makeText(Stage ownerStage, String toastMsg, int toastDelay, int fadeDelay, int fontSize) {
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        text = new Text(toastMsg);
        text.setFont(Font.font("Verdana", fontSize));
        text.setLayoutY(600);
        text.setLayoutX(650);

        root = new Group(text);
        root.setStyle("-fx-background-radius: 2;" +
                " -fx-padding: 5px;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
        toastStage.show();

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished(event -> new Thread(() -> {
            try {
                Thread.sleep(toastDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Timeline fadeOutTimeline = new Timeline();
            KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeDelay), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 0));
            fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
            fadeOutTimeline.setOnFinished(event1 -> toastStage.close());
            fadeOutTimeline.play();
        }).start());
        fadeInTimeline.play();
    }
}

