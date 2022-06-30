package config.model;

import java.io.Serializable;
import java.util.List;

public class ResModel implements Serializable {

    private List<String> icon;

    private List<String> value;

    public List<String> getIcon() {
        return icon;
    }

    public void setIcon(List<String> icon) {
        this.icon = icon;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ResModel{" +
            "icon=" + icon +
            ", value=" + value +
            '}';
    }
}
