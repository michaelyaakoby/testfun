package org.testfun.jee.runner;

import lombok.Data;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Data
public class PersistenceXml {

    private static final PersistenceXml INSTANCE = new PersistenceXml();

    private final String connectionUrl;
    private final String persistenceUnitName;

    private PersistenceXml() {
        Document document;
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(PersistenceXml.class.getResourceAsStream("/META-INF/persistence.xml"));

        } catch (Exception e) {
            throw new EjbWithMockitoRunnerException("Failed parsing " + PersistenceXml.class.getResource("/META-INF/persistence.xml"), e);
        }

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            connectionUrl = (String) xPath.evaluate("//*[local-name()='property' and @name='hibernate.connection.url']/@value", document, XPathConstants.STRING);
            persistenceUnitName = (String) xPath.evaluate("//*[local-name()='persistence-unit']/@name", document, XPathConstants.STRING);

        } catch (XPathExpressionException e) {
            throw new EjbWithMockitoRunnerException("Failed initializing XPath expressions");
        }
    }

    public static PersistenceXml getInstnace() {
        return INSTANCE;
    }

}
