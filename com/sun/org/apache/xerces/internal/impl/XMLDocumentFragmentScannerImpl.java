package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.XMLBufferListener;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.EOFException;
import java.io.IOException;

public class XMLDocumentFragmentScannerImpl extends XMLScanner implements XMLDocumentScanner, XMLComponent, XMLEntityHandler, XMLBufferListener {
  protected int fElementAttributeLimit;
  
  protected ExternalSubsetResolver fExternalSubsetResolver;
  
  protected static final int SCANNER_STATE_START_OF_MARKUP = 21;
  
  protected static final int SCANNER_STATE_CONTENT = 22;
  
  protected static final int SCANNER_STATE_PI = 23;
  
  protected static final int SCANNER_STATE_DOCTYPE = 24;
  
  protected static final int SCANNER_STATE_XML_DECL = 25;
  
  protected static final int SCANNER_STATE_ROOT_ELEMENT = 26;
  
  protected static final int SCANNER_STATE_COMMENT = 27;
  
  protected static final int SCANNER_STATE_REFERENCE = 28;
  
  protected static final int SCANNER_STATE_ATTRIBUTE = 29;
  
  protected static final int SCANNER_STATE_ATTRIBUTE_VALUE = 30;
  
  protected static final int SCANNER_STATE_END_OF_INPUT = 33;
  
  protected static final int SCANNER_STATE_TERMINATED = 34;
  
  protected static final int SCANNER_STATE_CDATA = 35;
  
  protected static final int SCANNER_STATE_TEXT_DECL = 36;
  
  protected static final int SCANNER_STATE_CHARACTER_DATA = 37;
  
  protected static final int SCANNER_STATE_START_ELEMENT_TAG = 38;
  
  protected static final int SCANNER_STATE_END_ELEMENT_TAG = 39;
  
  protected static final int SCANNER_STATE_CHAR_REFERENCE = 40;
  
  protected static final int SCANNER_STATE_BUILT_IN_REFS = 41;
  
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  static final String EXTERNAL_ACCESS_DEFAULT = "all";
  
