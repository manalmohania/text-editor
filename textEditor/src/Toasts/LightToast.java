package Toasts;

import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class LightToast extends Toast {
    public LightToast(){
        super();
    }

    @Override
    public void makeText(Stage ownerStage, String toastMsg, int toastDelay, int fadeDelay, int fontSize) {
        super.makeText(ownerStage, toastMsg, toastDelay, fadeDelay, fontSize);
        text.setFill(Color.BLACK);
        root.setStyle("-fx-background-color: rgba(150, 150, 150, 0);");
    }
}
