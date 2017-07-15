import Toasts.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class GUI extends Application {

    private Stage mainStage;
    private Group root;
    private MenuBar menuBar;
    private TabManager tabManager;
    private ArrayList<TabInfo> tabInfos;
    private FileManager fileManager;
    private Preferences defaultPreferences;
    private boolean stageExists = false;
    private Scene scene;
    private Toast toast;
    private String css;

    /**
     * initialises various objects; calls the methods for menu, tab creation
     * */
    private void initialSettings() {

        fileManager = new FileManager(mainStage);
        getPreferences();

        tabManager = new TabManager();

        //register first tab
        tabInfos = new ArrayList<>();
        tabInfos.add(new TabInfo(tabManager.getCurrentTab()));
        tabManager.getCurrentTab().setContent(newTextArea());
        setTabCloseHandle(tabManager.getCurrentTab());

        menuBar = new MenuBar();
        createMenus();
        createMenuItems();
        setStageCloseRequest();

        root.getChildren().addAll(menuBar, tabManager.getTabPane());
    }

    /**
     * Alerts the user if
     *  - their work hasn't been saved
     *  - they are about to close multiple tabs
     * */
    private void setStageCloseRequest() {
        mainStage.setOnCloseRequest(event -> {
            if (tabManager.getTabs().size() > 1) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm to quit");
                alert.setContentText("You are about to close " + tabManager.getTabs().size() + " tabs. Do you really want to quit?");

                ButtonType no = new ButtonType("No, take me back");
                ButtonType yes = new ButtonType("Yes, I'd like to quit");
                alert.getButtonTypes().setAll(no, yes);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == no) {
                    alert.hide();
                    event.consume();
                }
            }
            else if (tabManager.getTabs().size() == 1 && !unChanged(tabManager.getCurrentTab())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm to quit");
                alert.setContentText("The content of the tab is not saved. Do you really want to quit?");
                ButtonType no = new ButtonType("No, take me back");
                ButtonType yes = new ButtonType("Yes, I'd like to quit");
                alert.getButtonTypes().setAll(no, yes);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == no) {
                    alert.hide();
                    event.consume();
                }
            }
        });
    }

    /**
     * Reads the global preferences set by the user.
     * */
    private void getPreferences() {

        if (!new File("preferences.ser").isFile()) {
            defaultPreferences = new Preferences("Arial", 17, Theme.Light);
        }
        else {
            defaultPreferences = fileManager.load();
        }
        toast = defaultPreferences.theme == Theme.Light ? new LightToast() : new DarkToast();

        if (defaultPreferences.theme == Theme.Light) {
            css = this.getClass().getResource("css/light.css").toExternalForm();
        }
        else {
            css = this.getClass().getResource("css/dark.css").toExternalForm();
        }
        scene.getStylesheets().clear();
        scene.getStylesheets().add("css/common.css");
        scene.getStylesheets().add(css);
    }

    /**
     * Creates the options in the menu bar
     * */
    private void createMenus() {
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");
        Menu menuFormat = new Menu("Format");
        Menu menuHelp = new Menu("Help");
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuFormat, menuHelp);
        menuBar.setPrefWidth(800);
    }
    /**
     * creates items for each of the individual menu items in the menu
     * */
    private void createMenuItems() {
        MenuItem createNew = new MenuItem("New");
        createNew.setOnAction(event -> createNewTab());
        createNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        MenuItem open = new MenuItem("Open");
        open.setOnAction(event -> readFile());
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        MenuItem save = new MenuItem("Save       ");
        save.setOnAction(event -> save());
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        MenuItem saveAs = new MenuItem("Save As");
        saveAs.setOnAction(event -> saveAs());
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
        MenuItem prefs = new MenuItem("Preferences");
        prefs.setOnAction(event -> setPreferences());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> quit());
        menuBar.getMenus().get(0).getItems().addAll(createNew, open, save, saveAs, prefs, exit);

        MenuItem copy = new MenuItem("Copy       ");
        copy.setDisable(true);
        copy.setOnAction(event -> getCurrentTextArea().copy());
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        MenuItem cut = new MenuItem("Cut");
        cut.setDisable(true);
        cut.setOnAction(event -> getCurrentTextArea().cut());
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(event -> getCurrentTextArea().paste());
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));

        menuBar.getMenus().get(1).getItems().addAll(copy, cut, paste);

        MenuItem wordCount = new MenuItem("Word Count");
        wordCount.setOnAction(event -> getWordCount());
        menuBar.getMenus().get(2).getItems().addAll(wordCount);

        Menu font = new Menu("Font");
        createFonts(font);

        Menu fontSize = new Menu("Font Size");
        createFontSizes(fontSize);
        menuBar.getMenus().get(3).getItems().addAll(font, fontSize);

        MenuItem view = new MenuItem("View on Github");
        view.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("http://www.google.com"));
            } catch (IOException e) {
                createWarningAlert("Unable to connect to web browser");
            } catch (URISyntaxException e) {
                createWarningAlert("Unable to connect to web browser");
                e.printStackTrace();
            }
        });
        MenuItem report = new MenuItem("Send feedback");
        report.setOnAction(event -> sendMail());
        menuBar.getMenus().get(4).getItems().addAll(view, report);
    }

    private void createFonts(Menu menu) {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(Font.getFontNames());
        ListView<String> list = new ListView<>(observableList);
        list.setPrefHeight(200);
        list.setOnMouseClicked(event -> {
            String x = list.getSelectionModel().getSelectedItem();
            getCurrentTextArea().setFont(Font.font(x));
        });
        CustomMenuItem customMenuItem = new CustomMenuItem(list, true);
        menu.getItems().add(customMenuItem);
    }

    private void createFontSizes(Menu menu) {
        ObservableList<Integer> observableList = FXCollections.observableArrayList();
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 3; i <= 24; i++) {
            nums.add(i);
        }
        observableList.addAll(nums);
        ListView<Integer> list = new ListView<>(observableList);
        list.setPrefHeight(200);
        list.setOnMouseClicked(event -> {
            int x = list.getSelectionModel().getSelectedItem();
            getCurrentTextArea().setFont(Font.font(x));
        });
        CustomMenuItem customMenuItem = new CustomMenuItem(list, true);
        menu.getItems().add(customMenuItem);
    }

    /**
     * responsible for setting the global preferences of the user.
     * The preferences are stored in the (serialised) preferences.ser file.
     * */
    private void setPreferences() {
        if (stageExists) return;
        stageExists = true;

        EventHandler<WindowEvent> original = mainStage.getOnCloseRequest();

        Stage stage = new Stage();
        Pane group = new Pane();
        Scene prefScene = new Scene(group, 400, 300);

        prefScene.getStylesheets().clear();
        prefScene.getStylesheets().add("css/common.css");
        prefScene.getStylesheets().add(css);
        group.setId("popup");

        Text discText = new Text("These changes will take place when the editor is restarted");
        discText.setLayoutX(20);
        discText.setLayoutY(20);
        discText.setFont(Font.font("Verdana", 12));
        discText.setId("popup-text");

        Text fontText = new Text("Default font:");
        fontText.setId("popup-text");
        fontText.setLayoutX(10);
        fontText.setLayoutY(95);

        ComboBox<String> fonts = new ComboBox<>();
        fonts.setLayoutX(100);
        fonts.setLayoutY(80);
        fonts.setValue(defaultPreferences.font);
        fonts.setPrefWidth(294);
        fonts.setVisibleRowCount(5);
        for (String font : Font.getFontNames()) {
            fonts.getItems().add(font);
        }

        Text fontSize = new Text("Default font size:");
        fontSize.setId("popup-text");
        fontSize.setLayoutX(10);
        fontSize.setLayoutY(125);

        ComboBox<Integer> fontSizes = new ComboBox<>();
        fontSizes.setLayoutX(330);
        fontSizes.setLayoutY(110);
        fontSizes.setValue(defaultPreferences.fontSize);
        for (int i = 0; i < 22; i++) {
            fontSizes.getItems().add(i + 3);
        }

        Text theme = new Text("Default theme:");
        theme.setId("popup-text");
        theme.setLayoutX(10);
        theme.setLayoutY(155);

        ComboBox<Theme> themes = new ComboBox<>();
        themes.setLayoutX(315);
        themes.setLayoutY(140);
        themes.setValue(defaultPreferences.theme);
        for (Theme theme1 : Theme.values()) {
            themes.getItems().add(theme1);
        }

        Button apply = new Button("Apply Changes");
        apply.setLayoutX(290);
        apply.setLayoutY(260);
        apply.setId("apply");
        apply.setOnMouseClicked(event -> {
            defaultPreferences.font = fonts.getValue();
            defaultPreferences.fontSize = fontSizes.getValue();
            defaultPreferences.theme = themes.getValue();
            defaultPreferences.save();
            stageExists = false;
            stage.close();
            mainStage.onCloseRequestProperty().setValue(original);
        });

        group.getChildren().addAll(discText, fontText, fonts, fontSize, fontSizes, theme, themes, apply);

        stage.setScene(prefScene);
        stage.show();

        mainStage.setOnCloseRequest(Event::consume);
        stage.setOnCloseRequest(event -> {
            stageExists = false;
            mainStage.onCloseRequestProperty().setValue(original);
        });
    }

    private void sendMail() {
        try {
            Desktop.getDesktop().mail(URI.create("mailto:manalmohania@gmail.com"));
        } catch (IOException e) {
            createWarningAlert("Unable to connect to email client");
        }
    }

    private void getWordCount() {
        String[] words;
        if (getCurrentTextArea().getText().equals("")) {
            words = new String[0];
        }
        else if (getCurrentTextArea().getSelectedText().equals("")) {
            words = getCurrentTextArea().getText().split(" ");
        }
        else {
            words = getCurrentTextArea().getSelectedText().split(" ");
        }
        toast.makeText(mainStage, words.length + " words", 3000, 100, 100, 15);
    }

    /**
     * creates the onExitButtonPressed handlers
     * */
    private void quit() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm to quit");
        ButtonType no = new ButtonType("No, take me back");
        ButtonType yes = new ButtonType("Yes, I'd like to quit");

        if (tabManager.getTabs().size() > 1) {
            alert.setContentText("You are about to close " + tabManager.getTabs().size() + " tabs. Do you really want to quit?");
            alert.getButtonTypes().setAll(no, yes);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == no) {
                alert.hide();
                return;
            }
        }
        else if (tabManager.getTabs().size() == 1 && !unChanged(tabManager.getCurrentTab())) {
            alert.setContentText("The content of the tab is not saved. Do you really want to quit?");
            alert.getButtonTypes().setAll(no, yes);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == no) {
                alert.hide();
                return;
            }
        }
        Platform.exit();
    }

    private void save(){
        File file = null;
        for (TabInfo tabInfo : tabInfos) {
            if (tabInfo.getTab().equals(tabManager.getCurrentTab()) && tabInfo.getFile() == null) {
                saveAs();
                return;
            }
        }

        // get the associated file
        for (TabInfo tabInfo : tabInfos) {
            if (tabInfo.getTab().equals(tabManager.getCurrentTab())) {
                file = tabInfo.getFile();
            }
        }
        if (!fileManager.writeFile(file, getCurrentTextArea().getText())) {
            createWarningAlert("Error saving file");
        }
        else {
            toast.makeText(mainStage, "File saved", 1000, 100, 100, 10);
            tabInfos.stream()
                    .filter(tabInfo -> tabInfo.getTab().equals(tabManager.getCurrentTab()))
                    .forEach(tabInfo -> tabInfo.setHash(getCurrentTextArea().getText().hashCode()));
        }

    }

    private void saveAs() {
        File file = fileManager.setTextFile();
        if (file == null) {
            return;
        }
        // if a previous file already exists, remove those details from tabInfos
        int toRemove = -1;
        for (int i = 0; i < tabInfos.size(); i++) {
            if (tabInfos.get(i).getTab().equals(tabManager.getCurrentTab()) && tabInfos.get(i).getFile() != null) {
                toRemove = i;
            }
        }
        if (toRemove != -1) {
            tabInfos.remove(toRemove);
            tabInfos.add(new TabInfo(tabManager.getCurrentTab()));
        }
        tabInfos.stream()
                .filter(tabInfo -> tabInfo.getTab().equals(tabManager.getCurrentTab()))
                .forEach(tabInfo -> tabInfo.setFile(file));
        save();
    }

    private void readFile() {
        File file = fileManager.getTextFile();
        if (file == null) {
            return;
        }
        String content = fileManager.readFile(file);
        if (tabManager.getCurrentTab() == null || !getCurrentTextArea().getText().equals("")) {
            createNewTab();
        }
        getCurrentTextArea().setText(content);

        tabInfos.stream()
                .filter(tabInfo -> tabInfo.getTab().equals(tabManager.getCurrentTab()))
                .forEach(tabInfo -> tabInfo.setHash(content.hashCode()));

        int dotPos = file.getName().lastIndexOf('.');
        String name = dotPos > 0 ? file.getName().substring(0, dotPos) : file.getName();
        tabManager.getCurrentTab().setText(name);
        tabInfos.stream()
                .filter(tabInfo -> tabInfo.getTab().equals(tabManager.getCurrentTab()))
                .forEach(tabInfo -> tabInfo.setFile(file));
    }

    private TextArea newTextArea() {
        TextArea textArea = new TextArea();
        textArea.setLayoutX(0);
        textArea.setMinWidth(802);
        textArea.setMaxWidth(802);
        textArea.setPrefHeight(572);
        textArea.setWrapText(true);
        textArea.setOnMouseExited(event -> {
            Menu edit = menuBar.getMenus().get(1);
            if (textArea.getSelectedText().equals("")) {
                edit.getItems().get(0).setDisable(true);
                edit.getItems().get(1).setDisable(true);
            }
            else {
                edit.getItems().get(0).setDisable(false);
                edit.getItems().get(1).setDisable(false);
            }
        });
        textArea.setFont(Font.font(defaultPreferences.font, defaultPreferences.fontSize));
        return textArea;
    }

    private void setTabCloseHandle(Tab tab) {
        tab.setOnClosed(event -> {
            for (int i = 0; i < tabInfos.size(); i++) {
                if (tabInfos.get(i).getTab().equals(tab)) {
                    tabInfos.remove(i);
                    break;
                }
            }
        });
    }

    /**
     * returns true if the text in the textarea of the tab is unchanged
     * */
    private boolean unChanged(Tab tab) {
        for (TabInfo tabInfo : tabInfos) {
            if (tabInfo.getTab().equals(tab)) {
                return tabInfo.getHash() == ((TextArea) tab.getContent()).getText().hashCode();
            }
        }
        return false;
    }

    /**
     * creates a warning alert
     * @param message: the message to display in the alert
     * */
    private void createWarningAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.show();
    }

    private void createNewTab() {
        Tab tab = tabManager.newTab();
        if (tabInfos == null) {
            tabInfos = new ArrayList<>();
        }
        if (tab != null) {
            tabManager.getTabs().add(tab);
            tab.setContent(newTextArea());
            tabInfos.add(new TabInfo(tab));
            setTabCloseHandle(tab);
        }
        else {
            createWarningAlert("Tab limit reached!");
        }
    }

    /**
     * returns the TextArea of the current tab
     * */
    private TextArea getCurrentTextArea(){
        return (TextArea)tabManager.getCurrentTab().getContent();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Text Editor");
        root = new Group();
        Scene scene = new Scene(root, 800, 600);
        this.scene = scene;
        mainStage = primaryStage;
        initialSettings();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // TODO : remove this in the future
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
