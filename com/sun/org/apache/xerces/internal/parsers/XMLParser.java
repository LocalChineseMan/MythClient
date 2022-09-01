package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class XMLParser {
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-handler" };
  
  protected XMLParserConfiguration fConfiguration;
  
  XMLSecurityManager securityManager;
  
  XMLSecurityPropertyManager securityPropertyManager;
  
  public boolean getFeature(String featureId) throws SAXNotSupportedException, SAXNotRecognizedException {
    return this.fConfiguration.getFeature(featureId);
  }
  
  protected XMLParser(XMLParserConfiguration config) {
    this.fConfiguration = config;
    this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
  }
  
  public void parse(XMLInputSource inputSource) throws XNIException, IOException {
    if (this.securityManager == null) {
      this.securityManager = new XMLSecurityManager(true);
      this.fConfiguration.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager);
    } 
    if (this.securityPropertyManager == null) {
      this.securityPropertyManager = new XMLSecurityPropertyManager();
      this.fConfiguration.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
    } 
    reset();
    this.fConfiguration.parse(inputSource);
  }
  
  protected void reset() throws XNIException {}
}
