import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

final class Preferences implements Serializable{

    // not using Font as a field because it is not serialisable

    String font;
    int fontSize;
    Theme theme;

    Preferences(String font, int fontSize, Theme theme) {
        this.font = font;
        this.fontSize = fontSize;
        this.theme = theme;
    }

    /**
     * serialises a Preferences object and saves it in a file called preferences.ser
     * */
    void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("preferences.ser"));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
