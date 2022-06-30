package config.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<File> fileList = new ArrayList();
    public static List<File> getFileList(File dir) {
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i]); // 获取文件绝对路径
                } else  { // 判断文件名
                    String strFileName = files[i].getAbsolutePath();
                    fileList.add(files[i]);
                }
            }

        }
        return fileList;
    }


    public static void mkdirParentFile(File file){
        File parentFile = file.getParentFile();
        if(parentFile != null && !parentFile.exists()){
            parentFile.mkdirs();
            mkdirParentFile(parentFile);
        }
    }

    public static int smailCount(File appDir){
        int outSmaliDirCount = 0;

        File[] files = appDir.listFiles();
        for (File file : files) {
            if (file.getName().startsWith("smali")) {
                outSmaliDirCount += 1;
            }
        }
        return outSmaliDirCount;
    }



    public static List<File> list = new ArrayList();
    public static List getAllNullDirectorys(File root) {
        File[] dirs = root.listFiles();
        if (dirs != null) {
            for (int i = 0; i < dirs.length; i++) {
                if (dirs[i].isDirectory()) {
                    list.add(dirs[i]);
                }
                getAllNullDirectorys(dirs[i]);
            }
        }
        return list;
    }
    //删除操作
    public static void removeNullFile(File root) {
        for (int i = 0; i < 10; i++) {
            list.clear();
            List<File> list = getAllNullDirectorys(root);

            for (int j = 0; j < list.size(); j++) {
                File temp = list.get(j);
                if (temp.isDirectory() && temp.listFiles().length <= 0) {
                    temp.delete();
                }
            }
        }
    }


    public static void copyFile(File in,File out){
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(in).getChannel();
            outputChannel = new FileOutputStream(out).getChannel();
            outputChannel.transferFrom(inputChannel,0,inputChannel.size());
        }catch (Exception e){

        }finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
