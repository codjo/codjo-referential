package net.codjo.referential.gui;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.plugin.ReferentialGuiPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 */
public class XmlReferentialMapping implements ReferentialMapping {
    private Document xmlDoc;


    private XmlReferentialMapping(Document xmlDoc) {
        this.xmlDoc = xmlDoc;
    }


    public SortedMap<String, Referential> getReferentialsByPreferenceId() throws Exception {
        return extractFromXml(xmlDoc);
    }


    public static ReferentialMapping loadDefault()
          throws ParserConfigurationException, SAXException, IOException {
        return loadFrom(ReferentialGuiPlugin.MAPPING_FILE_PATH);
    }


    public static ReferentialMapping loadFrom(String resource)
          throws ParserConfigurationException, SAXException, IOException {
        InputStream mappingFile = XmlReferentialMapping.class.getResourceAsStream(resource);
        try {
            return new XmlReferentialMapping(parse(mappingFile));
        }
        finally {
            mappingFile.close();
        }
    }


    private static Document parse(InputStream stream)
          throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(stream);
    }


    private static SortedMap<String, Referential> extractFromXml(Document doc) throws Exception {
        SortedMap<String, Referential> referentialList = new TreeMap<String, Referential>();
        NodeList nodeList = doc.getElementsByTagName("linkReferential");
        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
            Node node = nodeList.item(nodeIndex);
            NamedNodeMap attributes = node.getAttributes();
            int attributeIndex = 0;
            Referential referential = new Referential();
            while (attributeIndex < attributes.getLength()) {
                Node attribute = attributes.item(attributeIndex);
                if ("preferenceId".equals(attribute.getNodeName())) {
                    referential.setPreferenceId(attribute.getNodeValue());
                }
                else if ("label".equals(attribute.getNodeName())) {
                    referential.setTitle(attribute.getNodeValue());
                }
                else if ("name".equals(attribute.getNodeName())) {
                    referential.setName(attribute.getNodeValue());
                }
                attributeIndex++;
            }
            processFields(node, referential);
            referentialList.put(referential.getPreferenceId(), referential);
        }
        return referentialList;
    }


    private static void processFields(Node parentNode, Referential referential) {
        NodeList nodeList = parentNode.getChildNodes();
        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
            Node node = nodeList.item(nodeIndex);
            if (node instanceof Element) {
                Field field = new Field();
                NamedNodeMap attributes = node.getAttributes();

                for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
                    Node attribute = attributes.item(attributeIndex);
                    fillField(attribute, field);
                }
                processHandlerNode(node, field);
                referential.addField(field);
            }
        }
    }


    private static void fillField(Node attribute, Field field) {
        if ("name".equals(attribute.getNodeName())) {
            field.setName(attribute.getNodeValue());
        }
        else if ("label".equals(attribute.getNodeName())) {
            field.setLabel(attribute.getNodeValue());
        }
        else if ("type".equals(attribute.getNodeName())) {
            field.setType(attribute.getNodeValue());
        }
        else if ("precision".equals(attribute.getNodeName())) {
            String value = attribute.getNodeValue();
            if (value.contains(",")) {
                String[] precision = value.split(",");
                field.setLength(Integer.parseInt(precision[0]));
                field.setDecimalLength(Integer.parseInt(precision[1]));
            }
            else {
                field.setLength(Integer.parseInt(value));
            }
        }
        else if ("primary".equals(attribute.getNodeName())) {
            field.setPrimaryKey(Boolean.valueOf(attribute.getNodeValue()));
        }
        else if ("required".equals(attribute.getNodeName())) {
            field.setRequired(Boolean.valueOf(attribute.getNodeValue()));
        }
        else if ("structure".equals(attribute.getNodeName())) {
            field.setType(attribute.getNodeValue());
        }
        else if ("isGenerated".equals(attribute.getNodeName())) {
            field.setGenerated(Boolean.valueOf(attribute.getNodeValue()));
        }
        else if ("default".equals(attribute.getNodeName())) {
            field.setDefaultValue(attribute.getNodeValue());
        }
    }


    private static void processHandlerNode(Node parentNode, Field field) {
        NodeList nodeList = parentNode.getChildNodes();
        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
            Node node = nodeList.item(nodeIndex);
            if (node instanceof Element) {
                if ("refHandler".equals(node.getNodeName())) {
                    NamedNodeMap attributes = node.getAttributes();
                    for (int handlerAttributeIndex = 0;
                         handlerAttributeIndex < attributes.getLength();
                         handlerAttributeIndex++) {
                        Node handlerAttribute = attributes.item(handlerAttributeIndex);
                        if ("id".equals(handlerAttribute.getNodeName())) {
                            field.setHandlerId(handlerAttribute.getNodeValue());
                        }
                    }
                }
            }
        }
    }
}
