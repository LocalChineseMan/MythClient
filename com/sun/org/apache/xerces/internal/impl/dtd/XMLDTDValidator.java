package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.models.ContentModelValidator;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import java.io.IOException;

public class XMLDTDValidator implements XMLComponent, XMLDocumentFilter, XMLDTDValidatorFilter, RevalidationHandler {
  private static final int TOP_LEVEL_SCOPE = -1;
  
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
  
  protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
  
  protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  private static final String[] RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/balance-syntax-trees" };
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[] { null, null, Boolean.FALSE, Boolean.FALSE };
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager" };
  
  private static final Object[] PROPERTY_DEFAULTS = new Object[] { null, null, null, null, null };
  
  private static final boolean DEBUG_ATTRIBUTES = false;
  
  private static final boolean DEBUG_ELEMENT_CHILDREN = false;
  
  protected ValidationManager fValidationManager = null;
  
  protected final ValidationState fValidationState = new ValidationState();
  
  protected boolean fNamespaces;
  
  protected boolean fValidation;
  
  protected boolean fDTDValidation;
  
  protected boolean fDynamicValidation;
  
  protected boolean fBalanceSyntaxTrees;
  
  protected boolean fWarnDuplicateAttdef;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLGrammarPool fGrammarPool;
  
  protected DTDGrammarBucket fGrammarBucket;
  
  protected XMLLocator fDocLocation;
  
  protected NamespaceContext fNamespaceContext = null;
  
  protected DTDDVFactory fDatatypeValidatorFactory;
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected XMLDocumentSource fDocumentSource;
  
  protected DTDGrammar fDTDGrammar;
  
  protected boolean fSeenDoctypeDecl = false;
  
  private boolean fPerformValidation;
  
  private String fSchemaType;
  
  private final QName fCurrentElement = new QName();
  
  private int fCurrentElementIndex = -1;
  
  private int fCurrentContentSpecType = -1;
  
  private final QName fRootElement = new QName();
  
  private boolean fInCDATASection = false;
  
  private int[] fElementIndexStack = new int[8];
  
  private int[] fContentSpecTypeStack = new int[8];
  
  private QName[] fElementQNamePartsStack = new QName[8];
  
  private QName[] fElementChildren = new QName[32];
  
  private int fElementChildrenLength = 0;
  
  private int[] fElementChildrenOffsetStack = new int[32];
  
  private int fElementDepth = -1;
  
  private boolean fSeenRootElement = false;
  
  private boolean fInElementContent = false;
  
  private XMLElementDecl fTempElementDecl = new XMLElementDecl();
  
  private final XMLAttributeDecl fTempAttDecl = new XMLAttributeDecl();
  
  private final XMLEntityDecl fEntityDecl = new XMLEntityDecl();
  
  private final QName fTempQName = new QName();
  
  private final StringBuffer fBuffer = new StringBuffer();
  
  protected DatatypeValidator fValID;
  
  protected DatatypeValidator fValIDRef;
  
  protected DatatypeValidator fValIDRefs;
  
  protected DatatypeValidator fValENTITY;
  
  protected DatatypeValidator fValENTITIES;
  
  protected DatatypeValidator fValNMTOKEN;
  
  protected DatatypeValidator fValNMTOKENS;
  
  protected DatatypeValidator fValNOTATION;
  
  public XMLDTDValidator() {
    for (int i = 0; i < this.fElementQNamePartsStack.length; i++)
      this.fElementQNamePartsStack[i] = new QName(); 
    this.fGrammarBucket = new DTDGrammarBucket();
  }
  
