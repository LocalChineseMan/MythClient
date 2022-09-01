package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.XMLBufferListener;
import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;

public class XMLDocumentScannerImpl extends XMLDocumentFragmentScannerImpl {
  protected static final int SCANNER_STATE_XML_DECL = 42;
  
  protected static final int SCANNER_STATE_PROLOG = 43;
  
  protected static final int SCANNER_STATE_TRAILING_MISC = 44;
  
  protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 45;
  
  protected static final int SCANNER_STATE_DTD_EXTERNAL = 46;
  
  protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 47;
  
  protected static final int SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION = 48;
  
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
  
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  private static final String[] RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/disallow-doctype-decl" };
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[] { Boolean.TRUE, Boolean.FALSE };
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validation-manager" };
  
  private static final Object[] PROPERTY_DEFAULTS = new Object[] { null, null };
  
  protected XMLDTDScanner fDTDScanner = null;
  
  protected ValidationManager fValidationManager;
  
  protected XMLStringBuffer fDTDDecl = null;
  
  protected boolean fReadingDTD = false;
  
  protected boolean fAddedListener = false;
  
  protected String fDoctypeName;
  
  protected String fDoctypePublicId;
  
  protected String fDoctypeSystemId;
  
  protected NamespaceContext fNamespaceContext = new NamespaceSupport();
  
  protected boolean fLoadExternalDTD = true;
  
  protected boolean fSeenDoctypeDecl;
  
  protected boolean fScanEndElement;
  
  protected XMLDocumentFragmentScannerImpl.Driver fXMLDeclDriver = new XMLDeclDriver();
  
  protected XMLDocumentFragmentScannerImpl.Driver fPrologDriver = new PrologDriver();
  
  protected XMLDocumentFragmentScannerImpl.Driver fDTDDriver = null;
  
  protected XMLDocumentFragmentScannerImpl.Driver fTrailingMiscDriver = new TrailingMiscDriver();
  
  protected int fStartPos = 0;
  
  protected int fEndPos = 0;
  
  protected boolean fSeenInternalSubset = false;
  
  private String[] fStrings = new String[3];
  
  private XMLInputSource fExternalSubsetSource = null;
  
  private final XMLDTDDescription fDTDDescription = new XMLDTDDescription(null, null, null, null, null);
  
  private XMLString fString = new XMLString();
  
  private static final char[] DOCTYPE = new char[] { 'D', 'O', 'C', 'T', 'Y', 'P', 'E' };
  
  private static final char[] COMMENTSTRING = new char[] { '-', '-' };
  
  public void setInputSource(XMLInputSource inputSource) throws IOException {
    this.fEntityManager.setEntityHandler((XMLEntityHandler)this);
    this.fEntityManager.startDocumentEntity(inputSource);
    setScannerState(7);
  }
  
  public int getScannetState() {
    return this.fScannerState;
  }
  
