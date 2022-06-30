package config.model;

import java.io.Serializable;

public class ReplaceResModel implements Serializable {

    private String quick;

    private String apkFileDir;

    private String replacePath;

    public ReplaceResModel(Builder builder) {
        this.quick = builder.quick;;
        this.apkFileDir = builder.apkFileDir;
        this.replacePath = builder.replacePath;
    }

    public String getQuick() {
        return quick;
    }

    public String getApkFileDir() {
        return apkFileDir;
    }

    public String getReplacePath() {
        return replacePath;
    }

    @Override
    public String toString() {
        return "ReplaceResModel{" +
            "apkFileDir='" + apkFileDir + '\'' +
            ", replacePath='" + replacePath + '\'' +
            '}';
    }

    public static class Builder{

        private String quick;

        private String apkFileDir;

        private String replacePath;

        public Builder setQuick(String quick) {
            this.quick = quick;
            return this;
        }

        public Builder setApkFileDir(String apkFileDir) {
            this.apkFileDir = apkFileDir;
            return this;
        }

        public Builder setReplacePath(String replacePath) {
            this.replacePath = replacePath;
            return this;
        }
        public ReplaceResModel build(){
            return new ReplaceResModel(this);
        }
    }
}
