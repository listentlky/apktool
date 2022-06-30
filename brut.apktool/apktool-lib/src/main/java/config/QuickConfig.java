package config;

import brut.androlib.meta.MetaInfo;
import brut.androlib.res.data.ResResource;
import brut.directory.ExtFile;
import brut.util.BrutIO;
import com.alibaba.fastjson.JSON;
import config.model.*;
import config.res.ResManager;
import config.utils.FileUtils;
import config.utils.MergeDom4jUtil;
import config.utils.TextUtils;
import config.utils.XmlUtils;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
     * @param replaceApkDir
     * @param quick
     */
    public void updateQuickConfigInfo(ExtFile appDir, MetaInfo metaInfo, File replaceApkDir, String quick) {
        // 更新版本号 yml文件
        updateMeta(metaInfo);
        // 更新合并渠道配置清单
        updateAndroidManifest(appDir, quick);
        // 更新合并代码和资源文件
        //  updateSmaliAndRes(appDir, replaceApkDir, quick);

        updateSmailAndRes2(appDir, quick);

    }


    /**
     * 更新配置清单
     *
     * @param appDir
     */
    private void updateAndroidManifest(ExtFile appDir, String quick) {
        if (quickConfigModel == null) {
            return;
        }
        File updateAndroidManifest = new File(appDir, "AndroidManifest.xml");

        System.out.println("updateAndroidManifest.getName(): " + updateAndroidManifest.getPath() + " ; quick: " + quick);

        MergeDom4jUtil.mergeAndroidManifestXml(updateAndroidManifest.getPath(), quick);

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(updateAndroidManifest);
            Element rootElement = document.getDocumentElement();

      /*      File oldAndroidManifest = new File(appDir, "old_AndroidManifest.xml"); // 保留原始xml
            if (!oldAndroidManifest.exists()) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(new DOMSource(document), new StreamResult(oldAndroidManifest));
            }*/

            //读取原包包名
            String oldPackage = rootElement.getAttributes().getNamedItem("package").getNodeValue();
            System.out.println("oldPackage: " + oldPackage);

            if (currentQuickInfo != null) {
                //读取icon
                String icon = rootElement.getElementsByTagName("application").item(0).getAttributes().getNamedItem("android:icon").getNodeValue();
                currentQuickInfo.setReplaceIconType(icon.split("/")[0].split("@")[1]);
                currentQuickInfo.setReplaceIconName(icon.split("/")[1]);
                System.out.println("currentQuickInfo ReplaceIconType: " + currentQuickInfo.getReplaceIconType());
                System.out.println("currentQuickInfo ReplaceIconName: " + currentQuickInfo.getReplaceIconName());
            }

            String newPackage = null;

            List<String> currentManifest = new ArrayList<>();

            for (QuickInfoModel quickInfoModel : quickConfigModel.getQuick().getInfo()) {
                if (quick.equals(quickInfoModel.getSdk())) {
                    currentManifest.addAll(quickInfoModel.getAndroidManifest());
                }
            }

            for (String updateInfo : currentManifest) {
                if (updateInfo.contains("package")) {
                    newPackage = updateInfo.split("=")[2];
                }
            }
            System.out.println("newPackage: " + newPackage);
            QuickConfig.getInstance().getCurrentQuickInfo().setPackageName(newPackage);

            for (String updateInfo : currentManifest) {
                if (TextUtils.isEmpty(updateInfo)) {
                    continue;
                }
                if (!updateInfo.contains("=")) {
                    System.out.println("update AndroidManifest error: 配置无效");
                    continue;
                }
                String[] split = updateInfo.split("=");
                NodeList elementsByTagName = document.getElementsByTagName(split[0]);
                for (int i = 0; i < elementsByTagName.getLength(); i++) {
                    Node item = elementsByTagName.item(i);

                    NamedNodeMap attributes = item.getAttributes(); //获取二级标签
                    if (item.getNodeName().equals("meta-data")) {
                        if (attributes.getNamedItem("android:name").getNodeValue().equals(split[1])) {
                            attributes.getNamedItem("android:value").setNodeValue(TextUtils.isEmpty(split[2]) ? "" : split[2]);
                        }
                    } else {
                        for (int j = 0; j < attributes.getLength(); j++) { //遍历二级标签下的所有元素
                            Node attributesItem = attributes.item(j);

                            if (attributesItem.getNodeName().equals(split[1])) {
                                String nodeValue = attributesItem.getNodeValue();
                                if (nodeValue.contains(oldPackage)) {
                                    attributesItem.setNodeValue(nodeValue.replaceAll(oldPackage, newPackage));
                                } else {
                                    attributesItem.setNodeValue(TextUtils.isEmpty(split[2]) ? nodeValue : split[2]);
                                }
                            }
                        }
                    }
                }
            }

            //更新内容
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(updateAndroidManifest));
        } catch (Exception e) {

        }
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
        ApktoolModel apktool = currentQuickInfo.getApktool();
        if (!TextUtils.isEmpty(apktool.getVersionCode())) {
            meta.versionInfo.versionCode = apktool.getVersionCode();
        }
        if (!TextUtils.isEmpty(apktool.getVersionName())) {
            meta.versionInfo.versionName = apktool.getVersionName();
        }
        if (!TextUtils.isEmpty(apktool.getMinSdkVersion())) {
            meta.sdkInfo.put("minSdkVersion", apktool.getMinSdkVersion());
        }
        if (!TextUtils.isEmpty(apktool.getTargetSdkVersion())) {
            meta.sdkInfo.put("targetSdkVersion", apktool.getTargetSdkVersion());
        }
    }

    public static void main(String args[]) {
     //   updateSmailAndRes2(new ExtFile("E:/devcopy/xipusdkDemo-release"), "xipu");

        File dirs = new File("E:\\devcopy\\hcdemo_vivo-release\\res\\values-watch-v20\\dimens.xml");

        //method call to create directories
        dirs.getParentFile().mkdirs();

    }


    public static void updateSmailAndRes2(ExtFile appDir, String quick) {
        try {

            /**
             * 非目标渠道先删
             */
            for (QuickInfoModel quickInfoModel : QuickConfig.getInstance().getQuickModel().getQuick().getInfo()) {
                if (!quickInfoModel.getSdk().equals(quick)) {// 非目标渠道删除
                    File quickFile = new File(quickInfoModel.getApkpath().replace(".apk", ""));
                    FileUtils.fileList.clear();
                    List<File> allQuickFileList = FileUtils.getFileList(quickFile);
                    int outSmaliDirCount = FileUtils.smailCount(appDir);
                    for (File path : allQuickFileList) {

                        if (path.getPath().contains("AndroidManifest.xml") || path.getPath().contains("apktool.yml")) {
                            continue;
                        }


                        //       System.out.println("获取所有文件: " + path);

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
                            //       System.out.println("endPath: " + endPath);
                            for (int i = 0; i < outSmaliDirCount; i++) { // 目标生成的smali 个数
                                smaliPath = i == 0 ? appDir + "\\smali\\" + endPath : appDir + "\\smali_classes" + (i + 1) + "\\" + endPath;

                                File file = new File(smaliPath);
                                //     System.out.println("不同渠道遍历smail目录path是否存在: " + file.exists());
                                if (file.exists()) {
                                    File parentFile = file.getParentFile();
                                    for (File file1 : parentFile.listFiles()) {
                                        file1.delete();
                                    }
                                }
                            }

                        }
                    }
                }
            }

            //删除所有空文件下
            FileUtils.removeNullFile(appDir);

            /**
             * 目标渠道添加
             */
            for (QuickInfoModel quickInfoModel : QuickConfig.getInstance().getQuickModel().getQuick().getInfo()) {
                if (quickInfoModel.getSdk().equals(quick)) {
                    File quickFile = new File(quickInfoModel.getApkpath().replace(".apk", ""));
                    FileUtils.fileList.clear();
                    List<File> allQuickFileList = FileUtils.getFileList(quickFile);
                    int outSmaliDirCount = FileUtils.smailCount(appDir);
                    for (File path : allQuickFileList) {

                        if (path.getPath().contains("AndroidManifest.xml") || path.getPath().contains("apktool.yml")) {
                            continue;
                        }

                        //       System.out.println("获取所有文件: " + path);

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
                            String outSmaliName = "";


                            if (smaliName.equals("smali")) {
                                outSmaliName = outSmaliDirCount <= 0 ? "smali" : "smali_classes" + (outSmaliDirCount + 1);
                            } else {
                                int smali_classesCount = Integer.valueOf(smaliName.replace("smali_classes", ""));
                                outSmaliName = "smali_classes" + (outSmaliDirCount + smali_classesCount);
                            }

                            File outFile = new File(appDir, outSmaliName + "\\" + endPath);

                            //       System.out.println("copy smali  in: "+path+" ; out: " + outFile.getPath());

                            FileUtils.mkdirParentFile(outFile);

                            try {
                                BrutIO.copyAndClose(new FileInputStream(path), new FileOutputStream(outFile));
                            }catch (Exception e) {
                                System.out.println("copy 出错: Exception " + e);
                            }

                        }else if (path.getPath().contains("values")) {

                            FileUtils.mkdirParentFile(outFilePath);

                            if(!outFilePath.exists()){
                                try {
                                    BrutIO.copyAndClose(new FileInputStream(path), new FileOutputStream(outFilePath));
                                }catch (Exception e) {
                                    System.out.println("copy 出错: Exception " + e);
                                }
                            }else {
                                if (path.getPath().endsWith("public.xml")) {
                                    MergeDom4jUtil.mergePublicXml(outFilePath.getPath(), path.getPath());
                                } else {

                                    try {
                                        XmlUtils.mergingXml(outFilePath.getPath(), path.getPath());
                                    } catch (Exception e) {
                                        System.out.println("copy values Exception " + e);
                                    }
                                }
                            }
                        } else {
                            FileUtils.mkdirParentFile(outFilePath);
                            try {
                                BrutIO.copyAndClose(new FileInputStream(path), new FileOutputStream(outFilePath));
                            }catch (Exception e) {
                                System.out.println("copy 出错: Exception " + e);
                            }
                        }
                    }
                }
            }

            /**
             * 删除icon
             */
            if (QuickConfig.getInstance().getCurrentQuickInfo().getRes().getIcon().size() > 0) {
                List<String> copyIconName = new ArrayList<>();
                File file = new File(appDir, "res");
                for (File file1 : file.listFiles()) {

                    if (file1.getName().contains(QuickConfig.getInstance().getCurrentQuickInfo().getReplaceIconType())) {
                        for (File file2 : file1.listFiles()) {
                            if (file2.getName().contains(QuickConfig.getInstance().getCurrentQuickInfo().getReplaceIconName()) &&
                                !file2.getPath().endsWith("xml")) {
                                copyIconName.add(file2.getName());
                                file2.delete();
                            }
                        }
                    }
                }

                /**
                 * 放置目标icon
                 */

                for (String replaceIcon : QuickConfig.getInstance().getCurrentQuickInfo().getRes().getIcon()) {
                    String[] split = replaceIcon.split("=");
                    for (String iconName : copyIconName) {
                        try {
                            BrutIO.copyAndClose(new FileInputStream(split[1]), new FileOutputStream(appDir.getPath() + "/res/" + QuickConfig.getInstance().getCurrentQuickInfo().getReplaceIconType() + "-" + split[0] + "/" + iconName));
                        } catch (Exception e) {
                            System.out.println("copy 出错: Exception " + e);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("报错啦：" + e);
        }

    }


    private void updateSmaliAndRes(ExtFile appDir, File replaceApkDir, String quick) {

        List<ReplaceResModel> resModelList = ResManager.getInstance().getResModelList();
        System.out.println("updateSmali：" + resModelList.size() + " ; quick: " + quick + " ; " + appDir);

        int inSmaliDirCount = 0;
        int outSmaliDirCount = 0;

        File[] replaceApkFiles = replaceApkDir.listFiles();
        for (File file : replaceApkFiles) {
            if (file.getName().startsWith("smali")) {
                inSmaliDirCount += 1;
            }
        }

        System.out.println("inSmaliDirCount:" + inSmaliDirCount);

        File[] files = appDir.listFiles();
        for (File file : files) {
            if (file.getName().startsWith("smali")) {
                outSmaliDirCount += 1;
            }
        }

        System.out.println("outSmaliDirCount:" + outSmaliDirCount);

        for (ReplaceResModel replaceResModel : resModelList) {

            System.out.println("全部渠道素材路径：" + replaceResModel.getReplacePath());

            if (replaceResModel.getReplacePath().startsWith("libs")) {
                continue;
            }

            String replacePath = replaceResModel.getReplacePath();

            if (replaceResModel.getQuick().equals(quick)) {
                String inPath;
                String outPath;
                if (replacePath.startsWith("com")) {
                    System.out.println("copy Smali 开始 ");
                    int inSmali = 0;
                    int outSmali = 0;

                    // 存在相同代码，先删除
                    for (int i = 0; i < outSmaliDirCount; i++) { // 目标生成的smali 个数
                        outPath = i == 0 ? "smali/" + replacePath : "smali_classes" + (i + 1) + "/" + replacePath;  //个数为1 路径为 smali/com...  其他为smali_classes+(i+1)/com...
                        File file = new File(appDir, outPath);
                        if (file.exists()) { //存在同路径代码文件  删除
                            System.out.println("replacePath:存在 ");
                            file.delete();
                            outSmali = (i + 1); //输出smali路径为 smali文件
                        } else { //不存在输出smali路径为 smali个数+1 单独存放
                            System.out.println("replacePath:不存在 ");
                            outSmali = outSmaliDirCount + 1;
                        }
                    }

                    for (int i = 0; i < inSmaliDirCount; i++) {
                        inPath = i == 0 ? "smali/" + replacePath : "smali_classes" + (i + 1);//个数为1 路径为 smali/com...  其他为smali_classes+(i+1)/com...
                        //      System.out.println("inPath: " + inPath);
                        File file = new File(appDir, inPath + "/" + replaceResModel.getReplacePath());
                        if (file.exists()) {
                            System.out.println("replacePath:存在 ");
                            inSmali = (i + 1);
                        }
                    }

                    String inSmailPath = replaceResModel.getApkFileDir() + "/" + (inSmali <= 1 ? "smali/" + replacePath : "smali_classes" + inSmali + "/" + replacePath);

                    outSmali += inSmali; //outsmail 路径为 自身个数+ inSmali个数

                    String outSmailPath = outSmali <= 1 ? "smali/" + replacePath : "smali_classes" + outSmali + "/" + replacePath;

                    try {
                        BrutIO.copyAndClose(new FileInputStream(inSmailPath), appDir.getDirectory().getFileOutput(outSmailPath));
                    } catch (Exception e) {
                        System.out.println("copy 出错: Exception " + e);
                    }

                } else if (replacePath.startsWith("res/values")) {
                    /**
                     * 合并 values下的资源
                     */

                    File inResFile = new File(replaceResModel.getApkFileDir() + "/res");

                    for (File file : inResFile.listFiles()) {
                        if (file.getName().contains("values")) {
                            File inValueFile = new File(replaceResModel.getApkFileDir() + "/res/" + file.getName());
                            for (File valueFile : inValueFile.listFiles()) {
                                if (valueFile.getName().equals("public.xml")) {
                                    MergeDom4jUtil.mergePublicXml(appDir + "/res" + valueFile.getPath().split("res")[1], valueFile.getPath());
                                } else {
                                    if (valueFile.getPath().endsWith("xml")) {
                                        System.out.println("看看文件名： valueFile: " + valueFile + " ; name: " + valueFile.getName());

                                        /**
                                         * 合并xml
                                         */
                                        try {
                                            XmlUtils.mergingXml(appDir + "/res" + valueFile.getPath().split("res")[1], valueFile.getPath());
                                        } catch (Exception e) {
                                            System.out.println("copy values Exception " + e);
                                        }
                                    }
                                }
                            }
                        }

                    }

                } else {

                    if (replacePath.equals("AndroidManifest.xml") || replacePath.equals("R.txt")) {
                        continue;
                    }

                    File file = new File(replaceResModel.getApkFileDir() + "/" + replacePath);
                    if (!file.exists()) {
                        for (int i = 4; i < 50; i++) {
                            if (replacePath.contains("-v" + i)) {
                                replacePath = replacePath.replace("-v" + i, "");
                            }
                        }
                    }


                    System.out.println("copy素材： in: " + replaceResModel.getApkFileDir() + "/" + replacePath + "; out: " + appDir + "/" + replacePath);
                    try {
                        BrutIO.copyAndClose(new FileInputStream(replaceResModel.getApkFileDir() + "/" + replacePath), appDir.getDirectory().getFileOutput(replacePath));
                    } catch (Exception e) {
                        System.out.println("copy 出错: Exception " + e);
                    }
                }
            } else {  // 非目标渠道
                if (replacePath.startsWith("com")) {
                    System.out.println("非目标渠道: " + replacePath);
                    // 存在相同代码，删除
                    for (int i = 0; i < outSmaliDirCount; i++) { // 目标生成的smali 个数
                        String outPath = i == 0 ? "smali/" + replacePath : "smali_classes" + (i + 1) + "/" + replacePath;  //个数为1 路径为 smali/com...  其他为smali_classes+(i+1)/com...
                        File file = new File(appDir, outPath);

                        System.out.println("非目标渠道代码是否存在： " + file.getPath() + " ; " + file.exists());


                        System.out.println("非目标渠道代码上层目录是否存在： " + file.getParentFile() + " ; " + file.getParentFile().exists());

                        if (file.exists()) { //存在同路径代码文件  删除

                            File parentFile = file.getParentFile();
                            for (File file1 : parentFile.listFiles()) {
                                file1.delete();
                            }
                            if (parentFile.listFiles().length <= 0) {

                            }
                        }
                    }
                }
            }
        }

        //删除所有空文件下
        FileUtils.removeNullFile(appDir);

        /**
         * 删除icon
         */
        if (QuickConfig.getInstance().getCurrentQuickInfo().getRes().getIcon().size() > 0) {
            List<String> copyIconName = new ArrayList<>();
            File file = new File(appDir, "res");
            for (File file1 : file.listFiles()) {

                if (file1.getName().contains(QuickConfig.getInstance().getCurrentQuickInfo().getReplaceIconType())) {
                    for (File file2 : file1.listFiles()) {
                        if (file2.getName().contains(QuickConfig.getInstance().getCurrentQuickInfo().getReplaceIconName())) {
                            copyIconName.add(file2.getName());
                            file2.delete();
                        }
                    }
                }
            }

            /**
             * 放置目标icon
             */
            for (String replaceIcon : QuickConfig.getInstance().getCurrentQuickInfo().getRes().getIcon()) {
                String[] split = replaceIcon.split("=");
                for (String iconName : copyIconName) {
                    try {
                        BrutIO.copyAndClose(new FileInputStream(split[1]), new FileOutputStream(appDir.getPath() + "/res/" + QuickConfig.getInstance().getCurrentQuickInfo().getReplaceIconType() + "-" + split[0] + "/" + iconName));
                    } catch (Exception e) {
                        System.out.println("copy 出错: Exception " + e);
                    }
                }
            }
        }
    }


    /**
     * 更新values下资源
     *
     * @param res
     * @return
     */
    public String UpdateValue(ResResource res) {
      /*  if (updateConfigModel == null) {
            return "";
        }
        if (updateConfigModel.getRes() != null) {
            for (String value : updateConfigModel.getRes().getValue()) {
                String[] replaceSplit = value.split("=");

                if (res.getResSpec().getType().equals("string")) {

                    String[] valueSplit = res.getFilePath().split("/");

                    if (res.getResSpec().getName().equals("app_name")) { //如果是游戏名,不考虑多语言直接替换
                        return replaceSplit[2];
                    }

                    if (valueSplit[0].contains("-")) { //多语言判断前两项
                        String[] split1 = valueSplit[0].split("-");
                        if (replaceSplit[0].equals(split1[0] + "-" + split1[1])) {
                            if (replaceSplit[1].equals(res.getResSpec().getName())) {
                                return replaceSplit[2];
                            }
                        }
                    } else {
                        if (replaceSplit[0].equals(valueSplit[0])) {
                            if (replaceSplit[1].equals(res.getResSpec().getName())) {
                                return replaceSplit[2];
                            }
                        }
                    }
                }
            }
        }*/
        return "";
    }



/*    public void updateSmail(ExtFile appDir) {
        if (updateConfigModel == null) {
            return;
        }
        System.out.println("updateSmail...");
        for (String type : updateConfigModel.getQuick().getType()) {
            List<String> updatePath = new ArrayList<>();
            String[] split = type.split("=");
            System.out.println("updateSmail type: " + split[0]);
          *//*  ApkDecoder apkDecoder = new ApkDecoder();
            try {


                Set<String> files = new ExtFile(new File(split[1])).getDirectory().getFiles(true);



            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            new Androlib(new BuildOptions()).decodeSourcesSmali(split[1],new File(descDir),);*//*

            if (split[0].equals("xipusdk")) {
                File file = new File(split[1]);
                File[] files = file.listFiles();

                for (File file1 : files) {
                    System.out.println("file111111111111: "+file1);
                    String zipFilePath;
                    zipFilePath = file1.getPath();
                    if (!zipFilePath.endsWith("zip")) {
                        zipFilePath = file1.getPath().replace("aar", "zip");
                        File zipFile = new File(zipFilePath);
                        if (!zipFile.exists()) {
                            try {
                                BrutIO.copyAndClose(new FileInputStream(file1), new FileOutputStream(zipFile));
                            } catch (Exception e) {

                            }
                            updatePath.add(zipFilePath);
                        }
                    }else {
                        updatePath.add(zipFilePath);
                    }
                }

                for (String path:updatePath) {
                    System.out.println("zipFileName: " + path);
                    String unzipFileName = path.replace(".zip", "");
                 //  UZipUtils.unZipFiles(new File(path), unzipFileName,isCurrentOut);
                }
            }
        }*/




       /* try {
            BrutIO.copyAndClose(new FileInputStream(split[1]), outDir.getFileOutput(outFileName));
        }catch (Exception e){

        }*/

  /*  public void document1To2(File inFile,File outFile){

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inFile);
            Element rootElement = document.getDocumentElement();

            System.out.println("document1To2111: "+rootElement.getNodeName());


            Document document2 = documentBuilder.parse(outFile);
            Element rootElement2 = document2.getDocumentElement();

            if(rootElement.hasChildNodes()) {
                NodeList childNodes = rootElement.getChildNodes();

                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node item = childNodes.item(i);




                    if(item.hasChildNodes()){

                    }else {

                    }


                    document2.createElement()
                }
            }


            System.out.println("document1To2222: "+rootElement2.getNodeName());

        }catch (Exception e){

        }
    }*/


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
