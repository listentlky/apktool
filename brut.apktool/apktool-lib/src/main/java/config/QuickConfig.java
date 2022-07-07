package config;

import brut.androlib.meta.MetaInfo;
import brut.androlib.res.data.ResResource;
import brut.directory.ExtFile;
import brut.util.BrutIO;
import com.alibaba.fastjson.JSON;
import config.model.*;
import config.utils.FileUtils;
import config.utils.Dom4jUtil;
import config.utils.TextUtils;
import config.utils.XmlUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QuickConfig {

    private static QuickConfigModel quickConfigModel;

    private static QuickInfoModel currentQuickInfo;

    private static boolean autoBuildQuick = false;

    private static QuickConfig instance;

    public static QuickConfig getInstance() {
        if (instance == null) {
            synchronized (QuickConfig.class) {
                if (instance == null) {
                    instance = new QuickConfig();
                }
            }
        }
        return instance;
    }

    public void initQuickConfig(String configPath) {

        try {
            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(configPath), "UTF-8"));
            String strLine;
            String str = "";
            while ((strLine = bufferedReader.readLine()) != null) {
                str += strLine;
            }
            bufferedReader.close();

            quickConfigModel = JSON.parseObject(str, QuickConfigModel.class);
            System.out.println("加载配置: " + quickConfigModel.toString());

        } catch (Exception e) {
            System.out.println("加载配置 Exception: " + e);
        }
    }

    public void setApkFileList(List<DecodeApkFileModel> apkFileList) {
        if (quickConfigModel == null || quickConfigModel.getQuick() == null || quickConfigModel.getQuick().getInfo() == null) {
            return;
        }

        for (QuickInfoModel quickInfoModel : quickConfigModel.getQuick().getInfo()) {

            apkFileList.add(new DecodeApkFileModel.Builder().setQuickMark(quickInfoModel.getSdk()).setApkFileName(new File(quickInfoModel.getApkpath())).setApkOutDir(new File(quickInfoModel.getApkpath().replace(".apk", ""))).build());
        }

    }

    /**
     * 更新渠道配置信息
     *
     * @param appDir
     * @param metaInfo
     * @param quick
     */
    public void updateQuickConfigInfo(ExtFile appDir, MetaInfo metaInfo, String quick) {
        if (getCurrentQuickInfo() == null) {
            return;
        }

        // 更新版本号 yml文件
        updateMeta(metaInfo);
        // 更新合并渠道配置清单
        updateAndroidManifest(appDir, quick);
        // 更新合并代码和资源文件
        updateSmaliAndRes(appDir, quick);
    }


    /**
     * 更新配置清单
     *
     * @param appDir
     */
    private void updateAndroidManifest(ExtFile appDir, String quick) {
        if (currentQuickInfo == null) {
            return;
        }

        File updateAndroidManifest = new File(appDir, "AndroidManifest.xml");

        Dom4jUtil.mergeAndroidManifestXml(updateAndroidManifest.getPath(), quick);

        Dom4jUtil.updateAndroidManifestXml(appDir,updateAndroidManifest.getPath());
    }

    /**
     * 更新meta apktool.yml文件
     *
     * @param meta
     */
    private void updateMeta(MetaInfo meta) {
        if (quickConfigModel == null || currentQuickInfo == null || currentQuickInfo.getApktool() == null) {
            return;
        }
        try {
            currentQuickInfo.setTargetSdkVersion(Integer.valueOf(meta.sdkInfo.get("targetSdkVersion")));
        } catch (Exception e) {

        }
        ApktoolModel apktool = currentQuickInfo.getApktool();
        if (!TextUtils.isEmpty(apktool.getVersionCode())) {
            meta.versionInfo.versionCode = apktool.getVersionCode();
        } else {
            apktool.setVersionCode(meta.versionInfo.versionCode);
        }
        if (!TextUtils.isEmpty(apktool.getVersionName())) {
            meta.versionInfo.versionName = apktool.getVersionName();
        } else {
            apktool.setVersionName(meta.versionInfo.versionName);
        }
        if (!TextUtils.isEmpty(apktool.getMinSdkVersion())) {
            meta.sdkInfo.put("minSdkVersion", apktool.getMinSdkVersion());
        }
        if (!TextUtils.isEmpty(apktool.getTargetSdkVersion())) {
            meta.sdkInfo.put("targetSdkVersion", apktool.getTargetSdkVersion());
        }
    }

    public static void main(String args[]) {

    //    File dirs = new File("E:\\devcopy\\a.xml");

        //method call to create directories
        //    dirs.getParentFile().mkdirs();
     /*   File quickFile = new File("E:\\devcopy\\googlesdk\\hcdemo_google-release");
        List<File> oneQuickFileList = FileUtils.getFileList(quickFile,new ArrayList<>());

        for (File file:oneQuickFileList) {
            System.out.println("文件："+file.getPath());
        }*/
      /*  HashMap<String, String> map = new HashMap<>();
        map.put("notification_template_part_chronometer", "0x101010101");
        map.put("support_simple_spinner_dropdown_item", "0x2020202020");

        FileUtils.resetRFile(dirs, map);*/

    //    File quickFile = new File("E:\\devcopy\\55555\\res\\values\\public.xml");

    //    List<File> oneQuickFileList = FileUtils.getFileList(quickFile, new ArrayList<>());

 /*       HashMap<String, String> allPublic = Dom4jUtil.getAllPublic(quickFile);

        Iterator<String> iterator = allPublic.keySet().iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            System.out.println("name: "+next+" ; ID: "+allPublic.get(next));
        }*/

   /*     String line = ".field public static final btn_checkbox_to_checked_box_inner_merged_animation:I   = 0x7f070140";

        String name = line.split("final")[1].split(":")[0].replaceAll(" ","");
        System.out.println("name :"+name);*/
      /*  String[] split2 = "com.startobj.tsdk.osdk.OSDKApplication".split("\\.");
        System.out.println("111： "+split2[split2.length-1]);*/
        File file = new File("E:\\wx_jh_am_xiaosb_20220706_174320");

        List<File> fileList = FileUtils.getFileList(file, new ArrayList<>());
        for (File file1:fileList) {
         //   if(file1.getName().endsWith(".smali")){
                FileUtils.searchID(file1);
        //    }
        }



        /*String outFile = "E:\\devcopy\\xipusdk\\smali\\com\\google";
        FileUtils.mkdirParentFile(new File(outFile));

        File file = new File(path);
        for (File file1:file.listFiles()) {
            try {
                BrutIO.copyAndClose(new FileInputStream(file1), new FileOutputStream(outFile+"\\"+file1.getName()));
            } catch (Exception e) {
                System.out.println("copy 出错: Exception " + e);
            }
        }*/

    }

    public static List<String> otherQuickResList = new ArrayList<>();
    public static List<QuickReplaceSmaliModel> allQuickSmaliList = new ArrayList<>();

    public static void updateSmaliAndRes(ExtFile appDir, String quick) {
        otherQuickResList.clear();
        try {
            System.out.println("开始删除所有渠道相同smali文件");
            /**
             * 1.删除其他渠道smali文件
             * 2.删除匹配渠道相同文件并保存路径
             */
            for (QuickInfoModel quickInfoModel : QuickConfig.getInstance().getQuickModel().getQuick().getInfo()) {
                File quickFile = new File(quickInfoModel.getApkpath().replace(".apk", ""));

                List<File> oneQuickFileList = FileUtils.getFileList(quickFile, new ArrayList<>());

                int outSmaliDirCount = FileUtils.smailCount(appDir);

                for (File path : oneQuickFileList) {

                    if (path.getPath().contains("AndroidManifest.xml") || path.getPath().contains("apktool.yml") ||
                        path.getPath().contains("okhttp3") || path.getPath().contains("okio")) {
                        continue;
                    }

                    String[] split = path.getPath().split(quickFile.getName());

                    if (path.getPath().contains("smali")) {
                        String smaliPath;
                        String[] split1 = split[1].split("\\\\");
                        String endPath = "";
                        for (int i = 2; i < split1.length; i++) {
                            endPath += split1[i];
                            if (i < split1.length - 1) {
                                endPath += "\\";
                            }
                        }
                        for (int i = 0; i < outSmaliDirCount; i++) { // 目标生成的smali 个数
                            smaliPath = i == 0 ? appDir + "\\smali\\" + endPath : appDir + "\\smali_classes" + (i + 1) + "\\" + endPath;

                            File file = new File(smaliPath);
                            if (file.exists()) {
                                if(!path.getPath().contains("com\\startobj\\hc") && !path.getPath().contains("com\\startobj\\util")) {
                                    allQuickSmaliList.add(new QuickReplaceSmaliModel(quickInfoModel.getSdk(), endPath.replace(path.getName(), "")
                                        , file.getPath().replace(endPath, "")));
                                }
                                FileUtils.deleteAllFile(file.getParentFile());
                            }
                        }
                    } else if (path.getPath().contains("layout")
                        && !path.getName().equals("activity_main.xml")
                        && !path.getName().startsWith("abc_")
                        && !path.getName().startsWith("select_dialog_")) {
                        if(!quickInfoModel.getSdk().equals(quick)) {
                            String[] split1 = split[1].split("\\\\");
                            String resPath = "";
                            for (int i = 2; i < split1.length; i++) {
                                resPath += split1[i];
                                if (i < split1.length - 1) {
                                    resPath += "\\";
                                }
                            }
                            File resFile = new File(appDir + "\\res\\" + resPath);
                            if (resFile.exists()) {
                                otherQuickResList.add(path.getName().substring(0, path.getName().indexOf(".")));
                                resFile.delete();
                            }
                        }
                    }
                }
            }

            System.out.println("开始复制 " + quick + " 渠道文件");
            /**
             * 目标渠道添加
             */
            for (QuickInfoModel quickInfoModel : QuickConfig.getInstance().getQuickModel().getQuick().getInfo()) {
                if (quickInfoModel.getSdk().equals(quick)) {
                    File quickFile = new File(quickInfoModel.getApkpath().replace(".apk", ""));

                    List<File> oneQuickFileList = FileUtils.getFileList(quickFile, new ArrayList<>());

                    int outSmaliDirCount = FileUtils.smailCount(appDir);
                    for (File path : oneQuickFileList) {

                        if (path.getPath().contains("AndroidManifest.xml") || path.getPath().contains("apktool.yml") ||
                            path.getPath().contains("MainActivity") || path.getName().equals("activity_main.xml")) {
                            continue;
                        }

                        String[] split = path.getPath().split(quickFile.getName());
                        File outFilePath = new File(appDir, split[1]);

                        if (path.getPath().contains("smali")) {

                            String[] split1 = split[1].split("\\\\");
                            String smaliName = split1[1];
                            String endPath = "";
                            for (int i = 2; i < split1.length; i++) {
                                endPath += split1[i];
                                if (i < split1.length - 1) {
                                    endPath += "\\";
                                }
                            }
                            File outFile = null;
                            boolean isFind = false;

                            for (QuickReplaceSmaliModel replaceSmaliModel : allQuickSmaliList) {
                                if (replaceSmaliModel.getQuick().equals(quick) && path.getPath().contains(replaceSmaliModel.getEndPath())) {
                                    isFind = true;
                                    outFile = new File(replaceSmaliModel.getPath() + endPath);
                                }
                            }

                            if (!isFind) {
                                String outSmaliName = "";
                                if (smaliName.equals("smali")) {
                                    String[] split2 = currentQuickInfo.getApplication().split("\\.");
                                    if(path.getPath().contains(split2[split2.length-1])){
                                        outSmaliName = "smali";
                                    }else {
                                        outSmaliName = outSmaliDirCount <= 0 ? "smali" : "smali_classes" + (outSmaliDirCount + 1);
                                    }
                                } else {
                                    int smali_classesCount = Integer.valueOf(smaliName.replace("smali_classes", ""));
                                    outSmaliName = "smali_classes" + (outSmaliDirCount + smali_classesCount);
                                }
                                outFile = new File(appDir, outSmaliName + "\\" + endPath);
                            }

                            FileUtils.mkdirParentFile(outFile);

                            try {
                                BrutIO.copyAndClose(new FileInputStream(path), new FileOutputStream(outFile));
                            } catch (Exception e) {
                                System.out.println("copy 出错: Exception " + e);
                            }

                        } else if (path.getPath().contains("values")) {
                            FileUtils.mkdirParentFile(outFilePath);

                            if (!outFilePath.exists()) {
                                try {
                                    BrutIO.copyAndClose(new FileInputStream(path), new FileOutputStream(outFilePath));
                                } catch (Exception e) {
                                    System.out.println("copy 出错: Exception " + e);
                                }
                            } else {
                                if (path.getPath().endsWith("public.xml")) {
                                    Dom4jUtil.mergePublicXml(outFilePath.getPath(), path.getPath(), otherQuickResList);
                                } else {

                                    try {
                                        XmlUtils.mergingXml(outFilePath.getPath(), path.getPath(),otherQuickResList);
                                    } catch (Exception e) {
                                        System.out.println("copy values Exception " + e);
                                    }
                                }
                            }
                        } else {
                            FileUtils.mkdirParentFile(outFilePath);
                            try {
                                BrutIO.copyAndClose(new FileInputStream(path), new FileOutputStream(outFilePath));
                            } catch (Exception e) {
                                System.out.println("copy 出错: Exception " + e);
                            }
                        }
                    }
                }
            }

            /**
             * 重新设置R文件引用
             */
            File publicXml = new File(appDir, "res\\values\\public.xml");
            HashMap<String, String> allPublic = Dom4jUtil.getAllPublic(publicXml);
            List<File> outQuickFileList = FileUtils.getFileList(appDir, new ArrayList<>());
            for (File file : outQuickFileList) {
                if (file.getPath().endsWith(".smali")) {
                    if(file.getName().startsWith("R$")) {
                        FileUtils.resetRFile(file, allPublic);
                    }else {
                        FileUtils.resetRSmaliFile(file,allPublic);
                    }
                }
            }

            /**
             * 删除icon
             */
            if (currentQuickInfo.getRes().getIcon().size() > 0) {
                System.out.println("删除icon");
                List<String> copyIconName = new ArrayList<>();
                File file = new File(appDir, "res");
                for (File file1 : file.listFiles()) {

                    for (UpdateResModel resIconModel: currentQuickInfo.getReplaceIcon()) {
                        if (file1.getName().contains(resIconModel.getName())) {
                            for (File file2 : file1.listFiles()) {
                                if (file2.getName().substring(0, file2.getName().indexOf(".")).equals(resIconModel.getValue()) &&
                                    !file2.getPath().endsWith("xml")) {
                                    if(!copyIconName.contains(file2.getName()))
                                        copyIconName.add(file2.getName());
                                    file2.delete();
                                }
                            }
                        }
                    }
                }

                for (String s:copyIconName) {
                    System.out.println("需要复制的icon路径: "+s);
                }

                /**
                 * 放置目标icon
                 */

                for (UpdateResModel replaceIconModel : currentQuickInfo.getRes().getIcon()) {
                    System.out.println("放置icon");
                    String name = replaceIconModel.getName();
                    String value = replaceIconModel.getValue();
                    for (String iconName : copyIconName) {
                        String iconFileName ="";
                        for (UpdateResModel resModel: currentQuickInfo.getReplaceIcon()) {
                            if(iconName.contains(resModel.getValue())){
                                iconFileName = resModel.getName();
                            }
                        }

                        try {
                            BrutIO.copyAndClose(new FileInputStream(value), new FileOutputStream(appDir.getPath() + "/res/" + iconFileName + "-" + name + "/" + iconName));
                        } catch (Exception e) {
                            System.out.println("copy 出错: Exception " + e);
                        }
                    }
                }
            }else {
                System.out.println("未配置icon地址，无需处理");
            }

            //删除所有空文件下
            FileUtils.removeNullFile(appDir);

        } catch (Exception e) {
            System.out.println("updateSmailAndRes 报错啦：" + e);
        }

    }

    public QuickConfigModel getQuickModel() {
        return quickConfigModel;
    }

    public QuickInfoModel getCurrentQuickInfo() {
        return currentQuickInfo;
    }

    public void setCurrentQuickInfo(QuickInfoModel currentQuickInfo) {
        QuickConfig.currentQuickInfo = currentQuickInfo;
    }

    public boolean isAutoBuildQuick() {
        return autoBuildQuick;
    }

    public void setAutoBuildQuick(boolean autoBuildQuick) {
        QuickConfig.autoBuildQuick = autoBuildQuick;
    }
}
