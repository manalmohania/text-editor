import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;


class TabManager {

    private TabPane tabPane;

    TabManager(){
        this.tabPane = new TabPane();
        tabPane.setLayoutY(28); // fixed for now
        tabPane.getTabs().add(newTab());
    }

    Tab newTab() {
        int numTabs = tabPane.getTabs().size();
        if (numTabs >= 6) {
            return null;
        }
        Tab tab =  new Tab("untitled");
        tabPane.getSelectionModel().select(tab);
        return tab;
    }

    ObservableList<Tab> getTabs(){
        return tabPane.getTabs();
    }

    TabPane getTabPane() {
        return tabPane;
    }

    Tab getCurrentTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

}
