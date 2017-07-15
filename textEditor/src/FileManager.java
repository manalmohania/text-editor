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

    File getTextFile(){
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(stage);
    }

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

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

    File setTextFile() {
        fileChooser.setTitle("Save text file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showSaveDialog(stage);
    }

    Preferences load() {
        Preferences res = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("preferences.ser"));
            res = (Preferences) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

}
