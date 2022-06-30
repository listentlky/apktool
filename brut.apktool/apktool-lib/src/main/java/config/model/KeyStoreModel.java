package config.model;

import java.io.Serializable;

public class KeyStoreModel implements Serializable {

    private String path;

    private String alias;

    private String password;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "KeyStoreModel{" +
            "path='" + path + '\'' +
            ", alias='" + alias + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