  DTDGrammarBucket getGrammarBucket() {
    return this.fGrammarBucket;
  }
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    this.fDTDGrammar = null;
    this.fSeenDoctypeDecl = false;
    this.fInCDATASection = false;
    this.fSeenRootElement = false;
    this.fInElementContent = false;
    this.fCurrentElementIndex = -1;
    this.fCurrentContentSpecType = -1;
    this.fRootElement.clear();
    this.fValidationState.resetIDTables();
    this.fGrammarBucket.clear();
    this.fElementDepth = -1;
    this.fElementChildrenLength = 0;
    boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!parser_settings) {
      this.fValidationManager.addValidationState(this.fValidationState);
      return;
    } 
    this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
    this.fDTDValidation = !componentManager.getFeature("http://apache.org/xml/features/validation/schema", false);
    this.fDynamicValidation = componentManager.getFeature("http://apache.org/xml/features/validation/dynamic", false);
    this.fBalanceSyntaxTrees = componentManager.getFeature("http://apache.org/xml/features/validation/balance-syntax-trees", false);
    this.fWarnDuplicateAttdef = componentManager.getFeature("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", false);
    this.fSchemaType = (String)componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
    this.fValidationManager = (ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
    this.fValidationManager.addValidationState(this.fValidationState);
    this.fValidationState.setUsingNamespaces(this.fNamespaces);
    this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
    this.fDatatypeValidatorFactory = (DTDDVFactory)componentManager.getProperty("http://apache.org/xml/properties/internal/datatype-validator-factory");
    init();
  }
  
  public String[] getRecognizedFeatures() {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {}
  
  public String[] getRecognizedProperties() {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public void setProperty(String propertyId, Object value) throws XMLConfigurationException {}
  
  public Boolean getFeatureDefault(String featureId) {
    for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
      if (RECOGNIZED_FEATURES[i].equals(featureId))
        return FEATURE_DEFAULTS[i]; 
    } 
    return null;
  }
  
  public Object getPropertyDefault(String propertyId) {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(propertyId))
        return PROPERTY_DEFAULTS[i]; 
    } 
    return null;
  }
  
  public void setDocumentHandler(XMLDocumentHandler documentHandler) {
    this.fDocumentHandler = documentHandler;
  }
  
  public XMLDocumentHandler getDocumentHandler() {
    return this.fDocumentHandler;
  }
  
  public void setDocumentSource(XMLDocumentSource source) {
    this.fDocumentSource = source;
  }
  
  public XMLDocumentSource getDocumentSource() {
    return this.fDocumentSource;
  }
  
  public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
    if (this.fGrammarPool != null) {
      Grammar[] grammars = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/TR/REC-xml");
      int length = (grammars != null) ? grammars.length : 0;
      for (int i = 0; i < length; i++)
        this.fGrammarBucket.putGrammar((DTDGrammar)grammars[i]); 
    } 
    this.fDocLocation = locator;
    this.fNamespaceContext = namespaceContext;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs); 
  }
  
  public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
    this.fGrammarBucket.setStandalone((standalone != null && standalone.equals("yes")));
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs); 
  }
  
  public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
    this.fSeenDoctypeDecl = true;
    this.fRootElement.setValues(null, rootElement, rootElement, null);
    String eid = null;
    try {
      eid = XMLEntityManager.expandSystemId(systemId, this.fDocLocation.getExpandedSystemId(), false);
    } catch (IOException iOException) {}
    XMLDTDDescription grammarDesc = new XMLDTDDescription(publicId, systemId, this.fDocLocation.getExpandedSystemId(), eid, rootElement);
    this.fDTDGrammar = this.fGrammarBucket.getGrammar(grammarDesc);
    if (this.fDTDGrammar == null)
      if (this.fGrammarPool != null && (systemId != null || publicId != null))
        this.fDTDGrammar = (DTDGrammar)this.fGrammarPool.retrieveGrammar(grammarDesc);  
    if (this.fDTDGrammar == null) {
      if (!this.fBalanceSyntaxTrees) {
        this.fDTDGrammar = new DTDGrammar(this.fSymbolTable, grammarDesc);
      } else {
        this.fDTDGrammar = new BalancedDTDGrammar(this.fSymbolTable, grammarDesc);
      } 
    } else {
      this.fValidationManager.setCachedDTD(true);
    } 
    this.fGrammarBucket.setActiveGrammar(this.fDTDGrammar);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs); 
  }
  
  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
    handleStartElement(element, attributes, augs);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startElement(element, attributes, augs); 
  }
  
  public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
    boolean removed = handleStartElement(element, attributes, augs);
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.emptyElement(element, attributes, augs); 
    if (!removed)
      handleEndElement(element, augs, true); 
  }
  
  public void characters(XMLString text, Augmentations augs) throws XNIException {
    boolean callNextCharacters = true;
    boolean allWhiteSpace = true;
    for (int i = text.offset; i < text.offset + text.length; i++) {
      if (!isSpace(text.ch[i])) {
        allWhiteSpace = false;
        break;
      } 
    } 
    if (this.fInElementContent && allWhiteSpace && !this.fInCDATASection && 
      this.fDocumentHandler != null) {
      this.fDocumentHandler.ignorableWhitespace(text, augs);
      callNextCharacters = false;
    } 
    if (this.fPerformValidation) {
      if (this.fInElementContent) {
        if (this.fGrammarBucket.getStandalone() && this.fDTDGrammar
          .getElementDeclIsExternal(this.fCurrentElementIndex) && 
          allWhiteSpace)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_WHITE_SPACE_IN_ELEMENT_CONTENT_WHEN_STANDALONE", null, (short)1); 
        if (!allWhiteSpace)
          charDataInContent(); 
        if (augs != null && augs.getItem("CHAR_REF_PROBABLE_WS") == Boolean.TRUE)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, this.fDTDGrammar
                
                .getContentSpecAsString(this.fElementDepth), "character reference" }, (short)1); 
      } 
      if (this.fCurrentContentSpecType == 1)
        charDataInContent(); 
    } 
    if (callNextCharacters && this.fDocumentHandler != null)
      this.fDocumentHandler.characters(text, augs); 
  }
  
  public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.ignorableWhitespace(text, augs); 
  }
  
  public void endElement(QName element, Augmentations augs) throws XNIException {
    handleEndElement(element, augs, false);
  }
  
  public void startCDATA(Augmentations augs) throws XNIException {
    if (this.fPerformValidation && this.fInElementContent)
      charDataInContent(); 
    this.fInCDATASection = true;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startCDATA(augs); 
  }
  
  public void endCDATA(Augmentations augs) throws XNIException {
    this.fInCDATASection = false;
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endCDATA(augs); 
  }
  
  public void endDocument(Augmentations augs) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endDocument(augs); 
  }
  
  public void comment(XMLString text, Augmentations augs) throws XNIException {
    if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
      this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
      if (this.fTempElementDecl.type == 1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "comment" }, (short)1); 
    } 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.comment(text, augs); 
  }
  
  public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
    if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
      this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
      if (this.fTempElementDecl.type == 1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "processing instruction" }, (short)1); 
    } 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.processingInstruction(target, data, augs); 
  }
  
  public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
    if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
      this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
      if (this.fTempElementDecl.type == 1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "ENTITY" }, (short)1); 
      if (this.fGrammarBucket.getStandalone())
        XMLDTDLoader.checkStandaloneEntityRef(name, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter); 
    } 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs); 
  }
  
  public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endGeneralEntity(name, augs); 
  }
  
  public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.textDecl(version, encoding, augs); 
  }
  
  public final boolean hasGrammar() {
    return (this.fDTDGrammar != null);
  }
  
  public final boolean validate() {
    return (this.fSchemaType != Constants.NS_XMLSCHEMA && ((!this.fDynamicValidation && this.fValidation) || (this.fDynamicValidation && this.fSeenDoctypeDecl)) && (this.fDTDValidation || this.fSeenDoctypeDecl));
  }
  
  protected void addDTDDefaultAttrsAndValidate(QName elementName, int elementIndex, XMLAttributes attributes) throws XNIException {
    if (elementIndex == -1 || this.fDTDGrammar == null)
      return; 
    int attlistIndex = this.fDTDGrammar.getFirstAttributeDeclIndex(elementIndex);
    while (attlistIndex != -1) {
      this.fDTDGrammar.getAttributeDecl(attlistIndex, this.fTempAttDecl);
      String attPrefix = this.fTempAttDecl.name.prefix;
      String attLocalpart = this.fTempAttDecl.name.localpart;
      String attRawName = this.fTempAttDecl.name.rawname;
      String attType = getAttributeTypeName(this.fTempAttDecl);
      int attDefaultType = this.fTempAttDecl.simpleType.defaultType;
      String attValue = null;
      if (this.fTempAttDecl.simpleType.defaultValue != null)
        attValue = this.fTempAttDecl.simpleType.defaultValue; 
      boolean specified = false;
      boolean required = (attDefaultType == 2);
      boolean cdata = (attType == XMLSymbols.fCDATASymbol);
      if (!cdata || required || attValue != null) {
        int j = attributes.getLength();
        for (int k = 0; k < j; k++) {
          if (attributes.getQName(k) == attRawName) {
            specified = true;
            break;
          } 
        } 
      } 
      if (!specified)
        if (required) {
          if (this.fPerformValidation) {
            Object[] args = { elementName.localpart, attRawName };
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REQUIRED_ATTRIBUTE_NOT_SPECIFIED", args, (short)1);
          } 
        } else if (attValue != null) {
          if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && 
            this.fDTDGrammar.getAttributeDeclIsExternal(attlistIndex)) {
            Object[] args = { elementName.localpart, attRawName };
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DEFAULTED_ATTRIBUTE_NOT_SPECIFIED", args, (short)1);
          } 
          if (this.fNamespaces) {
            int index = attRawName.indexOf(':');
            if (index != -1) {
              attPrefix = attRawName.substring(0, index);
              attPrefix = this.fSymbolTable.addSymbol(attPrefix);
              attLocalpart = attRawName.substring(index + 1);
              attLocalpart = this.fSymbolTable.addSymbol(attLocalpart);
            } 
          } 
          this.fTempQName.setValues(attPrefix, attLocalpart, attRawName, this.fTempAttDecl.name.uri);
          int j = attributes.addAttribute(this.fTempQName, attType, attValue);
        }  
      attlistIndex = this.fDTDGrammar.getNextAttributeDeclIndex(attlistIndex);
    } 
    int attrCount = attributes.getLength();
    for (int i = 0; i < attrCount; i++) {
      String attrRawName = attributes.getQName(i);
      boolean declared = false;
      if (this.fPerformValidation && 
        this.fGrammarBucket.getStandalone()) {
        String nonNormalizedValue = attributes.getNonNormalizedValue(i);
        if (nonNormalizedValue != null) {
          String entityName = getExternalEntityRefInAttrValue(nonNormalizedValue);
          if (entityName != null)
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { entityName }, (short)1); 
        } 
      } 
      int attDefIndex = -1;
      int position = this.fDTDGrammar.getFirstAttributeDeclIndex(elementIndex);
      while (position != -1) {
        this.fDTDGrammar.getAttributeDecl(position, this.fTempAttDecl);
        if (this.fTempAttDecl.name.rawname == attrRawName) {
          attDefIndex = position;
          declared = true;
          break;
        } 
        position = this.fDTDGrammar.getNextAttributeDeclIndex(position);
      } 
      if (!declared) {
        if (this.fPerformValidation) {
          Object[] args = { elementName.rawname, attrRawName };
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_NOT_DECLARED", args, (short)1);
        } 
      } else {
        String type = getAttributeTypeName(this.fTempAttDecl);
        attributes.setType(i, type);
        attributes.getAugmentations(i).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
        boolean changedByNormalization = false;
        String oldValue = attributes.getValue(i);
        String attrValue = oldValue;
        if (attributes.isSpecified(i) && type != XMLSymbols.fCDATASymbol) {
          changedByNormalization = normalizeAttrValue(attributes, i);
          attrValue = attributes.getValue(i);
          if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && changedByNormalization && this.fDTDGrammar
            
            .getAttributeDeclIsExternal(position))
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTVALUE_CHANGED_DURING_NORMALIZATION_WHEN_STANDALONE", new Object[] { attrRawName, oldValue, attrValue }, (short)1); 
        } 
        if (this.fPerformValidation) {
          if (this.fTempAttDecl.simpleType.defaultType == 1) {
            String defaultValue = this.fTempAttDecl.simpleType.defaultValue;
            if (!attrValue.equals(defaultValue)) {
              Object[] args = { elementName.localpart, attrRawName, attrValue, defaultValue };
              this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_FIXED_ATTVALUE_INVALID", args, (short)1);
            } 
          } 
          if (this.fTempAttDecl.simpleType.type == 1 || this.fTempAttDecl.simpleType.type == 2 || this.fTempAttDecl.simpleType.type == 3 || this.fTempAttDecl.simpleType.type == 4 || this.fTempAttDecl.simpleType.type == 5 || this.fTempAttDecl.simpleType.type == 6)
            validateDTDattribute(elementName, attrValue, this.fTempAttDecl); 
        } 
      } 
    } 
  }
  
  protected String getExternalEntityRefInAttrValue(String nonNormalizedValue) {
    int valLength = nonNormalizedValue.length();
    int ampIndex = nonNormalizedValue.indexOf('&');
    while (ampIndex != -1) {
      if (ampIndex + 1 < valLength && nonNormalizedValue
        .charAt(ampIndex + 1) != '#') {
        int semicolonIndex = nonNormalizedValue.indexOf(';', ampIndex + 1);
        String entityName = nonNormalizedValue.substring(ampIndex + 1, semicolonIndex);
        entityName = this.fSymbolTable.addSymbol(entityName);
        int entIndex = this.fDTDGrammar.getEntityDeclIndex(entityName);
        if (entIndex > -1) {
          this.fDTDGrammar.getEntityDecl(entIndex, this.fEntityDecl);
          if (this.fEntityDecl.inExternal || (
            entityName = getExternalEntityRefInAttrValue(this.fEntityDecl.value)) != null)
            return entityName; 
        } 
      } 
      ampIndex = nonNormalizedValue.indexOf('&', ampIndex + 1);
    } 
    return null;
  }
  
  protected void validateDTDattribute(QName element, String attValue, XMLAttributeDecl attributeDecl) throws XNIException {
    boolean bool1;
    boolean found;
    boolean isAlistAttribute;
    String[] enumVals;
    switch (attributeDecl.simpleType.type) {
      case 1:
        bool1 = attributeDecl.simpleType.list;
        try {
          if (bool1) {
            this.fValENTITIES.validate(attValue, this.fValidationState);
            break;
          } 
          this.fValENTITY.validate(attValue, this.fValidationState);
        } catch (InvalidDatatypeValueException ex) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", ex
              .getKey(), ex
              .getArgs(), (short)1);
        } 
        break;
      case 2:
      case 6:
        found = false;
        enumVals = attributeDecl.simpleType.enumeration;
        if (enumVals == null) {
          found = false;
        } else {
          for (int i = 0; i < enumVals.length; i++) {
            if (attValue == enumVals[i] || attValue.equals(enumVals[i])) {
              found = true;
              break;
            } 
          } 
        } 
        if (!found) {
          StringBuffer enumValueString = new StringBuffer();
          if (enumVals != null)
            for (int i = 0; i < enumVals.length; i++)
              enumValueString.append(enumVals[i] + " ");  
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_VALUE_NOT_IN_LIST", new Object[] { attributeDecl.name.rawname, attValue, enumValueString }, (short)1);
        } 
        break;
      case 3:
        try {
          this.fValID.validate(attValue, this.fValidationState);
        } catch (InvalidDatatypeValueException ex) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", ex
              .getKey(), ex
              .getArgs(), (short)1);
        } 
        break;
      case 4:
        isAlistAttribute = attributeDecl.simpleType.list;
        try {
          if (isAlistAttribute) {
            this.fValIDRefs.validate(attValue, this.fValidationState);
            break;
          } 
          this.fValIDRef.validate(attValue, this.fValidationState);
        } catch (InvalidDatatypeValueException ex) {
          if (isAlistAttribute) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IDREFSInvalid", new Object[] { attValue }, (short)1);
            break;
          } 
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", ex
              .getKey(), ex
              .getArgs(), (short)1);
        } 
        break;
      case 5:
        isAlistAttribute = attributeDecl.simpleType.list;
        try {
          if (isAlistAttribute) {
            this.fValNMTOKENS.validate(attValue, this.fValidationState);
            break;
          } 
          this.fValNMTOKEN.validate(attValue, this.fValidationState);
        } catch (InvalidDatatypeValueException ex) {
          if (isAlistAttribute) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NMTOKENSInvalid", new Object[] { attValue }, (short)1);
            break;
          } 
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NMTOKENInvalid", new Object[] { attValue }, (short)1);
        } 
        break;
    } 
  }
  
  protected boolean invalidStandaloneAttDef(QName element, QName attribute) {
    boolean state = true;
    return state;
  }
  
  private boolean normalizeAttrValue(XMLAttributes attributes, int index) {
    boolean leadingSpace = true;
    boolean spaceStart = false;
    boolean readingNonSpace = false;
    int count = 0;
    int eaten = 0;
    String attrValue = attributes.getValue(index);
    char[] attValue = new char[attrValue.length()];
    this.fBuffer.setLength(0);
    attrValue.getChars(0, attrValue.length(), attValue, 0);
    for (int i = 0; i < attValue.length; i++) {
      if (attValue[i] == ' ') {
        if (readingNonSpace) {
          spaceStart = true;
          readingNonSpace = false;
        } 
        if (spaceStart && !leadingSpace) {
          spaceStart = false;
          this.fBuffer.append(attValue[i]);
          count++;
        } else if (leadingSpace || !spaceStart) {
          eaten++;
        } 
      } else {
        readingNonSpace = true;
        spaceStart = false;
        leadingSpace = false;
        this.fBuffer.append(attValue[i]);
        count++;
      } 
    } 
    if (count > 0 && this.fBuffer.charAt(count - 1) == ' ')
      this.fBuffer.setLength(count - 1); 
    String newValue = this.fBuffer.toString();
    attributes.setValue(index, newValue);
    return !attrValue.equals(newValue);
  }
  
  private final void rootElementSpecified(QName rootElement) throws XNIException {
    if (this.fPerformValidation) {
      String root1 = this.fRootElement.rawname;
      String root2 = rootElement.rawname;
      if (root1 == null || !root1.equals(root2))
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { root1, root2 }, (short)1); 
    } 
  }
  
  private int checkContent(int elementIndex, QName[] children, int childOffset, int childCount) throws XNIException {
    this.fDTDGrammar.getElementDecl(elementIndex, this.fTempElementDecl);
    String elementType = this.fCurrentElement.rawname;
    int contentType = this.fCurrentContentSpecType;
    if (contentType == 1) {
      if (childCount != 0)
        return 0; 
    } else if (contentType != 0) {
      if (contentType == 2 || contentType == 3) {
        ContentModelValidator cmElem = null;
        cmElem = this.fTempElementDecl.contentModelValidator;
        int result = cmElem.validate(children, childOffset, childCount);
        return result;
      } 
      if (contentType != -1)
        if (contentType == 4); 
    } 
    return -1;
  }
  
  private int getContentSpecType(int elementIndex) {
    int contentSpecType = -1;
    if (elementIndex > -1 && 
      this.fDTDGrammar.getElementDecl(elementIndex, this.fTempElementDecl))
      contentSpecType = this.fTempElementDecl.type; 
    return contentSpecType;
  }
  
  private void charDataInContent() {
    if (this.fElementChildren.length <= this.fElementChildrenLength) {
      QName[] newarray = new QName[this.fElementChildren.length * 2];
      System.arraycopy(this.fElementChildren, 0, newarray, 0, this.fElementChildren.length);
      this.fElementChildren = newarray;
    } 
    QName qname = this.fElementChildren[this.fElementChildrenLength];
    if (qname == null) {
      for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; i++)
        this.fElementChildren[i] = new QName(); 
      qname = this.fElementChildren[this.fElementChildrenLength];
    } 
    qname.clear();
    this.fElementChildrenLength++;
  }
  
  private String getAttributeTypeName(XMLAttributeDecl attrDecl) {
    StringBuffer buffer;
    int i;
    switch (attrDecl.simpleType.type) {
      case 1:
        return attrDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
      case 2:
        buffer = new StringBuffer();
        buffer.append('(');
        for (i = 0; i < attrDecl.simpleType.enumeration.length; i++) {
          if (i > 0)
            buffer.append('|'); 
          buffer.append(attrDecl.simpleType.enumeration[i]);
        } 
        buffer.append(')');
        return this.fSymbolTable.addSymbol(buffer.toString());
      case 3:
        return XMLSymbols.fIDSymbol;
      case 4:
        return attrDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
      case 5:
        return attrDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
      case 6:
        return XMLSymbols.fNOTATIONSymbol;
    } 
    return XMLSymbols.fCDATASymbol;
  }
  
  protected void init() {
    if (this.fValidation || this.fDynamicValidation)
      try {
        this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDSymbol);
        this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSymbol);
        this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSSymbol);
        this.fValENTITY = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITYSymbol);
        this.fValENTITIES = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITIESSymbol);
        this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSymbol);
        this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSSymbol);
        this.fValNOTATION = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNOTATIONSymbol);
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }  
  }
  
  private void ensureStackCapacity(int newElementDepth) {
    if (newElementDepth == this.fElementQNamePartsStack.length) {
      QName[] newStackOfQueue = new QName[newElementDepth * 2];
      System.arraycopy(this.fElementQNamePartsStack, 0, newStackOfQueue, 0, newElementDepth);
      this.fElementQNamePartsStack = newStackOfQueue;
      QName qname = this.fElementQNamePartsStack[newElementDepth];
      if (qname == null)
        for (int i = newElementDepth; i < this.fElementQNamePartsStack.length; i++)
          this.fElementQNamePartsStack[i] = new QName();  
      int[] newStack = new int[newElementDepth * 2];
      System.arraycopy(this.fElementIndexStack, 0, newStack, 0, newElementDepth);
      this.fElementIndexStack = newStack;
      newStack = new int[newElementDepth * 2];
      System.arraycopy(this.fContentSpecTypeStack, 0, newStack, 0, newElementDepth);
      this.fContentSpecTypeStack = newStack;
    } 
  }
  
  protected boolean handleStartElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
    if (!this.fSeenRootElement) {
      this.fPerformValidation = validate();
      this.fSeenRootElement = true;
      this.fValidationManager.setEntityState(this.fDTDGrammar);
      this.fValidationManager.setGrammarFound(this.fSeenDoctypeDecl);
      rootElementSpecified(element);
    } 
    if (this.fDTDGrammar == null) {
      if (!this.fPerformValidation) {
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fInElementContent = false;
      } 
      if (this.fPerformValidation)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { element.rawname }, (short)1); 
      if (this.fDocumentSource != null) {
        this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
        if (this.fDocumentHandler != null)
          this.fDocumentHandler.setDocumentSource(this.fDocumentSource); 
        return true;
      } 
    } else {
      this.fCurrentElementIndex = this.fDTDGrammar.getElementDeclIndex(element);
      this.fCurrentContentSpecType = this.fDTDGrammar.getContentSpecType(this.fCurrentElementIndex);
      if (this.fCurrentContentSpecType == -1 && this.fPerformValidation)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_NOT_DECLARED", new Object[] { element.rawname }, (short)1); 
      addDTDDefaultAttrsAndValidate(element, this.fCurrentElementIndex, attributes);
    } 
    this.fInElementContent = (this.fCurrentContentSpecType == 3);
    this.fElementDepth++;
    if (this.fPerformValidation) {
      if (this.fElementChildrenOffsetStack.length <= this.fElementDepth) {
        int[] newarray = new int[this.fElementChildrenOffsetStack.length * 2];
        System.arraycopy(this.fElementChildrenOffsetStack, 0, newarray, 0, this.fElementChildrenOffsetStack.length);
        this.fElementChildrenOffsetStack = newarray;
      } 
      this.fElementChildrenOffsetStack[this.fElementDepth] = this.fElementChildrenLength;
      if (this.fElementChildren.length <= this.fElementChildrenLength) {
        QName[] newarray = new QName[this.fElementChildrenLength * 2];
        System.arraycopy(this.fElementChildren, 0, newarray, 0, this.fElementChildren.length);
        this.fElementChildren = newarray;
      } 
      QName qname = this.fElementChildren[this.fElementChildrenLength];
      if (qname == null) {
        for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; i++)
          this.fElementChildren[i] = new QName(); 
        qname = this.fElementChildren[this.fElementChildrenLength];
      } 
      qname.setValues(element);
      this.fElementChildrenLength++;
    } 
    this.fCurrentElement.setValues(element);
    ensureStackCapacity(this.fElementDepth);
    this.fElementQNamePartsStack[this.fElementDepth].setValues(this.fCurrentElement);
    this.fElementIndexStack[this.fElementDepth] = this.fCurrentElementIndex;
    this.fContentSpecTypeStack[this.fElementDepth] = this.fCurrentContentSpecType;
    startNamespaceScope(element, attributes, augs);
    return false;
  }
  
  protected void startNamespaceScope(QName element, XMLAttributes attributes, Augmentations augs) {}
  
  protected void handleEndElement(QName element, Augmentations augs, boolean isEmpty) throws XNIException {
    this.fElementDepth--;
    if (this.fPerformValidation) {
      int elementIndex = this.fCurrentElementIndex;
      if (elementIndex != -1 && this.fCurrentContentSpecType != -1) {
        QName[] children = this.fElementChildren;
        int childrenOffset = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1;
        int childrenLength = this.fElementChildrenLength - childrenOffset;
        int result = checkContent(elementIndex, children, childrenOffset, childrenLength);
        if (result != -1) {
          this.fDTDGrammar.getElementDecl(elementIndex, this.fTempElementDecl);
          if (this.fTempElementDecl.type == 1) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID", new Object[] { element.rawname, "EMPTY" }, (short)1);
          } else {
            String messageKey = (result != childrenLength) ? "MSG_CONTENT_INVALID" : "MSG_CONTENT_INCOMPLETE";
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", messageKey, new Object[] { element.rawname, this.fDTDGrammar
                  
                  .getContentSpecAsString(elementIndex) }, (short)1);
          } 
        } 
      } 
      this.fElementChildrenLength = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1;
    } 
    endNamespaceScope(this.fCurrentElement, augs, isEmpty);
    if (this.fElementDepth < -1)
      throw new RuntimeException("FWK008 Element stack underflow"); 
    if (this.fElementDepth < 0) {
      this.fCurrentElement.clear();
      this.fCurrentElementIndex = -1;
      this.fCurrentContentSpecType = -1;
      this.fInElementContent = false;
      if (this.fPerformValidation) {
        String value = this.fValidationState.checkIDRefID();
        if (value != null)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_WITH_ID_REQUIRED", new Object[] { value }, (short)1); 
      } 
      return;
    } 
    this.fCurrentElement.setValues(this.fElementQNamePartsStack[this.fElementDepth]);
    this.fCurrentElementIndex = this.fElementIndexStack[this.fElementDepth];
    this.fCurrentContentSpecType = this.fContentSpecTypeStack[this.fElementDepth];
    this.fInElementContent = (this.fCurrentContentSpecType == 3);
  }
  
  protected void endNamespaceScope(QName element, Augmentations augs, boolean isEmpty) {
    if (this.fDocumentHandler != null && !isEmpty)
      this.fDocumentHandler.endElement(this.fCurrentElement, augs); 
  }
  
  protected boolean isSpace(int c) {
    return XMLChar.isSpace(c);
  }
  
  public boolean characterData(String data, Augmentations augs) {
    characters(new XMLString(data.toCharArray(), 0, data.length()), augs);
    return true;
  }
}
