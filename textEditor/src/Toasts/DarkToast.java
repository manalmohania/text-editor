package Toasts;

import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class DarkToast extends Toast{
    public DarkToast(){
        super();
    }

    @Override
    public void makeText(Stage ownerStage, String toastMsg, int toastDelay, int fadeInDelay, int fadeOutDelay, int fontSize) {
        super.makeText(ownerStage, toastMsg, toastDelay, fadeInDelay, fadeOutDelay, fontSize);
        text.setFill(Color.WHITE);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
    }
}
