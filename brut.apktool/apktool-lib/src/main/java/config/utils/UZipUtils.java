package config.utils;

import brut.androlib.Androlib;
import brut.androlib.options.BuildOptions;
import brut.util.BrutIO;
import config.QuickConfig;
import config.model.QuickInfoModel;
import config.model.ReplaceResModel;
import config.res.ResManager;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UZipUtils {

    /**
     * 解压文件到指定目录
     */
    public static void unZipFiles(File zipFile, String descDir, QuickInfoModel quickInfoModel) {
        System.out.println("start unzip zipName: " + zipFile + " ; targetDirName: " + descDir);
        try {

            ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));//解决中文文件夹乱码
            //    String name = zip.getName().substring(zip.getName().lastIndexOf('\\') + 1, zip.getName().lastIndexOf('.'));

            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }

            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                //     System.out.println("zipEntryName: "+zipEntryName);
                InputStream in = zip.getInputStream(entry);
                InputStream in2 = zip.getInputStream(entry);
                String outPath = (descDir + "/" + zipEntryName).replaceAll("\\*", "/");

                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                FileOutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }

                if(zipEntryName.endsWith(".jar")){
                    System.out.println("jar文件：" +zipEntryName);
                    String zipJarFilePath = zipEntryName.replace("jar", "zip");
                    System.out.println("jar文件修改：" +zipJarFilePath);
                    System.out.println("jar解压路径：" +descDir+"/"+zipJarFilePath);
                    File zipJarFile = new File(descDir+"/"+zipJarFilePath);
                    if(!zipJarFile.exists()) {
                        try {
                            BrutIO.copyAndClose(in2, new FileOutputStream(zipJarFile));
                        } catch (Exception e) {

                        }
                    }

                    unZipFiles(zipJarFile, (descDir + "/" + zipJarFilePath).replace("zip", ""),quickInfoModel);
                }

               String replacePath = zipEntryName;

                if(zipEntryName.endsWith(".class")){
                    replacePath = zipEntryName.replace(".class",".smali");
                }

                if(zipEntryName.startsWith("jni")){
                    replacePath = zipEntryName.replace("jni","lib");
                }

                System.out.println("添加渠道素材路径: "+replacePath);
                ResManager.getInstance().getResModelList().add(new ReplaceResModel.Builder()
                        .setQuick(quickInfoModel.getSdk())
                        .setApkFileDir(quickInfoModel.getApkpath().replace(".apk",""))
                        .setReplacePath(replacePath)
                        .build());
/*                if(zipEntryName.startsWith("res")&&zipEntryName.contains(".") &&!zipEntryName.contains("values")){
                    String[] split = zipEntryName.split("/");
                    String type;
                    if(split[1].contains("-")){
                        type = split[1].split("-")[0];
                    }else {
                        type = split[1];
                    }



                }else if(zipEntryName.endsWith(".so")){

                }else if(zipEntryName.endsWith(".jar")){
                    System.out.println("jar文件：" +zipEntryName);
                    String zipJarFilePath = zipEntryName.replace("jar", "zip");
                    System.out.println("jar文件修改：" +zipJarFilePath);
                    System.out.println("jar解压路径：" +descDir+"/"+zipJarFilePath);
                    File zipJarFile = new File(descDir+"/"+zipJarFilePath);
                    if(!zipJarFile.exists()) {
                        try {
                            BrutIO.copyAndClose(in2, new FileOutputStream(zipJarFile));
                            new Androlib(new BuildOptions()).decodeSourcesSmali(new File(descDir+"/xipusdkDemo-release2.apk"),new File(descDir),
                                "classes.dex",false,0);
                            //    unZipFiles(zipJarFile, (descDir + "/" + zipJarFilePath).replace("zip", ""), appDir);
                        } catch (Exception e) {

                        }
                    }else {
                        try {
                            BrutIO.copyAndClose(in2, new FileOutputStream(zipJarFile));
                            new Androlib(new BuildOptions()).decodeSourcesSmali(new File(descDir+"/xipusdkDemo-release2.apk"),new File(descDir),
                                "classes.dex",false,0);
                            //    unZipFiles(zipJarFile, (descDir + "/" + zipJarFilePath).replace("zip", ""), appDir);
                        } catch (Exception e) {

                        }
                    }
                }else if(zipEntryName.startsWith("com")){
                    System.out.println("com开头的：" +zipEntryName);

                }*/

/*
                if (zipEntryName.endsWith("jar")) {
                    System.out.println("jar结尾的： "+zipEntryName);
                    String zipJarFilePath = descDir+"/"+zipEntryName.replace("jar", "zip");
                    File zipJarFile = new File(zipJarFilePath);
                    try {
                        BrutIO.copyAndClose(new FileInputStream(zipEntryName), new FileOutputStream(zipJarFile));
                    } catch (Exception e) {

                    }
                    unZipFiles(zipJarFile, zipJarFilePath.replace("zip", ""), appDir);
                }else if(zipEntryName.startsWith("com.")){

                    String path = appDir + "/smali/" + zipEntryName;
                    *//**
                     * copy 资源
                     *//*
                    try {
                        BrutIO.copyAndClose(in, appDir.getDirectory().getFileOutput(path));
                    } catch (Exception e) {

                    }

                }else if(zipEntryName.startsWith("res")){

                        String path = appDir + "/" + zipEntryName;
                 //       System.out.println("out path: " + path);
                        *//**
                         * copy 资源
                         *//*
                        try {
                            BrutIO.copyAndClose(in, appDir.getDirectory().getFileOutput(path));
                        } catch (Exception e) {

                        }
                    }*/
                in.close();
                out.close();
            }
        } catch (Exception e) {


        }
        System.out.println("end unzip zipName: " + zipFile + " ; targetDirName: " + descDir);
    }
}
