package config.utils;

import config.QuickConfig;
import config.model.QuickInfoModel;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    public static List<File> getFileList(File dir,List<File> fileList) {

        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i],fileList); // 获取文件绝对路径
                } else  {
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

    public static void deleteAllFile(File file){
        for (File file1:file.listFiles()) {
            if(file1.isDirectory()){
                deleteAllFile(file1);
            }else {
                file1.delete();
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

    public static void resetRFile(File file, HashMap<String, String> allPublic){
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String str = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                Iterator<String> iterator = allPublic.keySet().iterator();
                while (iterator.hasNext()){
                    String next = iterator.next();
                    if(line.contains(next) && line.contains("=")){
                        line = line.split("=")[0]+" = "+allPublic.get(next);
                    }
                }
                line+="\n";
                str+=line;
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            bufferedWriter.write(str);

        }catch (Exception e){

        }finally {
           try {
               bufferedReader.close();
               bufferedWriter.close();
           }catch (Exception e){

           }
        }
    }

    public static void resetRSmaliFile(File file, HashMap<String, String> allPublic){
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String str = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                if(line.contains("0x7f")) {
                    Iterator<String> oldIterator = QuickConfig.getInstance().getQuickModel().getOriginalPublic().keySet().iterator();
                    while (oldIterator.hasNext()) {
                        String next = oldIterator.next();
                        String ID = QuickConfig.getInstance().getQuickModel().getOriginalPublic().get(next);
                        if (line.contains(ID)) {
                            Iterator<String> resetIterator = allPublic.keySet().iterator();
                            while (resetIterator.hasNext()) {
                                String next2 = resetIterator.next();
                                if (next.equals(next2)) {
                                    line = line.replace(ID, allPublic.get(next2));
                                }
                            }
                        }
                    }
                }
                line+="\n";
                str+=line;
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            bufferedWriter.write(str);

        }catch (Exception e){

        }finally {
            try {
                bufferedReader.close();
                bufferedWriter.close();
            }catch (Exception e){

            }
        }
    }

    public static void resetExtendsApplication(File file,String application){
        String applicationPath = application.replaceAll("\\.","/");
        File applicationSmaliFile = new File(file,"smali\\"+applicationPath+".smali");
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(applicationSmaliFile),"UTF-8"));
            String str = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null){

                for (QuickInfoModel infoModel:QuickConfig.getInstance().getQuickModel().getQuick().getInfo()){
                    String quickApplication = infoModel.getApplication().replaceAll("\\.","/");
                    if(line.contains(quickApplication)){
                        String currentQuickApplication = QuickConfig.getInstance().getCurrentQuickInfo().getApplication().replaceAll("\\.", "/");
                        line = line.replace(quickApplication,currentQuickApplication);
                    }
                }
                line+="\n";
                str+=line;
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(applicationSmaliFile),"UTF-8"));
            bufferedWriter.write(str);

        }catch (Exception e){
        }finally {
            try {
                bufferedReader.close();
                bufferedWriter.close();
            }catch (Exception e){

            }
        }
    }

}
