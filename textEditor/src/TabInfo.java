import javafx.scene.control.Tab;

import java.io.File;


class TabInfo {
    private Tab tab;
    private File file = null;
    private int stringHash = "".hashCode();

    TabInfo(Tab tab){
        this.tab = tab;
    }

    Tab getTab() {
        return tab;
    }

    void setFile(File file) {
        this.file = file;
    }

    File getFile() {
        return file;
    }

    void setHash(int num) {
        stringHash = num;
    }

    int getHash() {
        return stringHash;
    }

}
