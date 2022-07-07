package config.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import brut.directory.ExtFile;
import config.QuickConfig;
import config.model.QuickInfoModel;
import config.model.UpdateResModel;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Dom4jUtil {

    /* 输出xml文档
     * @param doc 要输出的文档
     * @param fileName 路径
     */
    public static void writeXML(Document doc, String fileName) {
        try {
            // 设置XML文档格式
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            // 设置XML编码方式,即是用指定的编码方式保存XML文档到字符串(String),这里也可以指定为GBK或是ISO8859-1
            outputFormat.setEncoding("UTF-8");
            //     outputFormat.setSuppressDeclaration(true); //是否生产xml头
            outputFormat.setIndent(true); //设置是否缩进
            outputFormat.setIndent("    "); //以四个空格方式实现缩进
            outputFormat.setNewlines(true); //设置是否换行
            outputFormat.setNewLineAfterDeclaration(false);

            XMLWriter writer = new XMLWriter(new FileWriter(fileName), outputFormat);
            writer.write(doc);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Document getDocument(String path) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(path);
    }

    public static List<Element> getElements(String path) throws DocumentException {

        return getDocument(path).getRootElement().elements();
    }

    public static List<Element> getManifestElements(String path) throws DocumentException {
        List<Element> elements = new ArrayList<>();
        try {
            for (Element element : getDocument(path).getRootElement().elements()) {
                elements.add(element);
                elements.addAll(element.elements());
            }
        } catch (Exception e) {

        }

        return elements;
    }

    public static String getOriginalPackageName(File appDir) {
        File oldAndroidManifest = new File(appDir, "AndroidManifest.xml");
        try {
            Document document = getDocument(oldAndroidManifest.getPath());
            Element rootElement = document.getRootElement();
            return rootElement.attributeValue("package");
        } catch (DocumentException e) {

        }
        return null;
    }


    /**
     * 获取两个xml的所有标签
     * 合并两个xml的标签(去除不必要标签)
     * 插入相同的type标签位置
     *
     * @param outPath
     * @param inPath
     */
    public static void mergePublicXml(String outPath, String inPath, List<String> filterName) {
        List<Element> list3 = new ArrayList<>();
        List<Element> merageList = new ArrayList<>();
        try {
            List<Element> list1 = getElements(outPath);
            List<Element> list2 = getElements(inPath);
            merageList.addAll(list1);
            //合并两个xml的标签(去除不必要标签)
            for (Element element : list2) {
                String name = element.attribute("name").getStringValue();
                boolean isFind = false;
                for (Element element2 : list1) {
                    String name2 = element2.attribute("name").getStringValue();
                    isFind = false;
                    if (name.equals(name2)) {
                        isFind = true;
                        break;
                    }
                }

                if (!isFind) {
                    //    String findName = element.attributeValue("name");
                    //   if(!resModel.contains(findName)){
                    list3.add(element);
                    //    }
                   /* if (!isContainName(findName)) {
                        list3.add(element);
                    }*/
                }
            }

            //插入相同的type标签位置
            int index = 0;
            for (int i = 0; i < list1.size(); i++) {
                Element element = list1.get(i);
                String type = element.attribute("type").getStringValue();
                Iterator iterator = list3.iterator();
                while (iterator.hasNext()) {
                    Element element2 = (Element) iterator.next();

                    if (element2.attribute("type") != null) {
                        String type2 = element2.attribute("type").getStringValue();
                        String findName = element2.attributeValue("name");
                        if (type.equals(type2)) {
                            merageList.add(i + index, element2);
                            //      System.out.println("插入位置：" + i + index + "  类型：" + type2 + "  名称：" + findName);
                            index++;
                            iterator.remove();

                        }
                    }

                }
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Document resources = createPublicDocument("resources", merageList, filterName);

        writeXML(resources, outPath);

    }

    /**
     * 创建xml 并且对资源重新赋值
     *
     * @param tagName
     * @param list
     * @return
     */
    public static Document createPublicDocument(String tagName, List<Element> list, List<String> filterName) {
        Document document = DocumentHelper.createDocument();
        Element tagNameElement = document.addElement(tagName);
        String temptType = "";
        String tempId = "7f000000";
        String elementId = "0x7f000000";
        for (Element element : list) {
         //   boolean isFilter = false;
            String name = element.attributeValue("name");
            if(filterName.contains(name)){
                continue;
            }  /*          for (String filter : filterName) {
                if (name.equals(filter)) {
                    isFilter = true;
                }
            }*/

            /*if (isFilter) {
                continue;
            }*/

            //获取type属性值
            String typeName = element.attribute("type").getStringValue();
            long idvalue;
            //上一个值和当前值不一样 那就是新的type类型
            if (!temptType.equals(typeName)) {
                tempId = "7f" + tempId.substring(2, 4) + "0000";
                idvalue = Integer.parseInt(tempId, 16);
                tempId = Long.toHexString(65536 + idvalue);
                elementId = "0x" + tempId;
            } else {
                idvalue = Integer.parseInt(tempId, 16);
                tempId = Long.toHexString(1 + idvalue);
                elementId = "0x" + tempId;
            }
            temptType = typeName;
            tagNameElement.addElement("public")
                .addAttribute("type", element.attributeValue("type"))
                .addAttribute("name", element.attributeValue("name"))
                .addAttribute("id", elementId);

        }
        return document;
    }

    /**
     * 是否包含该名字
     *
     * @param name
     * @return
     */
    public static boolean isContainName(String name) {
        String noMerageList[] = {"activity_main", "layout_item", "network_security_config", "ic_launcher_background", "$ic_launcher_foreground__0",
            "ic_launcher", "ic_launcher_round"};
        for (String noMegrage : noMerageList) {
            if (noMegrage.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void updateAndroidManifestXml(ExtFile appDir,String outPath) {

        try {
            String oldpackageName = null, newPackageName = null;
            Document document = getDocument(outPath);
            Element rootElement = document.getRootElement();
            oldpackageName = rootElement.attributeValue("package");
            System.out.println("旧包名：" + oldpackageName);
            Element application = rootElement.element("application");

            List<UpdateResModel> updateResIconModels = new ArrayList<>();

            if(application.attribute("icon") != null) {
                String icon = application.attributeValue("icon");
                updateResIconModels.add(new UpdateResModel(icon.split("/")[0].split("@")[1], icon.split("/")[1]));
            }

            if(application.attribute("roundIcon") != null) {
                String roundIcon = application.attributeValue("roundIcon");
                updateResIconModels.add(new UpdateResModel(roundIcon.split("/")[0].split("@")[1], roundIcon.split("/")[1]));
            }

            QuickInfoModel currentQuickInfo = QuickConfig.getInstance().getCurrentQuickInfo();
            currentQuickInfo.setReplaceIcon(updateResIconModels);

            List<UpdateResModel> currentManifest = currentQuickInfo.getAndroidManifest();

            for (UpdateResModel updateInfo : currentManifest) {
                if (updateInfo == null) {
                    continue;
                }
                String type = updateInfo.getType();
                String name = updateInfo.getName();
                String value = updateInfo.getValue();
                if (type.equals("manifest")) {
                    if (rootElement.attribute(name) != null) {
                        rootElement.attribute(name).setValue(value);
                    }
                } else if (type.equals("application")) {
                    if (application.attribute(name) != null) {
                        application.attribute(name).setValue(value);
                    }
                } else if (type.equals("meta-data")) {
                    for (Element metaDataElement : rootElement.elements()) {
                        if (metaDataElement.getName().equals("meta-data") && metaDataElement.attributeValue("name") != null
                            && metaDataElement.attributeValue("name").equals(name)) {
                            metaDataElement.attribute("value").setValue(value);
                        }
                    }

                    for (Element metaDataElement : application.elements()) {
                        if (metaDataElement.getName().equals("meta-data") && metaDataElement.attributeValue("name") != null
                            && metaDataElement.attributeValue("name").equals(name)) {
                            metaDataElement.attribute("value").setValue(value);
                        }
                    }
                }
            }

            newPackageName = currentQuickInfo.getPackageName();

            if (!TextUtils.isEmpty(newPackageName)) {
                System.out.println("新包名: " + newPackageName);
            } else {
                System.out.println("未设置新包名沿用旧包");
                newPackageName = QuickConfig.getInstance().getQuickModel().getOldPackageName();
            }
            System.out.println("设置包名");
            rootElement.attribute("package").setValue(newPackageName);

            for (Element element : rootElement.elements()) {
                if (element.getName().equals("uses-permission") ||
                    element.getName().equals("permission")
                        && element.attributeValue("authorities") != null) {
                    String authorities = element.attributeValue("name");
                    if (authorities.contains(oldpackageName) && !TextUtils.isEmpty(newPackageName)) {
                        element.attribute("name").setValue(authorities.replace(oldpackageName, newPackageName));
                    }
                }
            }

            for (Element element : application.elements()) {
                if (element.getName().equals("provider") && element.attributeValue("authorities") != null) {
                    String authorities = element.attributeValue("authorities");
                    if (authorities.contains(oldpackageName) && !TextUtils.isEmpty(newPackageName)) {
                        element.attribute("authorities").setValue(authorities.replace(oldpackageName, newPackageName));
                    }
                }
            }

            /**
             * 判断application是否是渠道命名
             */
            System.out.println("修改application");
            boolean isExists = false;
            String applicationName = application.attributeValue("name");
            for (QuickInfoModel infoModel:QuickConfig.getInstance().getQuickModel().getQuick().getInfo()){
                if(applicationName.equals(infoModel.getApplication())){
                    isExists = true;
                }
            }

            if(isExists){
                application.attribute("name").setValue(currentQuickInfo.getApplication());
            }else {
                FileUtils.resetExtendsApplication(appDir,applicationName);
            }

            writeXML(document, outPath);

        } catch (Exception e) {
            System.out.println("updateAndroidManifestXml 异常: " + e);
        }

    }


    /**
     * 合并配置清单
     *
     * @param outPath
     * @param quick
     */
    public static void mergeAndroidManifestXml(String outPath, String quick) {

        HashMap<String, List<Element>> nodeMaps = new HashMap<>();

        try {
            List<Element> outElements = new ArrayList<>();

            List<String> removeName = new ArrayList<>();

            List<Element> tagElements = getManifestElements(outPath);

            Element currentQuickRootElement = null;

            /**
             * 读取所有配置渠道清单信息
             */
            for (QuickInfoModel quickInfoModel : QuickConfig.getInstance().getQuickModel().getQuick().getInfo()) {
                String quickPath = quickInfoModel.getApkpath().replace(".apk", "") + "/AndroidManifest.xml";
                List<Element> quickElements = getManifestElements(quickPath);
                if (quickInfoModel.getSdk().equals(quick)) {
                    currentQuickRootElement = getDocument(quickPath).getRootElement();
                }

                //删除渠道内的主入口
                Iterator<Element> quickIterator = quickElements.iterator();
                while (quickIterator.hasNext()) {
                    Element element = quickIterator.next();
                    String elementName = element.attribute("name") != null ?
                        element.attribute("name").getStringValue() : element.getName();
                    if (elementName.contains("MainActivity")) {
                        quickIterator.remove();
                    }
                }

                nodeMaps.put(quickInfoModel.getSdk(), quickElements);
            }

            Iterator<String> iterator = nodeMaps.keySet().iterator();
            /**
             * 匹配非目标渠道需要删除的
             */
            while (iterator.hasNext()) {
                String next = iterator.next();

                List<Element> oneQuickList = nodeMaps.get(next);
                for (Element element : oneQuickList) {
                    String elementName = element.attribute("name") != null ?
                        element.attribute("name").getStringValue() : element.getName();

                    Iterator<Element> tagIterator = tagElements.iterator();
                    while (tagIterator.hasNext()) {
                        Element tagElement = tagIterator.next();
                        String tagElementName = tagElement.attribute("name") != null ?
                            tagElement.attribute("name").getStringValue() : tagElement.getName();
                        if (elementName.equals(tagElementName)) {
                            tagIterator.remove();
                        }
                    }
                }
            }

            outElements.addAll(tagElements);
            outElements.addAll(nodeMaps.get(quick));

            Document manifestDocument = createManifestDocument(outPath, outElements, removeName, currentQuickRootElement);
            writeXML(manifestDocument, outPath);
        } catch (
            Exception e) {
            System.out.println("mergeAndroidManifestXml Exception: " + e);
        }

    }

    private static Document createManifestDocument(String outPath, List<Element> outElement,
                                                   List<String> removeName, Element currentQuickElement) {
        Document document = DocumentHelper.createDocument();
        Element tagNameElement = document.addElement("manifest");

        try {
            Element rootElement = getDocument(outPath).getRootElement();
            String outPackageName = rootElement.attribute("package").getStringValue();
            List<Attribute> attributes = rootElement.attributes();
            tagNameElement.setAttributes(attributes);

            for (Element element : outElement) {
                String elementName = element.attribute("name") != null ?
                    element.attribute("name").getStringValue() : element.getName();
                boolean isRemove = false;

                for (String remove : removeName) {
                    if (elementName.equals(remove)) {
                        isRemove = true;
                    }
                }
                if (!isRemove) {
                    Element element1;
                    if (element.getParent().getName().equals("queries")) {
                        try {
                            element1 = tagNameElement.element("queries").addElement(element.getName());
                            addChildElement(element, element1);
                        } catch (NullPointerException e) {
                            tagNameElement.addElement("queries");
                            element1 = tagNameElement.element("queries").addElement(element.getName());
                            addChildElement(element, element1);
                        }
                    } else if (element.getParent().getName().equals("application")) {
                        try {
                            element1 = tagNameElement.element("application").addElement(element.getName());
                            addChildElement(element, element1);
                        } catch (NullPointerException e) {
                            Element element1Element = tagNameElement.addElement("application");
                            Element application = getDocument(outPath).getRootElement().element("application");
                            element1Element.setAttributes(currentQuickElement.element("application").attributes());

                            if (element1Element.attributeValue("name") != null &&
                                application.attributeValue("name") != null) {
                                element1Element.attribute("name").setValue(application.attributeValue("name"));
                            }

                            if (element1Element.attributeValue("label") != null &&
                                application.attributeValue("label") != null) {
                                element1Element.attribute("label").setValue(application.attributeValue("label"));
                            }

                            if (element1Element.attributeValue("icon") != null &&
                                application.attributeValue("icon") != null) {
                                element1Element.attribute("icon").setValue(application.attributeValue("icon"));
                            }

                            element1 = tagNameElement.element("application").addElement(element.getName());
                            addChildElement(element, element1);
                        }
                    } else {
                        if (element.getName().equals("queries") || element.getName().equals("application")) {
                            continue;
                        }
                        element1 = tagNameElement.addElement(element.getName());
                    }

                    element1.setAttributes(element.attributes());
                    if (element1.getName().equals("provider") && element1.attributeValue("authorities") != null) {
                        element1.attribute("authorities").setValue(element1.attributeValue("authorities")
                            .replace(currentQuickElement.attribute("package").getStringValue(), outPackageName));
                    } else if (element1.getName().equals("uses-permission") ||
                        element1.getName().equals("permission")
                            && element1.attributeValue("name") != null) {
                        element1.attribute("name").setValue(element1.attributeValue("name")
                            .replace(currentQuickElement.attribute("package").getStringValue(), outPackageName));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("createManifestDocument 异常: " + e);
        }

        return document;
    }

    public static Element addChildElement(Element inElement, Element outElement) {
        if (inElement.elements().size() > 0) {
            for (Element element : inElement.elements()) {
                Element element2 = outElement.addElement(element.getName());
                element2.setAttributes(element.attributes());
                addChildElement(element, element2);
            }
        }
        return outElement;
    }

    public static HashMap<String, String> getAllPublic(File file) {
        HashMap<String, String> publicMap = new HashMap<>();
        try {
            List<Element> elements = getDocument(file.getPath()).getRootElement().elements();
            for (Element element : elements) {
                publicMap.put(element.attributeValue("name"), element.attributeValue("id"));
            }
        } catch (Exception e) {

        }
        return publicMap;
    }

}
