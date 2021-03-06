package config.model;

import java.io.Serializable;
import java.util.List;

public class QuickInfoModel implements Serializable {

    private String sdk;

    private String apkpath;

    private String signVersionPath;

    private String packageName;

    private String application;

    private List<UpdateResModel> androidManifest;

    private ApktoolModel apktool;

    private ResModel res;

    private KeyStoreModel keystore;

    private List<UpdateResModel> replaceIcon;

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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public List<UpdateResModel> getAndroidManifest() {
        return androidManifest;
    }

    public void setAndroidManifest(List<UpdateResModel> androidManifest) {
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

    public List<UpdateResModel> getReplaceIcon() {
        return replaceIcon;
    }

    public void setReplaceIcon(List<UpdateResModel> replaceIcon) {
        this.replaceIcon = replaceIcon;
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
            ", packageName='" + packageName + '\'' +
            ", application='" + application + '\'' +
            ", androidManifest=" + androidManifest +
            ", apktool=" + apktool +
            ", res=" + res +
            ", keystore=" + keystore +
            ", replaceIcon=" + replaceIcon +
            ", targetSdkVersion=" + targetSdkVersion +
            '}';
    }
}
