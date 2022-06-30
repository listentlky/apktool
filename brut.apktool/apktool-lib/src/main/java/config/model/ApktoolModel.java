package config.model;

import java.io.Serializable;

public class ApktoolModel implements Serializable {

    private String minSdkVersion;
    private String targetSdkVersion;
    private String versionCode;
    private String versionName;

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

    public String getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public void setTargetSdkVersion(String targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "ApktoolModel{" +
            "minSdkVersion='" + minSdkVersion + '\'' +
            ", targetSdkVersion='" + targetSdkVersion + '\'' +
            ", versionCode='" + versionCode + '\'' +
            ", versionName='" + versionName + '\'' +
            '}';
    }
}
