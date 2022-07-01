package config.model;

import java.io.Serializable;
import java.util.List;

public class QuickInfoModel implements Serializable {

    private String sdk;

    private String apkpath;

    private String signVersionPath;

    private List<String> androidManifest;

    private ApktoolModel apktool;

    private ResModel res;

    private KeyStoreModel keystore;

    private String replaceIconType;

    private String replaceIconName;

    private String packageName;

    private int targetSdkVersion;

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getApkpath() {
        return apkpath;
    }

    public void setApkpath(String apkpath) {
        this.apkpath = apkpath;
    }

    public String getSignVersionPath() {
        return signVersionPath;
    }

    public void setSignVersionPath(String signVersionPath) {
        this.signVersionPath = signVersionPath;
    }

    public List<String> getAndroidManifest() {
        return androidManifest;
    }

    public void setAndroidManifest(List<String> androidManifest) {
        this.androidManifest = androidManifest;
    }

    public ApktoolModel getApktool() {
        return apktool;
    }

    public void setApktool(ApktoolModel apktool) {
        this.apktool = apktool;
    }

    public ResModel getRes() {
        return res;
    }

    public void setRes(ResModel res) {
        this.res = res;
    }

    public KeyStoreModel getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStoreModel keystore) {
        this.keystore = keystore;
    }

    public String getReplaceIconType() {
        return replaceIconType;
    }

    public void setReplaceIconType(String replaceIconType) {
        this.replaceIconType = replaceIconType;
    }

    public String getReplaceIconName() {
        return replaceIconName;
    }

    public void setReplaceIconName(String replaceIconName) {
        this.replaceIconName = replaceIconName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public void setTargetSdkVersion(int targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
    }

    @Override
    public String toString() {
        return "QuickInfoModel{" +
            "sdk='" + sdk + '\'' +
            ", apkpath='" + apkpath + '\'' +
            ", signVersionPath='" + signVersionPath + '\'' +
            ", androidManifest=" + androidManifest +
            ", apktool=" + apktool +
            ", res=" + res +
            ", keystore=" + keystore +
            ", replaceIconType='" + replaceIconType + '\'' +
            ", replaceIconName='" + replaceIconName + '\'' +
            ", packageName='" + packageName + '\'' +
            ", targetSdkVersion=" + targetSdkVersion +
            '}';
    }
}
