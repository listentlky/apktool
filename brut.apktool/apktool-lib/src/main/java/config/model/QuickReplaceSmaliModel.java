package config.model;

import java.io.Serializable;

public class QuickReplaceSmaliModel implements Serializable {

    private String quick;
    private String endPath;

    private String path;

    public QuickReplaceSmaliModel(String quick,String endPath, String path) {
        this.quick = quick;
        this.endPath = endPath;
        this.path = path;
    }

    public String getQuick() {
        return quick;
    }

    public String getEndPath() {
        return endPath;
    }

    public String getPath() {
        return path;
    }
}