  private static final String[] RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "report-cdata-event" };
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[] { Boolean.TRUE, null, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE };
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  
  private static final Object[] PROPERTY_DEFAULTS = new Object[] { null, null, null, "all" };
  
  private static final char[] cdata = new char[] { '[', 'C', 'D', 'A', 'T', 'A', '[' };
  
  static final char[] xmlDecl = new char[] { '<', '?', 'x', 'm', 'l' };
  
  private static final char[] endTag = new char[] { '<', '/' };
  
  private static final boolean DEBUG_SCANNER_STATE = false;
  
  private static final boolean DEBUG_DISPATCHER = false;
  
  protected static final boolean DEBUG_START_END_ELEMENT = false;
  
  protected static final boolean DEBUG_NEXT = false;
  
  protected static final boolean DEBUG = false;
  
  protected static final boolean DEBUG_COALESCE = false;
  
  protected XMLDocumentHandler fDocumentHandler;
  
  protected int fScannerLastState;
  
  protected XMLEntityStorage fEntityStore;
  
  protected int[] fEntityStack = new int[4];
  
  protected int fMarkupDepth;
  
  protected boolean fEmptyElement;
  
  protected boolean fReadingAttributes = false;
  
  protected int fScannerState;
  
  protected boolean fInScanContent = false;
  
  protected boolean fLastSectionWasCData = false;
  
  protected boolean fLastSectionWasEntityReference = false;
  
  protected boolean fLastSectionWasCharacterData = false;
  
  protected boolean fHasExternalDTD;
  
  protected boolean fStandaloneSet;
  
  protected boolean fStandalone;
  
  protected String fVersion;
  
  protected QName fCurrentElement;
  
  protected ElementStack fElementStack = new ElementStack();
  
  protected ElementStack2 fElementStack2 = new ElementStack2();
  
  protected String fPITarget;
  
  protected XMLString fPIData = new XMLString();
  
  protected boolean fNotifyBuiltInRefs = false;
  
  protected boolean fSupportDTD = true;
  
  protected boolean fReplaceEntityReferences = true;
  
  protected boolean fSupportExternalEntities = false;
  
  protected boolean fReportCdataEvent = false;
  
  protected boolean fIsCoalesce = false;
  
  protected String fDeclaredEncoding = null;
  
  protected boolean fDisallowDoctype = false;
  
  protected String fAccessExternalDTD = "all";
  
  protected boolean fStrictURI;
  
  protected Driver fDriver;
  
  protected Driver fContentDriver = createContentDriver();
  
  protected QName fElementQName = new QName();
  
  protected QName fAttributeQName = new QName();
  
  protected XMLAttributesIteratorImpl fAttributes = new XMLAttributesIteratorImpl();
  
  protected XMLString fTempString = new XMLString();
  
  protected XMLString fTempString2 = new XMLString();
  
  private String[] fStrings = new String[3];
  
  protected XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  protected XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  
  protected XMLStringBuffer fContentBuffer = new XMLStringBuffer();
  
  private final char[] fSingleChar = new char[1];
  
  private String fCurrentEntityName = null;
  
  protected boolean fScanToEnd = false;
  
  protected DTDGrammarUtil dtdGrammarUtil = null;
  
  protected boolean fAddDefaultAttr = false;
  
  protected boolean foundBuiltInRefs = false;
  
  static final short MAX_DEPTH_LIMIT = 5;
  
  static final short ELEMENT_ARRAY_LENGTH = 200;
  
  static final short MAX_POINTER_AT_A_DEPTH = 4;
  
  static final boolean DEBUG_SKIP_ALGORITHM = false;
  
  String[] fElementArray = new String[200];
  
  short fLastPointerLocation = 0;
  
  short fElementPointer = 0;
  
  short[][] fPointerInfo = new short[5][4];
  
  protected String fElementRawname;
  
  protected boolean fShouldSkip = false;
  
  protected boolean fAdd = false;
  
  protected boolean fSkip = false;
  
  private Augmentations fTempAugmentations = null;
  
  protected boolean fUsebuffer;
  
  public void setInputSource(XMLInputSource inputSource) throws IOException {
    this.fEntityManager.setEntityHandler((XMLEntityHandler)this);
    this.fEntityManager.startEntity("$fragment$", inputSource, false, true);
  }
  
  public boolean scanDocument(boolean complete) throws IOException, XNIException {
    this.fEntityManager.setEntityHandler((XMLEntityHandler)this);
    int event = next();
    do {
      switch (event) {
        case 7:
        case 1:
          break;
        case 4:
          this.fDocumentHandler.characters(getCharacterData(), null);
          break;
        case 6:
        case 9:
          break;
        case 3:
          this.fDocumentHandler.processingInstruction(getPITarget(), getPIData(), null);
          break;
        case 5:
          this.fDocumentHandler.comment(getCharacterData(), null);
          break;
        case 11:
          break;
        case 12:
          this.fDocumentHandler.startCDATA(null);
          this.fDocumentHandler.characters(getCharacterData(), null);
          this.fDocumentHandler.endCDATA(null);
          break;
        case 14:
        case 15:
        case 13:
        case 10:
        case 2:
          break;
        default:
          throw new InternalError("processing event: " + event);
      } 
      event = next();
    } while (event != 8 && complete);
    if (event == 8) {
      this.fDocumentHandler.endDocument(null);
      return false;
    } 
    return true;
  }
  
  public QName getElementQName() {
    if (this.fScannerLastState == 2)
      this.fElementQName.setValues(this.fElementStack.getLastPoppedElement()); 
    return this.fElementQName;
  }
  
  public int next() throws IOException, XNIException {
    return this.fDriver.next();
  }
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    super.reset(componentManager);
    this.fReportCdataEvent = componentManager.getFeature("report-cdata-event", true);
    this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
    this.fNotifyBuiltInRefs = componentManager.getFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", false);
    Object resolver = componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
    this.fExternalSubsetResolver = (resolver instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)resolver : null;
    this.fReadingAttributes = false;
    this.fSupportExternalEntities = true;
    this.fReplaceEntityReferences = true;
    this.fIsCoalesce = false;
    setScannerState(22);
    setDriver(this.fContentDriver);
    XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
    this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this.fStrictURI = componentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
    resetCommon();
  }
  
  public void reset(PropertyManager propertyManager) {
    super.reset(propertyManager);
    this.fNamespaces = ((Boolean)propertyManager.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue();
    this.fNotifyBuiltInRefs = false;
    Boolean bo = (Boolean)propertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences");
    this.fReplaceEntityReferences = bo.booleanValue();
    bo = (Boolean)propertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities");
    this.fSupportExternalEntities = bo.booleanValue();
    Boolean cdata = (Boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/report-cdata-event");
    if (cdata != null)
      this.fReportCdataEvent = cdata.booleanValue(); 
    Boolean coalesce = (Boolean)propertyManager.getProperty("javax.xml.stream.isCoalescing");
    if (coalesce != null)
      this.fIsCoalesce = coalesce.booleanValue(); 
    this.fReportCdataEvent = this.fIsCoalesce ? false : (this.fReportCdataEvent);
    this.fReplaceEntityReferences = this.fIsCoalesce ? true : this.fReplaceEntityReferences;
    XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)propertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this.fSecurityManager = (XMLSecurityManager)propertyManager.getProperty("http://apache.org/xml/properties/security-manager");
    resetCommon();
  }
  
  void resetCommon() {
    this.fMarkupDepth = 0;
    this.fCurrentElement = null;
    this.fElementStack.clear();
    this.fHasExternalDTD = false;
    this.fStandaloneSet = false;
    this.fStandalone = false;
    this.fInScanContent = false;
    this.fShouldSkip = false;
    this.fAdd = false;
    this.fSkip = false;
    this.fEntityStore = this.fEntityManager.getEntityStore();
    this.dtdGrammarUtil = null;
    if (this.fSecurityManager != null) {
      this.fElementAttributeLimit = this.fSecurityManager.getLimit(XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT);
    } else {
      this.fElementAttributeLimit = 0;
    } 
    this.fLimitAnalyzer = new XMLLimitAnalyzer();
    this.fEntityManager.setLimitAnalyzer(this.fLimitAnalyzer);
  }
  
  public String[] getRecognizedFeatures() {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    super.setFeature(featureId, state);
    if (featureId.startsWith("http://apache.org/xml/features/")) {
      String feature = featureId.substring("http://apache.org/xml/features/".length());
      if (feature.equals("scanner/notify-builtin-refs"))
        this.fNotifyBuiltInRefs = state; 
    } 
  }
  
  public String[] getRecognizedProperties() {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    super.setProperty(propertyId, value);
    if (propertyId.startsWith("http://apache.org/xml/properties/")) {
      int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
      if (suffixLength == "internal/entity-manager".length() && propertyId
        .endsWith("internal/entity-manager")) {
        this.fEntityManager = (XMLEntityManager)value;
        return;
      } 
      if (suffixLength == "internal/entity-resolver".length() && propertyId
        .endsWith("internal/entity-resolver")) {
        this.fExternalSubsetResolver = (value instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)value : null;
        return;
      } 
    } 
    if (propertyId.startsWith("http://apache.org/xml/properties/")) {
      String property = propertyId.substring("http://apache.org/xml/properties/".length());
      if (property.equals("internal/entity-manager"))
        this.fEntityManager = (XMLEntityManager)value; 
      return;
    } 
    if (propertyId.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)value;
      this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    } 
  }
  
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
  
  public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
    if (this.fEntityDepth == this.fEntityStack.length) {
      int[] entityarray = new int[this.fEntityStack.length * 2];
      System.arraycopy(this.fEntityStack, 0, entityarray, 0, this.fEntityStack.length);
      this.fEntityStack = entityarray;
    } 
    this.fEntityStack[this.fEntityDepth] = this.fMarkupDepth;
    super.startEntity(name, identifier, encoding, augs);
    if (this.fStandalone && this.fEntityStore.isEntityDeclInExternalSubset(name))
      reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { name }); 
    if (this.fDocumentHandler != null && !this.fScanningAttribute && 
      !name.equals("[xml]"))
      this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs); 
  }
  
  public void endEntity(String name, Augmentations augs) throws IOException, XNIException {
    super.endEntity(name, augs);
    if (this.fMarkupDepth != this.fEntityStack[this.fEntityDepth])
      reportFatalError("MarkupEntityMismatch", null); 
    if (this.fDocumentHandler != null && !this.fScanningAttribute && 
      !name.equals("[xml]"))
      this.fDocumentHandler.endGeneralEntity(name, augs); 
  }
  
  protected Driver createContentDriver() {
    return new FragmentContentDriver();
  }
  
  protected void scanXMLDeclOrTextDecl(boolean scanningTextDecl) throws IOException, XNIException {
    scanXMLDeclOrTextDecl(scanningTextDecl, this.fStrings);
    this.fMarkupDepth--;
    String version = this.fStrings[0];
    String encoding = this.fStrings[1];
    String standalone = this.fStrings[2];
    this.fDeclaredEncoding = encoding;
    this.fStandaloneSet = (standalone != null);
    this.fStandalone = (this.fStandaloneSet && standalone.equals("yes"));
    this.fEntityManager.setStandalone(this.fStandalone);
    if (this.fDocumentHandler != null)
      if (scanningTextDecl) {
        this.fDocumentHandler.textDecl(version, encoding, null);
      } else {
        this.fDocumentHandler.xmlDecl(version, encoding, standalone, null);
      }  
    if (version != null) {
      this.fEntityScanner.setVersion(version);
      this.fEntityScanner.setXMLVersion(version);
    } 
    if (encoding != null && !this.fEntityScanner.getCurrentEntity().isEncodingExternallySpecified())
      this.fEntityScanner.setEncoding(encoding); 
  }
  
  public String getPITarget() {
    return this.fPITarget;
  }
  
  public XMLStringBuffer getPIData() {
    return this.fContentBuffer;
  }
  
  public XMLString getCharacterData() {
    if (this.fUsebuffer)
      return this.fContentBuffer; 
    return this.fTempString;
  }
  
  protected void scanPIData(String target, XMLStringBuffer data) throws IOException, XNIException {
    super.scanPIData(target, data);
    this.fPITarget = target;
    this.fMarkupDepth--;
  }
  
  protected void scanComment() throws IOException, XNIException {
    this.fContentBuffer.clear();
    scanComment(this.fContentBuffer);
    this.fUsebuffer = true;
    this.fMarkupDepth--;
  }
  
  public String getComment() {
    return this.fContentBuffer.toString();
  }
  
  void addElement(String rawname) {
    if (this.fElementPointer < 200) {
      this.fElementArray[this.fElementPointer] = rawname;
      if (this.fElementStack.fDepth < 5) {
        short column = storePointerForADepth(this.fElementPointer);
        if (column > 0) {
          short pointer = getElementPointer((short)this.fElementStack.fDepth, (short)(column - 1));
          if (rawname == this.fElementArray[pointer]) {
            this.fShouldSkip = true;
            this.fLastPointerLocation = pointer;
            resetPointer((short)this.fElementStack.fDepth, column);
            this.fElementArray[this.fElementPointer] = null;
            return;
          } 
          this.fShouldSkip = false;
        } 
      } 
      this.fElementPointer = (short)(this.fElementPointer + 1);
    } 
  }
  
  void resetPointer(short depth, short column) {
    this.fPointerInfo[depth][column] = 0;
  }
  
  short storePointerForADepth(short elementPointer) {
    short depth = (short)this.fElementStack.fDepth;
    for (short i = 0; i < 4; i = (short)(i + 1)) {
      if (canStore(depth, i)) {
        this.fPointerInfo[depth][i] = elementPointer;
        return i;
      } 
    } 
    return -1;
  }
  
  boolean canStore(short depth, short column) {
    return (this.fPointerInfo[depth][column] == 0);
  }
  
  short getElementPointer(short depth, short column) {
    return this.fPointerInfo[depth][column];
  }
  
  boolean skipFromTheBuffer(String rawname) throws IOException {
    if (this.fEntityScanner.skipString(rawname)) {
      char c = (char)this.fEntityScanner.peekChar();
      if (c == ' ' || c == '/' || c == '>') {
        this.fElementRawname = rawname;
        return true;
      } 
      return false;
    } 
    return false;
  }
  
  boolean skipQElement(String rawname) throws IOException {
    int c = this.fEntityScanner.getChar(rawname.length());
    if (XMLChar.isName(c))
      return false; 
    return this.fEntityScanner.skipString(rawname);
  }
  
  protected boolean skipElement() throws IOException {
    if (!this.fShouldSkip)
      return false; 
    if (this.fLastPointerLocation != 0) {
      String rawname = this.fElementArray[this.fLastPointerLocation + 1];
      if (rawname != null && skipFromTheBuffer(rawname)) {
        this.fLastPointerLocation = (short)(this.fLastPointerLocation + 1);
        return true;
      } 
      this.fLastPointerLocation = 0;
    } 
    return (this.fShouldSkip && skipElement((short)0));
  }
  
  boolean skipElement(short column) throws IOException {
    short depth = (short)this.fElementStack.fDepth;
    if (depth > 5)
      return this.fShouldSkip = false; 
    for (short i = column; i < 4; i = (short)(i + 1)) {
      short pointer = getElementPointer(depth, i);
      if (pointer == 0)
        return this.fShouldSkip = false; 
      if (this.fElementArray[pointer] != null && skipFromTheBuffer(this.fElementArray[pointer])) {
        this.fLastPointerLocation = pointer;
        return this.fShouldSkip = true;
      } 
    } 
    return this.fShouldSkip = false;
  }
  
  protected boolean scanStartElement() throws IOException, XNIException {
    if (this.fSkip && !this.fAdd) {
      QName name = this.fElementStack.getNext();
      this.fSkip = this.fEntityScanner.skipString(name.rawname);
      if (this.fSkip) {
        this.fElementStack.push();
        this.fElementQName = name;
      } else {
        this.fElementStack.reposition();
      } 
    } 
    if (!this.fSkip || this.fAdd) {
      this.fElementQName = this.fElementStack.nextElement();
      if (this.fNamespaces) {
        this.fEntityScanner.scanQName(this.fElementQName);
      } else {
        String name = this.fEntityScanner.scanName();
        this.fElementQName.setValues(null, name, name, null);
      } 
    } 
    if (this.fAdd)
      this.fElementStack.matchElement(this.fElementQName); 
    this.fCurrentElement = this.fElementQName;
    String rawname = this.fElementQName.rawname;
    this.fEmptyElement = false;
    this.fAttributes.removeAllAttributes();
    checkDepth(rawname);
    if (!seekCloseOfStartTag()) {
      this.fReadingAttributes = true;
      this.fAttributeCacheUsedCount = 0;
      this.fStringBufferIndex = 0;
      this.fAddDefaultAttr = true;
      while (true) {
        scanAttribute(this.fAttributes);
        if (this.fSecurityManager != null && !this.fSecurityManager.isNoLimit(this.fElementAttributeLimit) && this.fAttributes
          .getLength() > this.fElementAttributeLimit)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { rawname, 
                
                Integer.valueOf(this.fElementAttributeLimit) }, (short)2); 
        if (seekCloseOfStartTag()) {
          this.fReadingAttributes = false;
          break;
        } 
      } 
    } 
    if (this.fEmptyElement) {
      this.fMarkupDepth--;
      if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1])
        reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname }); 
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null); 
      this.fElementStack.popElement();
    } else {
      if (this.dtdGrammarUtil != null)
        this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes); 
      if (this.fDocumentHandler != null)
        this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null); 
    } 
    return this.fEmptyElement;
  }
  
  protected boolean seekCloseOfStartTag() throws IOException, XNIException {
    boolean sawSpace = this.fEntityScanner.skipSpaces();
    int c = this.fEntityScanner.peekChar();
    if (c == 62) {
      this.fEntityScanner.scanChar();
      return true;
    } 
    if (c == 47) {
      this.fEntityScanner.scanChar();
      if (!this.fEntityScanner.skipChar(62))
        reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname }); 
      this.fEmptyElement = true;
      return true;
    } 
    if (!isValidNameStartChar(c) || !sawSpace)
      reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname }); 
    return false;
  }
  
  public boolean hasAttributes() {
    return (this.fAttributes.getLength() > 0);
  }
  
  public XMLAttributesIteratorImpl getAttributeIterator() {
    if (this.dtdGrammarUtil != null && this.fAddDefaultAttr) {
      this.dtdGrammarUtil.addDTDDefaultAttrs(this.fElementQName, this.fAttributes);
      this.fAddDefaultAttr = false;
    } 
    return this.fAttributes;
  }
  
  public boolean standaloneSet() {
    return this.fStandaloneSet;
  }
  
  public boolean isStandAlone() {
    return this.fStandalone;
  }
  
  protected void scanAttribute(XMLAttributes attributes) throws IOException, XNIException {
    if (this.fNamespaces) {
      this.fEntityScanner.scanQName(this.fAttributeQName);
    } else {
      String name = this.fEntityScanner.scanName();
      this.fAttributeQName.setValues(null, name, name, null);
    } 
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(61))
      reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    this.fEntityScanner.skipSpaces();
    int attIndex = 0;
    boolean isVC = (this.fHasExternalDTD && !this.fStandalone);
    XMLString tmpStr = getString();
    scanAttributeValue(tmpStr, this.fTempString2, this.fAttributeQName.rawname, attributes, attIndex, isVC, this.fCurrentElement.rawname);
    int oldLen = attributes.getLength();
    attIndex = attributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
    if (oldLen == attributes.getLength())
      reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname }); 
    attributes.setValue(attIndex, null, tmpStr);
    attributes.setSpecified(attIndex, true);
  }
  
  protected int scanContent(XMLStringBuffer content) throws IOException, XNIException {
    this.fTempString.length = 0;
    int c = this.fEntityScanner.scanContent(this.fTempString);
    content.append(this.fTempString);
    this.fTempString.length = 0;
    if (c == 13) {
      this.fEntityScanner.scanChar();
      content.append((char)c);
      c = -1;
    } else if (c == 93) {
      content.append((char)this.fEntityScanner.scanChar());
      this.fInScanContent = true;
      if (this.fEntityScanner.skipChar(93)) {
        content.append(']');
        while (this.fEntityScanner.skipChar(93))
          content.append(']'); 
        if (this.fEntityScanner.skipChar(62))
          reportFatalError("CDEndInContent", null); 
      } 
      this.fInScanContent = false;
      c = -1;
    } 
    if (this.fDocumentHandler == null || content.length > 0);
    return c;
  }
  
  protected boolean scanCDATASection(XMLStringBuffer contentBuffer, boolean complete) throws IOException, XNIException {
    if (this.fDocumentHandler != null);
    while (this.fEntityScanner.scanData("]]>", contentBuffer)) {
      int c = this.fEntityScanner.peekChar();
      if (c != -1 && isInvalidLiteral(c))
        if (XMLChar.isHighSurrogate(c)) {
          scanSurrogates(contentBuffer);
        } else {
          reportFatalError("InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) });
          this.fEntityScanner.scanChar();
        }  
      if (this.fDocumentHandler != null);
    } 
    this.fMarkupDepth--;
    if (this.fDocumentHandler == null || contentBuffer.length > 0);
    if (this.fDocumentHandler != null);
    return true;
  }
  
  protected int scanEndElement() throws IOException, XNIException {
    QName endElementName = this.fElementStack.popElement();
    String rawname = endElementName.rawname;
    if (!this.fEntityScanner.skipString(endElementName.rawname))
      reportFatalError("ETagRequired", new Object[] { rawname }); 
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(62))
      reportFatalError("ETagUnterminated", new Object[] { rawname }); 
    this.fMarkupDepth--;
    this.fMarkupDepth--;
    if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1])
      reportFatalError("ElementEntityMismatch", new Object[] { rawname }); 
    if (this.fDocumentHandler != null)
      this.fDocumentHandler.endElement(endElementName, null); 
    if (this.dtdGrammarUtil != null)
      this.dtdGrammarUtil.endElement(endElementName); 
    return this.fMarkupDepth;
  }
  
  protected void scanCharReference() throws IOException, XNIException {
    this.fStringBuffer2.clear();
    int ch = scanCharReferenceValue(this.fStringBuffer2, null);
    this.fMarkupDepth--;
    if (ch != -1)
      if (this.fDocumentHandler != null) {
        if (this.fNotifyCharRefs)
          this.fDocumentHandler.startGeneralEntity(this.fCharRefLiteral, null, null, null); 
        Augmentations augs = null;
        if (this.fValidation && ch <= 32) {
          if (this.fTempAugmentations != null) {
            this.fTempAugmentations.removeAllItems();
          } else {
            this.fTempAugmentations = new AugmentationsImpl();
          } 
          augs = this.fTempAugmentations;
          augs.putItem("CHAR_REF_PROBABLE_WS", Boolean.TRUE);
        } 
        if (this.fNotifyCharRefs)
          this.fDocumentHandler.endGeneralEntity(this.fCharRefLiteral, null); 
      }  
  }
  
  protected void scanEntityReference(XMLStringBuffer content) throws IOException, XNIException {
    String name = this.fEntityScanner.scanName();
    if (name == null) {
      reportFatalError("NameRequiredInReference", null);
      return;
    } 
    if (!this.fEntityScanner.skipChar(59))
      reportFatalError("SemicolonRequiredInReference", new Object[] { name }); 
    if (this.fEntityStore.isUnparsedEntity(name))
      reportFatalError("ReferenceToUnparsedEntity", new Object[] { name }); 
    this.fMarkupDepth--;
    this.fCurrentEntityName = name;
    if (name == fAmpSymbol) {
      handleCharacter('&', fAmpSymbol, content);
      this.fScannerState = 41;
      return;
    } 
    if (name == fLtSymbol) {
      handleCharacter('<', fLtSymbol, content);
      this.fScannerState = 41;
      return;
    } 
    if (name == fGtSymbol) {
      handleCharacter('>', fGtSymbol, content);
      this.fScannerState = 41;
      return;
    } 
    if (name == fQuotSymbol) {
      handleCharacter('"', fQuotSymbol, content);
      this.fScannerState = 41;
      return;
    } 
    if (name == fAposSymbol) {
      handleCharacter('\'', fAposSymbol, content);
      this.fScannerState = 41;
      return;
    } 
    boolean isEE = this.fEntityStore.isExternalEntity(name);
    if ((isEE && !this.fSupportExternalEntities) || (!isEE && !this.fReplaceEntityReferences) || this.foundBuiltInRefs) {
      this.fScannerState = 28;
      return;
    } 
    if (!this.fEntityStore.isDeclaredEntity(name)) {
      if (!this.fSupportDTD && this.fReplaceEntityReferences) {
        reportFatalError("EntityNotDeclared", new Object[] { name });
        return;
      } 
      if (this.fHasExternalDTD && !this.fStandalone) {
        if (this.fValidation)
          this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { name }, (short)1); 
      } else {
        reportFatalError("EntityNotDeclared", new Object[] { name });
      } 
    } 
    this.fEntityManager.startEntity(name, false);
  }
  
  void checkDepth(String elementName) {
    this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, elementName, this.fElementStack.fDepth);
    if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      reportFatalError("MaxElementDepthLimit", new Object[] { elementName, 
            Integer.valueOf(this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT)), 
            Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT)), "maxElementDepth" });
    } 
  }
  
  private void handleCharacter(char c, String entity, XMLStringBuffer content) throws XNIException {
    this.foundBuiltInRefs = true;
    content.append(c);
    if (this.fDocumentHandler != null) {
      this.fSingleChar[0] = c;
      if (this.fNotifyBuiltInRefs)
        this.fDocumentHandler.startGeneralEntity(entity, null, null, null); 
      this.fTempString.setValues(this.fSingleChar, 0, 1);
      if (this.fNotifyBuiltInRefs)
        this.fDocumentHandler.endGeneralEntity(entity, null); 
    } 
  }
  
  protected final void setScannerState(int state) {
    this.fScannerState = state;
  }
  
  protected final void setDriver(Driver driver) {
    this.fDriver = driver;
  }
  
  protected String getScannerStateName(int state) {
    switch (state) {
      case 24:
        return "SCANNER_STATE_DOCTYPE";
      case 26:
        return "SCANNER_STATE_ROOT_ELEMENT";
      case 21:
        return "SCANNER_STATE_START_OF_MARKUP";
      case 27:
        return "SCANNER_STATE_COMMENT";
      case 23:
        return "SCANNER_STATE_PI";
      case 22:
        return "SCANNER_STATE_CONTENT";
      case 28:
        return "SCANNER_STATE_REFERENCE";
      case 33:
        return "SCANNER_STATE_END_OF_INPUT";
      case 34:
        return "SCANNER_STATE_TERMINATED";
      case 35:
        return "SCANNER_STATE_CDATA";
      case 36:
        return "SCANNER_STATE_TEXT_DECL";
      case 29:
        return "SCANNER_STATE_ATTRIBUTE";
      case 30:
        return "SCANNER_STATE_ATTRIBUTE_VALUE";
      case 38:
        return "SCANNER_STATE_START_ELEMENT_TAG";
      case 39:
        return "SCANNER_STATE_END_ELEMENT_TAG";
      case 37:
        return "SCANNER_STATE_CHARACTER_DATA";
    } 
    return "??? (" + state + ')';
  }
  
  public String getEntityName() {
    return this.fCurrentEntityName;
  }
  
  public String getDriverName(Driver driver) {
    return "null";
  }
  
  String checkAccess(String systemId, String allowedProtocols) throws IOException {
    String baseSystemId = this.fEntityScanner.getBaseSystemId();
    String expandedSystemId = XMLEntityManager.expandSystemId(systemId, baseSystemId, this.fStrictURI);
    return SecuritySupport.checkAccess(expandedSystemId, allowedProtocols, "all");
  }
  
  protected static final class XMLDocumentFragmentScannerImpl {}
  
  protected class ElementStack2 {
    protected QName[] fQName = new QName[20];
    
    protected int fDepth;
    
    protected int fCount;
    
    protected int fPosition;
    
    protected int fMark;
    
    protected int fLastDepth;
    
    public ElementStack2() {
      for (int i = 0; i < this.fQName.length; i++)
        this.fQName[i] = new QName(); 
      this.fMark = this.fPosition = 1;
    }
    
    public void resize() {
      int oldLength = this.fQName.length;
      QName[] tmp = new QName[oldLength * 2];
      System.arraycopy(this.fQName, 0, tmp, 0, oldLength);
      this.fQName = tmp;
      for (int i = oldLength; i < this.fQName.length; i++)
        this.fQName[i] = new QName(); 
    }
    
    public boolean matchElement(QName element) {
      boolean match = false;
      if (this.fLastDepth > this.fDepth && this.fDepth <= 2)
        if (element.rawname == (this.fQName[this.fDepth]).rawname) {
          XMLDocumentFragmentScannerImpl.this.fAdd = false;
          this.fMark = this.fDepth - 1;
          this.fPosition = this.fMark + 1;
          match = true;
          this.fCount--;
        } else {
          XMLDocumentFragmentScannerImpl.this.fAdd = true;
        }  
      this.fLastDepth = this.fDepth++;
      return match;
    }
    
    public QName nextElement() {
      if (this.fCount == this.fQName.length) {
        XMLDocumentFragmentScannerImpl.this.fShouldSkip = false;
        XMLDocumentFragmentScannerImpl.this.fAdd = false;
        return this.fQName[--this.fCount];
      } 
      return this.fQName[this.fCount++];
    }
    
    public QName getNext() {
      if (this.fPosition == this.fCount)
        this.fPosition = this.fMark; 
      return this.fQName[this.fPosition++];
    }
    
    public int popElement() {
      return this.fDepth--;
    }
    
    public void clear() {
      this.fLastDepth = 0;
      this.fDepth = 0;
      this.fCount = 0;
      this.fPosition = this.fMark = 1;
    }
  }
  
  protected class ElementStack {
    protected QName[] fElements;
    
    protected int[] fInt = new int[20];
    
    protected int fDepth;
    
    protected int fCount;
    
    protected int fPosition;
    
    protected int fMark;
    
    protected int fLastDepth;
    
    public ElementStack() {
      this.fElements = new QName[20];
      for (int i = 0; i < this.fElements.length; i++)
        this.fElements[i] = new QName(); 
    }
    
    public QName pushElement(QName element) {
      if (this.fDepth == this.fElements.length) {
        QName[] array = new QName[this.fElements.length * 2];
        System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
        this.fElements = array;
        for (int i = this.fDepth; i < this.fElements.length; i++)
          this.fElements[i] = new QName(); 
      } 
      this.fElements[this.fDepth].setValues(element);
      return this.fElements[this.fDepth++];
    }
    
    public QName getNext() {
      if (this.fPosition == this.fCount)
        this.fPosition = this.fMark; 
      return this.fElements[this.fPosition];
    }
    
    public void push() {
      this.fInt[++this.fDepth] = this.fPosition++;
    }
    
    public boolean matchElement(QName element) {
      boolean match = false;
      if (this.fLastDepth > this.fDepth && this.fDepth <= 3)
        if (element.rawname == (this.fElements[this.fDepth - 1]).rawname) {
          XMLDocumentFragmentScannerImpl.this.fAdd = false;
          this.fMark = this.fDepth - 1;
          this.fPosition = this.fMark;
          match = true;
          this.fCount--;
        } else {
          XMLDocumentFragmentScannerImpl.this.fAdd = true;
        }  
      if (match) {
        this.fInt[this.fDepth] = this.fPosition++;
      } else {
        this.fInt[this.fDepth] = this.fCount - 1;
      } 
      if (this.fCount == this.fElements.length) {
        XMLDocumentFragmentScannerImpl.this.fSkip = false;
        XMLDocumentFragmentScannerImpl.this.fAdd = false;
        reposition();
        return false;
      } 
      this.fLastDepth = this.fDepth;
      return match;
    }
    
    public QName nextElement() {
      if (XMLDocumentFragmentScannerImpl.this.fSkip) {
        this.fDepth++;
        return this.fElements[this.fCount++];
      } 
      if (this.fDepth == this.fElements.length) {
        QName[] array = new QName[this.fElements.length * 2];
        System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
        this.fElements = array;
        for (int i = this.fDepth; i < this.fElements.length; i++)
          this.fElements[i] = new QName(); 
      } 
      return this.fElements[this.fDepth++];
    }
    
    public QName popElement() {
      if (XMLDocumentFragmentScannerImpl.this.fSkip || XMLDocumentFragmentScannerImpl.this.fAdd)
        return this.fElements[this.fInt[this.fDepth--]]; 
      return this.fElements[--this.fDepth];
    }
    
    public void reposition() {
      for (int i = 2; i <= this.fDepth; i++)
        this.fElements[i - 1] = this.fElements[this.fInt[i]]; 
    }
    
    public void clear() {
      this.fDepth = 0;
      this.fLastDepth = 0;
      this.fCount = 0;
      this.fPosition = this.fMark = 1;
    }
    
    public QName getLastPoppedElement() {
      return this.fElements[this.fDepth];
    }
  }
  
  protected static interface Driver {
    int next() throws IOException, XNIException;
  }
  
  protected class FragmentContentDriver implements Driver {
    private boolean fContinueDispatching = true;
    
    private boolean fScanningForMarkup = true;
    
    private void startOfMarkup() throws IOException {
      XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
      int ch = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
      switch (ch) {
        case 63:
          XMLDocumentFragmentScannerImpl.this.setScannerState(23);
          XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch);
          return;
        case 33:
          XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch);
          if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45)) {
            if (!XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45))
              XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCommentStart", null); 
            XMLDocumentFragmentScannerImpl.this.setScannerState(27);
          } else if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.cdata)) {
            XMLDocumentFragmentScannerImpl.this.setScannerState(35);
          } else if (!scanForDoctypeHook()) {
            XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
          } 
          return;
        case 47:
          XMLDocumentFragmentScannerImpl.this.setScannerState(39);
          XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch);
          return;
      } 
      if (XMLDocumentFragmentScannerImpl.this.isValidNameStartChar(ch)) {
        XMLDocumentFragmentScannerImpl.this.setScannerState(38);
      } else {
        XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
      } 
    }
    
    private void startOfContent() throws IOException {
      if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60)) {
        XMLDocumentFragmentScannerImpl.this.setScannerState(21);
      } else if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(38)) {
        XMLDocumentFragmentScannerImpl.this.setScannerState(28);
      } else {
        XMLDocumentFragmentScannerImpl.this.setScannerState(37);
      } 
    }
    
    public void decideSubState() throws IOException {
      while (XMLDocumentFragmentScannerImpl.this.fScannerState == 22 || XMLDocumentFragmentScannerImpl.this.fScannerState == 21) {
        switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
          case 22:
            startOfContent();
          case 21:
            startOfMarkup();
        } 
      } 
    }
    
    public int next() throws IOException, XNIException {
      try {
        while (true) {
          int ch;
          int c;
          switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
            case 22:
              ch = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
              if (ch == 60) {
                XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                XMLDocumentFragmentScannerImpl.this.setScannerState(21);
              } else {
                if (ch == 38) {
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                  XMLDocumentFragmentScannerImpl.this.setScannerState(28);
                  break;
                } 
                XMLDocumentFragmentScannerImpl.this.setScannerState(37);
                break;
              } 
            case 21:
              startOfMarkup();
              break;
          } 
          if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
            XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
            if (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData) {
              if (XMLDocumentFragmentScannerImpl.this.fScannerState != 35 && XMLDocumentFragmentScannerImpl.this.fScannerState != 28 && XMLDocumentFragmentScannerImpl.this.fScannerState != 37) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                return 4;
              } 
            } else if (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference) {
              if (XMLDocumentFragmentScannerImpl.this.fScannerState != 35 && XMLDocumentFragmentScannerImpl.this.fScannerState != 28 && XMLDocumentFragmentScannerImpl.this.fScannerState != 37) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                return 4;
              } 
            } 
          } 
          switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
            case 7:
              return 7;
            case 38:
              XMLDocumentFragmentScannerImpl.this.fEmptyElement = XMLDocumentFragmentScannerImpl.this.scanStartElement();
              if (XMLDocumentFragmentScannerImpl.this.fEmptyElement) {
                XMLDocumentFragmentScannerImpl.this.setScannerState(39);
              } else {
                XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              } 
              return 1;
            case 37:
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData);
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              } else {
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              } 
              XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
              c = XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanContent(XMLDocumentFragmentScannerImpl.this.fTempString);
              if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60)) {
                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(47)) {
                  XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
                  XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                  XMLDocumentFragmentScannerImpl.this.setScannerState(39);
                } else if (XMLChar.isNameStart(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                  XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
                  XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                  XMLDocumentFragmentScannerImpl.this.setScannerState(38);
                } else {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(21);
                  if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                    XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                    XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                    XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
                    XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
                    continue;
                  } 
                } 
                if (XMLDocumentFragmentScannerImpl.this.fUsebuffer) {
                  XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
                  XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
                } 
                checkLimit(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                if (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null && XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer))
                  return 6; 
                return 4;
              } 
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
              XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
              if (c == 13) {
                XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)c);
                c = -1;
              } else if (c == 93) {
                XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
                XMLDocumentFragmentScannerImpl.this.fInScanContent = true;
                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93)) {
                  XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']');
                  while (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93))
                    XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']'); 
                  if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(62))
                    XMLDocumentFragmentScannerImpl.this.reportFatalError("CDEndInContent", null); 
                } 
                c = -1;
                XMLDocumentFragmentScannerImpl.this.fInScanContent = false;
              } 
              while (true) {
                if (c == 60) {
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                  XMLDocumentFragmentScannerImpl.this.setScannerState(21);
                  break;
                } 
                if (c == 38) {
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                  XMLDocumentFragmentScannerImpl.this.setScannerState(28);
                  break;
                } 
                if (c != -1 && XMLDocumentFragmentScannerImpl.this.isInvalidLiteral(c)) {
                  if (XMLChar.isHighSurrogate(c)) {
                    XMLDocumentFragmentScannerImpl.this.scanSurrogates(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                    break;
                  } 
                  XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(c, 16) });
                  XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                  break;
                } 
                c = XMLDocumentFragmentScannerImpl.this.scanContent(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                if (!XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  break;
                } 
              } 
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                continue;
              } 
              checkLimit(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
              if (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null && XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer))
                return 6; 
              return 4;
            case 39:
              if (XMLDocumentFragmentScannerImpl.this.fEmptyElement) {
                XMLDocumentFragmentScannerImpl.this.fEmptyElement = false;
                XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                return (XMLDocumentFragmentScannerImpl.this.fMarkupDepth == 0 && elementDepthIsZeroHook()) ? 2 : 2;
              } 
              if (XMLDocumentFragmentScannerImpl.this.scanEndElement() == 0)
                if (elementDepthIsZeroHook())
                  return 2;  
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 2;
            case 27:
              XMLDocumentFragmentScannerImpl.this.scanComment();
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 5;
            case 23:
              XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              XMLDocumentFragmentScannerImpl.this.scanPI(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 3;
            case 35:
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
              } else {
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              } 
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              XMLDocumentFragmentScannerImpl.this.scanCDATASection(XMLDocumentFragmentScannerImpl.this.fContentBuffer, true);
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
                continue;
              } 
              if (XMLDocumentFragmentScannerImpl.this.fReportCdataEvent)
                return 12; 
              return 4;
            case 28:
              XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
              XMLDocumentFragmentScannerImpl.this.foundBuiltInRefs = false;
              if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
              } else {
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              } 
              XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
              if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(35)) {
                XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
                XMLDocumentFragmentScannerImpl.this.fMarkupDepth--;
                if (!XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  return 4;
                } 
              } else {
                XMLDocumentFragmentScannerImpl.this.scanEntityReference(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                if (XMLDocumentFragmentScannerImpl.this.fScannerState == 41 && !XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  return 4;
                } 
                if (XMLDocumentFragmentScannerImpl.this.fScannerState == 36) {
                  XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
                  continue;
                } 
                if (XMLDocumentFragmentScannerImpl.this.fScannerState == 28) {
                  XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                  if (XMLDocumentFragmentScannerImpl.this.fReplaceEntityReferences && XMLDocumentFragmentScannerImpl.this.fEntityStore.isDeclaredEntity(XMLDocumentFragmentScannerImpl.this.fCurrentEntityName))
                    continue; 
                  return 9;
                } 
              } 
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
              continue;
            case 36:
              if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
                XMLDocumentFragmentScannerImpl.this.fMarkupDepth++;
                if (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                  XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
                  XMLDocumentFragmentScannerImpl.this.fStringBuffer.append("xml");
                  if (XMLDocumentFragmentScannerImpl.this.fNamespaces) {
                    while (XMLDocumentFragmentScannerImpl.this.isValidNCName(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar()))
                      XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar()); 
                  } else {
                    while (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar()))
                      XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar()); 
                  } 
                  String target = XMLDocumentFragmentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentFragmentScannerImpl.this.fStringBuffer.ch, XMLDocumentFragmentScannerImpl.this.fStringBuffer.offset, XMLDocumentFragmentScannerImpl.this.fStringBuffer.length);
                  XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                  XMLDocumentFragmentScannerImpl.this.scanPIData(target, XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                } else {
                  XMLDocumentFragmentScannerImpl.this.scanXMLDeclOrTextDecl(true);
                } 
              } 
              XMLDocumentFragmentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              continue;
            case 26:
              if (scanRootElementHook()) {
                XMLDocumentFragmentScannerImpl.this.fEmptyElement = true;
                return 1;
              } 
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 1;
            case 40:
              XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
              XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
              XMLDocumentFragmentScannerImpl.this.fMarkupDepth--;
              XMLDocumentFragmentScannerImpl.this.setScannerState(22);
              return 4;
          } 
          break;
        } 
        throw new XNIException("Scanner State " + XMLDocumentFragmentScannerImpl.this.fScannerState + " not Recognized ");
      } catch (EOFException e) {
        endOfFileHook(e);
        return -1;
      } 
    }
    
    protected void checkLimit(XMLStringBuffer buffer) {
      if (XMLDocumentFragmentScannerImpl.this.fLimitAnalyzer.isTracking(XMLDocumentFragmentScannerImpl.this.fCurrentEntityName)) {
        XMLDocumentFragmentScannerImpl.this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, XMLDocumentFragmentScannerImpl.this.fCurrentEntityName, buffer.length);
        if (XMLDocumentFragmentScannerImpl.this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, XMLDocumentFragmentScannerImpl.this.fLimitAnalyzer)) {
          XMLDocumentFragmentScannerImpl.this.fSecurityManager.debugPrint(XMLDocumentFragmentScannerImpl.this.fLimitAnalyzer);
          XMLDocumentFragmentScannerImpl.this.reportFatalError("MaxEntitySizeLimit", new Object[] { XMLDocumentFragmentScannerImpl.access$100(this.this$0), 
                Integer.valueOf(this.this$0.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT)), 
                Integer.valueOf(this.this$0.fSecurityManager.getLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT)), this.this$0.fSecurityManager
                .getStateLiteral(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT) });
        } 
        if (XMLDocumentFragmentScannerImpl.this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, XMLDocumentFragmentScannerImpl.this.fLimitAnalyzer)) {
          XMLDocumentFragmentScannerImpl.this.fSecurityManager.debugPrint(XMLDocumentFragmentScannerImpl.this.fLimitAnalyzer);
          XMLDocumentFragmentScannerImpl.this.reportFatalError("TotalEntitySizeLimit", new Object[] { Integer.valueOf(this.this$0.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), 
                Integer.valueOf(this.this$0.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), this.this$0.fSecurityManager
                .getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) });
        } 
      } 
    }
    
    protected boolean scanForDoctypeHook() throws IOException, XNIException {
      return false;
    }
    
    protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
      return false;
    }
    
    protected boolean scanRootElementHook() throws IOException, XNIException {
      return false;
    }
    
    protected void endOfFileHook(EOFException e) throws IOException, XNIException {
      if (XMLDocumentFragmentScannerImpl.this.fMarkupDepth != 0)
        XMLDocumentFragmentScannerImpl.this.reportFatalError("PrematureEOF", null); 
    }
  }
  
  static void pr(String str) {
    System.out.println(str);
  }
  
  protected XMLString getString() {
    if (this.fAttributeCacheUsedCount < this.initialCacheCount || this.fAttributeCacheUsedCount < this.attributeValueCache.size())
      return this.attributeValueCache.get(this.fAttributeCacheUsedCount++); 
    XMLString str = new XMLString();
    this.fAttributeCacheUsedCount++;
    this.attributeValueCache.add(str);
    return str;
  }
  
  public void refresh() {
    refresh(0);
  }
  
  public void refresh(int refreshPosition) {
    if (this.fReadingAttributes)
      this.fAttributes.refresh(); 
    if (this.fScannerState == 37) {
      this.fContentBuffer.append(this.fTempString);
      this.fTempString.length = 0;
      this.fUsebuffer = true;
    } 
  }
}
