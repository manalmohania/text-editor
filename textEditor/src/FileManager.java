import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;


class FileManager {

    private FileChooser fileChooser;
    private Stage stage;

    FileManager(Stage stage){
        fileChooser = new FileChooser();
        this.stage = stage;
    }

    /**
     * Opens a file chooser open dialog and returns the file selected by the user
     * */
    File getTextFile(){
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * reads a txt file and returns the content in a string
     * */
    String readFile(File file) {
        if (file == null) {
            throw new NullPointerException("Empty file passed as argument in FileManager.readFile");
        }
        String content = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * Given a file and some text, the method writes the text to the file
     *
     * @param file: the file to which the text has to be written
     * @param text: the text that is to be written
     *
     * @return true if the text has been successfully written to the file, false otherwise
     * */
    boolean writeFile(File file, String text) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            writer.write(text);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Opens a file chooser save dialog and returns the file selected by the user
     * */
    File setTextFile() {
        fileChooser.setTitle("Save text file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showSaveDialog(stage);
    }

    /**
     * loads the serialised preferences.ser file
     * */
    Preferences load() {
        Preferences res = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("preferences.ser"));
            res = (Preferences) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

}
