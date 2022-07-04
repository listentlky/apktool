package config.model;

import java.io.Serializable;

public class UpdateResModel implements Serializable {

    private String type;

    private String name;

    private String value;

    public UpdateResModel(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "updateResModel{" +
            "type='" + type + '\'' +
            ", name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
