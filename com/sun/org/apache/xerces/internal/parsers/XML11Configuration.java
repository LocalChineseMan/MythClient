package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11DocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11NSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class XML11Configuration extends ParserConfigurationSettings implements XMLPullParserConfiguration, XML11Configurable {
  protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
  
  protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
  
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  
  protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  
  protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
  
  protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  
  protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  
  protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  
  protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  
  protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  
  protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  
  protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  
  protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
  
  protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
  
  protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
  
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLInputSource fInputSource;
  
  protected ValidationManager fValidationManager;
  
  protected XMLVersionDetector fVersionDetector;
  
  protected XMLLocator fLocator;
  
  protected Locale fLocale;
  
  protected ArrayList fComponents;
  
  protected ArrayList fXML11Components = null;
  
  protected ArrayList fCommonComponents = null;
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDTDHandler fDTDHandler;
  
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  
  protected XMLDocumentSource fLastComponent;
  
  protected boolean fParseInProgress = false;
  
  protected boolean fConfigUpdated = false;
  
  protected DTDDVFactory fDatatypeValidatorFactory;
  
  protected XMLNSDocumentScannerImpl fNamespaceScanner;
  
  protected XMLDocumentScannerImpl fNonNSScanner;
  
  protected XMLDTDValidator fDTDValidator;
  
  protected XMLDTDValidator fNonNSDTDValidator;
  
  protected XMLDTDScanner fDTDScanner;
  
  protected XMLDTDProcessor fDTDProcessor;
  
  protected DTDDVFactory fXML11DatatypeFactory = null;
  
  protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
  
  protected XML11DocumentScannerImpl fXML11DocScanner = null;
  
  protected XML11NSDTDValidator fXML11NSDTDValidator = null;
  
  protected XML11DTDValidator fXML11DTDValidator = null;
  
  protected XML11DTDScannerImpl fXML11DTDScanner = null;
  
  protected XML11DTDProcessor fXML11DTDProcessor = null;
  
  protected XMLGrammarPool fGrammarPool;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityManager fEntityManager;
  
  protected XMLSchemaValidator fSchemaValidator;
  
  protected XMLDocumentScanner fCurrentScanner;
  
  protected DTDDVFactory fCurrentDVFactory;
  
  protected XMLDTDScanner fCurrentDTDScanner;
  
  private boolean f11Initialized = false;
  
  public XML11Configuration() {
    this((SymbolTable)null, (XMLGrammarPool)null, (XMLComponentManager)null);
  }
  
  public XML11Configuration(SymbolTable symbolTable) {
    this(symbolTable, (XMLGrammarPool)null, (XMLComponentManager)null);
  }
  
  public XML11Configuration(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
    this(symbolTable, grammarPool, (XMLComponentManager)null);
  }
  
  public XML11Configuration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings) {
    super(parentSettings);
    this.fComponents = new ArrayList();
    this.fXML11Components = new ArrayList();
    this.fCommonComponents = new ArrayList();
    this.fFeatures = new HashMap<>();
    this.fProperties = new HashMap<>();
    String[] recognizedFeatures = { 
        "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", 
        "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings", "http://javax.xml.XMLConstants/feature/secure-processing" };
    addRecognizedFeatures(recognizedFeatures);
    this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
    this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/validation/schema/element-default", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/validation/schema/augment-psvi", Boolean.TRUE);
    this.fFeatures.put("http://apache.org/xml/features/generate-synthetic-annotations", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/validate-annotations", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/honour-all-schemaLocations", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/namespace-growth", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/internal/tolerate-duplicates", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", Boolean.FALSE);
    this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
    this.fFeatures.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
    String[] recognizedProperties = { 
        "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", 
        "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/validator/schema", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", 
        "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
    addRecognizedProperties(recognizedProperties);
    if (symbolTable == null)
      symbolTable = new SymbolTable(); 
    this.fSymbolTable = symbolTable;
    this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
    this.fGrammarPool = grammarPool;
    if (this.fGrammarPool != null)
      this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool); 
    this.fEntityManager = new XMLEntityManager();
    this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
    addCommonComponent(this.fEntityManager);
    this.fErrorReporter = new XMLErrorReporter();
    this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
    addCommonComponent(this.fErrorReporter);
    this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
    addComponent(this.fNamespaceScanner);
    this.fDTDScanner = new XMLDTDScannerImpl();
    this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
    addComponent((XMLComponent)this.fDTDScanner);
    this.fDTDProcessor = new XMLDTDProcessor();
    this.fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
    addComponent(this.fDTDProcessor);
    this.fDTDValidator = new XMLNSDTDValidator();
    this.fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
    addComponent(this.fDTDValidator);
    this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
    this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
    this.fValidationManager = new ValidationManager();
    this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
    this.fVersionDetector = new XMLVersionDetector();
    if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
      XMLMessageFormatter xmft = new XMLMessageFormatter();
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
      this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
    } 
    try {
      setLocale(Locale.getDefault());
    } catch (XNIException xNIException) {}
    this.fConfigUpdated = false;
  }
  
  public void setInputSource(XMLInputSource inputSource) throws XMLConfigurationException, IOException {
    this.fInputSource = inputSource;
  }
  
  public void setLocale(Locale locale) throws XNIException {
    this.fLocale = locale;
    this.fErrorReporter.setLocale(locale);
  }
  
  public void setDocumentHandler(XMLDocumentHandler documentHandler) {
    this.fDocumentHandler = documentHandler;
    if (this.fLastComponent != null) {
      this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fLastComponent); 
    } 
  }
  
  public XMLDocumentHandler getDocumentHandler() {
    return this.fDocumentHandler;
  }
  
  public void setDTDHandler(XMLDTDHandler dtdHandler) {
    this.fDTDHandler = dtdHandler;
  }
  
  public XMLDTDHandler getDTDHandler() {
    return this.fDTDHandler;
  }
  
  public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {
    this.fDTDContentModelHandler = handler;
  }
  
  public XMLDTDContentModelHandler getDTDContentModelHandler() {
    return this.fDTDContentModelHandler;
  }
  
  public void setEntityResolver(XMLEntityResolver resolver) {
    this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
  }
  
  public XMLEntityResolver getEntityResolver() {
    return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
  }
  
  public void setErrorHandler(XMLErrorHandler errorHandler) {
    this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
  }
  
  public XMLErrorHandler getErrorHandler() {
    return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
  }
  
  public void cleanup() {
    this.fEntityManager.closeReaders();
  }
  
  public void parse(XMLInputSource source) throws XNIException, IOException {
    if (this.fParseInProgress)
      throw new XNIException("FWK005 parse may not be called while parsing."); 
    this.fParseInProgress = true;
    try {
      setInputSource(source);
      parse(true);
    } catch (XNIException ex) {
      throw ex;
    } catch (IOException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new XNIException(ex);
    } finally {
      this.fParseInProgress = false;
      cleanup();
    } 
  }
  
  public boolean parse(boolean complete) throws XNIException, IOException {
    if (this.fInputSource != null)
      try {
        this.fValidationManager.reset();
        this.fVersionDetector.reset(this);
        this.fConfigUpdated = true;
        resetCommon();
        short version = this.fVersionDetector.determineDocVersion(this.fInputSource);
        if (version == 2) {
          initXML11Components();
          configureXML11Pipeline();
          resetXML11();
        } else {
          configurePipeline();
          reset();
        } 
        this.fConfigUpdated = false;
        this.fVersionDetector.startDocumentParsing((XMLEntityHandler)this.fCurrentScanner, version);
        this.fInputSource = null;
      } catch (XNIException ex) {
        throw ex;
      } catch (IOException ex) {
        throw ex;
      } catch (RuntimeException ex) {
        throw ex;
      } catch (Exception ex) {
        throw new XNIException(ex);
      }  
    try {
      return this.fCurrentScanner.scanDocument(complete);
    } catch (XNIException ex) {
      throw ex;
    } catch (IOException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new XNIException(ex);
    } 
  }
  
  public FeatureState getFeatureState(String featureId) throws XMLConfigurationException {
    if (featureId.equals("http://apache.org/xml/features/internal/parser-settings"))
      return FeatureState.is(this.fConfigUpdated); 
    return super.getFeatureState(featureId);
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    this.fConfigUpdated = true;
    int count = this.fComponents.size();
    int i;
    for (i = 0; i < count; i++) {
      XMLComponent c = this.fComponents.get(i);
      c.setFeature(featureId, state);
    } 
    count = this.fCommonComponents.size();
    for (i = 0; i < count; i++) {
      XMLComponent c = this.fCommonComponents.get(i);
      c.setFeature(featureId, state);
    } 
    count = this.fXML11Components.size();
    for (i = 0; i < count; i++) {
      XMLComponent c = this.fXML11Components.get(i);
      try {
        c.setFeature(featureId, state);
      } catch (Exception exception) {}
    } 
    super.setFeature(featureId, state);
  }
  
  public PropertyState getPropertyState(String propertyId) throws XMLConfigurationException {
    if ("http://apache.org/xml/properties/locale".equals(propertyId))
      return PropertyState.is(getLocale()); 
    return super.getPropertyState(propertyId);
  }
  
  public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    this.fConfigUpdated = true;
    if ("http://apache.org/xml/properties/locale".equals(propertyId))
      setLocale((Locale)value); 
    int count = this.fComponents.size();
    int i;
    for (i = 0; i < count; i++) {
      XMLComponent c = this.fComponents.get(i);
      c.setProperty(propertyId, value);
    } 
    count = this.fCommonComponents.size();
    for (i = 0; i < count; i++) {
      XMLComponent c = this.fCommonComponents.get(i);
      c.setProperty(propertyId, value);
    } 
    count = this.fXML11Components.size();
    for (i = 0; i < count; i++) {
      XMLComponent c = this.fXML11Components.get(i);
      try {
        c.setProperty(propertyId, value);
      } catch (Exception exception) {}
    } 
    super.setProperty(propertyId, value);
  }
  
  public Locale getLocale() {
    return this.fLocale;
  }
  
  protected void reset() throws XNIException {
    int count = this.fComponents.size();
    for (int i = 0; i < count; i++) {
      XMLComponent c = this.fComponents.get(i);
      c.reset(this);
    } 
  }
  
  protected void resetCommon() throws XNIException {
    int count = this.fCommonComponents.size();
    for (int i = 0; i < count; i++) {
      XMLComponent c = this.fCommonComponents.get(i);
      c.reset(this);
    } 
  }
  
  protected void resetXML11() throws XNIException {
    int count = this.fXML11Components.size();
    for (int i = 0; i < count; i++) {
      XMLComponent c = this.fXML11Components.get(i);
      c.reset(this);
    } 
  }
  
  protected void configureXML11Pipeline() {
    if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
      this.fCurrentDVFactory = this.fXML11DatatypeFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    } 
    if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
      this.fCurrentDTDScanner = this.fXML11DTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fXML11DTDProcessor);
    } 
    this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
    this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
    this.fXML11DTDProcessor.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null)
      this.fDTDHandler.setDTDSource(this.fXML11DTDProcessor); 
    this.fXML11DTDScanner.setDTDContentModelHandler(this.fXML11DTDProcessor);
    this.fXML11DTDProcessor.setDTDContentModelSource(this.fXML11DTDScanner);
    this.fXML11DTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDProcessor); 
    if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
      if (this.fCurrentScanner != this.fXML11NSDocScanner) {
        this.fCurrentScanner = this.fXML11NSDocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11NSDocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11NSDTDValidator);
      } 
      this.fXML11NSDocScanner.setDTDValidator(this.fXML11NSDTDValidator);
      this.fXML11NSDocScanner.setDocumentHandler(this.fXML11NSDTDValidator);
      this.fXML11NSDTDValidator.setDocumentSource(this.fXML11NSDocScanner);
      this.fXML11NSDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fXML11NSDTDValidator); 
      this.fLastComponent = this.fXML11NSDTDValidator;
    } else {
      if (this.fXML11DocScanner == null) {
        this.fXML11DocScanner = new XML11DocumentScannerImpl();
        addXML11Component(this.fXML11DocScanner);
        this.fXML11DTDValidator = new XML11DTDValidator();
        addXML11Component(this.fXML11DTDValidator);
      } 
      if (this.fCurrentScanner != this.fXML11DocScanner) {
        this.fCurrentScanner = this.fXML11DocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11DocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11DTDValidator);
      } 
      this.fXML11DocScanner.setDocumentHandler(this.fXML11DTDValidator);
      this.fXML11DTDValidator.setDocumentSource(this.fXML11DocScanner);
      this.fXML11DTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fXML11DTDValidator); 
      this.fLastComponent = this.fXML11DTDValidator;
    } 
    if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
      if (this.fSchemaValidator == null) {
        this.fSchemaValidator = new XMLSchemaValidator();
        setProperty("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
        addCommonComponent(this.fSchemaValidator);
        this.fSchemaValidator.reset(this);
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
          XSMessageFormatter xmft = new XSMessageFormatter();
          this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
        } 
      } 
      this.fLastComponent.setDocumentHandler(this.fSchemaValidator);
      this.fSchemaValidator.setDocumentSource(this.fLastComponent);
      this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fSchemaValidator); 
      this.fLastComponent = this.fSchemaValidator;
    } 
  }
  
  protected void configurePipeline() {
    if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
      this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
    } 
    if (this.fCurrentDTDScanner != this.fDTDScanner) {
      this.fCurrentDTDScanner = this.fDTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
    } 
    this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
    this.fDTDProcessor.setDTDSource(this.fDTDScanner);
    this.fDTDProcessor.setDTDHandler(this.fDTDHandler);
    if (this.fDTDHandler != null)
      this.fDTDHandler.setDTDSource(this.fDTDProcessor); 
    this.fDTDScanner.setDTDContentModelHandler(this.fDTDProcessor);
    this.fDTDProcessor.setDTDContentModelSource(this.fDTDScanner);
    this.fDTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDProcessor); 
    if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
      if (this.fCurrentScanner != this.fNamespaceScanner) {
        this.fCurrentScanner = this.fNamespaceScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
      } 
      this.fNamespaceScanner.setDTDValidator(this.fDTDValidator);
      this.fNamespaceScanner.setDocumentHandler(this.fDTDValidator);
      this.fDTDValidator.setDocumentSource(this.fNamespaceScanner);
      this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fDTDValidator); 
      this.fLastComponent = this.fDTDValidator;
    } else {
      if (this.fNonNSScanner == null) {
        this.fNonNSScanner = new XMLDocumentScannerImpl();
        this.fNonNSDTDValidator = new XMLDTDValidator();
        addComponent(this.fNonNSScanner);
        addComponent(this.fNonNSDTDValidator);
      } 
      if (this.fCurrentScanner != this.fNonNSScanner) {
        this.fCurrentScanner = this.fNonNSScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fNonNSDTDValidator);
      } 
      this.fNonNSScanner.setDocumentHandler(this.fNonNSDTDValidator);
      this.fNonNSDTDValidator.setDocumentSource(this.fNonNSScanner);
      this.fNonNSDTDValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fNonNSDTDValidator); 
      this.fLastComponent = this.fNonNSDTDValidator;
    } 
    if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
      if (this.fSchemaValidator == null) {
        this.fSchemaValidator = new XMLSchemaValidator();
        setProperty("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
        addCommonComponent(this.fSchemaValidator);
        this.fSchemaValidator.reset(this);
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
          XSMessageFormatter xmft = new XSMessageFormatter();
          this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
        } 
      } 
      this.fLastComponent.setDocumentHandler(this.fSchemaValidator);
      this.fSchemaValidator.setDocumentSource(this.fLastComponent);
      this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.setDocumentSource(this.fSchemaValidator); 
      this.fLastComponent = this.fSchemaValidator;
    } 
  }
  
  protected FeatureState checkFeature(String featureId) throws XMLConfigurationException {
    if (featureId.startsWith("http://apache.org/xml/features/")) {
      int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
      if (suffixLength == "validation/dynamic".length() && featureId
        .endsWith("validation/dynamic"))
        return FeatureState.RECOGNIZED; 
      if (suffixLength == "validation/default-attribute-values".length() && featureId
        .endsWith("validation/default-attribute-values"))
        return FeatureState.NOT_SUPPORTED; 
      if (suffixLength == "validation/validate-content-models".length() && featureId
        .endsWith("validation/validate-content-models"))
        return FeatureState.NOT_SUPPORTED; 
      if (suffixLength == "nonvalidating/load-dtd-grammar".length() && featureId
        .endsWith("nonvalidating/load-dtd-grammar"))
        return FeatureState.RECOGNIZED; 
      if (suffixLength == "nonvalidating/load-external-dtd".length() && featureId
        .endsWith("nonvalidating/load-external-dtd"))
        return FeatureState.RECOGNIZED; 
      if (suffixLength == "validation/validate-datatypes".length() && featureId
        .endsWith("validation/validate-datatypes"))
        return FeatureState.NOT_SUPPORTED; 
      if (suffixLength == "validation/schema".length() && featureId
        .endsWith("validation/schema"))
        return FeatureState.RECOGNIZED; 
      if (suffixLength == "validation/schema-full-checking".length() && featureId
        .endsWith("validation/schema-full-checking"))
        return FeatureState.RECOGNIZED; 
      if (suffixLength == "validation/schema/normalized-value".length() && featureId
        .endsWith("validation/schema/normalized-value"))
        return FeatureState.RECOGNIZED; 
      if (suffixLength == "validation/schema/element-default".length() && featureId
        .endsWith("validation/schema/element-default"))
        return FeatureState.RECOGNIZED; 
      if (suffixLength == "internal/parser-settings".length() && featureId
        .endsWith("internal/parser-settings"))
        return FeatureState.NOT_SUPPORTED; 
    } 
    return super.checkFeature(featureId);
  }
  
  protected PropertyState checkProperty(String propertyId) throws XMLConfigurationException {
    if (propertyId.startsWith("http://apache.org/xml/properties/")) {
      int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
      if (suffixLength == "internal/dtd-scanner".length() && propertyId
        .endsWith("internal/dtd-scanner"))
        return PropertyState.RECOGNIZED; 
      if (suffixLength == "schema/external-schemaLocation".length() && propertyId
        .endsWith("schema/external-schemaLocation"))
        return PropertyState.RECOGNIZED; 
      if (suffixLength == "schema/external-noNamespaceSchemaLocation".length() && propertyId
        .endsWith("schema/external-noNamespaceSchemaLocation"))
        return PropertyState.RECOGNIZED; 
    } 
    if (propertyId.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
      int suffixLength = propertyId.length() - "http://java.sun.com/xml/jaxp/properties/".length();
      if (suffixLength == "schemaSource".length() && propertyId
        .endsWith("schemaSource"))
        return PropertyState.RECOGNIZED; 
    } 
    if (propertyId.startsWith("http://xml.org/sax/properties/")) {
      int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
      if (suffixLength == "xml-string".length() && propertyId
        .endsWith("xml-string"))
        return PropertyState.NOT_SUPPORTED; 
    } 
    return super.checkProperty(propertyId);
  }
  
  protected void addComponent(XMLComponent component) {
    if (this.fComponents.contains(component))
      return; 
    this.fComponents.add(component);
    addRecognizedParamsAndSetDefaults(component);
  }
  
  protected void addCommonComponent(XMLComponent component) {
    if (this.fCommonComponents.contains(component))
      return; 
    this.fCommonComponents.add(component);
    addRecognizedParamsAndSetDefaults(component);
  }
  
  protected void addXML11Component(XMLComponent component) {
    if (this.fXML11Components.contains(component))
      return; 
    this.fXML11Components.add(component);
    addRecognizedParamsAndSetDefaults(component);
  }
  
  protected void addRecognizedParamsAndSetDefaults(XMLComponent component) {
    String[] recognizedFeatures = component.getRecognizedFeatures();
    addRecognizedFeatures(recognizedFeatures);
    String[] recognizedProperties = component.getRecognizedProperties();
    addRecognizedProperties(recognizedProperties);
    if (recognizedFeatures != null)
      for (int i = 0; i < recognizedFeatures.length; i++) {
        String featureId = recognizedFeatures[i];
        Boolean state = component.getFeatureDefault(featureId);
        if (state != null)
          if (!this.fFeatures.containsKey(featureId)) {
            this.fFeatures.put(featureId, state);
            this.fConfigUpdated = true;
          }  
      }  
    if (recognizedProperties != null)
      for (int i = 0; i < recognizedProperties.length; i++) {
        String propertyId = recognizedProperties[i];
        Object value = component.getPropertyDefault(propertyId);
        if (value != null)
          if (!this.fProperties.containsKey(propertyId)) {
            this.fProperties.put(propertyId, value);
            this.fConfigUpdated = true;
          }  
      }  
  }
  
  private void initXML11Components() {
    if (!this.f11Initialized) {
      this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
      this.fXML11DTDScanner = new XML11DTDScannerImpl();
      addXML11Component(this.fXML11DTDScanner);
      this.fXML11DTDProcessor = new XML11DTDProcessor();
      addXML11Component(this.fXML11DTDProcessor);
      this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
      addXML11Component(this.fXML11NSDocScanner);
      this.fXML11NSDTDValidator = new XML11NSDTDValidator();
      addXML11Component(this.fXML11NSDTDValidator);
      this.f11Initialized = true;
    } 
  }
  
  FeatureState getFeatureState0(String featureId) throws XMLConfigurationException {
    return super.getFeatureState(featureId);
  }
}
