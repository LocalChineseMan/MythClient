package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class XMLDTDProcessor implements XMLComponent, XMLDTDFilter, XMLDTDContentModelFilter {
  private static final int TOP_LEVEL_SCOPE = -1;
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  
  protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
  
  protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
  
  private static final String[] RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/scanner/notify-char-refs" };
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[] { null, Boolean.FALSE, Boolean.FALSE, null };
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/validator/dtd" };
  
  private static final Object[] PROPERTY_DEFAULTS = new Object[] { null, null, null, null };
  
  protected boolean fValidation;
  
  protected boolean fDTDValidation;
  
  protected boolean fWarnDuplicateAttdef;
  
  protected boolean fWarnOnUndeclaredElemdef;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected DTDGrammarBucket fGrammarBucket;
  
  protected XMLDTDValidator fValidator;
  
  protected XMLGrammarPool fGrammarPool;
  
  protected Locale fLocale;
  
  protected XMLDTDHandler fDTDHandler;
  
  protected XMLDTDSource fDTDSource;
  
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  
  protected XMLDTDContentModelSource fDTDContentModelSource;
  
  protected DTDGrammar fDTDGrammar;
  
  private boolean fPerformValidation;
  
  protected boolean fInDTDIgnore;
  
  private boolean fMixed;
  
  private final XMLEntityDecl fEntityDecl = new XMLEntityDecl();
  
  private final HashMap fNDataDeclNotations = new HashMap<>();
  
  private String fDTDElementDeclName = null;
  
  private final ArrayList fMixedElementTypes = new ArrayList();
  
  private final ArrayList fDTDElementDecls = new ArrayList();
  
  private HashMap fTableOfIDAttributeNames;
  
  private HashMap fTableOfNOTATIONAttributeNames;
  
  private HashMap fNotationEnumVals;
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!parser_settings) {
      reset();
      return;
    } 
    this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
    this
      
      .fDTDValidation = !componentManager.getFeature("http://apache.org/xml/features/validation/schema", false);
    this.fWarnDuplicateAttdef = componentManager.getFeature("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", false);
    this.fWarnOnUndeclaredElemdef = componentManager.getFeature("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", false);
    this
      .fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this
      .fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
    try {
      this.fValidator = (XMLDTDValidator)componentManager.getProperty("http://apache.org/xml/properties/internal/validator/dtd", null);
    } catch (ClassCastException e) {
      this.fValidator = null;
    } 
    if (this.fValidator != null) {
      this.fGrammarBucket = this.fValidator.getGrammarBucket();
    } else {
      this.fGrammarBucket = null;
    } 
    reset();
  }
  
  protected void reset() {
    this.fDTDGrammar = null;
    this.fInDTDIgnore = false;
    this.fNDataDeclNotations.clear();
    if (this.fValidation) {
      if (this.fNotationEnumVals == null)
        this.fNotationEnumVals = new HashMap<>(); 
      this.fNotationEnumVals.clear();
      this.fTableOfIDAttributeNames = new HashMap<>();
      this.fTableOfNOTATIONAttributeNames = new HashMap<>();
    } 
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
  
  public void setDTDHandler(XMLDTDHandler dtdHandler) {
    this.fDTDHandler = dtdHandler;
  }
  
  public XMLDTDHandler getDTDHandler() {
    return this.fDTDHandler;
  }
  
  public void setDTDContentModelHandler(XMLDTDContentModelHandler dtdContentModelHandler) {
    this.fDTDContentModelHandler = dtdContentModelHandler;
  }
  
  public XMLDTDContentModelHandler getDTDContentModelHandler() {
    return this.fDTDContentModelHandler;
  }
  
  public void startExternalSubset(XMLResourceIdentifier identifier, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.startExternalSubset(identifier, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.startExternalSubset(identifier, augs); 
  }
  
  public void endExternalSubset(Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.endExternalSubset(augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.endExternalSubset(augs); 
  }
  
  protected static void checkStandaloneEntityRef(String name, DTDGrammar grammar, XMLEntityDecl tempEntityDecl, XMLErrorReporter errorReporter) throws XNIException {
    int entIndex = grammar.getEntityDeclIndex(name);
    if (entIndex > -1) {
      grammar.getEntityDecl(entIndex, tempEntityDecl);
      if (tempEntityDecl.inExternal)
        errorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { name }, (short)1); 
    } 
  }
  
  public void comment(XMLString text, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.comment(text, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.comment(text, augs); 
  }
  
  public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.processingInstruction(target, data, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.processingInstruction(target, data, augs); 
  }
  
  public void startDTD(XMLLocator locator, Augmentations augs) throws XNIException {
    this.fNDataDeclNotations.clear();
    this.fDTDElementDecls.clear();
    if (!this.fGrammarBucket.getActiveGrammar().isImmutable())
      this.fDTDGrammar = this.fGrammarBucket.getActiveGrammar(); 
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.startDTD(locator, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.startDTD(locator, augs); 
  }
  
  public void ignoredCharacters(XMLString text, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.ignoredCharacters(text, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.ignoredCharacters(text, augs); 
  }
  
  public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.textDecl(version, encoding, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.textDecl(version, encoding, augs); 
  }
  
  public void startParameterEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
    if (this.fPerformValidation && this.fDTDGrammar != null && this.fGrammarBucket
      .getStandalone())
      checkStandaloneEntityRef(name, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter); 
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.startParameterEntity(name, identifier, encoding, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.startParameterEntity(name, identifier, encoding, augs); 
  }
  
  public void endParameterEntity(String name, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.endParameterEntity(name, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.endParameterEntity(name, augs); 
  }
  
  public void elementDecl(String name, String contentModel, Augmentations augs) throws XNIException {
    if (this.fValidation)
      if (this.fDTDElementDecls.contains(name)) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_ALREADY_DECLARED", new Object[] { name }, (short)1);
      } else {
        this.fDTDElementDecls.add(name);
      }  
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.elementDecl(name, contentModel, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.elementDecl(name, contentModel, augs); 
  }
  
  public void startAttlist(String elementName, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.startAttlist(elementName, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.startAttlist(elementName, augs); 
  }
  
  public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augs) throws XNIException {
    if (type != XMLSymbols.fCDATASymbol && defaultValue != null)
      normalizeDefaultAttrValue(defaultValue); 
    if (this.fValidation) {
      boolean duplicateAttributeDef = false;
      DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
      int elementIndex = grammar.getElementDeclIndex(elementName);
      if (grammar.getAttributeDeclIndex(elementIndex, attributeName) != -1) {
        duplicateAttributeDef = true;
        if (this.fWarnDuplicateAttdef)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ATTRIBUTE_DEFINITION", new Object[] { elementName, attributeName }, (short)0); 
      } 
      if (type == XMLSymbols.fIDSymbol) {
        if (defaultValue != null && defaultValue.length != 0 && (
          defaultType == null || (defaultType != XMLSymbols.fIMPLIEDSymbol && defaultType != XMLSymbols.fREQUIREDSymbol)))
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IDDefaultTypeInvalid", new Object[] { attributeName }, (short)1); 
        if (!this.fTableOfIDAttributeNames.containsKey(elementName)) {
          this.fTableOfIDAttributeNames.put(elementName, attributeName);
        } else if (!duplicateAttributeDef) {
          String previousIDAttributeName = (String)this.fTableOfIDAttributeNames.get(elementName);
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_ID_ATTRIBUTE", new Object[] { elementName, previousIDAttributeName, attributeName }, (short)1);
        } 
      } 
      if (type == XMLSymbols.fNOTATIONSymbol) {
        for (int i = 0; i < enumeration.length; i++)
          this.fNotationEnumVals.put(enumeration[i], attributeName); 
        if (!this.fTableOfNOTATIONAttributeNames.containsKey(elementName)) {
          this.fTableOfNOTATIONAttributeNames.put(elementName, attributeName);
        } else if (!duplicateAttributeDef) {
          String previousNOTATIONAttributeName = (String)this.fTableOfNOTATIONAttributeNames.get(elementName);
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_NOTATION_ATTRIBUTE", new Object[] { elementName, previousNOTATIONAttributeName, attributeName }, (short)1);
        } 
      } 
      if (type == XMLSymbols.fENUMERATIONSymbol || type == XMLSymbols.fNOTATIONSymbol)
        for (int i = 0; i < enumeration.length; i++) {
          for (int j = i + 1; j < enumeration.length; j++) {
            if (enumeration[i].equals(enumeration[j])) {
              this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", (type == XMLSymbols.fENUMERATIONSymbol) ? "MSG_DISTINCT_TOKENS_IN_ENUMERATION" : "MSG_DISTINCT_NOTATION_IN_ENUMERATION", new Object[] { elementName, enumeration[i], attributeName }, (short)1);
              // Byte code: goto -> 477
            } 
          } 
        }  
      boolean ok = true;
      if (defaultValue != null && (defaultType == null || (defaultType != null && defaultType == XMLSymbols.fFIXEDSymbol))) {
        String value = defaultValue.toString();
        if (type == XMLSymbols.fNMTOKENSSymbol || type == XMLSymbols.fENTITIESSymbol || type == XMLSymbols.fIDREFSSymbol) {
          StringTokenizer tokenizer = new StringTokenizer(value, " ");
          if (tokenizer.hasMoreTokens())
            do {
              String nmtoken = tokenizer.nextToken();
              if (type == XMLSymbols.fNMTOKENSSymbol) {
                if (!isValidNmtoken(nmtoken)) {
                  ok = false;
                  break;
                } 
              } else if (type == XMLSymbols.fENTITIESSymbol || type == XMLSymbols.fIDREFSSymbol) {
                if (!isValidName(nmtoken)) {
                  ok = false;
                  break;
                } 
              } 
            } while (tokenizer.hasMoreTokens()); 
        } else {
          if (type == XMLSymbols.fENTITYSymbol || type == XMLSymbols.fIDSymbol || type == XMLSymbols.fIDREFSymbol || type == XMLSymbols.fNOTATIONSymbol) {
            if (!isValidName(value))
              ok = false; 
          } else if (type == XMLSymbols.fNMTOKENSymbol || type == XMLSymbols.fENUMERATIONSymbol) {
            if (!isValidNmtoken(value))
              ok = false; 
          } 
          if (type == XMLSymbols.fNOTATIONSymbol || type == XMLSymbols.fENUMERATIONSymbol) {
            ok = false;
            for (int i = 0; i < enumeration.length; i++) {
              if (defaultValue.equals(enumeration[i]))
                ok = true; 
            } 
          } 
        } 
        if (!ok)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATT_DEFAULT_INVALID", new Object[] { attributeName, value }, (short)1); 
      } 
    } 
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augs); 
  }
  
  public void endAttlist(Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.endAttlist(augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.endAttlist(augs); 
  }
  
  public void internalEntityDecl(String name, XMLString text, XMLString nonNormalizedText, Augmentations augs) throws XNIException {
    DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
    int index = grammar.getEntityDeclIndex(name);
    if (index == -1) {
      if (this.fDTDGrammar != null)
        this.fDTDGrammar.internalEntityDecl(name, text, nonNormalizedText, augs); 
      if (this.fDTDHandler != null)
        this.fDTDHandler.internalEntityDecl(name, text, nonNormalizedText, augs); 
    } 
  }
  
  public void externalEntityDecl(String name, XMLResourceIdentifier identifier, Augmentations augs) throws XNIException {
    DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
    int index = grammar.getEntityDeclIndex(name);
    if (index == -1) {
      if (this.fDTDGrammar != null)
        this.fDTDGrammar.externalEntityDecl(name, identifier, augs); 
      if (this.fDTDHandler != null)
        this.fDTDHandler.externalEntityDecl(name, identifier, augs); 
    } 
  }
  
  public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier, String notation, Augmentations augs) throws XNIException {
    if (this.fValidation)
      this.fNDataDeclNotations.put(name, notation); 
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.unparsedEntityDecl(name, identifier, notation, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.unparsedEntityDecl(name, identifier, notation, augs); 
  }
  
  public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augs) throws XNIException {
    if (this.fValidation) {
      DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
      if (grammar.getNotationDeclIndex(name) != -1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UniqueNotationName", new Object[] { name }, (short)1); 
    } 
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.notationDecl(name, identifier, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.notationDecl(name, identifier, augs); 
  }
  
  public void startConditional(short type, Augmentations augs) throws XNIException {
    this.fInDTDIgnore = (type == 1);
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.startConditional(type, augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.startConditional(type, augs); 
  }
  
  public void endConditional(Augmentations augs) throws XNIException {
    this.fInDTDIgnore = false;
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.endConditional(augs); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.endConditional(augs); 
  }
  
  public void endDTD(Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null) {
      this.fDTDGrammar.endDTD(augs);
      if (this.fGrammarPool != null)
        this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[] { this.fDTDGrammar }); 
    } 
    if (this.fValidation) {
      DTDGrammar grammar = (this.fDTDGrammar != null) ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
      Iterator<Map.Entry> entities = this.fNDataDeclNotations.entrySet().iterator();
      while (entities.hasNext()) {
        Map.Entry entry = entities.next();
        String notation = (String)entry.getValue();
        if (grammar.getNotationDeclIndex(notation) == -1) {
          String entity = (String)entry.getKey();
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_UNPARSED_ENTITYDECL", new Object[] { entity, notation }, (short)1);
        } 
      } 
      Iterator<Map.Entry> notationVals = this.fNotationEnumVals.entrySet().iterator();
      while (notationVals.hasNext()) {
        Map.Entry entry = notationVals.next();
        String notation = (String)entry.getKey();
        if (grammar.getNotationDeclIndex(notation) == -1) {
          String attributeName = (String)entry.getValue();
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_NOTATIONTYPE_ATTRIBUTE", new Object[] { attributeName, notation }, (short)1);
        } 
      } 
      Iterator<Map.Entry> elementsWithNotations = this.fTableOfNOTATIONAttributeNames.entrySet().iterator();
      while (elementsWithNotations.hasNext()) {
        Map.Entry entry = elementsWithNotations.next();
        String elementName = (String)entry.getKey();
        int elementIndex = grammar.getElementDeclIndex(elementName);
        if (grammar.getContentSpecType(elementIndex) == 1) {
          String attributeName = (String)entry.getValue();
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NoNotationOnEmptyElement", new Object[] { elementName, attributeName }, (short)1);
        } 
      } 
      this.fTableOfIDAttributeNames = null;
      this.fTableOfNOTATIONAttributeNames = null;
      if (this.fWarnOnUndeclaredElemdef)
        checkDeclaredElements(grammar); 
    } 
    if (this.fDTDHandler != null)
      this.fDTDHandler.endDTD(augs); 
  }
  
  public void setDTDSource(XMLDTDSource source) {
    this.fDTDSource = source;
  }
  
  public XMLDTDSource getDTDSource() {
    return this.fDTDSource;
  }
  
  public void setDTDContentModelSource(XMLDTDContentModelSource source) {
    this.fDTDContentModelSource = source;
  }
  
  public XMLDTDContentModelSource getDTDContentModelSource() {
    return this.fDTDContentModelSource;
  }
  
  public void startContentModel(String elementName, Augmentations augs) throws XNIException {
    if (this.fValidation) {
      this.fDTDElementDeclName = elementName;
      this.fMixedElementTypes.clear();
    } 
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.startContentModel(elementName, augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.startContentModel(elementName, augs); 
  }
  
  public void any(Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.any(augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.any(augs); 
  }
  
  public void empty(Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.empty(augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.empty(augs); 
  }
  
  public void startGroup(Augmentations augs) throws XNIException {
    this.fMixed = false;
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.startGroup(augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.startGroup(augs); 
  }
  
  public void pcdata(Augmentations augs) {
    this.fMixed = true;
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.pcdata(augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.pcdata(augs); 
  }
  
  public void element(String elementName, Augmentations augs) throws XNIException {
    if (this.fMixed && this.fValidation)
      if (this.fMixedElementTypes.contains(elementName)) {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "DuplicateTypeInMixedContent", new Object[] { this.fDTDElementDeclName, elementName }, (short)1);
      } else {
        this.fMixedElementTypes.add(elementName);
      }  
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.element(elementName, augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.element(elementName, augs); 
  }
  
  public void separator(short separator, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.separator(separator, augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.separator(separator, augs); 
  }
  
  public void occurrence(short occurrence, Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.occurrence(occurrence, augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.occurrence(occurrence, augs); 
  }
  
  public void endGroup(Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.endGroup(augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.endGroup(augs); 
  }
  
  public void endContentModel(Augmentations augs) throws XNIException {
    if (this.fDTDGrammar != null)
      this.fDTDGrammar.endContentModel(augs); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.endContentModel(augs); 
  }
  
  private boolean normalizeDefaultAttrValue(XMLString value) {
    boolean skipSpace = true;
    int current = value.offset;
    int end = value.offset + value.length;
    for (int i = value.offset; i < end; i++) {
      if (value.ch[i] == ' ') {
        if (!skipSpace) {
          value.ch[current++] = ' ';
          skipSpace = true;
        } 
      } else {
        if (current != i)
          value.ch[current] = value.ch[i]; 
        current++;
        skipSpace = false;
      } 
    } 
    if (current != end) {
      if (skipSpace)
        current--; 
      value.length = current - value.offset;
      return true;
    } 
    return false;
  }
  
  protected boolean isValidNmtoken(String nmtoken) {
    return XMLChar.isValidNmtoken(nmtoken);
  }
  
  protected boolean isValidName(String name) {
    return XMLChar.isValidName(name);
  }
  
  private void checkDeclaredElements(DTDGrammar grammar) {
    int elementIndex = grammar.getFirstElementDeclIndex();
    XMLContentSpec contentSpec = new XMLContentSpec();
    while (elementIndex >= 0) {
      int type = grammar.getContentSpecType(elementIndex);
      if (type == 3 || type == 2)
        checkDeclaredElements(grammar, elementIndex, grammar
            
            .getContentSpecIndex(elementIndex), contentSpec); 
      elementIndex = grammar.getNextElementDeclIndex(elementIndex);
    } 
  }
  
  private void checkDeclaredElements(DTDGrammar grammar, int elementIndex, int contentSpecIndex, XMLContentSpec contentSpec) {
    grammar.getContentSpec(contentSpecIndex, contentSpec);
    if (contentSpec.type == 0) {
      String value = (String)contentSpec.value;
      if (value != null && grammar.getElementDeclIndex(value) == -1)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UndeclaredElementInContentSpec", new Object[] { (grammar.getElementDeclName(elementIndex)).rawname, value }, (short)0); 
    } else if (contentSpec.type == 4 || contentSpec.type == 5) {
      int leftNode = ((int[])contentSpec.value)[0];
      int rightNode = ((int[])contentSpec.otherValue)[0];
      checkDeclaredElements(grammar, elementIndex, leftNode, contentSpec);
      checkDeclaredElements(grammar, elementIndex, rightNode, contentSpec);
    } else if (contentSpec.type == 2 || contentSpec.type == 1 || contentSpec.type == 3) {
      int leftNode = ((int[])contentSpec.value)[0];
      checkDeclaredElements(grammar, elementIndex, leftNode, contentSpec);
    } 
  }
}
