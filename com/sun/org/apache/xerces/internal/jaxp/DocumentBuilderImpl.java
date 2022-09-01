package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderImpl extends DocumentBuilder implements JAXPConstants {
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  
  private static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
  
  private static final String CREATE_ENTITY_REF_NODES_FEATURE = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
  
  private static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
  
  private static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
  
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  
  private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
  
  private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
  
  public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  
  DocumentBuilderImpl(DocumentBuilderFactoryImpl dbf, Hashtable dbfAttrs, Hashtable features) throws SAXNotRecognizedException, SAXNotSupportedException {
    this(dbf, dbfAttrs, features, false);
  }
  
  private final DOMParser domParser = new DOMParser();
  
  private final Schema grammar;
  
  private final XMLComponent fSchemaValidator;
  
  private final XMLComponentManager fSchemaValidatorComponentManager;
  
  private final ValidationManager fSchemaValidationManager;
  
  private final UnparsedEntityHandler fUnparsedEntityHandler;
  
  private final ErrorHandler fInitErrorHandler;
  
  private final EntityResolver fInitEntityResolver;
  
  private XMLSecurityManager fSecurityManager;
  
  private XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  DocumentBuilderImpl(DocumentBuilderFactoryImpl dbf, Hashtable dbfAttrs, Hashtable features, boolean secureProcessing) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (dbf.isValidating()) {
      this.fInitErrorHandler = new DefaultValidationErrorHandler(this.domParser.getXMLParserConfiguration().getLocale());
      setErrorHandler(this.fInitErrorHandler);
    } else {
      this.fInitErrorHandler = this.domParser.getErrorHandler();
    } 
    this.domParser.setFeature("http://xml.org/sax/features/validation", dbf.isValidating());
    this.domParser.setFeature("http://xml.org/sax/features/namespaces", dbf.isNamespaceAware());
    this.domParser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", 
        !dbf.isIgnoringElementContentWhitespace());
    this.domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", 
        !dbf.isExpandEntityReferences());
    this.domParser.setFeature("http://apache.org/xml/features/include-comments", 
        !dbf.isIgnoringComments());
    this.domParser.setFeature("http://apache.org/xml/features/create-cdata-nodes", 
        !dbf.isCoalescing());
    if (dbf.isXIncludeAware())
      this.domParser.setFeature("http://apache.org/xml/features/xinclude", true); 
    this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    this.domParser.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
    this.fSecurityManager = new XMLSecurityManager(secureProcessing);
    this.domParser.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
    if (secureProcessing)
      if (features != null) {
        Object temp = features.get("http://javax.xml.XMLConstants/feature/secure-processing");
        if (temp != null) {
          boolean value = ((Boolean)temp).booleanValue();
          if (value && Constants.IS_JDK8_OR_ABOVE) {
            this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
            this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
          } 
        } 
      }  
    this.grammar = dbf.getSchema();
    if (this.grammar != null) {
      XMLParserConfiguration config = this.domParser.getXMLParserConfiguration();
      XMLComponent validatorComponent = null;
      if (this.grammar instanceof XSGrammarPoolContainer) {
        validatorComponent = new XMLSchemaValidator();
        this.fSchemaValidationManager = new ValidationManager();
        this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager);
        config.setDTDHandler(this.fUnparsedEntityHandler);
        this.fUnparsedEntityHandler.setDTDHandler(this.domParser);
        this.domParser.setDTDSource(this.fUnparsedEntityHandler);
        this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(config, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
      } else {
        validatorComponent = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
        this.fSchemaValidationManager = null;
        this.fUnparsedEntityHandler = null;
        this.fSchemaValidatorComponentManager = config;
      } 
      config.addRecognizedFeatures(validatorComponent.getRecognizedFeatures());
      config.addRecognizedProperties(validatorComponent.getRecognizedProperties());
      setFeatures(features);
      config.setDocumentHandler((XMLDocumentHandler)validatorComponent);
      ((XMLDocumentSource)validatorComponent).setDocumentHandler(this.domParser);
      this.domParser.setDocumentSource((XMLDocumentSource)validatorComponent);
      this.fSchemaValidator = validatorComponent;
    } else {
      this.fSchemaValidationManager = null;
      this.fUnparsedEntityHandler = null;
      this.fSchemaValidatorComponentManager = null;
      this.fSchemaValidator = null;
      setFeatures(features);
    } 
    setDocumentBuilderFactoryAttributes(dbfAttrs);
    this.fInitEntityResolver = this.domParser.getEntityResolver();
  }
  
  private void setFeatures(Hashtable features) throws SAXNotSupportedException, SAXNotRecognizedException {
    if (features != null) {
      Iterator<Map.Entry> entries = features.entrySet().iterator();
      while (entries.hasNext()) {
        Map.Entry entry = entries.next();
        String feature = (String)entry.getKey();
        boolean value = ((Boolean)entry.getValue()).booleanValue();
        this.domParser.setFeature(feature, value);
      } 
    } 
  }
  
  private void setDocumentBuilderFactoryAttributes(Hashtable dbfAttrs) throws SAXNotSupportedException, SAXNotRecognizedException {
    if (dbfAttrs == null)
      return; 
    Iterator<Map.Entry> entries = dbfAttrs.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry entry = entries.next();
      String name = (String)entry.getKey();
      Object val = entry.getValue();
      if (val instanceof Boolean) {
        this.domParser.setFeature(name, ((Boolean)val).booleanValue());
        continue;
      } 
      if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name)) {
        if ("http://www.w3.org/2001/XMLSchema".equals(val) && 
          isValidating()) {
          this.domParser.setFeature("http://apache.org/xml/features/validation/schema", true);
          this.domParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        } 
        continue;
      } 
      if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(name)) {
        if (isValidating()) {
          String value = (String)dbfAttrs.get("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
          if (value != null && "http://www.w3.org/2001/XMLSchema".equals(value)) {
            this.domParser.setProperty(name, val);
            continue;
          } 
          throw new IllegalArgumentException(
              DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
        } 
        continue;
      } 
      if (this.fSecurityManager == null || 
        !this.fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, val))
        if (this.fSecurityPropertyMgr == null || 
          !this.fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, val))
          this.domParser.setProperty(name, val);  
    } 
  }
  
  public Document newDocument() {
    return new DocumentImpl();
  }
  
  public DOMImplementation getDOMImplementation() {
    return DOMImplementationImpl.getDOMImplementation();
  }
  
  public Document parse(InputSource is) throws SAXException, IOException {
    if (is == null)
      throw new IllegalArgumentException(
          DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-null-input-source", null)); 
    if (this.fSchemaValidator != null) {
      if (this.fSchemaValidationManager != null) {
        this.fSchemaValidationManager.reset();
        this.fUnparsedEntityHandler.reset();
      } 
      resetSchemaValidator();
    } 
    this.domParser.parse(is);
    Document doc = this.domParser.getDocument();
    this.domParser.dropDocumentReferences();
    return doc;
  }
  
  public boolean isNamespaceAware() {
    try {
      return this.domParser.getFeature("http://xml.org/sax/features/namespaces");
    } catch (SAXException x) {
      throw new IllegalStateException(x.getMessage());
    } 
  }
  
  public boolean isValidating() {
    try {
      return this.domParser.getFeature("http://xml.org/sax/features/validation");
    } catch (SAXException x) {
      throw new IllegalStateException(x.getMessage());
    } 
  }
  
  public boolean isXIncludeAware() {
    try {
      return this.domParser.getFeature("http://apache.org/xml/features/xinclude");
    } catch (SAXException exc) {
      return false;
    } 
  }
  
  public void setEntityResolver(EntityResolver er) {
    this.domParser.setEntityResolver(er);
  }
  
  public void setErrorHandler(ErrorHandler eh) {
    this.domParser.setErrorHandler(eh);
  }
  
  public Schema getSchema() {
    return this.grammar;
  }
  
  public void reset() {
    if (this.domParser.getErrorHandler() != this.fInitErrorHandler)
      this.domParser.setErrorHandler(this.fInitErrorHandler); 
    if (this.domParser.getEntityResolver() != this.fInitEntityResolver)
      this.domParser.setEntityResolver(this.fInitEntityResolver); 
  }
  
  DOMParser getDOMParser() {
    return this.domParser;
  }
  
  private void resetSchemaValidator() throws SAXException {
    try {
      this.fSchemaValidator.reset(this.fSchemaValidatorComponentManager);
    } catch (XMLConfigurationException e) {
      throw new SAXException(e);
    } 
  }
}
