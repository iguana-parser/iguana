package org.iguana.util.config;

import org.iguana.util.Configuration;
import org.iguana.utils.logging.LogLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLConfigFileParser implements ConfigFileParser {

    private Element root;

    private XMLConfigFileParser(Element root) {
        this.root = root;
    }

    public static XMLConfigFileParser create(String name) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(Configuration.class.getResourceAsStream("/" + name));
        return new XMLConfigFileParser(doc.getDocumentElement());
    }

    @Override
    public Configuration getConfiguration() {
        Configuration.Builder builder = Configuration.builder();
        getParserConfigs(builder);
        getLoggingConfig(builder);
        return builder.build();
    }

    private void getParserConfigs(Configuration.Builder builder) {
        Node configurationNode = root.getElementsByTagName("Parser").item(0);
        NodeList childNodes = configurationNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            switch (node.getNodeName()) {
                case "Lookahead":
                    builder.setLookaheadCount(Integer.parseInt(node.getTextContent().toUpperCase()));
                    break;

                case "GSSLookupImpl":
                    builder.setGSSLookupImpl(Configuration.LookupImpl.valueOf(node.getTextContent().toUpperCase()));
                    break;

                case "MatcherType":
                    builder.setMatcherType(Configuration.MatcherType.valueOf(node.getTextContent().toUpperCase()));
                    break;

                case "HashMapImpl":
                    builder.setHashmapImpl(Configuration.HashMapImpl.valueOf(node.getTextContent().toUpperCase()));
                    break;

                case "EnvironmentImpl":
                    builder.setEnvironmentImpl(
                        Configuration.EnvironmentImpl.valueOf(node.getTextContent().toUpperCase()));
                    break;
            }
        }
    }

    private void getLoggingConfig(Configuration.Builder builder) {
        Node configurationNode = root.getElementsByTagName("Logging").item(0);
        NodeList childNodes = configurationNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            switch (node.getNodeName()) {

                case "LogLevel":
                    builder.setLogLevel(LogLevel.valueOf(node.getTextContent().toUpperCase()));
                    break;
            }
        }
    }

}
