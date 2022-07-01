package config.utils;

import config.QuickConfig;
import config.model.QuickInfoModel;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class XmlUtils {

    /**
     * 合并文件Manifest，同名覆盖
     *
     * @param outXmlFileName
     * @param quick
     */
    public static void mergingManifestXml(String outXmlFileName, String quick) {

        try {
            HashMap<String, HashMap<String, List<Node>>> hashMap = new HashMap<>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document docOut = db.parse(outXmlFileName);
            Element rootElement = docOut.getDocumentElement();
            /**
             * 读取所有配置渠道清单信息
             */
            //       for (QuickInfoModel quickInfoModel : QuickConfig.getInstance().getQuickModel().getQuick().getInfo()) {
            //           String path = quickInfoModel.getApkpath().replace(".apk", "") + "/AndroidManifest.xml";
            String path = "E:\\devcopy\\b.xml";
            System.out.println("mergingManifestXml path: " + path);
            Document doc1 = db.parse(path);
            Element quickElement = doc1.getDocumentElement();
            System.out.println("rootElement1: " + quickElement);

            HashMap<String, List<Node>> nodeMaps = new HashMap<>();
            List<Node> metaNodeLists = new ArrayList<>();
            List<Node> permissionNodeList = new ArrayList<>();
            List<Node> activityNodeList = new ArrayList<>();
            List<Node> serviceNodeList = new ArrayList<>();
            List<Node> providerNodeList = new ArrayList<>();
            List<Node> receiverNodeList = new ArrayList<>();

            NodeList childNodes2 = quickElement.getChildNodes();

            System.out.println("childNodes2: " + childNodes2.getLength());

            for (int x = 1; x < childNodes2.getLength(); x += 2) {

                Node nodeItem = childNodes2.item(x);
                switch (nodeItem.getNodeName()) {
                    case "meta-data":
                        metaNodeLists.add(nodeItem);
                        break;
                    case "uses-permission":
                    case "permission":
                        permissionNodeList.add(nodeItem);
                        break;
                    case "application":
                        if (nodeItem.hasChildNodes()) {
                            for (int y = 1; y < nodeItem.getChildNodes().getLength(); y += 2) {
                                Node childNodeItem = nodeItem.getChildNodes().item(y);
                                switch (childNodeItem.getNodeName()) {
                                    case "meta-data":
                                        metaNodeLists.add(childNodeItem);
                                        break;
                                    case "activity":
                                        activityNodeList.add(childNodeItem);
                                        break;
                                    case "service":
                                        serviceNodeList.add(childNodeItem);
                                        break;
                                    case "provider":
                                        providerNodeList.add(childNodeItem);
                                        break;
                                    case "receiver":
                                        receiverNodeList.add(childNodeItem);
                                        break;
                                }
                            }
                        }
                        break;
                }
            }
            nodeMaps.put("meta-data", metaNodeLists);
            nodeMaps.put("permission", permissionNodeList);
            nodeMaps.put("activity", activityNodeList);
            nodeMaps.put("provider", providerNodeList);
            nodeMaps.put("service", serviceNodeList);
            nodeMaps.put("receiver", receiverNodeList);
            //      hashMap.put(quickInfoModel.getSdk(), nodeMaps);
            hashMap.put("google", nodeMaps);
            //       }





            Iterator<String> iterator = hashMap.keySet().iterator();
            /**
             * 删除
             */
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (!next.equals(quick)) {
                    HashMap<String, List<Node>> outListHashMap = hashMap.get(next);

                    Iterator<String> iterator1 = outListHashMap.keySet().iterator();
                    while (iterator1.hasNext()) {
                        String next1 = iterator1.next();
                        List<Node> nodes = outListHashMap.get(next1);
                        for (Node node : nodes) {
                            isContains(rootElement, node, true);
                        }
                    }
                }
            }

            /**
             * 添加
             */
     /*       Iterator<String> iterator2 = hashMap.keySet().iterator();
            while (iterator2.hasNext()) {
                String next = iterator2.next();
                if (next.equals(quick)) {
                    HashMap<String, List<Node>> inListHashMap = hashMap.get(next);

                    Iterator<String> iterator1 = inListHashMap.keySet().iterator();
                    while (iterator1.hasNext()) {
                        String next1 = iterator1.next();
                        List<Node> nodes = inListHashMap.get(next1);
                        for (Node node : nodes) {
                            boolean contains = isContains(rootElement, node, false);
                            if (!contains) {
                                System.out.println("目标渠道追加: " + node.getAttributes().getNamedItem("android:name"));

                                Node importedNode = docOut.importNode(node, true);
                                Node application = rootElement.getElementsByTagName("application").item(0);
                                switch (next1) {
                                    case "meta-data":
                                        application.appendChild(importedNode);
                                        break;
                                    case "uses-permission":
                                    case "permission":
                                        rootElement.appendChild(importedNode);
                                        break;
                                    case "activity":
                                        application.appendChild(importedNode);
                                        break;
                                    case "service":
                                        application.appendChild(importedNode);
                                        break;
                                    case "provider":
                                        application.appendChild(importedNode);
                                        break;
                                    case "receiver":
                                        application.appendChild(importedNode);
                                        break;
                                }
                            }

                        }
                    }
                }
            }*/
            System.out.println("writeTo: " + outXmlFileName);
            writeTo(docOut, outXmlFileName);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    /**
     * 是否存在
     *
     * @param element
     * @param node
     * @return
     */
    public static boolean isContains(Element element, Node node, boolean isRemove) {
        boolean isContain = false;
        for (int x = 1; x < element.getChildNodes().getLength(); x += 2) {
            Node item = element.getChildNodes().item(x);

            if (item.getNodeName().equals("#text")) {
                continue;
            }

            System.out.println(element.getChildNodes().getLength()+" ； 外面看看是什么： " + item.getNodeName() + " ; "+node.getNodeName());
            if (item.getAttributes() != null) {
                System.out.println("外面看看是什么 android:name: "
                    + item.getAttributes().getNamedItem("android:name")
                    +" ; "+node.getAttributes().getNamedItem("android:name"));
            }

            if (item.getNodeName().equals("application")) {
                if (item.hasChildNodes()) {
                    for (int y = 1; y < item.getChildNodes().getLength(); y += 2) {
                        Node item1 = item.getChildNodes().item(y);
                        if (item1.getNodeName().equals("#text")) {
                            continue;
                        }

                        System.out.println(item.getChildNodes().getLength()+" ； 里面看看是什么： " + item1.getNodeName() + " ; "+node.getNodeName());
                        if (item1.getAttributes() != null && item1.getAttributes().getNamedItem("android:name") != null) {
                            System.out.println("里面看看是什么 android:name: "
                                + item1.getAttributes().getNamedItem("android:name").getNodeValue()
                            +" ; "+node.getAttributes().getNamedItem("android:name").getNodeValue());
                        }


                        if (item1.getAttributes() != null && node.getAttributes() != null &&
                            item1.getAttributes().getNamedItem("android:name") != null &&
                                node.getAttributes().getNamedItem("android:name") != null &&
                            item1.getAttributes().getNamedItem("android:name").getNodeValue().equals(node.getAttributes().getNamedItem("android:name").getNodeValue())) {
                            isContain = true;
                            if (isRemove) {
                                System.out.println("非目标渠道删除: " + item1.getAttributes().getNamedItem("android:name"));
                                item.removeChild(item1);
                            }
                            break;
                        }else {
                            System.out.println("里面不相同");
                        }
                    }
                }
            } else {

                if (item.getAttributes() != null && node.getAttributes() != null &&
                    item.getAttributes().getNamedItem("android:name") != null &&
                    node.getAttributes().getNamedItem("android:name") != null &&
                    item.getAttributes().getNamedItem("android:name").getNodeValue()
                        .equals(node.getAttributes().getNamedItem("android:name").getNodeValue())) {

                    isContain = true;
                    if (isRemove) {
                        System.out.println("非目标渠道删除: " + item.getAttributes().getNamedItem("android:name").getNodeValue());
                        element.removeChild(item);
                    }
                    break;
                }else {
                }
            }

        }
        return isContain;

    }


/*
            System.out.println("mergingXml: outXmlFileName: " + outXmlFileName + " ; inXmlFileName: " + inXmlFileName);




            // Copy Attributes

            NamedNodeMap namedNodeMap2 = rootElement2.getAttributes();

            for (int x = 0; x < namedNodeMap2.getLength(); x++) {
                Attr importedNode = (Attr) doc1.importNode(namedNodeMap2.item(x), true);

                rootElement1.setAttributeNodeNS(importedNode);

            }

            // Copy Child Nodes

            NodeList childNodes2 = rootElement2.getChildNodes();

            for (int x = 1; x < childNodes2.getLength(); x += 2) {

                boolean isAdd = true;
                Node replaceItem = childNodes2.item(x);

                if (replaceItem.getAttributes() != null) {

                    for (int i = 1; i < rootElement1.getChildNodes().getLength(); i += 2) {

                        Node rootItem = rootElement1.getChildNodes().item(i);
                        if (rootItem.getAttributes() != null) {

                            Node inNodeName = replaceItem.getAttributes().getNamedItem("name");
                            Node rootNodeName = rootItem.getAttributes().getNamedItem("name");
                            if (inNodeName.getNodeValue().equals(rootNodeName.getNodeValue())) {
                                isAdd = false;
                                System.out.println("覆盖" + rootElement1.getNodeName());
                                Node importedNode = doc1.importNode(replaceItem, true);
                                rootElement1.replaceChild(importedNode, rootItem);
                            }
                        }
                    }
                }

                if (isAdd) {
                    Node importedNode = doc1.importNode(replaceItem, true);
                    rootElement1.appendChild(importedNode);
                }

            }

            *//**
     * 修改指定 strings value 值
     *//*
            for (String replaceString : QuickConfig.getInstance().getCurrentQuickInfo().getRes().getValue()) {
                String[] replaceStringSplit = replaceString.split("=");

                if (outXmlFileName.contains(replaceStringSplit[0]) && outXmlFileName.endsWith("strings.xml")) {

                    for (int k = 1; k < rootElement1.getChildNodes().getLength(); k += 2) {

                        Node item = rootElement1.getChildNodes().item(k);
                        if (item.getAttributes().getNamedItem("name").getNodeValue().equals(replaceStringSplit[1])) {
                            item.setTextContent(replaceStringSplit[2]);
                        }
                    }
                }

            }

            writeTo(doc1, outXmlFileName);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }*/

    /**
     * 合并文件，同名覆盖
     *
     * @param outXmlFileName
     * @param inXmlFileName
     */
    public static void mergingXml(String outXmlFileName, String inXmlFileName) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

       //     System.out.println("mergingXml: outXmlFileName: " + outXmlFileName + " ; inXmlFileName: " + inXmlFileName);

            DocumentBuilder db = dbf.newDocumentBuilder();


            Document doc1 = db.parse(outXmlFileName);

            Element rootElement1 = doc1.getDocumentElement();

            File file2 = new File(inXmlFileName);

            Document doc2 = db.parse(file2);

            Element rootElement2 = doc2.getDocumentElement();

            // Copy Attributes

            NamedNodeMap namedNodeMap2 = rootElement2.getAttributes();

            for (int x = 0; x < namedNodeMap2.getLength(); x++) {
                Attr importedNode = (Attr) doc1.importNode(namedNodeMap2.item(x), true);

                rootElement1.setAttributeNodeNS(importedNode);

            }

            // Copy Child Nodes

            NodeList childNodes2 = rootElement2.getChildNodes();

            for (int x = 1; x < childNodes2.getLength(); x += 2) {

                boolean isAdd = true;
                Node replaceItem = childNodes2.item(x);

                if (replaceItem.getAttributes() != null) {

                    for (int i = 1; i < rootElement1.getChildNodes().getLength(); i += 2) {

                        Node rootItem = rootElement1.getChildNodes().item(i);
                        if (rootItem.getAttributes() != null) {

                            Node inNodeName = replaceItem.getAttributes().getNamedItem("name");
                            Node rootNodeName = rootItem.getAttributes().getNamedItem("name");
                            if (inNodeName.getNodeValue().equals(rootNodeName.getNodeValue())) {
                                isAdd = false;
                                Node importedNode = doc1.importNode(replaceItem, true);
                                rootElement1.replaceChild(importedNode, rootItem);
                            }
                        }
                    }
                }

                if (isAdd) {
                    Node importedNode = doc1.importNode(replaceItem, true);
                    rootElement1.appendChild(importedNode);
                }

            }

            /**
             * 修改指定 strings value 值
             */
            for (String replaceString : QuickConfig.getInstance().getCurrentQuickInfo().getRes().getValue()) {
                String[] replaceStringSplit = replaceString.split("=");

                if (outXmlFileName.contains(replaceStringSplit[0]) && outXmlFileName.endsWith("strings.xml")) {

                    for (int k = 1; k < rootElement1.getChildNodes().getLength(); k += 2) {

                        Node item = rootElement1.getChildNodes().item(k);
                        if (item.getAttributes().getNamedItem("name").getNodeValue().equals(replaceStringSplit[1])) {
                            item.setTextContent(replaceStringSplit[2]);
                        }
                    }
                }

            }
            writeTo(doc1, outXmlFileName);

        } catch (DOMException e) {
            System.out.println("Exception: " + e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 写入xml
     *
     * @param doc
     * @param fileName
     * @return
     */
    private static boolean writeTo(Document doc, String fileName) {

        boolean isOver = false;

        DOMSource doms = new DOMSource(doc);

        File f = new File(fileName);

        StreamResult sr = new StreamResult(f);

        try {

            TransformerFactory tf = TransformerFactory.newInstance();

            Transformer t = tf.newTransformer();

            Properties properties = t.getOutputProperties();

            properties.setProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            properties.setProperty(OutputKeys.ENCODING, "UTF-8");

            properties.setProperty(OutputKeys.INDENT, "yes");

            t.setOutputProperties(properties);

            t.transform(doms, sr);

            isOver = true;

        } catch (TransformerConfigurationException tce) {

            tce.printStackTrace();

        } catch (TransformerException te) {

            te.printStackTrace();

        }

        return isOver;

    }

    public static void main(String[] args) {

        try {

            String sourcefile = "E:\\devcopy\\a.xml";

            String targetfile = "E:\\devcopy\\b.xml";

            //     mergingXml(sourcefile,targetfile);

            //     MergeDom4jUtil.mergePublicXml(sourcefile, targetfile);

            File file = new File("E:\\devcopy\\cwqmx_tg_20220507\\res");

               MergeDom4jUtil.mergeAndroidManifestXml(sourcefile,"xipu");

       //     mergingManifestXml(sourcefile, "xipu");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
