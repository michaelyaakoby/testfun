package org.testfun.jee.runner;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
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

            connectionUrl = extractConnectionURL(document, xPath);
            persistenceUnitName = (String) xPath.evaluate("//*[local-name()='persistence-unit']/@name", document, XPathConstants.STRING);

        } catch (XPathExpressionException e) {
            throw new EjbWithMockitoRunnerException("Failed initializing XPath expressions");
        }
    }

    private String extractConnectionURL(final Document document, final XPath xPath) throws XPathExpressionException {
        String connectionURL = getHibernateURL(document, xPath);

        if (StringUtils.isBlank(connectionURL)) {
            connectionURL = getJPAURL(document, xPath);
        }

        if (StringUtils.isBlank(connectionURL)){
            final String errorMessage = "We could not find the JPA/Hibernate jdbc URL. " +
                    "\n Did you set the hibernate.connection.url or the javax.persistence.jdbc.url attribute in the persistence.xml file?";
            throw new IllegalArgumentException(errorMessage);
        }

        return connectionURL;
    }

    private String getHibernateURL(final Document document, final XPath xPath) throws XPathExpressionException {
        return (String) xPath.evaluate("//*[local-name()='property' and @name='hibernate.connection.url']/@value", document, XPathConstants.STRING);
    }

    private String getJPAURL(final Document document, final XPath xPath) throws XPathExpressionException {
        return (String) xPath.evaluate("//*[local-name()='property' and @name='javax.persistence.jdbc.url']/@value", document, XPathConstants.STRING);
    }

    public static PersistenceXml getInstnace() {
        return INSTANCE;
    }

}
