package config.model;

import java.io.Serializable;
import java.util.List;

public class ResModel implements Serializable {

    private List<UpdateResModel> icon;

    private List<UpdateResModel> value;

    public List<UpdateResModel> getIcon() {
        return icon;
    }

    public void setIcon(List<UpdateResModel> icon) {
        this.icon = icon;
    }

    public List<UpdateResModel> getValue() {
        return value;
    }

    public void setValue(List<UpdateResModel> value) {
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