  public void reset(PropertyManager propertyManager) {
    super.reset(propertyManager);
    this.fDoctypeName = null;
    this.fDoctypePublicId = null;
    this.fDoctypeSystemId = null;
    this.fSeenDoctypeDecl = false;
    this.fNamespaceContext.reset();
    this.fSupportDTD = ((Boolean)propertyManager.getProperty("javax.xml.stream.supportDTD")).booleanValue();
    this.fLoadExternalDTD = !((Boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd")).booleanValue();
    setScannerState(7);
    setDriver(this.fXMLDeclDriver);
    this.fSeenInternalSubset = false;
    if (this.fDTDScanner != null)
      ((XMLDTDScannerImpl)this.fDTDScanner).reset(propertyManager); 
    this.fEndPos = 0;
    this.fStartPos = 0;
    if (this.fDTDDecl != null)
      this.fDTDDecl.clear(); 
  }
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    super.reset(componentManager);
    this.fDoctypeName = null;
    this.fDoctypePublicId = null;
    this.fDoctypeSystemId = null;
    this.fSeenDoctypeDecl = false;
    this.fExternalSubsetSource = null;
    this.fLoadExternalDTD = componentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
    this.fDisallowDoctype = componentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
    this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fSeenInternalSubset = false;
    this.fDTDScanner = (XMLDTDScanner)componentManager.getProperty("http://apache.org/xml/properties/internal/dtd-scanner");
    this.fValidationManager = (ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null);
    try {
      this.fNamespaceContext = (NamespaceContext)componentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
    } catch (XMLConfigurationException xMLConfigurationException) {}
    if (this.fNamespaceContext == null)
      this.fNamespaceContext = new NamespaceSupport(); 
    this.fNamespaceContext.reset();
    this.fEndPos = 0;
    this.fStartPos = 0;
    if (this.fDTDDecl != null)
      this.fDTDDecl.clear(); 
    setScannerState(42);
    setDriver(this.fXMLDeclDriver);
  }
  
  public String[] getRecognizedFeatures() {
    String[] featureIds = super.getRecognizedFeatures();
    int length = (featureIds != null) ? featureIds.length : 0;
    String[] combinedFeatureIds = new String[length + RECOGNIZED_FEATURES.length];
    if (featureIds != null)
      System.arraycopy(featureIds, 0, combinedFeatureIds, 0, featureIds.length); 
    System.arraycopy(RECOGNIZED_FEATURES, 0, combinedFeatureIds, length, RECOGNIZED_FEATURES.length);
    return combinedFeatureIds;
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    super.setFeature(featureId, state);
    if (featureId.startsWith("http://apache.org/xml/features/")) {
      int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
      if (suffixLength == "nonvalidating/load-external-dtd".length() && featureId
        .endsWith("nonvalidating/load-external-dtd")) {
        this.fLoadExternalDTD = state;
        return;
      } 
      if (suffixLength == "disallow-doctype-decl".length() && featureId
        .endsWith("disallow-doctype-decl")) {
        this.fDisallowDoctype = state;
        return;
      } 
    } 
  }
  
  public String[] getRecognizedProperties() {
    String[] propertyIds = super.getRecognizedProperties();
    int length = (propertyIds != null) ? propertyIds.length : 0;
    String[] combinedPropertyIds = new String[length + RECOGNIZED_PROPERTIES.length];
    if (propertyIds != null)
      System.arraycopy(propertyIds, 0, combinedPropertyIds, 0, propertyIds.length); 
    System.arraycopy(RECOGNIZED_PROPERTIES, 0, combinedPropertyIds, length, RECOGNIZED_PROPERTIES.length);
    return combinedPropertyIds;
  }
  
  public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    super.setProperty(propertyId, value);
    if (propertyId.startsWith("http://apache.org/xml/properties/")) {
      int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
      if (suffixLength == "internal/dtd-scanner".length() && propertyId
        .endsWith("internal/dtd-scanner"))
        this.fDTDScanner = (XMLDTDScanner)value; 
      if (suffixLength == "internal/namespace-context".length() && propertyId
        .endsWith("internal/namespace-context") && 
        value != null)
        this.fNamespaceContext = (NamespaceContext)value; 
      return;
    } 
  }
  
