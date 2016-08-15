package com.ken;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;

/**
 * Created by ken on 16-8-4.
 */
public class StringsMergeUtil {

    /**
     *
     * @param xmlFile
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private static Document parseXml(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        return doc;
    }


    /**
     *
     * @param nodeList
     * @param attrVal
     * @return
     */
    private static Node getNodeByNameAttribute(NodeList nodeList, String attrVal) {
        int len = nodeList.getLength();
        for (int i = 0; i < len; i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attrMap = node.getAttributes();
            String val = attrMap.getNamedItem("name").getNodeValue();
            if (attrVal.equals(val)) {
                return node;
            }
        }
        return null;
    }


    /**
     *
     * @param nodeList
     * @return
     */
    private static HashMap<String, String> getNameValueAndContent(NodeList nodeList) {
        HashMap<String, String> nameValueAndContent = new HashMap<String, String>();
        int len = nodeList.getLength();
        for (int i = 0; i < len; i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attrMap = node.getAttributes();
            String nameVal = attrMap.getNamedItem("name").getNodeValue();
            String content = node.getTextContent();
            nameValueAndContent.put(nameVal, content);
        }
        return nameValueAndContent;
    }


    /**
     *
     * @param nodeList
     * @param nameValAndContent
     * @return
     */
    private static NodeList replaceNodesContent(NodeList nodeList, HashMap<String, String> nameValAndContent) {
        int mapSize = nameValAndContent.size();
        for (String key : nameValAndContent.keySet()) {
            Node node = getNodeByNameAttribute(nodeList, key);
            if (node != null) {
                node.setTextContent(nameValAndContent.get(key));
            }
        }

        return nodeList;
    }

    /**
     *
     * @param fileSourceXml
     * @param fileTargetXml
     */
    public static void mergeStringsXml(File fileSourceXml, File fileTargetXml) {
        try {
            if (!fileTargetXml.exists() || !fileSourceXml.exists()) {
                return;
            }

            Document docSource = parseXml(fileSourceXml);
            Document docTarget = parseXml(fileTargetXml);

            Element rootElementSource = docSource.getDocumentElement();
            Element rootElementTarget = docTarget.getDocumentElement();

            NodeList nodeListSource = rootElementSource.getElementsByTagName("string");
            NodeList nodeListTarget = rootElementTarget.getElementsByTagName("string");

            HashMap<String, String> map = getNameValueAndContent(nodeListTarget);
            replaceNodesContent(nodeListSource, map);

            writeFileContent(fileSourceXml, convertDocumentToXml(docSource));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


    private static String convertDocumentToXml(Document doc) throws TransformerException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transFormer = transFactory.newTransformer();
        transFormer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource domSource = new DOMSource(doc);

        StringWriter sw = new StringWriter();
        StreamResult xmlResult = new StreamResult(sw);

        transFormer.transform(domSource, xmlResult);

        return sw.toString();
    }

    /**
     * FIXME: 写出时用utf-8编码
     * /
    private static void writeFileContent(File file, String content) throws IOException {
        if (file == null || content == null) {
            throw new IllegalArgumentException("file or content must not be null.");
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
        fw.close();
    }

}
