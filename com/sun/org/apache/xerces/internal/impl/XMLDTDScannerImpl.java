package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import java.io.EOFException;
import java.io.IOException;

public class XMLDTDScannerImpl extends XMLScanner implements XMLDTDScanner, XMLComponent, XMLEntityHandler {
  protected static final int SCANNER_STATE_END_OF_INPUT = 0;
  
  protected static final int SCANNER_STATE_TEXT_DECL = 1;
  
  protected static final int SCANNER_STATE_MARKUP_DECL = 2;
  
  private static final String[] RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-char-refs" };
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[] { null, Boolean.FALSE };
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager" };
  
  private static final Object[] PROPERTY_DEFAULTS = new Object[] { null, null, null };
  
  private static final boolean DEBUG_SCANNER_STATE = false;
  
  public XMLDTDHandler fDTDHandler = null;
  
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  
  protected int fScannerState;
  
  protected boolean fStandalone;
  
  protected boolean fSeenExternalDTD;
  
  protected boolean fSeenExternalPE;
  
  private boolean fStartDTDCalled;
  
  private XMLAttributesImpl fAttributes = new XMLAttributesImpl();
  
  private int[] fContentStack = new int[5];
  
  private int fContentDepth;
  
  private int[] fPEStack = new int[5];
  
  private boolean[] fPEReport = new boolean[5];
  
  private int fPEDepth;
  
  private int fMarkUpDepth;
  
  private int fExtEntityDepth;
  
  private int fIncludeSectDepth;
  
  private String[] fStrings = new String[3];
  
  private XMLString fString = new XMLString();
  
  private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  
  private XMLString fLiteral = new XMLString();
  
  private XMLString fLiteral2 = new XMLString();
  
  private String[] fEnumeration = new String[5];
  
  private int fEnumerationCount;
  
  private XMLStringBuffer fIgnoreConditionalBuffer = new XMLStringBuffer(128);
  
  DTDGrammar nvGrammarInfo = null;
  
  boolean nonValidatingMode = false;
  
  public XMLDTDScannerImpl() {}
  
  public XMLDTDScannerImpl(SymbolTable symbolTable, XMLErrorReporter errorReporter, XMLEntityManager entityManager) {
    this.fSymbolTable = symbolTable;
    this.fErrorReporter = errorReporter;
    this.fEntityManager = entityManager;
    entityManager.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
  }
  
  public void setInputSource(XMLInputSource inputSource) throws IOException {
    if (inputSource == null) {
      if (this.fDTDHandler != null) {
        this.fDTDHandler.startDTD(null, null);
        this.fDTDHandler.endDTD(null);
      } 
      if (this.nonValidatingMode) {
        this.nvGrammarInfo.startDTD(null, null);
        this.nvGrammarInfo.endDTD(null);
      } 
      return;
    } 
    this.fEntityManager.setEntityHandler(this);
    this.fEntityManager.startDTDEntity(inputSource);
  }
  
  public void setLimitAnalyzer(XMLLimitAnalyzer limitAnalyzer) {
    this.fLimitAnalyzer = limitAnalyzer;
  }
  
  public boolean scanDTDExternalSubset(boolean complete) throws IOException, XNIException {
    this.fEntityManager.setEntityHandler(this);
    if (this.fScannerState == 1) {
      this.fSeenExternalDTD = true;
      boolean textDecl = scanTextDecl();
      if (this.fScannerState == 0)
        return false; 
      setScannerState(2);
      if (textDecl && !complete)
        return true; 
    } 
    do {
      if (!scanDecls(complete))
        return false; 
    } while (complete);
    return true;
  }
  
