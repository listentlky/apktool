package config.res;

import brut.util.BrutIO;
import config.QuickConfig;
import config.model.QuickInfoModel;
import config.model.ReplaceResModel;
import config.utils.UZipUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResManager {
    private static List<ReplaceResModel> resModelHashMap;
    private static ResManager instance;

    public static ResManager getInstance() {
        if (instance == null) {
            synchronized (ResManager.class) {
                if (instance == null) {
                    instance = new ResManager();
                }
            }
        }
        return instance;
    }


    /**
     * 读取替换资源
     */
    public void readQuickRes(QuickInfoModel quickInfoModel){
        File sdkFile = new File("");
        List<String> zipFilePathList = new ArrayList<>();
        for (File file:sdkFile.listFiles()) {
            System.out.println("sdk/file: "+file);
            String zipFilePath;
            zipFilePath = file.getPath();
            if (zipFilePath.endsWith("aar")) {
                zipFilePath = file.getPath().replace("aar", "zip");
                File zipFile = new File(zipFilePath);
                if (!zipFile.exists()) {
                    try {
                        BrutIO.copyAndClose(new FileInputStream(file), new FileOutputStream(zipFile));
                    } catch (Exception e) {

                    }
                    zipFilePathList.add(zipFilePath);
                }
            }else if(zipFilePath.endsWith("zip")){
                zipFilePathList.add(zipFilePath);
            }
        }

        for (String zipFilePath:zipFilePathList) {
            System.out.println("zipFilePath: " + zipFilePath);
            UZipUtils.unZipFiles(new File(zipFilePath), zipFilePath.replace(".zip", ""),quickInfoModel);
        }

    }


    public List<ReplaceResModel> getResModelList() {
        if(resModelHashMap == null){
            resModelHashMap = new ArrayList<>();
        }
        return resModelHashMap;
    }

}
