package config.model;

import java.io.File;
import java.io.Serializable;

public class DecodeApkFileModel implements Serializable {

    private String quickMark;
    private File apkFileName;

    private File apkOutDir;

    public DecodeApkFileModel(Builder builder) {
        this.quickMark = builder.quickMark;
        this.apkFileName = builder.apkFileName;
        this.apkOutDir = builder.apkOutDir;
    }

    public String getQuickMark() {
        return quickMark;
    }

    public File getApkFileName() {
        return apkFileName;
    }

    public File getApkOutDir() {
        return apkOutDir;
    }

    @Override
    public String toString() {
        return "DecodeApkFileModel{" +
            "quickMark='" + quickMark + '\'' +
            ", apkFileName=" + apkFileName +
            ", apkOutDir=" + apkOutDir +
            '}';
    }

    public static class Builder{

        private String quickMark;

        private File apkFileName;

        private File apkOutDir;

        public Builder setQuickMark(String quickMark) {
            this.quickMark = quickMark;
            return this;
        }

        public Builder setApkFileName(File apkFileName) {
            this.apkFileName = apkFileName;
            return this;
        }

        public Builder setApkOutDir(File apkOutDir) {
            this.apkOutDir = apkOutDir;
            return this;
        }

        public DecodeApkFileModel build(){
            return new DecodeApkFileModel(this);
        }
    }
}