  public boolean scanDTDInternalSubset(boolean complete, boolean standalone, boolean hasExternalSubset) throws IOException, XNIException {
    this.fEntityScanner = this.fEntityManager.getEntityScanner();
    this.fEntityManager.setEntityHandler(this);
    this.fStandalone = standalone;
    if (this.fScannerState == 1) {
      if (this.fDTDHandler != null) {
        this.fDTDHandler.startDTD(this.fEntityScanner, null);
        this.fStartDTDCalled = true;
      } 
      if (this.nonValidatingMode) {
        this.fStartDTDCalled = true;
        this.nvGrammarInfo.startDTD(this.fEntityScanner, null);
      } 
      setScannerState(2);
    } 
    do {
      if (!scanDecls(complete)) {
        if (this.fDTDHandler != null && !hasExternalSubset)
          this.fDTDHandler.endDTD(null); 
        if (this.nonValidatingMode && !hasExternalSubset)
          this.nvGrammarInfo.endDTD(null); 
        setScannerState(1);
        return false;
      } 
    } while (complete);
    return true;
  }
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    super.reset(componentManager);
    init();
  }
  
  public void reset() {
    super.reset();
    init();
  }
  
  public void reset(PropertyManager props) {
    setPropertyManager(props);
    super.reset(props);
    init();
    this.nonValidatingMode = true;
    this.nvGrammarInfo = new DTDGrammar(this.fSymbolTable);
  }
  
  public String[] getRecognizedFeatures() {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public String[] getRecognizedProperties() {
    return (String[])RECOGNIZED_PROPERTIES.clone();
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
  
  public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
    super.startEntity(name, identifier, encoding, augs);
    boolean dtdEntity = name.equals("[dtd]");
    if (dtdEntity) {
      if (this.fDTDHandler != null && !this.fStartDTDCalled)
        this.fDTDHandler.startDTD(this.fEntityScanner, null); 
      if (this.fDTDHandler != null)
        this.fDTDHandler.startExternalSubset(identifier, null); 
      this.fEntityManager.startExternalSubset();
      this.fEntityStore.startExternalSubset();
      this.fExtEntityDepth++;
    } else if (name.charAt(0) == '%') {
      pushPEStack(this.fMarkUpDepth, this.fReportEntity);
      if (this.fEntityScanner.isExternal())
        this.fExtEntityDepth++; 
    } 
    if (this.fDTDHandler != null && !dtdEntity && this.fReportEntity)
      this.fDTDHandler.startParameterEntity(name, identifier, encoding, null); 
  }
  
  public void endEntity(String name, Augmentations augs) throws XNIException, IOException {
    super.endEntity(name, augs);
    if (this.fScannerState == 0)
      return; 
    boolean reportEntity = this.fReportEntity;
    if (name.startsWith("%")) {
      reportEntity = peekReportEntity();
      int startMarkUpDepth = popPEStack();
      if (startMarkUpDepth == 0 && startMarkUpDepth < this.fMarkUpDepth)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ILL_FORMED_PARAMETER_ENTITY_WHEN_USED_IN_DECL", new Object[] { this.fEntityManager.fCurrentEntity.name }, (short)2); 
      if (startMarkUpDepth != this.fMarkUpDepth) {
        reportEntity = false;
        if (this.fValidation)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ImproperDeclarationNesting", new Object[] { name }, (short)1); 
      } 
      if (this.fEntityScanner.isExternal())
        this.fExtEntityDepth--; 
    } 
    boolean dtdEntity = name.equals("[dtd]");
    if (this.fDTDHandler != null && !dtdEntity && reportEntity)
      this.fDTDHandler.endParameterEntity(name, null); 
    if (dtdEntity) {
      if (this.fIncludeSectDepth != 0)
        reportFatalError("IncludeSectUnterminated", null); 
      this.fScannerState = 0;
      this.fEntityManager.endExternalSubset();
      this.fEntityStore.endExternalSubset();
      if (this.fDTDHandler != null) {
        this.fDTDHandler.endExternalSubset(null);
        this.fDTDHandler.endDTD(null);
      } 
      this.fExtEntityDepth--;
    } 
    if (augs != null && Boolean.TRUE.equals(augs.getItem("LAST_ENTITY")) && (this.fMarkUpDepth != 0 || this.fExtEntityDepth != 0 || this.fIncludeSectDepth != 0))
      throw new EOFException(); 
  }
  
  protected final void setScannerState(int state) {
    this.fScannerState = state;
  }
  
  private static String getScannerStateName(int state) {
    return "??? (" + state + ')';
  }
  
  protected final boolean scanningInternalSubset() {
    return (this.fExtEntityDepth == 0);
  }
  
  protected void startPE(String name, boolean literal) throws IOException, XNIException {
    int depth = this.fPEDepth;
    String pName = "%" + name;
    if (this.fValidation && !this.fEntityStore.isDeclaredEntity(pName))
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { name }, (short)1); 
    this.fEntityManager.startEntity(this.fSymbolTable.addSymbol(pName), literal);
    if (depth != this.fPEDepth && this.fEntityScanner.isExternal())
      scanTextDecl(); 
  }
  
  protected final boolean scanTextDecl() throws IOException, XNIException {
    boolean textDecl = false;
    if (this.fEntityScanner.skipString("<?xml")) {
      this.fMarkUpDepth++;
      if (isValidNameChar(this.fEntityScanner.peekChar())) {
        this.fStringBuffer.clear();
        this.fStringBuffer.append("xml");
        while (isValidNameChar(this.fEntityScanner.peekChar()))
          this.fStringBuffer.append((char)this.fEntityScanner.scanChar()); 
        String target = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
        scanPIData(target, this.fString);
      } else {
        String version = null;
        String encoding = null;
        scanXMLDeclOrTextDecl(true, this.fStrings);
        textDecl = true;
        this.fMarkUpDepth--;
        version = this.fStrings[0];
        encoding = this.fStrings[1];
        this.fEntityScanner.setEncoding(encoding);
        if (this.fDTDHandler != null)
          this.fDTDHandler.textDecl(version, encoding, null); 
      } 
    } 
    this.fEntityManager.fCurrentEntity.mayReadChunks = true;
    return textDecl;
  }
  
  protected final void scanPIData(String target, XMLString data) throws IOException, XNIException {
    this.fMarkUpDepth--;
    if (this.fDTDHandler != null)
      this.fDTDHandler.processingInstruction(target, data, null); 
  }
  
  protected final void scanComment() throws IOException, XNIException {
    this.fReportEntity = false;
    scanComment(this.fStringBuffer);
    this.fMarkUpDepth--;
    if (this.fDTDHandler != null)
      this.fDTDHandler.comment(this.fStringBuffer, null); 
    this.fReportEntity = true;
  }
  
  protected final void scanElementDecl() throws IOException, XNIException {
    this.fReportEntity = false;
    if (!skipSeparator(true, !scanningInternalSubset()))
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL", null); 
    String name = this.fEntityScanner.scanName();
    if (name == null)
      reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL", null); 
    if (!skipSeparator(true, !scanningInternalSubset()))
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL", new Object[] { name }); 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.startContentModel(name, null); 
    String contentModel = null;
    this.fReportEntity = true;
    if (this.fEntityScanner.skipString("EMPTY")) {
      contentModel = "EMPTY";
      if (this.fDTDContentModelHandler != null)
        this.fDTDContentModelHandler.empty(null); 
    } else if (this.fEntityScanner.skipString("ANY")) {
      contentModel = "ANY";
      if (this.fDTDContentModelHandler != null)
        this.fDTDContentModelHandler.any(null); 
    } else {
      if (!this.fEntityScanner.skipChar(40))
        reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[] { name }); 
      if (this.fDTDContentModelHandler != null)
        this.fDTDContentModelHandler.startGroup(null); 
      this.fStringBuffer.clear();
      this.fStringBuffer.append('(');
      this.fMarkUpDepth++;
      skipSeparator(false, !scanningInternalSubset());
      if (this.fEntityScanner.skipString("#PCDATA")) {
        scanMixed(name);
      } else {
        scanChildren(name);
      } 
      contentModel = this.fStringBuffer.toString();
    } 
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.endContentModel(null); 
    this.fReportEntity = false;
    skipSeparator(false, !scanningInternalSubset());
    if (!this.fEntityScanner.skipChar(62))
      reportFatalError("ElementDeclUnterminated", new Object[] { name }); 
    this.fReportEntity = true;
    this.fMarkUpDepth--;
    if (this.fDTDHandler != null)
      this.fDTDHandler.elementDecl(name, contentModel, null); 
    if (this.nonValidatingMode)
      this.nvGrammarInfo.elementDecl(name, contentModel, null); 
  }
  
  private final void scanMixed(String elName) throws IOException, XNIException {
    String childName = null;
    this.fStringBuffer.append("#PCDATA");
    if (this.fDTDContentModelHandler != null)
      this.fDTDContentModelHandler.pcdata(null); 
    skipSeparator(false, !scanningInternalSubset());
    while (this.fEntityScanner.skipChar(124)) {
      this.fStringBuffer.append('|');
      if (this.fDTDContentModelHandler != null)
        this.fDTDContentModelHandler.separator((short)0, null); 
      skipSeparator(false, !scanningInternalSubset());
      childName = this.fEntityScanner.scanName();
      if (childName == null)
        reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT", new Object[] { elName }); 
      this.fStringBuffer.append(childName);
      if (this.fDTDContentModelHandler != null)
        this.fDTDContentModelHandler.element(childName, null); 
      skipSeparator(false, !scanningInternalSubset());
    } 
    if (this.fEntityScanner.skipString(")*")) {
      this.fStringBuffer.append(")*");
      if (this.fDTDContentModelHandler != null) {
        this.fDTDContentModelHandler.endGroup(null);
        this.fDTDContentModelHandler.occurrence((short)3, null);
      } 
    } else if (childName != null) {
      reportFatalError("MixedContentUnterminated", new Object[] { elName });
    } else if (this.fEntityScanner.skipChar(41)) {
      this.fStringBuffer.append(')');
      if (this.fDTDContentModelHandler != null)
        this.fDTDContentModelHandler.endGroup(null); 
    } else {
      reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[] { elName });
    } 
    this.fMarkUpDepth--;
  }
  
  private final void scanChildren(String elName) throws IOException, XNIException {
    this.fContentDepth = 0;
    pushContentStack(0);
    int currentOp = 0;
    while (true) {
      while (this.fEntityScanner.skipChar(40)) {
        this.fMarkUpDepth++;
        this.fStringBuffer.append('(');
        if (this.fDTDContentModelHandler != null)
          this.fDTDContentModelHandler.startGroup(null); 
        pushContentStack(currentOp);
        currentOp = 0;
        skipSeparator(false, !scanningInternalSubset());
      } 
      skipSeparator(false, !scanningInternalSubset());
      String childName = this.fEntityScanner.scanName();
      if (childName == null) {
        reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[] { elName });
        return;
      } 
      if (this.fDTDContentModelHandler != null)
        this.fDTDContentModelHandler.element(childName, null); 
      this.fStringBuffer.append(childName);
      int c = this.fEntityScanner.peekChar();
      if (c == 63 || c == 42 || c == 43) {
        if (this.fDTDContentModelHandler != null) {
          short oc;
          if (c == 63) {
            oc = 2;
          } else if (c == 42) {
            oc = 3;
          } else {
            oc = 4;
          } 
          this.fDTDContentModelHandler.occurrence(oc, null);
        } 
        this.fEntityScanner.scanChar();
        this.fStringBuffer.append((char)c);
      } 
      while (true) {
        skipSeparator(false, !scanningInternalSubset());
        c = this.fEntityScanner.peekChar();
        if (c == 44 && currentOp != 124) {
          currentOp = c;
          if (this.fDTDContentModelHandler != null)
            this.fDTDContentModelHandler.separator((short)1, null); 
          this.fEntityScanner.scanChar();
          this.fStringBuffer.append(',');
          break;
        } 
        if (c == 124 && currentOp != 44) {
          currentOp = c;
          if (this.fDTDContentModelHandler != null)
            this.fDTDContentModelHandler.separator((short)0, null); 
          this.fEntityScanner.scanChar();
          this.fStringBuffer.append('|');
          break;
        } 
        if (c != 41)
          reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[] { elName }); 
        if (this.fDTDContentModelHandler != null)
          this.fDTDContentModelHandler.endGroup(null); 
        currentOp = popContentStack();
        if (this.fEntityScanner.skipString(")?")) {
          this.fStringBuffer.append(")?");
          if (this.fDTDContentModelHandler != null) {
            short oc = 2;
            this.fDTDContentModelHandler.occurrence(oc, null);
          } 
        } else if (this.fEntityScanner.skipString(")+")) {
          this.fStringBuffer.append(")+");
          if (this.fDTDContentModelHandler != null) {
            short oc = 4;
            this.fDTDContentModelHandler.occurrence(oc, null);
          } 
        } else if (this.fEntityScanner.skipString(")*")) {
          this.fStringBuffer.append(")*");
          if (this.fDTDContentModelHandler != null) {
            short oc = 3;
            this.fDTDContentModelHandler.occurrence(oc, null);
          } 
        } else {
          this.fEntityScanner.scanChar();
          this.fStringBuffer.append(')');
        } 
        this.fMarkUpDepth--;
        if (this.fContentDepth == 0)
          return; 
      } 
      skipSeparator(false, !scanningInternalSubset());
    } 
  }
  
  protected final void scanAttlistDecl() throws IOException, XNIException {
    this.fReportEntity = false;
    if (!skipSeparator(true, !scanningInternalSubset()))
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL", null); 
    String elName = this.fEntityScanner.scanName();
    if (elName == null)
      reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL", null); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.startAttlist(elName, null); 
    if (!skipSeparator(true, !scanningInternalSubset())) {
      if (this.fEntityScanner.skipChar(62)) {
        if (this.fDTDHandler != null)
          this.fDTDHandler.endAttlist(null); 
        this.fMarkUpDepth--;
        return;
      } 
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF", new Object[] { elName });
    } 
    while (!this.fEntityScanner.skipChar(62)) {
      String name = this.fEntityScanner.scanName();
      if (name == null)
        reportFatalError("AttNameRequiredInAttDef", new Object[] { elName }); 
      if (!skipSeparator(true, !scanningInternalSubset()))
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF", new Object[] { elName, name }); 
      String type = scanAttType(elName, name);
      if (!skipSeparator(true, !scanningInternalSubset()))
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF", new Object[] { elName, name }); 
      String defaultType = scanAttDefaultDecl(elName, name, type, this.fLiteral, this.fLiteral2);
      String[] enumr = null;
      if ((this.fDTDHandler != null || this.nonValidatingMode) && 
        this.fEnumerationCount != 0) {
        enumr = new String[this.fEnumerationCount];
        System.arraycopy(this.fEnumeration, 0, enumr, 0, this.fEnumerationCount);
      } 
      if (defaultType != null && (defaultType.equals("#REQUIRED") || defaultType
        .equals("#IMPLIED"))) {
        if (this.fDTDHandler != null)
          this.fDTDHandler.attributeDecl(elName, name, type, enumr, defaultType, null, null, null); 
        if (this.nonValidatingMode)
          this.nvGrammarInfo.attributeDecl(elName, name, type, enumr, defaultType, null, null, null); 
      } else {
        if (this.fDTDHandler != null)
          this.fDTDHandler.attributeDecl(elName, name, type, enumr, defaultType, this.fLiteral, this.fLiteral2, null); 
        if (this.nonValidatingMode)
          this.nvGrammarInfo.attributeDecl(elName, name, type, enumr, defaultType, this.fLiteral, this.fLiteral2, null); 
      } 
      skipSeparator(false, !scanningInternalSubset());
    } 
    if (this.fDTDHandler != null)
      this.fDTDHandler.endAttlist(null); 
    this.fMarkUpDepth--;
    this.fReportEntity = true;
  }
  
  private final String scanAttType(String elName, String atName) throws IOException, XNIException {
    String type = null;
    this.fEnumerationCount = 0;
    if (this.fEntityScanner.skipString("CDATA")) {
      type = "CDATA";
    } else if (this.fEntityScanner.skipString("IDREFS")) {
      type = "IDREFS";
    } else if (this.fEntityScanner.skipString("IDREF")) {
      type = "IDREF";
    } else if (this.fEntityScanner.skipString("ID")) {
      type = "ID";
    } else if (this.fEntityScanner.skipString("ENTITY")) {
      type = "ENTITY";
    } else if (this.fEntityScanner.skipString("ENTITIES")) {
      type = "ENTITIES";
    } else if (this.fEntityScanner.skipString("NMTOKENS")) {
      type = "NMTOKENS";
    } else if (this.fEntityScanner.skipString("NMTOKEN")) {
      type = "NMTOKEN";
    } else {
      if (this.fEntityScanner.skipString("NOTATION")) {
        type = "NOTATION";
        if (!skipSeparator(true, !scanningInternalSubset()))
          reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE", new Object[] { elName, atName }); 
        int i = this.fEntityScanner.scanChar();
        if (i != 40)
          reportFatalError("MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE", new Object[] { elName, atName }); 
        this.fMarkUpDepth++;
        while (true) {
          skipSeparator(false, !scanningInternalSubset());
          String aName = this.fEntityScanner.scanName();
          if (aName == null)
            reportFatalError("MSG_NAME_REQUIRED_IN_NOTATIONTYPE", new Object[] { elName, atName }); 
          ensureEnumerationSize(this.fEnumerationCount + 1);
          this.fEnumeration[this.fEnumerationCount++] = aName;
          skipSeparator(false, !scanningInternalSubset());
          i = this.fEntityScanner.scanChar();
          if (i != 124) {
            if (i != 41)
              reportFatalError("NotationTypeUnterminated", new Object[] { elName, atName }); 
            this.fMarkUpDepth--;
            return type;
          } 
        } 
      } 
      type = "ENUMERATION";
      int c = this.fEntityScanner.scanChar();
      if (c != 40)
        reportFatalError("AttTypeRequiredInAttDef", new Object[] { elName, atName }); 
      this.fMarkUpDepth++;
      while (true) {
        skipSeparator(false, !scanningInternalSubset());
        String token = this.fEntityScanner.scanNmtoken();
        if (token == null)
          reportFatalError("MSG_NMTOKEN_REQUIRED_IN_ENUMERATION", new Object[] { elName, atName }); 
        ensureEnumerationSize(this.fEnumerationCount + 1);
        this.fEnumeration[this.fEnumerationCount++] = token;
        skipSeparator(false, !scanningInternalSubset());
        c = this.fEntityScanner.scanChar();
        if (c != 124) {
          if (c != 41)
            reportFatalError("EnumerationUnterminated", new Object[] { elName, atName }); 
          this.fMarkUpDepth--;
          return type;
        } 
      } 
    } 
    return type;
  }
  
  protected final String scanAttDefaultDecl(String elName, String atName, String type, XMLString defaultVal, XMLString nonNormalizedDefaultVal) throws IOException, XNIException {
    String defaultType = null;
    this.fString.clear();
    defaultVal.clear();
    if (this.fEntityScanner.skipString("#REQUIRED")) {
      defaultType = "#REQUIRED";
    } else if (this.fEntityScanner.skipString("#IMPLIED")) {
      defaultType = "#IMPLIED";
    } else {
      if (this.fEntityScanner.skipString("#FIXED")) {
        defaultType = "#FIXED";
        if (!skipSeparator(true, !scanningInternalSubset()))
          reportFatalError("MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL", new Object[] { elName, atName }); 
      } 
      boolean isVC = (!this.fStandalone && (this.fSeenExternalDTD || this.fSeenExternalPE));
      scanAttributeValue(defaultVal, nonNormalizedDefaultVal, atName, this.fAttributes, 0, isVC, elName);
    } 
    return defaultType;
  }
  
  private final void scanEntityDecl() throws IOException, XNIException {
    boolean isPEDecl = false;
    boolean sawPERef = false;
    this.fReportEntity = false;
    if (this.fEntityScanner.skipSpaces()) {
      if (!this.fEntityScanner.skipChar(37)) {
        isPEDecl = false;
      } else if (skipSeparator(true, !scanningInternalSubset())) {
        isPEDecl = true;
      } else if (scanningInternalSubset()) {
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", null);
        isPEDecl = true;
      } else if (this.fEntityScanner.peekChar() == 37) {
        skipSeparator(false, !scanningInternalSubset());
        isPEDecl = true;
      } else {
        sawPERef = true;
      } 
    } else if (scanningInternalSubset() || !this.fEntityScanner.skipChar(37)) {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", null);
      isPEDecl = false;
    } else if (this.fEntityScanner.skipSpaces()) {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL", null);
      isPEDecl = false;
    } else {
      sawPERef = true;
    } 
    if (sawPERef)
      while (true) {
        String peName = this.fEntityScanner.scanName();
        if (peName == null) {
          reportFatalError("NameRequiredInPEReference", null);
        } else if (!this.fEntityScanner.skipChar(59)) {
          reportFatalError("SemicolonRequiredInPEReference", new Object[] { peName });
        } else {
          startPE(peName, false);
        } 
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(37))
          break; 
        if (!isPEDecl) {
          if (skipSeparator(true, !scanningInternalSubset())) {
            isPEDecl = true;
            break;
          } 
          isPEDecl = this.fEntityScanner.skipChar(37);
        } 
      }  
    String name = this.fEntityScanner.scanName();
    if (name == null)
      reportFatalError("MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL", null); 
    if (!skipSeparator(true, !scanningInternalSubset()))
      reportFatalError("MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL", new Object[] { name }); 
    scanExternalID(this.fStrings, false);
    String systemId = this.fStrings[0];
    String publicId = this.fStrings[1];
    if (isPEDecl && systemId != null)
      this.fSeenExternalPE = true; 
    String notation = null;
    boolean sawSpace = skipSeparator(true, !scanningInternalSubset());
    if (!isPEDecl && this.fEntityScanner.skipString("NDATA")) {
      if (!sawSpace)
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL", new Object[] { name }); 
      if (!skipSeparator(true, !scanningInternalSubset()))
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL", new Object[] { name }); 
      notation = this.fEntityScanner.scanName();
      if (notation == null)
        reportFatalError("MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL", new Object[] { name }); 
    } 
    if (systemId == null) {
      scanEntityValue(name, isPEDecl, this.fLiteral, this.fLiteral2);
      this.fStringBuffer.clear();
      this.fStringBuffer2.clear();
      this.fStringBuffer.append(this.fLiteral.ch, this.fLiteral.offset, this.fLiteral.length);
      this.fStringBuffer2.append(this.fLiteral2.ch, this.fLiteral2.offset, this.fLiteral2.length);
    } 
    skipSeparator(false, !scanningInternalSubset());
    if (!this.fEntityScanner.skipChar(62))
      reportFatalError("EntityDeclUnterminated", new Object[] { name }); 
    this.fMarkUpDepth--;
    if (isPEDecl)
      name = "%" + name; 
    if (systemId != null) {
      String baseSystemId = this.fEntityScanner.getBaseSystemId();
      if (notation != null) {
        this.fEntityStore.addUnparsedEntity(name, publicId, systemId, baseSystemId, notation);
      } else {
        this.fEntityStore.addExternalEntity(name, publicId, systemId, baseSystemId);
      } 
      if (this.fDTDHandler != null) {
        this.fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId));
        if (notation != null) {
          this.fDTDHandler.unparsedEntityDecl(name, this.fResourceIdentifier, notation, null);
        } else {
          this.fDTDHandler.externalEntityDecl(name, this.fResourceIdentifier, null);
        } 
      } 
    } else {
      this.fEntityStore.addInternalEntity(name, this.fStringBuffer.toString());
      if (this.fDTDHandler != null)
        this.fDTDHandler.internalEntityDecl(name, this.fStringBuffer, this.fStringBuffer2, null); 
    } 
    this.fReportEntity = true;
  }
  
  protected final void scanEntityValue(String entityName, boolean isPEDecl, XMLString value, XMLString nonNormalizedValue) throws IOException, XNIException {
    // Byte code:
    //   0: aload_0
    //   1: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   4: invokevirtual scanChar : ()I
    //   7: istore #5
    //   9: iload #5
    //   11: bipush #39
    //   13: if_icmpeq -> 31
    //   16: iload #5
    //   18: bipush #34
    //   20: if_icmpeq -> 31
    //   23: aload_0
    //   24: ldc_w 'OpenQuoteMissingInDecl'
    //   27: aconst_null
    //   28: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   31: aload_0
    //   32: getfield fEntityDepth : I
    //   35: istore #6
    //   37: aload_0
    //   38: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   41: astore #7
    //   43: aload_0
    //   44: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   47: astore #8
    //   49: iconst_0
    //   50: istore #9
    //   52: aload_0
    //   53: getfield fLimitAnalyzer : Lcom/sun/org/apache/xerces/internal/utils/XMLLimitAnalyzer;
    //   56: ifnonnull -> 70
    //   59: aload_0
    //   60: new com/sun/org/apache/xerces/internal/utils/XMLLimitAnalyzer
    //   63: dup
    //   64: invokespecial <init> : ()V
    //   67: putfield fLimitAnalyzer : Lcom/sun/org/apache/xerces/internal/utils/XMLLimitAnalyzer;
    //   70: aload_0
    //   71: getfield fLimitAnalyzer : Lcom/sun/org/apache/xerces/internal/utils/XMLLimitAnalyzer;
    //   74: aload_1
    //   75: invokevirtual startEntity : (Ljava/lang/String;)V
    //   78: aload_0
    //   79: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   82: iload #5
    //   84: aload_0
    //   85: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   88: invokevirtual scanLiteral : (ILcom/sun/org/apache/xerces/internal/xni/XMLString;)I
    //   91: iload #5
    //   93: if_icmpeq -> 664
    //   96: aload_0
    //   97: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   100: invokevirtual clear : ()V
    //   103: aload_0
    //   104: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   107: invokevirtual clear : ()V
    //   110: iload_2
    //   111: ifeq -> 155
    //   114: aload_0
    //   115: getfield fLimitAnalyzer : Lcom/sun/org/apache/xerces/internal/utils/XMLLimitAnalyzer;
    //   118: ifnull -> 155
    //   121: aload_0
    //   122: new java/lang/StringBuilder
    //   125: dup
    //   126: invokespecial <init> : ()V
    //   129: ldc_w '%'
    //   132: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: aload_1
    //   136: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: invokevirtual toString : ()Ljava/lang/String;
    //   142: aload_0
    //   143: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   146: getfield length : I
    //   149: iload #9
    //   151: iadd
    //   152: invokespecial checkLimit : (Ljava/lang/String;I)V
    //   155: iconst_0
    //   156: istore #9
    //   158: aload_0
    //   159: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   162: aload_0
    //   163: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   166: invokevirtual append : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;)V
    //   169: aload_0
    //   170: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   173: aload_0
    //   174: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   177: invokevirtual append : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;)V
    //   180: aload_0
    //   181: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   184: bipush #38
    //   186: invokevirtual skipChar : (I)Z
    //   189: ifeq -> 343
    //   192: aload_0
    //   193: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   196: bipush #35
    //   198: invokevirtual skipChar : (I)Z
    //   201: ifeq -> 230
    //   204: aload_0
    //   205: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   208: ldc_w '&#'
    //   211: invokevirtual append : (Ljava/lang/String;)V
    //   214: aload_0
    //   215: aload_0
    //   216: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   219: aload_0
    //   220: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   223: invokevirtual scanCharReferenceValue : (Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;)I
    //   226: pop
    //   227: goto -> 609
    //   230: aload_0
    //   231: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   234: bipush #38
    //   236: invokevirtual append : (C)V
    //   239: aload_0
    //   240: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   243: bipush #38
    //   245: invokevirtual append : (C)V
    //   248: aload_0
    //   249: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   252: invokevirtual scanName : ()Ljava/lang/String;
    //   255: astore #10
    //   257: aload #10
    //   259: ifnonnull -> 273
    //   262: aload_0
    //   263: ldc_w 'NameRequiredInReference'
    //   266: aconst_null
    //   267: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   270: goto -> 291
    //   273: aload_0
    //   274: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   277: aload #10
    //   279: invokevirtual append : (Ljava/lang/String;)V
    //   282: aload_0
    //   283: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   286: aload #10
    //   288: invokevirtual append : (Ljava/lang/String;)V
    //   291: aload_0
    //   292: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   295: bipush #59
    //   297: invokevirtual skipChar : (I)Z
    //   300: ifne -> 322
    //   303: aload_0
    //   304: ldc_w 'SemicolonRequiredInReference'
    //   307: iconst_1
    //   308: anewarray java/lang/Object
    //   311: dup
    //   312: iconst_0
    //   313: aload #10
    //   315: aastore
    //   316: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   319: goto -> 340
    //   322: aload_0
    //   323: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   326: bipush #59
    //   328: invokevirtual append : (C)V
    //   331: aload_0
    //   332: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   335: bipush #59
    //   337: invokevirtual append : (C)V
    //   340: goto -> 609
    //   343: aload_0
    //   344: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   347: bipush #37
    //   349: invokevirtual skipChar : (I)Z
    //   352: ifeq -> 494
    //   355: aload_0
    //   356: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   359: bipush #37
    //   361: invokevirtual append : (C)V
    //   364: aload_0
    //   365: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   368: invokevirtual scanName : ()Ljava/lang/String;
    //   371: astore #10
    //   373: aload #10
    //   375: ifnonnull -> 389
    //   378: aload_0
    //   379: ldc_w 'NameRequiredInPEReference'
    //   382: aconst_null
    //   383: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   386: goto -> 461
    //   389: aload_0
    //   390: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   393: bipush #59
    //   395: invokevirtual skipChar : (I)Z
    //   398: ifne -> 420
    //   401: aload_0
    //   402: ldc_w 'SemicolonRequiredInPEReference'
    //   405: iconst_1
    //   406: anewarray java/lang/Object
    //   409: dup
    //   410: iconst_0
    //   411: aload #10
    //   413: aastore
    //   414: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   417: goto -> 461
    //   420: aload_0
    //   421: invokevirtual scanningInternalSubset : ()Z
    //   424: ifeq -> 443
    //   427: aload_0
    //   428: ldc_w 'PEReferenceWithinMarkup'
    //   431: iconst_1
    //   432: anewarray java/lang/Object
    //   435: dup
    //   436: iconst_0
    //   437: aload #10
    //   439: aastore
    //   440: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   443: aload_0
    //   444: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   447: aload #10
    //   449: invokevirtual append : (Ljava/lang/String;)V
    //   452: aload_0
    //   453: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   456: bipush #59
    //   458: invokevirtual append : (C)V
    //   461: aload_0
    //   462: aload #10
    //   464: iconst_1
    //   465: invokevirtual startPE : (Ljava/lang/String;Z)V
    //   468: aload_0
    //   469: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   472: invokevirtual skipSpaces : ()Z
    //   475: pop
    //   476: aload_0
    //   477: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   480: bipush #37
    //   482: invokevirtual skipChar : (I)Z
    //   485: ifne -> 491
    //   488: goto -> 609
    //   491: goto -> 355
    //   494: iinc #9, 1
    //   497: aload_0
    //   498: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   501: invokevirtual peekChar : ()I
    //   504: istore #10
    //   506: iload #10
    //   508: invokestatic isHighSurrogate : (I)Z
    //   511: ifeq -> 526
    //   514: aload_0
    //   515: aload_0
    //   516: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   519: invokevirtual scanSurrogates : (Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;)Z
    //   522: pop
    //   523: goto -> 609
    //   526: aload_0
    //   527: iload #10
    //   529: invokevirtual isInvalidLiteral : (I)Z
    //   532: ifeq -> 565
    //   535: aload_0
    //   536: ldc_w 'InvalidCharInLiteral'
    //   539: iconst_1
    //   540: anewarray java/lang/Object
    //   543: dup
    //   544: iconst_0
    //   545: iload #10
    //   547: invokestatic toHexString : (I)Ljava/lang/String;
    //   550: aastore
    //   551: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   554: aload_0
    //   555: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   558: invokevirtual scanChar : ()I
    //   561: pop
    //   562: goto -> 609
    //   565: iload #10
    //   567: iload #5
    //   569: if_icmpne -> 581
    //   572: iload #6
    //   574: aload_0
    //   575: getfield fEntityDepth : I
    //   578: if_icmpeq -> 609
    //   581: aload_0
    //   582: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   585: iload #10
    //   587: i2c
    //   588: invokevirtual append : (C)V
    //   591: aload_0
    //   592: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   595: iload #10
    //   597: i2c
    //   598: invokevirtual append : (C)V
    //   601: aload_0
    //   602: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   605: invokevirtual scanChar : ()I
    //   608: pop
    //   609: aload_0
    //   610: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   613: iload #5
    //   615: aload_0
    //   616: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   619: invokevirtual scanLiteral : (ILcom/sun/org/apache/xerces/internal/xni/XMLString;)I
    //   622: iload #5
    //   624: if_icmpne -> 110
    //   627: aload_0
    //   628: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   631: aload_0
    //   632: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   635: invokevirtual append : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;)V
    //   638: aload_0
    //   639: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   642: aload_0
    //   643: getfield fString : Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   646: invokevirtual append : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;)V
    //   649: aload_0
    //   650: getfield fStringBuffer : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   653: astore #7
    //   655: aload_0
    //   656: getfield fStringBuffer2 : Lcom/sun/org/apache/xerces/internal/util/XMLStringBuffer;
    //   659: astore #8
    //   661: goto -> 694
    //   664: iload_2
    //   665: ifeq -> 694
    //   668: aload_0
    //   669: new java/lang/StringBuilder
    //   672: dup
    //   673: invokespecial <init> : ()V
    //   676: ldc_w '%'
    //   679: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   682: aload_1
    //   683: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   686: invokevirtual toString : ()Ljava/lang/String;
    //   689: aload #7
    //   691: invokespecial checkLimit : (Ljava/lang/String;Lcom/sun/org/apache/xerces/internal/xni/XMLString;)V
    //   694: aload_3
    //   695: aload #7
    //   697: invokevirtual setValues : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;)V
    //   700: aload #4
    //   702: aload #8
    //   704: invokevirtual setValues : (Lcom/sun/org/apache/xerces/internal/xni/XMLString;)V
    //   707: aload_0
    //   708: getfield fLimitAnalyzer : Lcom/sun/org/apache/xerces/internal/utils/XMLLimitAnalyzer;
    //   711: ifnull -> 725
    //   714: aload_0
    //   715: getfield fLimitAnalyzer : Lcom/sun/org/apache/xerces/internal/utils/XMLLimitAnalyzer;
    //   718: getstatic com/sun/org/apache/xerces/internal/utils/XMLSecurityManager$Limit.PARAMETER_ENTITY_SIZE_LIMIT : Lcom/sun/org/apache/xerces/internal/utils/XMLSecurityManager$Limit;
    //   721: aload_1
    //   722: invokevirtual endEntity : (Lcom/sun/org/apache/xerces/internal/utils/XMLSecurityManager$Limit;Ljava/lang/String;)V
    //   725: aload_0
    //   726: getfield fEntityScanner : Lcom/sun/org/apache/xerces/internal/impl/XMLEntityScanner;
    //   729: iload #5
    //   731: invokevirtual skipChar : (I)Z
    //   734: ifne -> 745
    //   737: aload_0
    //   738: ldc_w 'CloseQuoteMissingInDecl'
    //   741: aconst_null
    //   742: invokevirtual reportFatalError : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   745: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1624	-> 0
    //   #1625	-> 9
    //   #1626	-> 23
    //   #1629	-> 31
    //   #1631	-> 37
    //   #1632	-> 43
    //   #1633	-> 49
    //   #1634	-> 52
    //   #1635	-> 59
    //   #1637	-> 70
    //   #1639	-> 78
    //   #1640	-> 96
    //   #1641	-> 103
    //   #1643	-> 110
    //   #1644	-> 121
    //   #1646	-> 155
    //   #1647	-> 158
    //   #1648	-> 169
    //   #1649	-> 180
    //   #1650	-> 192
    //   #1651	-> 204
    //   #1652	-> 214
    //   #1655	-> 230
    //   #1656	-> 239
    //   #1657	-> 248
    //   #1658	-> 257
    //   #1659	-> 262
    //   #1663	-> 273
    //   #1664	-> 282
    //   #1666	-> 291
    //   #1667	-> 303
    //   #1671	-> 322
    //   #1672	-> 331
    //   #1674	-> 340
    //   #1676	-> 343
    //   #1678	-> 355
    //   #1679	-> 364
    //   #1680	-> 373
    //   #1681	-> 378
    //   #1684	-> 389
    //   #1685	-> 401
    //   #1689	-> 420
    //   #1690	-> 427
    //   #1693	-> 443
    //   #1694	-> 452
    //   #1696	-> 461
    //   #1700	-> 468
    //   #1701	-> 476
    //   #1702	-> 488
    //   #1703	-> 491
    //   #1706	-> 494
    //   #1707	-> 497
    //   #1708	-> 506
    //   #1709	-> 514
    //   #1711	-> 526
    //   #1712	-> 535
    //   #1713	-> 547
    //   #1712	-> 551
    //   #1714	-> 554
    //   #1719	-> 565
    //   #1720	-> 581
    //   #1721	-> 591
    //   #1722	-> 601
    //   #1725	-> 609
    //   #1726	-> 627
    //   #1727	-> 638
    //   #1728	-> 649
    //   #1729	-> 655
    //   #1731	-> 664
    //   #1732	-> 668
    //   #1735	-> 694
    //   #1736	-> 700
    //   #1737	-> 707
    //   #1738	-> 714
    //   #1741	-> 725
    //   #1742	-> 737
    //   #1744	-> 745
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   257	83	10	eName	Ljava/lang/String;
    //   373	118	10	peName	Ljava/lang/String;
    //   506	103	10	c	I
    //   0	746	0	this	Lcom/sun/org/apache/xerces/internal/impl/XMLDTDScannerImpl;
    //   0	746	1	entityName	Ljava/lang/String;
    //   0	746	2	isPEDecl	Z
    //   0	746	3	value	Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   0	746	4	nonNormalizedValue	Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   9	737	5	quote	I
    //   37	709	6	entityDepth	I
    //   43	703	7	literal	Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   49	697	8	literal2	Lcom/sun/org/apache/xerces/internal/xni/XMLString;
    //   52	694	9	countChar	I
  }
  
  private final void scanNotationDecl() throws IOException, XNIException {
    this.fReportEntity = false;
    if (!skipSeparator(true, !scanningInternalSubset()))
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL", null); 
    String name = this.fEntityScanner.scanName();
    if (name == null)
      reportFatalError("MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL", null); 
    if (!skipSeparator(true, !scanningInternalSubset()))
      reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL", new Object[] { name }); 
    scanExternalID(this.fStrings, true);
    String systemId = this.fStrings[0];
    String publicId = this.fStrings[1];
    String baseSystemId = this.fEntityScanner.getBaseSystemId();
    if (systemId == null && publicId == null)
      reportFatalError("ExternalIDorPublicIDRequired", new Object[] { name }); 
    skipSeparator(false, !scanningInternalSubset());
    if (!this.fEntityScanner.skipChar(62))
      reportFatalError("NotationDeclUnterminated", new Object[] { name }); 
    this.fMarkUpDepth--;
    this.fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId));
    if (this.nonValidatingMode)
      this.nvGrammarInfo.notationDecl(name, this.fResourceIdentifier, null); 
    if (this.fDTDHandler != null)
      this.fDTDHandler.notationDecl(name, this.fResourceIdentifier, null); 
    this.fReportEntity = true;
  }
  
  private final void scanConditionalSect(int currPEDepth) throws IOException, XNIException {
    this.fReportEntity = false;
    skipSeparator(false, !scanningInternalSubset());
    if (this.fEntityScanner.skipString("INCLUDE")) {
      skipSeparator(false, !scanningInternalSubset());
      if (currPEDepth != this.fPEDepth && this.fValidation)
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[] { this.fEntityManager.fCurrentEntity.name }, (short)1); 
      if (!this.fEntityScanner.skipChar(91))
        reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null); 
      if (this.fDTDHandler != null)
        this.fDTDHandler.startConditional((short)0, null); 
      this.fIncludeSectDepth++;
      this.fReportEntity = true;
    } else {
      if (this.fEntityScanner.skipString("IGNORE")) {
        skipSeparator(false, !scanningInternalSubset());
        if (currPEDepth != this.fPEDepth && this.fValidation)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[] { this.fEntityManager.fCurrentEntity.name }, (short)1); 
        if (this.fDTDHandler != null)
          this.fDTDHandler.startConditional((short)1, null); 
        if (!this.fEntityScanner.skipChar(91))
          reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null); 
        this.fReportEntity = true;
        int initialDepth = ++this.fIncludeSectDepth;
        if (this.fDTDHandler != null)
          this.fIgnoreConditionalBuffer.clear(); 
        while (true) {
          while (this.fEntityScanner.skipChar(60)) {
            if (this.fDTDHandler != null)
              this.fIgnoreConditionalBuffer.append('<'); 
            if (this.fEntityScanner.skipChar(33)) {
              if (this.fEntityScanner.skipChar(91)) {
                if (this.fDTDHandler != null)
                  this.fIgnoreConditionalBuffer.append("!["); 
                this.fIncludeSectDepth++;
                continue;
              } 
              if (this.fDTDHandler != null)
                this.fIgnoreConditionalBuffer.append("!"); 
            } 
          } 
          if (this.fEntityScanner.skipChar(93)) {
            if (this.fDTDHandler != null)
              this.fIgnoreConditionalBuffer.append(']'); 
            if (this.fEntityScanner.skipChar(93)) {
              if (this.fDTDHandler != null)
                this.fIgnoreConditionalBuffer.append(']'); 
              while (this.fEntityScanner.skipChar(93)) {
                if (this.fDTDHandler != null)
                  this.fIgnoreConditionalBuffer.append(']'); 
              } 
              if (this.fEntityScanner.skipChar(62)) {
                if (this.fIncludeSectDepth-- == initialDepth) {
                  this.fMarkUpDepth--;
                  if (this.fDTDHandler != null) {
                    this.fLiteral.setValues(this.fIgnoreConditionalBuffer.ch, 0, this.fIgnoreConditionalBuffer.length - 2);
                    this.fDTDHandler.ignoredCharacters(this.fLiteral, null);
                    this.fDTDHandler.endConditional(null);
                  } 
                  return;
                } 
                if (this.fDTDHandler != null)
                  this.fIgnoreConditionalBuffer.append('>'); 
              } 
            } 
            continue;
          } 
          int c = this.fEntityScanner.scanChar();
          if (this.fScannerState == 0) {
            reportFatalError("IgnoreSectUnterminated", null);
            return;
          } 
          if (this.fDTDHandler != null)
            this.fIgnoreConditionalBuffer.append((char)c); 
        } 
      } 
      reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
    } 
  }
  
  protected final boolean scanDecls(boolean complete) throws IOException, XNIException {
    skipSeparator(false, true);
    boolean again = true;
    while (again && this.fScannerState == 2) {
      again = complete;
      if (this.fEntityScanner.skipChar(60)) {
        this.fMarkUpDepth++;
        if (this.fEntityScanner.skipChar(63)) {
          this.fStringBuffer.clear();
          scanPI(this.fStringBuffer);
          this.fMarkUpDepth--;
        } else if (this.fEntityScanner.skipChar(33)) {
          if (this.fEntityScanner.skipChar(45)) {
            if (!this.fEntityScanner.skipChar(45)) {
              reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
            } else {
              scanComment();
            } 
          } else if (this.fEntityScanner.skipString("ELEMENT")) {
            scanElementDecl();
          } else if (this.fEntityScanner.skipString("ATTLIST")) {
            scanAttlistDecl();
          } else if (this.fEntityScanner.skipString("ENTITY")) {
            scanEntityDecl();
          } else if (this.fEntityScanner.skipString("NOTATION")) {
            scanNotationDecl();
          } else if (this.fEntityScanner.skipChar(91) && 
            !scanningInternalSubset()) {
            scanConditionalSect(this.fPEDepth);
          } else {
            this.fMarkUpDepth--;
            reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
          } 
        } else {
          this.fMarkUpDepth--;
          reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
        } 
      } else if (this.fIncludeSectDepth > 0 && this.fEntityScanner.skipChar(93)) {
        if (!this.fEntityScanner.skipChar(93) || 
          !this.fEntityScanner.skipChar(62))
          reportFatalError("IncludeSectUnterminated", null); 
        if (this.fDTDHandler != null)
          this.fDTDHandler.endConditional(null); 
        this.fIncludeSectDepth--;
        this.fMarkUpDepth--;
      } else {
        if (scanningInternalSubset() && this.fEntityScanner
          .peekChar() == 93)
          return false; 
        if (!this.fEntityScanner.skipSpaces())
          reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null); 
      } 
      skipSeparator(false, true);
    } 
    return (this.fScannerState != 0);
  }
  
  private boolean skipSeparator(boolean spaceRequired, boolean lookForPERefs) throws IOException, XNIException {
    int depth = this.fPEDepth;
    boolean sawSpace = this.fEntityScanner.skipSpaces();
    if (!lookForPERefs || !this.fEntityScanner.skipChar(37))
      return (!spaceRequired || sawSpace || depth != this.fPEDepth); 
    while (true) {
      String name = this.fEntityScanner.scanName();
      if (name == null) {
        reportFatalError("NameRequiredInPEReference", null);
      } else if (!this.fEntityScanner.skipChar(59)) {
        reportFatalError("SemicolonRequiredInPEReference", new Object[] { name });
      } 
      startPE(name, false);
      this.fEntityScanner.skipSpaces();
      if (!this.fEntityScanner.skipChar(37))
        return true; 
    } 
  }
  
  private final void pushContentStack(int c) {
    if (this.fContentStack.length == this.fContentDepth) {
      int[] newStack = new int[this.fContentDepth * 2];
      System.arraycopy(this.fContentStack, 0, newStack, 0, this.fContentDepth);
      this.fContentStack = newStack;
    } 
    this.fContentStack[this.fContentDepth++] = c;
  }
  
  private final int popContentStack() {
    return this.fContentStack[--this.fContentDepth];
  }
  
  private final void pushPEStack(int depth, boolean report) {
    if (this.fPEStack.length == this.fPEDepth) {
      int[] newIntStack = new int[this.fPEDepth * 2];
      System.arraycopy(this.fPEStack, 0, newIntStack, 0, this.fPEDepth);
      this.fPEStack = newIntStack;
      boolean[] newBooleanStack = new boolean[this.fPEDepth * 2];
      System.arraycopy(this.fPEReport, 0, newBooleanStack, 0, this.fPEDepth);
      this.fPEReport = newBooleanStack;
    } 
    this.fPEReport[this.fPEDepth] = report;
    this.fPEStack[this.fPEDepth++] = depth;
  }
  
  private final int popPEStack() {
    return this.fPEStack[--this.fPEDepth];
  }
  
  private final boolean peekReportEntity() {
    return this.fPEReport[this.fPEDepth - 1];
  }
  
  private final void ensureEnumerationSize(int size) {
    if (this.fEnumeration.length == size) {
      String[] newEnum = new String[size * 2];
      System.arraycopy(this.fEnumeration, 0, newEnum, 0, size);
      this.fEnumeration = newEnum;
    } 
  }
  
  private void init() {
    this.fStartDTDCalled = false;
    this.fExtEntityDepth = 0;
    this.fIncludeSectDepth = 0;
    this.fMarkUpDepth = 0;
    this.fPEDepth = 0;
    this.fStandalone = false;
    this.fSeenExternalDTD = false;
    this.fSeenExternalPE = false;
    setScannerState(1);
    this.fLimitAnalyzer = new XMLLimitAnalyzer();
  }
  
  private void checkLimit(String entityName, XMLString buffer) {
    checkLimit(entityName, buffer.length);
  }
  
  private void checkLimit(String entityName, int len) {
    if (this.fLimitAnalyzer == null)
      this.fLimitAnalyzer = new XMLLimitAnalyzer(); 
    this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, entityName, len);
    if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      reportFatalError("MaxEntitySizeLimit", new Object[] { entityName, 
            Integer.valueOf(this.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT)), 
            Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT)), this.fSecurityManager
            .getStateLiteral(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT) });
    } 
    if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      reportFatalError("TotalEntitySizeLimit", new Object[] { Integer.valueOf(this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), 
            Integer.valueOf(this.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), this.fSecurityManager
            .getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) });
    } 
  }
  
  public DTDGrammar getGrammar() {
    return this.nvGrammarInfo;
  }
}
