package config.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import config.QuickConfig;
import config.model.QuickInfoModel;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;

public class MergeDom4jUtil {

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
        }catch (Exception e){

        }

        return elements;
    }


    /**
     * 获取两个xml的所有标签
     * 合并两个xml的标签(去除不必要标签)
     * 插入相同的type标签位置
     *
     * @param outPath
     * @param inPath
     */
    public static void mergePublicXml(String outPath, String inPath) {
        List<Element> list3 = new ArrayList<>();
        List<Element> merageList = new ArrayList<>();
        List<String> filterName = new ArrayList<>();
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
                    String findName = element.attributeValue("name");
                  //  if (!isContainName(findName)) {
                        list3.add(element);
                 //   }
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
/*             boolean isFilter = false;
            String name = element.attribute("name").getStringValue();
           for (String filter:filterName) {
                if(name.equals(filter)){
                    isFilter = true;
                }
            }

            if(isFilter){
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

            /**
             * 读取所有配置渠道清单信息
             */
            for (QuickInfoModel quickInfoModel : QuickConfig.getInstance().getQuickModel().getQuick().getInfo()) {
                String quickPath = quickInfoModel.getApkpath().replace(".apk", "") + "/AndroidManifest.xml";
                List<Element> quickElements = getManifestElements(quickPath);
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
                    System.out.println("渠道：" + next + " ； elementName: " + elementName);

                    Iterator<Element> tagIterator = tagElements.iterator();
                    while (tagIterator.hasNext()) {
                        Element tagElement = tagIterator.next();
                        String tagElementName = tagElement.attribute("name") != null ?
                            tagElement.attribute("name").getStringValue() : tagElement.getName();
                        if (elementName.equals(tagElementName)) {
                            System.out.println("不同渠道：" + next + " ； 相同remove elementName: " + elementName);
                            tagIterator.remove();
                        }
                    }
                }
            }

            outElements.addAll(tagElements);
            outElements.addAll(nodeMaps.get(quick));

            Document manifestDocument = createManifestDocument(outPath, outElements, removeName);
            writeXML(manifestDocument, outPath);
        } catch (
            Exception e) {
            System.out.println("mergeAndroidManifestXml Exception: " + e);
        }

    }

    private static Document createManifestDocument(String outPath, List<Element> outElement, List<String> removeName) {
        Document document = DocumentHelper.createDocument();
        Element tagNameElement = document.addElement("manifest");

        try {
            List<Attribute> attributes = getDocument(outPath).getRootElement().attributes();
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
                          //  element1 = tagNameElement.element("queries").addElement(element.getName());
                             element1 = tagNameElement.element("queries").addElement(element.getName());
                          //  element1 = addChildElement(tagNameElement,"queries",element);
                            addChildElement(element,element1);
                        } catch (NullPointerException e) {
                            tagNameElement.addElement("queries");
                            //    element1 = tagNameElement.element("queries").addElement(element.getName());
                        //    element1 = addChildElement(tagNameElement,"queries",element);
                            element1 = tagNameElement.element("queries").addElement(element.getName());
                            addChildElement(element,element1);
                        }
                    } else if (element.getParent().getName().equals("application")) {
                        try {
                       //     element1 = addChildElement(tagNameElement,"application",element);
                            element1 = tagNameElement.element("application").addElement(element.getName());
                            addChildElement(element,element1);
                        } catch (NullPointerException e) {
                            Element element1Element = tagNameElement.addElement("application");
                            element1Element.setAttributes(getDocument(outPath).getRootElement().element("application").attributes());
                        //    element1 = addChildElement(tagNameElement,"application",element);
                            element1 = tagNameElement.element("application").addElement(element.getName());
                            addChildElement(element,element1);
                        }
                    }else {
                        if (element.getName().equals("queries") || element.getName().equals("application")) {
                            continue;
                        }
                        element1 = tagNameElement.addElement(element.getName());
                    }
                    element1.setAttributes(element.attributes());
                }
            }

        } catch (Exception e) {

        }

        return document;
    }

    public static Element addChildElement(Element inElement,Element outElement){

        if(inElement.elements().size()>0){
            for (Element element:inElement.elements()) {
                Element element2 = outElement.addElement(element.getName());
                element2.setAttributes(element.attributes());
                addChildElement(element,element2);
            }
        }
        return outElement;
    }



/*    public static Element addChildElement(Element tagElement ,String name,Element element){
        Element element1 = tagElement.element(name).addElement(element.getName());
        if(element.elements().size()>0){
            for (Element element2:element.elements()) {
                Element element3 = element1.addElement(element2.getName());
                element3.setAttributes(element2.attributes());
                if(element2.elements().size()>0){
                    for (Element element4:element2.elements()) {
                        Element element5 = element3.addElement(element4.getName());
                        element5.setAttributes(element4.attributes());
                    }
                }

            }
        }
        return element1;
    }*/


}