  public Boolean getFeatureDefault(String featureId) {
    for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
      if (RECOGNIZED_FEATURES[i].equals(featureId))
        return FEATURE_DEFAULTS[i]; 
    } 
    return super.getFeatureDefault(featureId);
  }
  
  public Object getPropertyDefault(String propertyId) {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(propertyId))
        return PROPERTY_DEFAULTS[i]; 
    } 
    return super.getPropertyDefault(propertyId);
  }
  
  public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
    super.startEntity(name, identifier, encoding, augs);
    this.fEntityScanner.registerListener((XMLBufferListener)this);
    if (!name.equals("[xml]") && this.fEntityScanner.isExternal())
      if (augs == null || !((Boolean)augs.getItem("ENTITY_SKIPPED")).booleanValue())
        setScannerState(36);  
    if (this.fDocumentHandler != null && name.equals("[xml]"))
      this.fDocumentHandler.startDocument(this.fEntityScanner, encoding, this.fNamespaceContext, null); 
  }
  
  public void endEntity(String name, Augmentations augs) throws IOException, XNIException {
    super.endEntity(name, augs);
    if (name.equals("[xml]"))
      if (this.fMarkupDepth == 0 && this.fDriver == this.fTrailingMiscDriver) {
        setScannerState(34);
      } else {
        throw new EOFException();
      }  
  }
  
  public XMLStringBuffer getDTDDecl() {
    Entity entity = this.fEntityScanner.getCurrentEntity();
    this.fDTDDecl.append(((Entity.ScannedEntity)entity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
    if (this.fSeenInternalSubset)
      this.fDTDDecl.append("]>"); 
    return this.fDTDDecl;
  }
  
  public String getCharacterEncodingScheme() {
    return this.fDeclaredEncoding;
  }
  
  public int next() throws IOException, XNIException {
    return this.fDriver.next();
  }
  
  public NamespaceContext getNamespaceContext() {
    return this.fNamespaceContext;
  }
  
  protected XMLDocumentFragmentScannerImpl.Driver createContentDriver() {
    return new ContentDriver();
  }
  
  protected boolean scanDoctypeDecl(boolean supportDTD) throws IOException, XNIException {
    if (!this.fEntityScanner.skipSpaces())
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL", null); 
    this.fDoctypeName = this.fEntityScanner.scanName();
    if (this.fDoctypeName == null)
      reportFatalError("MSG_ROOT_ELEMENT_TYPE_REQUIRED", null); 
    if (this.fEntityScanner.skipSpaces()) {
      scanExternalID(this.fStrings, false);
      this.fDoctypeSystemId = this.fStrings[0];
      this.fDoctypePublicId = this.fStrings[1];
      this.fEntityScanner.skipSpaces();
    } 
    this.fHasExternalDTD = (this.fDoctypeSystemId != null);
    if (supportDTD && !this.fHasExternalDTD && this.fExternalSubsetResolver != null) {
      this.fDTDDescription.setValues(null, null, this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
      this.fDTDDescription.setRootName(this.fDoctypeName);
      this.fExternalSubsetSource = this.fExternalSubsetResolver.getExternalSubset(this.fDTDDescription);
      this.fHasExternalDTD = (this.fExternalSubsetSource != null);
    } 
    if (supportDTD && this.fDocumentHandler != null)
      if (this.fExternalSubsetSource == null) {
        this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fDoctypePublicId, this.fDoctypeSystemId, null);
      } else {
        this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fExternalSubsetSource.getPublicId(), this.fExternalSubsetSource.getSystemId(), null);
      }  
    boolean internalSubset = true;
    if (!this.fEntityScanner.skipChar(91)) {
      internalSubset = false;
      this.fEntityScanner.skipSpaces();
      if (!this.fEntityScanner.skipChar(62))
        reportFatalError("DoctypedeclUnterminated", new Object[] { this.fDoctypeName }); 
      this.fMarkupDepth--;
    } 
    return internalSubset;
  }
  
  protected void setEndDTDScanState() {
    setScannerState(43);
    setDriver(this.fPrologDriver);
    this.fEntityManager.setEntityHandler((XMLEntityHandler)this);
    this.fReadingDTD = false;
  }
  
  protected String getScannerStateName(int state) {
    switch (state) {
      case 42:
        return "SCANNER_STATE_XML_DECL";
      case 43:
        return "SCANNER_STATE_PROLOG";
      case 44:
        return "SCANNER_STATE_TRAILING_MISC";
      case 45:
        return "SCANNER_STATE_DTD_INTERNAL_DECLS";
      case 46:
        return "SCANNER_STATE_DTD_EXTERNAL";
      case 47:
        return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
    } 
    return super.getScannerStateName(state);
  }
  
  protected final class XMLDeclDriver implements XMLDocumentFragmentScannerImpl.Driver {
    public int next() throws IOException, XNIException {
      XMLDocumentScannerImpl.this.setScannerState(43);
      XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fPrologDriver);
      try {
        if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.xmlDecl)) {
          XMLDocumentScannerImpl.this.fMarkupDepth++;
          if (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
            XMLDocumentScannerImpl.this.fStringBuffer.clear();
            XMLDocumentScannerImpl.this.fStringBuffer.append("xml");
            while (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar()))
              XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar()); 
            String target = XMLDocumentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentScannerImpl.this.fStringBuffer.ch, XMLDocumentScannerImpl.this.fStringBuffer.offset, XMLDocumentScannerImpl.this.fStringBuffer.length);
            XMLDocumentScannerImpl.this.fContentBuffer.clear();
            XMLDocumentScannerImpl.this.scanPIData(target, XMLDocumentScannerImpl.this.fContentBuffer);
            XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
            return 3;
          } 
          XMLDocumentScannerImpl.this.scanXMLDeclOrTextDecl(false);
          XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
          return 7;
        } 
        XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
        return 7;
      } catch (EOFException e) {
        XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        return -1;
      } 
    }
  }
  
  protected final class PrologDriver implements XMLDocumentFragmentScannerImpl.Driver {
    public int next() throws IOException, XNIException {
      try {
        do {
          switch (XMLDocumentScannerImpl.this.fScannerState) {
            case 43:
              XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60)) {
                XMLDocumentScannerImpl.this.setScannerState(21);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(38)) {
                XMLDocumentScannerImpl.this.setScannerState(28);
                break;
              } 
              XMLDocumentScannerImpl.this.setScannerState(22);
              break;
            case 21:
              XMLDocumentScannerImpl.this.fMarkupDepth++;
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
                XMLDocumentScannerImpl.this.setScannerState(23);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
                if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45)) {
                  if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45))
                    XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null); 
                  XMLDocumentScannerImpl.this.setScannerState(27);
                  break;
                } 
                if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE)) {
                  XMLDocumentScannerImpl.this.setScannerState(24);
                  Entity entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
                  if (entity instanceof Entity.ScannedEntity)
                    XMLDocumentScannerImpl.this.fStartPos = ((Entity.ScannedEntity)entity).position; 
                  XMLDocumentScannerImpl.this.fReadingDTD = true;
                  if (XMLDocumentScannerImpl.this.fDTDDecl == null)
                    XMLDocumentScannerImpl.this.fDTDDecl = new XMLStringBuffer(); 
                  XMLDocumentScannerImpl.this.fDTDDecl.append("<!DOCTYPE");
                  break;
                } 
                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                break;
              } 
              if (XMLChar.isNameStart(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                XMLDocumentScannerImpl.this.setScannerState(26);
                XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                return XMLDocumentScannerImpl.this.fContentDriver.next();
              } 
              XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
              break;
          } 
        } while (XMLDocumentScannerImpl.this.fScannerState == 43 || XMLDocumentScannerImpl.this.fScannerState == 21);
        switch (XMLDocumentScannerImpl.this.fScannerState) {
          case 27:
            XMLDocumentScannerImpl.this.scanComment();
            XMLDocumentScannerImpl.this.setScannerState(43);
            return 5;
          case 23:
            XMLDocumentScannerImpl.this.fContentBuffer.clear();
            XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
            XMLDocumentScannerImpl.this.setScannerState(43);
            return 3;
          case 24:
            if (XMLDocumentScannerImpl.this.fDisallowDoctype)
              XMLDocumentScannerImpl.this.reportFatalError("DoctypeNotAllowed", null); 
            if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl)
              XMLDocumentScannerImpl.this.reportFatalError("AlreadySeenDoctype", null); 
            XMLDocumentScannerImpl.this.fSeenDoctypeDecl = true;
            if (XMLDocumentScannerImpl.this.scanDoctypeDecl(XMLDocumentScannerImpl.this.fSupportDTD)) {
              XMLDocumentScannerImpl.this.setScannerState(45);
              XMLDocumentScannerImpl.this.fSeenInternalSubset = true;
              if (XMLDocumentScannerImpl.this.fDTDDriver == null)
                XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this); 
              XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
              return XMLDocumentScannerImpl.this.fDTDDriver.next();
            } 
            if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
              Entity entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
              if (entity instanceof Entity.ScannedEntity)
                XMLDocumentScannerImpl.this.fEndPos = ((Entity.ScannedEntity)entity).position; 
              XMLDocumentScannerImpl.this.fReadingDTD = false;
            } 
            if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
              if ((XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || 
                !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                if (XMLDocumentScannerImpl.this.fSupportDTD) {
                  XMLDocumentScannerImpl.this.setScannerState(46);
                } else {
                  XMLDocumentScannerImpl.this.setScannerState(43);
                } 
                XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                if (XMLDocumentScannerImpl.this.fDTDDriver == null)
                  XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this); 
                return XMLDocumentScannerImpl.this.fDTDDriver.next();
              } 
            } else if (XMLDocumentScannerImpl.this.fExternalSubsetSource != null && (
              XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || 
              !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
              XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
              XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
              if (XMLDocumentScannerImpl.this.fSupportDTD) {
                XMLDocumentScannerImpl.this.setScannerState(47);
              } else {
                XMLDocumentScannerImpl.this.setScannerState(43);
              } 
              XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
              if (XMLDocumentScannerImpl.this.fDTDDriver == null)
                XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this); 
              return XMLDocumentScannerImpl.this.fDTDDriver.next();
            } 
            if (XMLDocumentScannerImpl.this.fDTDScanner != null)
              XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null); 
            XMLDocumentScannerImpl.this.setScannerState(43);
            return 11;
          case 22:
            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInProlog", null);
            XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
          case 28:
            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInProlog", null);
            break;
        } 
      } catch (EOFException e) {
        XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        return -1;
      } 
      return -1;
    }
  }
  
  protected final class XMLDocumentScannerImpl {}
  
  protected class ContentDriver extends XMLDocumentFragmentScannerImpl.FragmentContentDriver {
    protected boolean scanForDoctypeHook() throws IOException, XNIException {
      if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE)) {
        XMLDocumentScannerImpl.this.setScannerState(24);
        return true;
      } 
      return false;
    }
    
    protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
      XMLDocumentScannerImpl.this.setScannerState(44);
      XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
      return true;
    }
    
    protected boolean scanRootElementHook() throws IOException, XNIException {
      if (XMLDocumentScannerImpl.this.scanStartElement()) {
        XMLDocumentScannerImpl.this.setScannerState(44);
        XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
        return true;
      } 
      return false;
    }
    
    protected void endOfFileHook(EOFException e) throws IOException, XNIException {
      XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
    }
    
    protected void resolveExternalSubsetAndRead() throws IOException, XNIException {
      XMLDocumentScannerImpl.this.fDTDDescription.setValues(null, null, XMLDocumentScannerImpl.this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
      XMLDocumentScannerImpl.this.fDTDDescription.setRootName(XMLDocumentScannerImpl.this.fElementQName.rawname);
      XMLInputSource src = XMLDocumentScannerImpl.this.fExternalSubsetResolver.getExternalSubset(XMLDocumentScannerImpl.this.fDTDDescription);
      if (src != null) {
        XMLDocumentScannerImpl.this.fDoctypeName = XMLDocumentScannerImpl.this.fElementQName.rawname;
        XMLDocumentScannerImpl.this.fDoctypePublicId = src.getPublicId();
        XMLDocumentScannerImpl.this.fDoctypeSystemId = src.getSystemId();
        if (XMLDocumentScannerImpl.this.fDocumentHandler != null)
          XMLDocumentScannerImpl.this.fDocumentHandler.doctypeDecl(XMLDocumentScannerImpl.this.fDoctypeName, XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null); 
        try {
          XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(src);
          while (XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(true));
        } finally {
          XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler((XMLEntityHandler)XMLDocumentScannerImpl.this);
        } 
      } 
    }
  }
  
  protected final class TrailingMiscDriver implements XMLDocumentFragmentScannerImpl.Driver {
    public int next() throws IOException, XNIException {
      if (XMLDocumentScannerImpl.this.fEmptyElement) {
        XMLDocumentScannerImpl.this.fEmptyElement = false;
        return 2;
      } 
      try {
        int ch;
        if (XMLDocumentScannerImpl.this.fScannerState == 34)
          return 8; 
        do {
          switch (XMLDocumentScannerImpl.this.fScannerState) {
            case 44:
              XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
              if (XMLDocumentScannerImpl.this.fScannerState == 34)
                return 8; 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60)) {
                XMLDocumentScannerImpl.this.setScannerState(21);
                break;
              } 
              XMLDocumentScannerImpl.this.setScannerState(22);
              break;
            case 21:
              XMLDocumentScannerImpl.this.fMarkupDepth++;
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
                XMLDocumentScannerImpl.this.setScannerState(23);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
                XMLDocumentScannerImpl.this.setScannerState(27);
                break;
              } 
              if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(47)) {
                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                break;
              } 
              if (XMLChar.isNameStart(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                XMLDocumentScannerImpl.this.scanStartElement();
                XMLDocumentScannerImpl.this.setScannerState(22);
                break;
              } 
              XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
              break;
          } 
        } while (XMLDocumentScannerImpl.this.fScannerState == 21 || XMLDocumentScannerImpl.this.fScannerState == 44);
        switch (XMLDocumentScannerImpl.this.fScannerState) {
          case 23:
            XMLDocumentScannerImpl.this.fContentBuffer.clear();
            XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 3;
          case 27:
            if (!XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.COMMENTSTRING))
              XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null); 
            XMLDocumentScannerImpl.this.scanComment();
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 5;
          case 22:
            ch = XMLDocumentScannerImpl.this.fEntityScanner.peekChar();
            if (ch == -1) {
              XMLDocumentScannerImpl.this.setScannerState(34);
              return 8;
            } 
            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInTrailingMisc", null);
            XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 4;
          case 28:
            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInTrailingMisc", null);
            XMLDocumentScannerImpl.this.setScannerState(44);
            return 9;
          case 34:
            XMLDocumentScannerImpl.this.setScannerState(48);
            return 8;
          case 48:
            throw new NoSuchElementException("No more events to be parsed");
        } 
        throw new XNIException("Scanner State " + XMLDocumentScannerImpl.this.fScannerState + " not Recognized ");
      } catch (EOFException e) {
        if (XMLDocumentScannerImpl.this.fMarkupDepth != 0) {
          XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
          return -1;
        } 
        XMLDocumentScannerImpl.this.setScannerState(34);
        return 8;
      } 
    }
  }
  
  public void refresh(int refreshPosition) {
    super.refresh(refreshPosition);
    if (this.fReadingDTD) {
      Entity entity = this.fEntityScanner.getCurrentEntity();
      if (entity instanceof Entity.ScannedEntity)
        this.fEndPos = ((Entity.ScannedEntity)entity).position; 
      this.fDTDDecl.append(((Entity.ScannedEntity)entity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
      this.fStartPos = refreshPosition;
    } 
  }
}
